package com.example.voteboat.adapters;

import android.content.Context;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.voteboat.activities.MainActivity;
import com.example.voteboat.clients.GoogleCivicClient;
import com.example.voteboat.databinding.ItemElectionBinding;
import com.example.voteboat.fragments.ElectionDetailFragment;
import com.example.voteboat.models.Election;
import com.example.voteboat.models.User;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;

public class ElectionAdapter extends RecyclerView.Adapter<ElectionAdapter.ViewHolder> {

    public static final String TAG = "ElectionFeedAdapter";

    private Context context;
    private List<Election> elections;
    private List<Election> starredElections;
    public Address address;

    public ElectionAdapter(Context context, List<Election> elections, List<Election> starredElections) {
        this.context = context;
        this.elections = elections;
        this.starredElections = starredElections;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemElectionBinding binding = ItemElectionBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(elections.get(position));
    }

    @Override
    public int getItemCount() {
        return elections.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemElectionBinding binding;

        public ViewHolder(@NonNull ItemElectionBinding itemElectionBinding) {
            super(itemElectionBinding.getRoot());
            this.binding = itemElectionBinding;
            itemElectionBinding.getRoot().setOnClickListener(this);
        }

        public void bind(final Election election) {
            binding.tvTitle.setText(election.getTitle());
            binding.tvDate.setText(election.getElectionDate());
            binding.btnStar.getRoot();
            binding.btnStar.starButton.setLiked(starredElections.contains(election));
            setOnStarListener(election);
        }

        private void setOnStarListener(final Election election) {
            binding.btnStar.starButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    // only update if election is originally unstarred
                    if (!starredElections.contains(election)) {
                        User.starElection(election);
                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    // only update if election is originally starred
                    if (starredElections.contains(election)) {
                        User.unstarElection(election);
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            // Get correct election, then make query for election details
            Election election = elections.get(getAdapterPosition());
            if (address != null)
                getElectionDetails(election, address);
            else
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();

        }
    }


    // API request for more information on the election
    private void getElectionDetails(final Election election, Address address) {
        Log.i(TAG, "Election id is " + election.getGoogleId());

        GoogleCivicClient googleCivicClient = new GoogleCivicClient();
        googleCivicClient
                .voterInformationElections(election.getGoogleId(), address.getAddressLine(0), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "Got races " + json.toString());
                        try {
                            election.addDetails(json.jsonObject, new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        Log.e(TAG, "Could not add election details", e);
                                        return;
                                    }
                                    displayElectionDetail(election);
                                }
                            });
                        } catch (JSONException jsonExceptions){
                            jsonExceptions.printStackTrace();
                        }


                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "Could not get races " + response, throwable);
                    }
                });
    }


    private void displayElectionDetail(Election e) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Election.class.getSimpleName(), Parcels.wrap(e));
        bundle.putString("userOcdId", getOcdId(address));
        ElectionDetailFragment electionDetailFragment = new ElectionDetailFragment();
        electionDetailFragment.setArguments(bundle);

        MainActivity mainActivity = (MainActivity) context;
        mainActivity.changeFragment(electionDetailFragment);
    }

    private String getOcdId(Address address) {
        return "ocd-division/country:us/state:" + stateAbbreviation(address.getAdminArea());
    }

    // Clean all elements of the recycler
    public void clear() {
        elections.clear();
        starredElections.clear();
        notifyDataSetChanged();
    }

    public String stateAbbreviation(String stateName) {
        Map<String, String> states = new HashMap<String, String>();
        states.put("Alabama", "AL");
        states.put("Alaska", "AK");
        states.put("Alberta", "AB");
        states.put("American Samoa", "AS");
        states.put("Arizona", "AZ");
        states.put("Arkansas", "AR");
        states.put("Armed Forces (AE)", "AE");
        states.put("Armed Forces Americas", "AA");
        states.put("Armed Forces Pacific", "AP");
        states.put("British Columbia", "BC");
        states.put("California", "CA");
        states.put("Colorado", "CO");
        states.put("Connecticut", "CT");
        states.put("Delaware", "DE");
        states.put("District Of Columbia", "DC");
        states.put("Florida", "FL");
        states.put("Georgia", "GA");
        states.put("Guam", "GU");
        states.put("Hawaii", "HI");
        states.put("Idaho", "ID");
        states.put("Illinois", "IL");
        states.put("Indiana", "IN");
        states.put("Iowa", "IA");
        states.put("Kansas", "KS");
        states.put("Kentucky", "KY");
        states.put("Louisiana", "LA");
        states.put("Maine", "ME");
        states.put("Manitoba", "MB");
        states.put("Maryland", "MD");
        states.put("Massachusetts", "MA");
        states.put("Michigan", "MI");
        states.put("Minnesota", "MN");
        states.put("Mississippi", "MS");
        states.put("Missouri", "MO");
        states.put("Montana", "MT");
        states.put("Nebraska", "NE");
        states.put("Nevada", "NV");
        states.put("New Brunswick", "NB");
        states.put("New Hampshire", "NH");
        states.put("New Jersey", "NJ");
        states.put("New Mexico", "NM");
        states.put("New York", "NY");
        states.put("Newfoundland", "NF");
        states.put("North Carolina", "NC");
        states.put("North Dakota", "ND");
        states.put("Northwest Territories", "NT");
        states.put("Nova Scotia", "NS");
        states.put("Nunavut", "NU");
        states.put("Ohio", "OH");
        states.put("Oklahoma", "OK");
        states.put("Ontario", "ON");
        states.put("Oregon", "OR");
        states.put("Pennsylvania", "PA");
        states.put("Prince Edward Island", "PE");
        states.put("Puerto Rico", "PR");
        states.put("Quebec", "QC");
        states.put("Rhode Island", "RI");
        states.put("Saskatchewan", "SK");
        states.put("South Carolina", "SC");
        states.put("South Dakota", "SD");
        states.put("Tennessee", "TN");
        states.put("Texas", "TX");
        states.put("Utah", "UT");
        states.put("Vermont", "VT");
        states.put("Virgin Islands", "VI");
        states.put("Virginia", "VA");
        states.put("Washington", "WA");
        states.put("West Virginia", "WV");
        states.put("Wisconsin", "WI");
        states.put("Wyoming", "WY");
        states.put("Yukon Territory", "YT");
        return states.get(stateName).toLowerCase();
    }
}
