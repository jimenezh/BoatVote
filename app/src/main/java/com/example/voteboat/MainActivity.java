package com.example.voteboat;

import android.Manifest;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.voteboat.clients.GoogleCivicClient;
import com.example.voteboat.databinding.ActivityMainBinding;
import com.example.voteboat.fragments.ElectionFeedFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions // using https://github.com/permissions-dispatcher
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    ActivityMainBinding binding;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    final ElectionFeedFragment electionFeedFragment = new ElectionFeedFragment();

    public FusedLocationProviderClient fusedLocationProviderClient;
    private final int MAX_LOCATION_RESULTS = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding. Inflating XML file
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setting listener for bottom navigation
        setBottomNavigationListener();

        // Location
        // Checking if key is null
        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }
        // Generated class method to request permission for location
        MainActivityPermissionsDispatcher.getLocationWithPermissionCheck(this);
        getLocation();

    }

    // Delegates work to helper class
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    // Annotations from dependency. Includes fine + coarse location
    @SuppressWarnings({"MissingPermission"})
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void getLocation() {
        // Google API to get location
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Toast.makeText(MainActivity.this,"Got location", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "Location is "+location.toString());
                        // Getting address from Location Object
                        String address = getAddressfromLocation(location);
                        Log.i(TAG, "Address is "+address);
                        try {
                            URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        // Requesting elections
                        GoogleCivicClient googleCivicClient = new GoogleCivicClient();
                        Log.i(TAG,"done with client");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error trying to get last GPS location");
                        Toast.makeText(MainActivity.this,"No location", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });
    }

    private String getAddressfromLocation(Location location) {
        String address = "";
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        // Uses reverse geocoding
        Geocoder geocoder = new Geocoder(MainActivity.this);
        try {
            List<Address> addressList = geocoder.getFromLocation(lat,lng,MAX_LOCATION_RESULTS);
            Toast.makeText(MainActivity.this,"Success in getting address" , Toast.LENGTH_SHORT).show();
            address = addressList.get(0).getAddressLine(0);
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Could not get address", Toast.LENGTH_SHORT).show();
            Log.e(TAG,"No addresses available");
            e.printStackTrace();
        }
        return address;
    }


    private void setBottomNavigationListener() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = electionFeedFragment;
                    case R.id.action_calendar:
                        fragment = electionFeedFragment;
                    case R.id.action_profile:
                        fragment = electionFeedFragment;
                    default:
                        fragment = electionFeedFragment;
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        binding.bottomNavigation.setSelectedItemId(R.id.action_home);
    }
}