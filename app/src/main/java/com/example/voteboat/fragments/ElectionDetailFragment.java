package com.example.voteboat.fragments;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.voteboat.R;
import com.example.voteboat.activities.MapActivity;
import com.example.voteboat.activities.RaceDetailActivity;
import com.example.voteboat.databinding.FragmentDetailElectionBinding;
import com.example.voteboat.models.Election;
import com.example.voteboat.models.Poll;
import com.example.voteboat.models.Race;
import com.example.voteboat.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseRelation;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.List;

public class ElectionDetailFragment extends Fragment {

    public static final String TAG = "ElectionDetailFragment";

    Election election;
    String userOcdId;
    Poll poll;
    FragmentDetailElectionBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailElectionBinding.inflate(getLayoutInflater());
        // Getting races + election from args
        Bundle args = getArguments();
        election = Parcels.unwrap(args.getParcelable(Election.class.getSimpleName()));
        userOcdId = args.getString("userOcdId");
        poll = Parcels.unwrap(args.getParcelable(Poll.class.getSimpleName()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setElectionInformation();
        setPollInformation();
        setRaces();
        binding.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchMapActivity();
            }
        });

    }

    private void setRaces() {
        if (!election.hasRaces()) binding.btnRaces.setVisibility(View.GONE);
        else {
            binding.btnRaces.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchRacesActivity();
                }
            });
        }
    }

    private void setPollInformation() {
        if (poll == null) {
            binding.llDates.setVisibility(View.GONE);
            binding.tvPollTitle.setText("No polls nears you");
        } else {
            binding.tvAddress.setText(poll.getLocation());
            binding.tvDateOpen.setText(poll.getOpenDate());
            binding.tvTimesOpen.setText(poll.getPollingHours());
        }
    }


    private void setElectionInformation() {
        if (!election.getOcdId().equals(userOcdId)) {
            binding.llLinks.setVisibility(View.GONE);
        } else {
            binding.tvAbsentee.setText(election.getAbsenteeBallotUrl());
            binding.tvElectionInfo.setText(election.getElectionInfoUrl());
            binding.tvRegister.setText(election.getRegistrationUrl());
            binding.tvRules.setText(election.getElectionRulesUrl());
        }

        binding.tvElectionName.setText(election.getTitle());
        binding.tvDate.setText(election.getElectionDate());

    }

    private void launchMapActivity() {
        Intent intent = new Intent(getContext(), MapActivity.class);
        String addressLine = poll.getLocation();
        try {
            putLatAndLngInIntent(intent, addressLine);
            startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Could not show map", Toast.LENGTH_SHORT).show();
        }
    }

    private void putLatAndLngInIntent(Intent intent, String addressLine) throws IOException {
        Geocoder geocoder = new Geocoder(getContext());
        Address address = geocoder.getFromLocationName(addressLine, 1).get(0);
        intent.putExtra("address", Parcels.wrap(address));
        intent.putExtra("hours", election.getElectionDayPoll().getPollingHours());

    }

    private void launchRacesActivity() {
        ParseRelation<Race> relation = election.getRaces();
        relation.getQuery()
                .include("candidates")
                .orderByDescending("score")
                .findInBackground(new FindCallback<Race>() {
                    @Override
                    public void done(List<Race> objects, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Could not get races", e);
                            return;
                        }
                        Intent intent = new Intent(getContext(), RaceDetailActivity.class);
                        intent.putExtra(Race.class.getSimpleName(), Parcels.wrap(objects));
                        startActivity(intent);
                    }
                });

    }


}
