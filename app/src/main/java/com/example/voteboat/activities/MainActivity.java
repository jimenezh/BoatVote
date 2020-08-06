package com.example.voteboat.activities;

import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.voteboat.R;
import com.example.voteboat.databinding.ActivityMainBinding;
import com.example.voteboat.fragments.ElectionFragment;
import com.example.voteboat.fragments.ProfileFragment;
import com.example.voteboat.fragments.ToDoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseCloud;

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

        Toolbar toolbarMain = (Toolbar) binding.inToolbar.toolbar;
        setSupportActionBar(toolbarMain);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void showProgressBar() {
        if(miActionProgressItem == null)
            return;
        // Show progress item
        miActionProgressItem.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        if(miActionProgressItem == null)
            return;
        // Hide progress item
        miActionProgressItem.setVisibility(View.INVISIBLE);
    }


    /**
     * Adds custom transition when going from electionFragment to fragment
     * @param fragment
     * @param title
     * @param date
     */
    public void transitionToDetailView(Fragment fragment, TextView title, TextView date) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Transition floatTransform = TransitionInflater.from(this).inflateTransition(R.transition.election_title_transform);

            // Setup exit transition on first fragment for shared elements
            electionFragment.setSharedElementReturnTransition(floatTransform);

            // Setup enter transition on second fragment
            fragment.setSharedElementEnterTransition(floatTransform);
            fragment.setSharedElementReturnTransition(floatTransform);

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