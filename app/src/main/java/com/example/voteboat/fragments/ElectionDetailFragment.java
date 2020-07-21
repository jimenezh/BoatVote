package com.example.voteboat.fragments;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
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
import com.example.voteboat.adapters.RaceAdapter;
import com.example.voteboat.databinding.FragmentDetailElectionBinding;
import com.example.voteboat.models.Election;
import com.example.voteboat.models.Poll;
import com.example.voteboat.models.Race;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.List;

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

        binding.tvElectionName.setText(election.getTitle());
        binding.tvDate.setText(election.getElectionDate());
        binding.tvAbsentee.setText(election.getAbsenteeBallotUrl());
        binding.tvElectionInfo.setText(election.getElectionInfoUrl());
        binding.tvRegister.setText(election.getRegistrationUrl());

        // Making text views for poll locations
        addElectionDayPollViews(election.getElectionDayPolls(), binding.llPoll);

        binding.btnRaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Add intent (?) or fragment
                launchRacesActivity();
            }
        });

        binding.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

    }

    private void putLatAndLngInIntent(Intent intent, String addressLine) throws IOException {
        Geocoder geocoder = new Geocoder(getContext());
        Address address = geocoder.getFromLocationName(addressLine,1).get(0);
        double lat = address.getLatitude();
        double lng = address.getLongitude();
        intent.putExtra("lat",lat);
        intent.putExtra("lng", lng);
    }

    private void launchRacesActivity() {
        Intent intent = new Intent(getContext(), RaceDetailActivity.class);
        intent.putExtra(Race.class.getSimpleName(), Parcels.wrap(election.getRaces()));
        startActivity(intent);
    }

    private void addElectionDayPollViews(List<Poll> pollLocations, LinearLayout llPoll) {
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
