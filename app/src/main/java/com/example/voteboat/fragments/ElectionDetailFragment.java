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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.voteboat.R;
import com.example.voteboat.activities.MapActivity;
import com.example.voteboat.activities.RaceDetailActivity;
import com.example.voteboat.databinding.FragmentDetailElectionBinding;
import com.example.voteboat.databinding.ItemPollBinding;
import com.example.voteboat.models.Election;
import com.example.voteboat.models.Poll;
import com.example.voteboat.models.Race;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseRelation;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.example.voteboat.models.Poll.KEY_DROP_OFF;
import static com.example.voteboat.models.Poll.KEY_EARLY_VOTE;
import static com.example.voteboat.models.Poll.KEY_POLL;

public class ElectionDetailFragment extends Fragment {

    public static final String TAG = "ElectionDetailFragment";

    Election election;
    String userOcdId;
    Poll electionDayPoll;
    Poll absenteeDropOffLocation;
    Poll earlyVotingPoll;
    FragmentDetailElectionBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailElectionBinding.inflate(getLayoutInflater());
        // Getting races + election from args
        Bundle args = getArguments();
        election = Parcels.unwrap(args.getParcelable(Election.class.getSimpleName()));
        userOcdId = args.getString("userOcdId");

        // Polling locations
        HashMap<String, Poll> polls = Parcels.unwrap(args.getParcelable(Poll.class.getSimpleName()));
        electionDayPoll = polls.get(KEY_POLL);
        absenteeDropOffLocation = polls.get(KEY_DROP_OFF);
        earlyVotingPoll = polls.get(KEY_EARLY_VOTE);
        setPolls();


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setElectionInformation();
        // For election day poll, absentee locations, early voting
        setPolls();
        setRaces();

        // Back button
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    private void setPolls() {
        setPoll(electionDayPoll, binding.tvPollTitle, binding.electionDayPoll, getString(R.string.empty_poll));
        setPoll(earlyVotingPoll, binding.tvEarlyPollTitle, binding.earlyPoll, getString(R.string.empty_early_vote_site));
        setPoll(absenteeDropOffLocation, binding.tvAbsenteeTitle, binding.absenteePoll, getString(R.string.empty_drop_off_site));
    }

    private void setPoll(final Poll poll, TextView title, ItemPollBinding itemPollBinding, String isEmpty) {
        if (poll == null) {
            itemPollBinding.llDates.setVisibility(View.GONE);
            title.setText(isEmpty);
        } else {
            itemPollBinding.tvAddress.setText(poll.getLocation());
            itemPollBinding.tvDateOpen.setText(poll.getOpenDate());
            itemPollBinding.tvTimesOpen.setText(poll.getPollingHours());
            itemPollBinding.btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchMapActivity(poll);
                }
            });
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

    private void launchMapActivity(Poll poll) {
        Intent intent = new Intent(getContext(), MapActivity.class);
        String addressLine = poll.getLocation();
        try {
            putLatAndLngInIntent(intent, addressLine);
            startActivity(intent);
        } catch (IOException e) {
            Log.e(TAG, "launchMapActivity", e);
            Snackbar.make(binding.getRoot(), "Could not show map", Snackbar.LENGTH_SHORT).show();;
        }
    }

    private void putLatAndLngInIntent(Intent intent, String addressLine) throws IOException {
        Geocoder geocoder = new Geocoder(getContext());
        Address address = geocoder.getFromLocationName(addressLine, 1).get(0);
        intent.putExtra("address", Parcels.wrap(address));
        intent.putExtra("hours", electionDayPoll.getPollingHours());

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
