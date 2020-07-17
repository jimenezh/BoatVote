package com.example.voteboat.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.voteboat.adapters.ToDoAdapter;
import com.example.voteboat.databinding.FragmentToDoBinding;
import com.example.voteboat.models.Election;
import com.example.voteboat.models.ToDoItem;
import com.example.voteboat.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ToDoFragment extends Fragment {
    public static final String TAG ="ToDoFragment";
    FragmentToDoBinding binding;
    List<ToDoItem> toDoItems;
    ToDoAdapter toDoAdapter;

    public ToDoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentToDoBinding.inflate(inflater);

        toDoItems = new ArrayList<>();

        toDoAdapter= new ToDoAdapter(getContext(), toDoItems);

        binding.rvToDoList.setAdapter(toDoAdapter);
        binding.rvToDoList.setLayoutManager(new LinearLayoutManager(getContext()));


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

        return binding.getRoot();
    }
}