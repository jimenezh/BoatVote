package com.example.voteboat.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.voteboat.R;
import com.example.voteboat.activities.MainActivity;
import com.example.voteboat.adapters.ToDoAdapter;
import com.example.voteboat.clients.GoogleCivicClient;
import com.example.voteboat.databinding.FragmentToDoBinding;
import com.example.voteboat.models.Election;
import com.example.voteboat.models.Item;
import com.example.voteboat.models.Representative;
import com.example.voteboat.models.ToDoItem;
import com.example.voteboat.models.User;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.multilevelview.MultiLevelRecyclerView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import okhttp3.Headers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions
public class ToDoFragment extends Fragment {
    public static final String TAG = "ToDoFragment";
    FragmentToDoBinding binding;

    ToDoAdapter adapter;
    List<Item> items;
    MultiLevelRecyclerView multiLevelRecyclerView;
    LinearLayoutManager linearLayoutManager;

    // To prevent Geocoder from crashing
    Context context;

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public String photoFileName = "photo.jpg";

    public static final int CALL_ACTIVITY_REQUEST_CODE = 47;


    public ToDoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentToDoBinding.inflate(inflater);
        // Initialize data
        items = new ArrayList<>();
        items.add(new Item(0, "To Do:")); // Add label immediately so RV doesn't complain
        configureRecyclerView();
        // Setting up To Do tab. Query immediately for to do items since they're in parse
        populateToDo();
        // Setting up representatives tab, we query for reps once we have the address
        return binding.getRoot();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ToDoFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void configureRecyclerView() {
        multiLevelRecyclerView = binding.rvItems;
        linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);

        adapter = new ToDoAdapter(context, items, this, multiLevelRecyclerView, linearLayoutManager);
        multiLevelRecyclerView.setAdapter(adapter);
        multiLevelRecyclerView.setLayoutManager(linearLayoutManager);
        multiLevelRecyclerView.setAccordion(true);
        multiLevelRecyclerView.removeItemClickListeners();
        addSwipeDecorator();
    }

    // Call to API using GoogleCivicAPI
    public void getRepresentatives(String address) {
        GoogleCivicClient googleCivicClient = new GoogleCivicClient();
        googleCivicClient.getRepresentatives(address, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                ((MainActivity) context).hideProgressBar();
                Log.i(TAG, "onSuccess: retreived reps ");
                try {
                    // Transform json into list of Representative objects
                    Item repLabel = new Item(0, "Call your representatives!");
                    repLabel.addChildren(Representative.fromJSONArray(json.jsonObject));
                    items.add(repLabel);
                    // Notify adapter + expand
                    adapter.notifyDataSetChanged();
                    multiLevelRecyclerView.openTill(0, 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                ((MainActivity) context).hideProgressBar();
                Log.e(TAG, "onFailure: failed to retreive reps: " + response, throwable);
                Snackbar.make(binding.getRoot(), "Could not get representatives", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    // Parse query for user's toDOItems
    private void populateToDo() {

        // If possible, we get them 'fresh'
        ((MainActivity) context).showProgressBar();
        User.getToDo(new FindCallback<ToDoItem>() {
            @Override
            public void done(List<ToDoItem> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get ToDo's from Parse Server");// Get cached to do's in this case
                    getCachedToDos();
                    return;
                }
                // Since success, let's clear the cached ToDOItems
                items.clear();
                items.add(new Item(0, "To Do:")); // Re-add label

                for (int i = 0; i < objects.size(); i++) {
                    // Here, we check to see if the election has passed to see
                    // if the todoitem is still valid
                    addItemIfElectionHasNotPassed(i, objects.get(i));
                }
                // Caching
                ParseObject.pinAllInBackground(ToDoItem.class.getSimpleName(), objects);
                // We save the user
                User.saveUser("Could not move item to past Elections", "Moved Item to past elections");

                // Now we get the location + the reps
                ToDoFragmentPermissionsDispatcher.getLocationWithPermissionCheck(ToDoFragment.this);
            }
        });
    }

    private void getCachedToDos() {
        Snackbar.make(binding.getRoot(), "No internet connection", Snackbar.LENGTH_SHORT).show();
        ParseQuery<ToDoItem> query = new ParseQuery<>("ToDoItem");
        query.fromPin(ToDoItem.class.getSimpleName());
        query.findInBackground(new FindCallback<ToDoItem>() {
            @Override
            public void done(List<ToDoItem> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get cached todos");
                    return;
                }
                Log.i(TAG, "Got cached todos");
                // Adding to RV by wrapping it in Item object
                for (ToDoItem todo : objects)
                    addToDoToRecyclerView(todo);
                multiLevelRecyclerView.openTill(0, 1);
                ((MainActivity) context).hideProgressBar();
            }
        });
    }


    private void addItemIfElectionHasNotPassed(int i, final ToDoItem item) {
        final Election election = (Election) item.get("election");
        ParseQuery<Election> query = new ParseQuery<Election>("Election");
        query.whereEqualTo("objectId", election.getObjectId());
        query.include(Election.KEY_ELECTION_DATE);
        query.findInBackground(new FindCallback<Election>() {
            @Override
            public void done(List<Election> objects, ParseException e) {
                if (e != null)
                    Log.e(TAG, "Could not get election", e);
                else {
                    if (!objects.isEmpty()) {
                        Election result = objects.get(0);
                        if (hasElectionPassed(result))
                            // Delete the item, add it to past election, update election
                            updateElectionAndToDoItem(item, result);
                        else {
                            // Otherwise, still valid todoItem
                            addToDoToRecyclerView(item);
                        }
                    }

                }
            }
        });
    }

    private void addToDoToRecyclerView(ToDoItem item) {
        Item itemLabel = items.get(0);
        Item newToDo = new Item(1, item);
        itemLabel.addChild(newToDo);
        adapter.notifyDataSetChanged();
    }

    private void updateElectionAndToDoItem(ToDoItem item, Election election) {
        // Update election item
        if (!election.getHasPassed())
            election.setElectionHasPassed();
        // Add to user's past elections if user voted
        if (item.hasVoted())
            User.addToPastElections(election);
        // Remove item database
        item.deleteInBackground();
    }

    private boolean hasElectionPassed(Election election) {
        String d = election.getElectionDate();
        try {
            Date electionDate = new SimpleDateFormat("yyyy-MM-dd").parse(d);
            Date today = new Date();
            return electionDate.before(today);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Photo is on disk, we now publish it
                publishToFacebook();
            } else { // Result was a failure
                Snackbar.make(binding.getRoot(), "Picture wasn't taken!", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void publishToFacebook() {
        File file = getPhotoFileUri(photoFileName);
        Bitmap takenImage = BitmapFactory.decodeFile(file.getAbsolutePath());
        SharePhotoContent content = convertBitmapIntoFacebookContent(takenImage);
        openShareDialog(content);
    }

    private void openShareDialog(SharePhotoContent content) {
        ShareDialog shareDialog = new ShareDialog(this);
        if (!shareDialog.canShow(content)) {
            Log.e(TAG, "Cannot share to Facebook");
            Snackbar.make(binding.getRoot(), "Please install Facebook", Snackbar.LENGTH_SHORT).show();
        } else
            shareDialog.show(this, content);
    }

    private SharePhotoContent convertBitmapIntoFacebookContent(Bitmap takenImage) {
        // SharePhoto is Facebook's data model for photos
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(takenImage)
                .setCaption("I Voted!")
                .build();
        // Add the photo to the post's content
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        // Triggers intent to the Facebook app
        return content;
    }

    // Get safe storage directory for photos
    // Use `getExternalFilesDir` on Context to access package-specific directories.
    // This way, we don't need to request external read/write runtime permissions.
    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    // Annotations from dependency. Includes fine + coarse location
    @SuppressWarnings({"MissingPermission"})
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void getLocation() {
        // Google API to get location
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(context);
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            Log.e(TAG, "Null location");
                            Snackbar.make(binding.getRoot(), "Could not load representative", Snackbar.LENGTH_SHORT).show();
                        }

                        Log.i(TAG, "Location is " + location.toString());
                        // Getting address from Location Object to get reps
                        Address address = ElectionFragment.getAddressFromLocation(location, context);
                        if (address == null)
                            Snackbar.make(binding.getRoot(), "Could not load representative", Snackbar.LENGTH_SHORT).show();
                        else getRepresentatives(address.getAddressLine(0));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error trying to get last GPS location");
                        Snackbar.make(binding.getRoot(), "Error trying to get location", Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void addSwipeDecorator() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.i(TAG, String.valueOf(direction));
                int position = viewHolder.getAdapterPosition();
                // if not a valid position then we stop
                if (position == RecyclerView.NO_POSITION) return;
                // else we continue
                Item item = items.get(position);

                switch (item.getType()) {
                    case Item.TODO:
                        removeToDo(position, item);
                        break;
                    case Item.REP:
                        callRepresentative(item.getRepresentative(), viewHolder.getAdapterPosition());
                        break;
                    default:
                        return;
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                int position = viewHolder.getAdapterPosition();
                // if not a valid position then we stop
                if (position == RecyclerView.NO_POSITION) return;
                // else we continue
                Item item = items.get(position);
                int color = 0;
                int icon = 0;
                String label = "";
                switch (item.getType()) {
                    case Item.TODO:
                        color = R.color.quantum_googred;
                        icon = R.drawable.delete;
                        label = "Delete";
                        break;
                    case Item.REP:
                        color = R.color.quantum_googgreen;
                        icon = R.drawable.call;
                        label = "Call";
                        break;
                    default:
                        return;
                }

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(context, color))
                        .addSwipeLeftActionIcon(icon)
                        .addSwipeLeftLabel(label)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.rvItems);
    }

    private void callRepresentative(Representative representative, int position) {
        String phoneNumber = representative.getPhoneNumber();
        if (phoneNumber == null)
            Snackbar.make(binding.getRoot(), "No phone number", Snackbar.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                ToDoFragment.this.startActivityForResult(intent, CALL_ACTIVITY_REQUEST_CODE);
            }
            // to reset swipe state
            adapter.notifyItemChanged(position);

        }

    }

    private void removeToDo(int position, Item toDoItem) {
        // get label item for todoitems and delete child
        Item label = items.get(0);
        label.getChildren().remove(toDoItem);
        // Delete from adapter
        items.remove(position);
        adapter.notifyItemRemoved(position);

        // Unstar election
        User.unstarElection(toDoItem.getToDoItem().getElection());
    }


}