package com.example.voteboat.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.voteboat.adapters.RepresentativesAdapter;
import com.example.voteboat.adapters.ToDoAdapter;
import com.example.voteboat.clients.GoogleCivicClient;
import com.example.voteboat.databinding.FragmentToDoBinding;
import com.example.voteboat.models.Election;
import com.example.voteboat.models.Representative;
import com.example.voteboat.models.ToDoItem;
import com.example.voteboat.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Headers;

public class ToDoFragment extends Fragment {
    public static final String TAG = "ToDoFragment";
    FragmentToDoBinding binding;

    List<ToDoItem> toDoItems;
    ToDoAdapter toDoAdapter;
    String address;

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

        // Setting up To Do tab. Query immediately for to do items since they're in parse
        setToDoAdapter();
        getToDoItems();
        // Setting up representatives tab, we query for reps once we have the address
        setRepresentativesAdapter();
        return binding.getRoot();
    }

    private void setRepresentativesAdapter() {
        // NOTE: reps is initialized before creation of f
        representativesAdapter = new RepresentativesAdapter(getContext(), representatives);
        binding.rvRepresentatives.setAdapter(representativesAdapter);
        LinearLayoutManager representativeLinearLayout = new LinearLayoutManager(getContext());
        representativeLinearLayout.setReverseLayout(true); // reverse so most local reps show up
        binding.rvRepresentatives.setLayoutManager(representativeLinearLayout);
        binding.tvRepresentativesTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVisibility(binding.rvRepresentatives);
            }
        });
    }

    private void setToDoAdapter() {
        toDoItems = new ArrayList<>();
        toDoAdapter = new ToDoAdapter(getContext(), toDoItems);
        binding.rvToDoList.setAdapter(toDoAdapter);
        binding.rvToDoList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.tvToDoTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVisibility(binding.rvToDoList);
            }
        });
    }

    private void toggleVisibility(RecyclerView rv) {
        if (rv.getVisibility() == View.GONE)
            rv.setVisibility(View.VISIBLE);
        else
            rv.setVisibility(View.GONE);
    }

    // Call to API using GoogleCivicAPI
    public void getRepresentatives(String address) {
        representatives = new ArrayList<>();
        GoogleCivicClient googleCivicClient = new GoogleCivicClient();
        googleCivicClient.getRepresentatives(address, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess: retreived reps ");
                try {
                    // Transform json into list of Representative objects
                    representatives.addAll(Representative.fromJSONArray(json.jsonObject));
                    // Note: don't need to notify adapter, since it hasn't been created
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure: failed to retreive reps: " + response, throwable);
            }
        });
    }

    // Parse query for user's toDOItems
    private void getToDoItems() {
        User.getToDo(new FindCallback<ToDoItem>() {
            @Override
            public void done(List<ToDoItem> objects, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Could not get ToDo's");
                    return;
                }
                for (int i = 0; i < objects.size(); i++) {
                    // Here, we check to see if the election has passed to see
                    // if the todoitem is still valid
                    addItemIfElectionHasNotPassed(i, objects.get(i));
                }
                // We save the user
                User.saveUser("Could not move item to past Elections", "Moved Item to past elections");
                // Notify the adapter that we now have all the valid elections
                toDoAdapter.notifyDataSetChanged();
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
                    else
                        // Otherwise, still valid todoItem
                        toDoItems.add(item);
                }
            }
        });
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
}