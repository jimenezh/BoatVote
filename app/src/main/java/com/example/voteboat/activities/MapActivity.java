package com.example.voteboat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.voteboat.R;
import com.example.voteboat.databinding.ActivityMapBinding;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import org.parceler.Parcels;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions
public class MapActivity extends AppCompatActivity {

    private final float ZOOM_COUNTRY_LEVEL = 5.0f;
    private final float ZOOM_STREET_LEVEL = 15.0f;

    ActivityMapBinding binding;
    public static final String TAG = "MapActivity";

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private final static String KEY_LOCATION = "location";

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    LatLng pollLocation;
    String pollingHours;
    String addressLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflating layout
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // This checks if anything was saved, and, if so, gets the saved location
        getSavedLocation(savedInstanceState);
        // Initializing map fragment + checking that it's valid + loading map
        initializeMapFragment();
        extractPollLocationDetails();
        // Toolbar
        Toolbar toolbarMain = (Toolbar) binding.inToolbar.toolbar;
        setSupportActionBar(toolbarMain);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void extractPollLocationDetails() {
        Address address = Parcels.unwrap(getIntent().getParcelableExtra("address"));
        pollLocation = new LatLng(address.getLatitude(), address.getLongitude());
        addressLine = address.getAddressLine(0);
        pollingHours = getIntent().getStringExtra("hours");
    }

    private void getSavedLocation(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
    }

    private void initializeMapFragment() {
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() { // Load the map once it is ready (callback)
                @Override
                public void onMapReady(GoogleMap map) { // map is the result, loadMap binds it to our own map
                    loadMap(map);
                    addMarkerForPoll(map);
                }
            });
        } else {
            Snackbar.make(binding.getRoot(), "Cannot display map", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void addMarkerForPoll(GoogleMap map) {
        map.addMarker(new MarkerOptions()
                .position(pollLocation)
                .title(addressLine)
                .snippet(pollingHours));
        Log.i(TAG, "Max zoom is "+map.getMaxZoomLevel()+ " with min "+map.getMinZoomLevel());
        // Set max zoom level to country
        map.setMinZoomPreference(ZOOM_COUNTRY_LEVEL);
        // Moving to street level zoom or to device's max zoom if device is unable to zoom to street level
        map.moveCamera(CameraUpdateFactory.zoomTo(map.getMaxZoomLevel() < ZOOM_STREET_LEVEL ? map.getMaxZoomLevel() : ZOOM_STREET_LEVEL));
        // Pan camera to polling Location
        map.moveCamera(CameraUpdateFactory.newLatLng(pollLocation));
    }


    // Binds the resulting GoogleMap to the map field of the class + requests permission for location + gets location
    // + starts location updates
    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            MapActivityPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
            MapActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
        } else {
            Snackbar.make(binding.getRoot(), "Could not display map", Snackbar.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    // Implements methods called by generated class
    @SuppressWarnings({"MissingPermission"})
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        // Settings on GoogleMap object. To get location?
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        // Code to get location. From Google API?
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Now that we have the location, we go to this function
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Display the connection status

        if (mCurrentLocation != null) {
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.animateCamera(cameraUpdate);
        } else {
            Log.e(TAG, "Current location was null, enable GPS on emulator!");
            Snackbar.make(binding.getRoot(), "Please enable GPS", Snackbar.LENGTH_SHORT).show();
        }
        MapActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
    }

    // Overriding/implementing methods called by the generated class
    @SuppressLint("MissingPermission")
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void startLocationUpdates() {
        // Setting parameters for request
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        // Q: what is a builder????
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    // Updates location on UI
    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }
        // Report to the UI that the location was updated
        mCurrentLocation = location;
    }

    // Creates bundle to save location
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }


    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.miActionProgress).setVisible(false);
        return true;
    }

    // Method when back button is pressed
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                super.onBackPressed();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}