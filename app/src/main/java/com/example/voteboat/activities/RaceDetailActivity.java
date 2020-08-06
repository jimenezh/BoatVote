package com.example.voteboat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.voteboat.R;
import com.example.voteboat.adapters.RaceAdapter;
import com.example.voteboat.databinding.ActivityRaceDetailBinding;
import com.example.voteboat.models.Race;

import org.parceler.Parcels;

import java.util.List;

public class RaceDetailActivity extends AppCompatActivity {
    public static final String TAG = "RaceDetailActivity";
    ActivityRaceDetailBinding binding;
    List<Race> races;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRaceDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Toolbar
        Toolbar toolbarMain = (Toolbar) binding.inToolbar.toolbar;
        setSupportActionBar(toolbarMain);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get list of races
        races = Parcels.unwrap(
                getIntent()
                        .getParcelableExtra(Race.class.getSimpleName()));

        binding.rvRaces.setAdapter(new RaceAdapter(this, races));
        binding.rvRaces.setLayoutManager(new LinearLayoutManager(this));
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