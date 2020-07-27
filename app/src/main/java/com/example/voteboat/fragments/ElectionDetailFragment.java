package com.example.voteboat.fragments;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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

import org.parceler.Parcels;

import java.io.IOException;
import java.util.List;

public class ElectionDetailFragment extends Fragment {

    public static final String TAG ="ElectionDetailFragment";

    Election election;
    String userOcdId;
    FragmentDetailElectionBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailElectionBinding.inflate(getLayoutInflater());
        // Getting races + election from args
        Bundle args = getArguments();
        election = Parcels.unwrap( args.getParcelable(Election.class.getSimpleName()));
        userOcdId = args.getString("userOcdId");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setElectionInformation();
        addElectionDayPollViews(election.getElectionDayPolls(), binding.llPoll);
        if(election.getRaces().isEmpty()) binding.btnRaces.setVisibility(View.GONE);
        if(!election.getOcdId().equals(userOcdId)) binding.llLinks.setVisibility(View.GONE);
        binding.btnRaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRacesActivity();
            }
        });
        binding.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchMapActivity();
            }
        });

    }

    private void setElectionInformation() {
        binding.tvElectionName.setText(election.getTitle());
        binding.tvDate.setText(election.getElectionDate());
        binding.tvAbsentee.setText(election.getAbsenteeBallotUrl());
        binding.tvElectionInfo.setText(election.getElectionInfoUrl());
        binding.tvRegister.setText(election.getRegistrationUrl());
        binding.tvRules.setText(election.getElectionRulesUrl());
    }

    private void launchMapActivity() {
        Intent intent= new Intent(getContext(), MapActivity.class);
        String addressLine = election.getElectionDayPolls().get(0).getLocation();
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
        Address address = geocoder.getFromLocationName(addressLine,1).get(0);
        intent.putExtra("address",Parcels.wrap(address));
        intent.putExtra("hours",election.getElectionDayPolls().get(0).getPollingHours() );

    }

    private void launchRacesActivity() {
        Intent intent = new Intent(getContext(), RaceDetailActivity.class);
        intent.putExtra(Race.class.getSimpleName(), Parcels.wrap(election.getRaces()));
        startActivity(intent);
    }

    private void addElectionDayPollViews(List<Poll> pollLocations, LinearLayout llPoll) {
        if (pollLocations.isEmpty()) {
            binding.btnMap.setVisibility(View.GONE);
            binding.tvPollTitle.setText("No polls near you");
        }
        for(Poll pollLocation : pollLocations){
            View v = getLayoutInflater().inflate(R.layout.item_poll_location,null,false);
            TextView title = v.findViewById(R.id.tvAddress);
            title.setText(pollLocation.getLocation());

            TextView time = v.findViewById(R.id.tvTimesOpen);
            time.setText(pollLocation.getPollingHours());

            if(pollLocation.hasDates()) {
                TextView openDate = v.findViewById(R.id.tvDateOpen);
                TextView closeDate = v.findViewById(R.id.tvDateClose);
                openDate.setText(pollLocation.getOpenDate());
                closeDate.setText(pollLocation.getCloseDate());
            }
             else
                v.findViewById(R.id.llDates).setVisibility(View.GONE);
            llPoll.addView(v);
        }
    }


}
