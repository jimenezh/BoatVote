package com.example.voteboat;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.voteboat.databinding.ActivityMainBinding;
import com.example.voteboat.fragments.ElectionFeedFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    ActivityMainBinding binding;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    final ElectionFeedFragment electionFeedFragment = new ElectionFeedFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding. Inflating XML file
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setting listener for bottom navigation
        setBottomNavigationListener();

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