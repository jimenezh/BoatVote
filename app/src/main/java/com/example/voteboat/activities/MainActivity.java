package com.example.voteboat.activities;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.voteboat.R;
import com.example.voteboat.adapters.ElectionAdapter;
import com.example.voteboat.databinding.ActivityMainBinding;
import com.example.voteboat.fragments.ElectionDetailFragment;
import com.example.voteboat.fragments.ElectionFragment;
import com.example.voteboat.fragments.ProfileFragment;
import com.example.voteboat.fragments.ToDoFragment;
import com.example.voteboat.models.ToDoItem;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;

import org.parceler.Parcels;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements ElectionFragment.ElectionListener {

    public static final String TAG = "MainActivity";
    ActivityMainBinding binding;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    Address address;

    ProgressBar miActionProgressItem;

    final ElectionFragment electionFragment = new ElectionFragment();
    final ToDoFragment toDoFragment = new ToDoFragment();
    final ProfileFragment profileFragment = new ProfileFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding. Inflating XML file
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        miActionProgressItem = findViewById(R.id.pbProgressAction);

        // Setting listener for bottom navigation
        setBottomNavigationListener();

        // Location
        // Checking if key is null
        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        HashMap<String, HashMap> payload = new HashMap<>();
        HashMap<String, String> date = new HashMap<>();
        date.put("date", "July 19, 2020 18:46:00");
        payload.put("params", date);
        ParseCloud.callFunctionInBackground("schedule", payload);

    }

    private void setBottomNavigationListener() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Guarantees that we cannot switch out of election feed until we have the address


                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = electionFragment;
                        break;
                    case R.id.action_todo:
                        fragment = toDoFragment;
                        break;
                    case R.id.action_profile:
                        fragment = profileFragment;
                        break;
                }

                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        binding.bottomNavigation.setSelectedItemId(R.id.action_home);
    }

    /* Listens for when election is clicked on, to go to that elections races
     *  object is the data to be passed into the new fragment
     *  fragment is which fragment we are going to
     *  type is what data type the object is
     */
    @Override
    public void changeFragment(Fragment fragment, TextView title, TextView date) {
        // Replace frame layout with fragment
        transitionToDetailView(fragment, title, date);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar);
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisibility(View.INVISIBLE);
    }

    public void transitionToDetailView(Fragment fragment, TextView title, TextView date) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Transition explodeTransform = new Explode();
            Transition slideTransform = new Slide();
            Transition fadeTransform = new Fade();
//            Transition

            // Setup exit transition on first fragment
            electionFragment.setSharedElementReturnTransition(fadeTransform);
            electionFragment.setExitTransition(explodeTransform);

            // Setup enter transition on second fragment
            fragment.setSharedElementEnterTransition(fadeTransform);
            fragment.setEnterTransition(slideTransform);

            // Find the shared element (in Fragment A)

            // Add second fragment by replacing first
            fragmentManager
                    .beginTransaction()
                    .addSharedElement(title, "electionTitle")
                    .addSharedElement(date, "electionDate")
                    .replace(R.id.flContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}