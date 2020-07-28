package com.example.voteboat.fragments;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
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

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public String photoFileName = "photo.jpg";


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
        ToDoFragmentPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    private void configureRecyclerView() {
        multiLevelRecyclerView = binding.rvItems;
        adapter = new ToDoAdapter(getContext(), items,this, multiLevelRecyclerView);
        multiLevelRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        linearLayoutManager.getStackFromEnd();
        multiLevelRecyclerView.setLayoutManager(linearLayoutManager);
        multiLevelRecyclerView.setAccordion(true);
        multiLevelRecyclerView.removeItemClickListeners();
    }

    // Call to API using GoogleCivicAPI
    public void getRepresentatives(String address) {
        GoogleCivicClient googleCivicClient = new GoogleCivicClient();
        googleCivicClient.getRepresentatives(address, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                ((MainActivity)getContext()).hideProgressBar();
                Log.i(TAG, "onSuccess: retreived reps ");
                try {
                    // Transform json into list of Representative objects
                    Item repLabel = new Item(0, "Call your representatives!");
                    repLabel.addChildren(Representative.fromJSONArray(json.jsonObject));
                    items.add(repLabel);
                    // Notify adapter + expand
                    adapter.notifyDataSetChanged();
                    multiLevelRecyclerView.openTill(0,1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                ((MainActivity)getContext()).hideProgressBar();
                Log.e(TAG, "onFailure: failed to retreive reps: " + response, throwable);
                Toast.makeText(getContext(), "Could not get representatives", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Parse query for user's toDOItems
    private void populateToDo() {

        // If possible, we get them 'fresh'
        ((MainActivity)getContext()).showProgressBar();
        User.getToDo(new FindCallback<ToDoItem>() {
            @Override
            public void done(List<ToDoItem> objects, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Could not get ToDo's from Parse Server");// Get cached to do's in this case
                    getCachedToDos();
                    return;
                }
                // Since success, let's clear the cached ToDOItems
                items.clear();
                ParseObject.unpinAllInBackground(ToDoItem.class.getSimpleName());
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
        Toast.makeText(getContext(), "Could not connect to server", Toast.LENGTH_SHORT).show();
        ParseQuery<ToDoItem> query = new ParseQuery<>("ToDoItem");
        query.fromPin(ToDoItem.class.getSimpleName());
        query.findInBackground(new FindCallback<ToDoItem>() {
            @Override
            public void done(List<ToDoItem> objects, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Could not get cached todos");
                    return;
                }
                Log.i(TAG, "Got cached todos");
                // Adding to RV by wrapping it in Item object
                for(ToDoItem todo : objects)
                    addToDoToRecyclerView(todo);
                multiLevelRecyclerView.openTill(0,1);
                ((MainActivity)getContext()).hideProgressBar();
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
                if(e != null)
                    Log.e(TAG, "Could not get election", e);
                else{
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
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
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
        if(!shareDialog.canShow(content)) {
            Log.e(TAG, "Cannot share to Facebook");
            Toast.makeText(getContext(), "Please install Facebook", Toast.LENGTH_SHORT).show();
        }
        else
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
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
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
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(getContext());
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Toast.makeText(getContext(), "Got location", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "Location is " + location.toString());
                        // Getting address from Location Object to get reps
                        Address address = ElectionFragment.getAddressFromLocation(location, getContext());
                        if(address == null) Toast.makeText(getContext(), "Could not load representative", Toast.LENGTH_SHORT).show();
                        else getRepresentatives(address.getAddressLine(0));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error trying to get last GPS location");
                        Toast.makeText(getContext(), "No location", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });
    }

}