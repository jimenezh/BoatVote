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
import com.example.voteboat.adapters.ElectionAdapter;
import com.example.voteboat.clients.GoogleCivicClient;
import com.example.voteboat.databinding.FragmentElectionBinding;
import com.example.voteboat.models.Election;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public FusedLocationProviderClient fusedLocationProviderClient;
    private final int MAX_LOCATION_RESULTS = 5;
    Address address;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentElectionBinding.inflate(getLayoutInflater());
        // TODO: check if have user address
        // Generated class method to request permission for location
        ElectionFragmentPermissionsDispatcher.getLocationWithPermissionCheck(this);

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
        // Inititalizing empty list
        elections = new ArrayList<>();
        // Setting adapter
        adapter = new ElectionAdapter(getContext(), elections, address);
        binding.rvElections.setAdapter(adapter);
        binding.rvElections.setLayoutManager(new LinearLayoutManager(getContext()));
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
                        // Getting state + state abbreviation
                        final String ocd_id = getStateAbbreviation(adapter.address);
                        // Gets all elections
                        populateElectionList(ocd_id);
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

    @NotNull
    private String getStateAbbreviation(Address address) {
        Map<String, String> states = getStateHashmap();
        String state = states.get(address.getAdminArea()).toLowerCase();
        // TODO: replace with actual state
        return String.format("ocd-division/country:us/state:%s", DUMMY_STATE);
    }

    private void populateElectionList(final String ocd_id) {
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
        query.findInBackground(new FindCallback<Election>() {
            @Override
            public void done(List<Election> objects, ParseException e) {
                if(e != null)
                    Log.e(TAG, "Could not get elections", e);
                else{
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
                if(!objects.contains(election)){
                    addElectionToParse(election);
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void addElectionToParse(final Election election) {
        Log.i(TAG, election+" not in parse");
        try {
            election.putInParse(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null)
                        Log.e(TAG, "Could not save "+election);
                    else {
                        Log.i(TAG, "Saved " + election);
                        // Now we can add the saved election to the list
                        elections.add(election);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void addElectionIfInUserState(final JSONArray jsonArray, final String ocd_id) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.getString("ocdDivisionId").equals(ocd_id) || jsonObject.getString("id").equals("2000")) {
                // If it has the same id, then we want to get more of it's information
                elections.add(Election.basicInformationFromJson(jsonObject));
            }
        }
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
        } catch (IOException e) {
            Toast.makeText(getContext(), "Could not get address", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No addresses available");
            e.printStackTrace();
        }

        return address;
    }


    private Map<String, String> getStateHashmap() {
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
        return states;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}