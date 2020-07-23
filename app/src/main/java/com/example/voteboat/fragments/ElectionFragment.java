package com.example.voteboat.fragments;

import android.Manifest;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.voteboat.activities.MainActivity;
import com.example.voteboat.adapters.ElectionAdapter;
import com.example.voteboat.clients.GoogleCivicClient;
import com.example.voteboat.databinding.FragmentElectionBinding;
import com.example.voteboat.models.Election;
import com.example.voteboat.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions // using https://github.com/permissions-dispatcher
public class ElectionFragment extends Fragment {

    public static final String TAG = "ElectionFragment";

    FragmentElectionBinding binding;

    public static final String DUMMY_STATE = "mi";
    ElectionAdapter adapter;
    List<Election> elections;
    List<Election> starredElections;
    public FusedLocationProviderClient fusedLocationProviderClient;
    private final int MAX_LOCATION_RESULTS = 5;
    Address address;

    // Interface to access listener on
    public interface ElectionListener {
        void changeFragment(Object object, Fragment fragment, String type);
        void setUserAddress(Address address);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentElectionBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // Delegates work to helper class
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ElectionFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inititalizing empty lists
        elections = new ArrayList<>();
        starredElections = new ArrayList<>();
        // Setting the election adapter
        adapter = new ElectionAdapter(getContext(), elections, starredElections);
        binding.rvElections.setAdapter(adapter);
        binding.rvElections.setLayoutManager(new LinearLayoutManager(getContext()));
        // Populate the election adapter
        populateElectionFeed();
    }

    private void populateElectionFeed() {
        // Check if the user wants to use a custom address
        if (User.useCustomAddress()) {
            // Now we get the address from parse and transform that into an Address Object
            String parseAddress = User.getCurrentAddress();
            try {
                List<Address> addressList = (new Geocoder(getContext())).getFromLocationName(parseAddress, 1);
                if (!addressList.isEmpty()) {
                    adapter.address = addressList.get(0);
                    ((MainActivity) getContext()).setUserAddress(adapter.address);
                    getElections();
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // In case something fails
            Toast.makeText(getContext(), "Could not use address", Toast.LENGTH_SHORT).show();
        }
        // Else, use device location
        ElectionFragmentPermissionsDispatcher.getLocationWithPermissionCheck(this);

    }


    private void getStarredElections() {
        User.getStarredElections(new FindCallback<Election>() {
            @Override
            public void done(List<Election> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get starred elections");
                    return;
                }
                starredElections.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }

    // Annotations from dependency. Includes fine + coarse location
    @SuppressWarnings({"MissingPermission"})
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void getLocation() {
        // Google API to get location
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(getContext());
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Toast.makeText(getContext(), "Got location", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "Location is " + location.toString());
                        // Getting address from Location Object. Add this to adapter
                        // This will later be used to get details of the elections
                        adapter.address = getAddressFromLocation(location);
                        // Gets all elections
                        getElections();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error trying to get last GPS location");
                        Toast.makeText(getContext(), "No location", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });
    }

    private void getElections() {
        GoogleCivicClient googleCivicClient = new GoogleCivicClient();
        googleCivicClient.getElections(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Elections are: " + json.toString());
                try {
                    // Extract elections
                    JSONArray jsonArray = json.jsonObject.getJSONArray("elections");
                    // Now we get the elections from parse + compare to see if any new elections
                    synchronizeElectionsInParse(jsonArray);
                    // We also want to get the user's starred elections
                    getStarredElections();
                } catch (JSONException e) {
                    Log.e(TAG, "Could not add elections");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "No elections " + response, throwable);

            }
        });
    }

    private void synchronizeElectionsInParse(final JSONArray jsonArray) {
        // Get elections in parse
        ParseQuery<Election> query = new ParseQuery<>("Election");
        query.whereEqualTo("hasPassed", false); // Getting only current elections
        query.findInBackground(new FindCallback<Election>() {
            @Override
            public void done(List<Election> objects, ParseException e) {
                if (e != null)
                    Log.e(TAG, "Could not get elections", e);
                else {
                    // First we add the pre-existing elections to the list
                    elections.addAll(objects);
                    adapter.notifyDataSetChanged();
                    // We check the ids of all elections in jsonArray to make sure we have it in the database
                    checkIfElectionsInParse(objects, jsonArray);
                }
            }
        });
    }

    private void checkIfElectionsInParse(List<Election> objects, JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                final Election election = Election.basicInformationFromJson(jsonArray.getJSONObject(i));
                // If the election from the API call is not in parse, we add it
                if (!objects.contains(election)) {
                    addElectionToParse(election);
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void addElectionToParse(final Election election) {
        Log.i(TAG, election + " not in parse");
        election.putInParse(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    Log.e(TAG, "Could not save " + election);
                else {
                    Log.i(TAG, "Saved " + election);
                    // Now we can add the saved election to the list
                    elections.add(election);
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    private Address getAddressFromLocation(Location location) {
        // Result
        Address address = null;
        // Coordinates
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        // Using reverse geocoding
        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> addressList = geocoder.getFromLocation(lat, lng, MAX_LOCATION_RESULTS);
            Toast.makeText(getContext(), "Success in getting address", Toast.LENGTH_SHORT).show();
            address = addressList.get(0);
            // Setting address in MainActivity so other fragments can access it
            ((MainActivity) getContext()).setUserAddress(address);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Could not get address", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No addresses available");
            e.printStackTrace();
        }

        return address;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}