package com.example.voteboat.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voteboat.databinding.ItemLabelBinding;
import com.example.voteboat.databinding.ItemRepresentativeBinding;
import com.example.voteboat.databinding.ItemTodoBinding;
import com.example.voteboat.fragments.ToDoFragment;
import com.example.voteboat.models.Item;
import com.example.voteboat.models.Representative;
import com.example.voteboat.models.ToDoItem;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.multilevelview.MultiLevelAdapter;
import com.multilevelview.MultiLevelRecyclerView;

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

    @Override
    public int getItemViewType(int position) {
        return toDoItems.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case Item.LABEL:
                return new LabelHolder(ItemLabelBinding.inflate(LayoutInflater.from(context)));
            case Item.TODO:
                return new ToDoHolder(ItemTodoBinding.inflate(LayoutInflater.from(context)));
            default:
                return new RepHolder(ItemRepresentativeBinding.inflate(LayoutInflater.from(context)));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        Item item = toDoItems.get(i);
        switch (item.getType()) {
            case Item.LABEL:
                ((LabelHolder) viewHolder).bind(item.getLabel());
                break;
            case Item.TODO:
                ((ToDoHolder) viewHolder).bind(item.getToDoItem());
                break;
            default:
                ((RepHolder) viewHolder).bind(item.getRepresentative());
        }

    }

    @Override
    public int getItemCount() {
        return toDoItems.size();
    }

    class LabelHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemLabelBinding binding;

        public LabelHolder(@NonNull ItemLabelBinding itemLabelBinding) {
            super(itemLabelBinding.getRoot());
            binding = itemLabelBinding;
            itemLabelBinding.getRoot().setOnClickListener(this);
        }

        public void bind(String label) {
            binding.tvLabel.setText(label);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "Clicked on item " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
            multiLevelRecyclerView.toggleItemsGroup(getAdapterPosition());
        }
    }

    class ToDoHolder extends RecyclerView.ViewHolder {

        ItemTodoBinding binding;

        public ToDoHolder(@NonNull ItemTodoBinding itemTodoBinding) {
            super(itemTodoBinding.getRoot());
            this.binding = itemTodoBinding;
        }

        public void bind(final ToDoItem item) {
            binding.tvElectionName.setText(item.getName());
            binding.btnRegister.likeButton.setLiked(item.isRegistered());
            binding.btnDocs.likeButton.setLiked(item.hasDocuments());
            binding.btnVote.likeButton.setLiked(item.hasVoted());

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
            binding.btnVote.likeButton.setOnLikeListener(new OnLikeListener() {
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
            binding.btnDocs.likeButton.setOnLikeListener(new OnLikeListener() {
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
            binding.btnRegister.likeButton.setOnLikeListener(new OnLikeListener() {
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

    private class RepHolder extends RecyclerView.ViewHolder {
        ItemRepresentativeBinding binding;

        public RepHolder(@NonNull ItemRepresentativeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Representative representative) {
            binding.tvRepName.setText(representative.getName());
            binding.tvParty.setText(representative.getParty());
            setText(binding.tvPhone, representative.getPhoneNumber());
            setText(binding.tvPhone, representative.getPhoneNumber());
            setText(binding.tvEmail, representative.getEmail());
            setText(binding.tvUrl, representative.getUrl());

        }

        // In case Representative fields aren't available, check if null
        // and set appropriate text + visibility
        private void setText(TextView textView, String text) {
            if (text == null)
                textView.setVisibility(View.GONE);
            else {
                textView.setText(text);
                textView.setVisibility(View.VISIBLE);
            }
        }
    }
}
