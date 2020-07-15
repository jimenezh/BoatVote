package com.example.voteboat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.voteboat.R;
import com.example.voteboat.activities.RaceDetailActivity;
import com.example.voteboat.adapters.RaceAdapter;
import com.example.voteboat.databinding.FragmentDetailElectionBinding;
import com.example.voteboat.models.Election;
import com.example.voteboat.models.Poll;
import com.example.voteboat.models.Race;

import org.parceler.Parcels;

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
        binding.tvRules.setText(election.getElectionRulesUrl());
        // Making text views for poll locations
        addElectionDayPollViews();

        binding.btnRaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Add intent (?) or fragment
                Intent intent = new Intent(getContext(), RaceDetailActivity.class);
                intent.putExtra(Race.class.getSimpleName(), Parcels.wrap(election.getRaces()));
                startActivity(intent);
            }
        });

    }

    private void addElectionDayPollViews() {
        List<Poll> pollLocations = election.getElectionDayPolls();
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
            binding.llPoll.addView(v);
        }
    }


}
