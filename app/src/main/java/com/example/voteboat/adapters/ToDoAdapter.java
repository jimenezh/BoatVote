package com.example.voteboat.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voteboat.activities.MainActivity;
import com.example.voteboat.databinding.ItemTodoBinding;
import com.example.voteboat.fragments.PictureFragment;
import com.example.voteboat.fragments.ToDoFragment;
import com.example.voteboat.models.Item;
import com.example.voteboat.models.ToDoItem;
import com.facebook.FacebookSdk;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.multilevelview.MultiLevelAdapter;
import com.multilevelview.MultiLevelRecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

public class ToDoAdapter extends MultiLevelAdapter {


    private Context context;
    private List<Item> toDoItems;
    private ToDoFragment fragment;
    private MultiLevelRecyclerView multiLevelRecyclerView;

    public static final String TAG = "ToDoAdapter";

    // For camera
    private File photoFile;
    public String photoFileName = "photo.jpg";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;


    public ToDoAdapter(Context context, List<Item> elections, ToDoFragment fragment, MultiLevelRecyclerView multiLevelRecyclerView) {
        super(elections);
        this.context = context;
        this.toDoItems = elections;
        this.fragment = fragment;
        this.multiLevelRecyclerView = multiLevelRecyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTodoBinding binding = ItemTodoBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.bind(toDoItems.get(i).getToDoItem());
    }

    @Override
    public int getItemCount() {
        return toDoItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemTodoBinding binding;

        public ViewHolder(@NonNull ItemTodoBinding itemTodoBinding) {
            super(itemTodoBinding.getRoot());
            this.binding = itemTodoBinding;
        }

        public void bind(final ToDoItem item) {
            binding.tvElectionName.setText(item.getName());
            binding.btnRegister.starButton.setLiked(item.isRegistered());
            binding.btnDocs.starButton.setLiked(item.hasDocuments());
            binding.btnVote.starButton.setLiked(item.hasVoted());

            setRegisteredListener(item);
            setDocumentsListener(item);
            setVoteListener(item);
            // Camera listener
            binding.btnCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchCamera();
                }
            });
        }

        private void setVoteListener(final ToDoItem item) {
            binding.btnVote.starButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    // add to list
                    if (!item.hasVoted())
                        item.setVoted(true);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    // remove from list if registered
                    if (item.hasVoted())
                        item.setVoted(false);
                }
            });
        }

        private void setDocumentsListener(final ToDoItem item) {
            binding.btnDocs.starButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    // add to list
                    if (!item.hasDocuments())
                        item.setDocuments(true);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    // remove from list if registered
                    if (item.isRegistered())
                        item.setDocuments(false);
                }
            });
        }

        private void setRegisteredListener(final ToDoItem item) {
            binding.btnRegister.starButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    // add to list
                    if (!item.isRegistered())
                        item.setRegistered(true);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    // remove from list if registered
                    if (item.isRegistered())
                        item.setRegistered(false);
                }
            });
        }

        private void launchCamera() {
            // create Intent to take a picture and return control to the calling application
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Create a File reference for future access
            photoFile = fragment.getPhotoFileUri(photoFileName);

            // wrap File object into a content provider
            // required for API >= 24
            // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
            Uri fileProvider = FileProvider.getUriForFile(context, "com.voteboat.fileprovider", photoFile);
            Log.i(TAG, fileProvider.toString());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                // Start the image capture intent to take photo
                // Make sure to start from the fragment, so the activity result shows up on fragment
                fragment.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }

    }
}
