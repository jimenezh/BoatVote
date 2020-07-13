package com.example.voteboat;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.voteboat.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding. Inflating XML file
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Toast.makeText(MainActivity.this, "Clicked on home",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.action_calendar:
                        Toast.makeText(MainActivity.this, "Clicked on calendar",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.action_profile:
                        Toast.makeText(MainActivity.this, "Clicked on profile",Toast.LENGTH_SHORT).show();
                        return true;
                    default: return true;
                }
            }
        });

    }
}