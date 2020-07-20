package com.example.voteboat.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.voteboat.adapters.RepresentativesAdapter;
import com.example.voteboat.adapters.ToDoAdapter;
import com.example.voteboat.clients.GoogleCivicClient;
import com.example.voteboat.databinding.FragmentToDoBinding;
import com.example.voteboat.models.Representative;
import com.example.voteboat.models.ToDoItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ToDoFragment extends Fragment {
    public static final String TAG ="ToDoFragment";
    FragmentToDoBinding binding;

    List<ToDoItem> toDoItems;
    ToDoAdapter toDoAdapter;

    List<Representative> representatives;
    RepresentativesAdapter representativesAdapter;

    public ToDoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentToDoBinding.inflate(inflater);

        // Setting up To Do tab
        toDoItems = new ArrayList<>();
        toDoAdapter= new ToDoAdapter(getContext(), toDoItems);
        binding.rvToDoList.setAdapter(toDoAdapter);
        binding.rvToDoList.setLayoutManager(new LinearLayoutManager(getContext()));
        getToDoItems();
        // Setting up representatives tab
        representatives = new ArrayList<>();
        representativesAdapter = new RepresentativesAdapter(getContext(), representatives);
        binding.rvRepresentatives.setAdapter(representativesAdapter);
        LinearLayoutManager representativeLinearLayout = new LinearLayoutManager(getContext());
        representativeLinearLayout.setReverseLayout(true); // reverse so most local reps show up
        binding.rvRepresentatives.setLayoutManager(representativeLinearLayout);
        getRepresentatives();

        return binding.getRoot();
    }

    // Call to API using GoogleCivicAPI
    private void getRepresentatives() {
        GoogleCivicClient googleCivicClient = new GoogleCivicClient();
        googleCivicClient.getRepresentatives(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess: retreived reps ");
                try {
                    // Transform json into list of Representative objects
                    representatives.addAll( Representative.fromJSONArray(json.jsonObject));
                    // Notify adaptrs
                    representativesAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure: failed to retreive reps: "+response, throwable);
            }
        });
    }

    // Parse query for user's toDOItems
    private void getToDoItems() {
        ParseQuery<ToDoItem> query = new ParseQuery<>("ToDoItem");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ToDoItem>() {
            @Override
            public void done(List<ToDoItem> objects, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Query failed");
                    return;
                }
                for(ToDoItem election: objects)
                    Log.i(TAG,election.toString());
                toDoItems.addAll(objects);
                toDoAdapter.notifyDataSetChanged();

            }
        });
    }
}