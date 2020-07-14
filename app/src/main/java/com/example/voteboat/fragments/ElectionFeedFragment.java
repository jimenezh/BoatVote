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
import com.example.voteboat.MainActivity;
import com.example.voteboat.adapters.ElectionFeedAdapter;
import com.example.voteboat.clients.GoogleCivicClient;
import com.example.voteboat.databinding.FragmentElectionFeedBinding;
import com.example.voteboat.models.Election;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions // using https://github.com/permissions-dispatcher
public class ElectionFeedFragment extends Fragment {

    public static final String TAG = "ElectionFeedFragment";

    FragmentElectionFeedBinding binding;
    ElectionFeedAdapter adapter;
    List<Election> elections;

    public FusedLocationProviderClient fusedLocationProviderClient;
    private final int MAX_LOCATION_RESULTS = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentElectionFeedBinding.inflate(getLayoutInflater());

        // Generated class method to request permission for location
        ElectionFeedFragmentPermissionsDispatcher.getLocationWithPermissionCheck(this);

        return binding.getRoot();
    }

    // Delegates work to helper class
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ElectionFeedFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inititalizing empty list
        elections = new ArrayList<>();
        // Setting adapter
        adapter = new ElectionFeedAdapter(getContext(), elections);
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
                        // Getting address from Location Object
                        String address = getAddressfromLocation(location);
                        Log.i(TAG, "Address is " + address);
                        // Requesting elections
                        GoogleCivicClient googleCivicClient = new GoogleCivicClient();
                        googleCivicClient.getElections(address, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "Elections are: " + json.toString());
                                try {
                                    JSONArray jsonArray = json.jsonObject.getJSONArray("elections");
                                    elections.addAll(Election.fromJsonArray(jsonArray));
                                    Log.i(TAG,"Added all elections "+elections.size());
                                } catch (JSONException e) {
                                    Log.e(TAG,"Could not add elections");
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "No elections " + response, throwable);

                            }
                        });


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

    private String getAddressfromLocation(Location location) {
        String address = "";
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        // Uses reverse geocoding
        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> addressList = geocoder.getFromLocation(lat, lng, MAX_LOCATION_RESULTS);
            Toast.makeText(getContext(), "Success in getting address", Toast.LENGTH_SHORT).show();
            address = addressList.get(0).getAddressLine(0);
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