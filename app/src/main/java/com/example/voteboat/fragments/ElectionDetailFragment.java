package com.example.voteboat.fragments;

import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.voteboat.adapters.RaceAdapter;
import com.example.voteboat.clients.GoogleCivicClient;
import com.example.voteboat.databinding.FragmentDetailElectionBinding;
import com.example.voteboat.databinding.FragmentElectionBinding;
import com.example.voteboat.models.Election;
import com.example.voteboat.models.Poll;
import com.example.voteboat.models.Race;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ElectionDetailFragment extends Fragment {

    public static final String TAG ="RaceFragment";

    RaceAdapter adapter;
    List<Race> races;
    Election election;
    FragmentDetailElectionBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailElectionBinding.inflate(getLayoutInflater());
        // Getting races + election from args
        Bundle args = getArguments();
        election = Parcels.unwrap( args.getParcelable(Election.class.getSimpleName()));
        races = election.getRaces();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Setting adapter
        adapter = new RaceAdapter(getContext(), races);
//        binding.rvRaces.setAdapter(adapter);
//        binding.rvRaces.setLayoutManager(new LinearLayoutManager(getContext()));
//        binding.rvRaces.setNestedScrollingEnabled(false);
        binding.tvElectionName.setText(election.getTitle());
        binding.tvDate.setText(election.getElectionDate());
//        binding.tvPoll.setText(election.getElectionDayPolls().get(0).getLocation());
        List<Poll> pollLocations = election.getElectionDayPolls();
        for(Poll pollLocation : pollLocations){
            TextView textView = new TextView(getContext());
            textView.setText(pollLocation.getLocation());
            binding.llPoll.addView(textView);
        }
        
        binding.btnRaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Add intent (?) or fragment
                Toast.makeText(getContext(), "Clicked on Races Button", Toast.LENGTH_SHORT).show();
            }
        });

    }





}
