package com.example.voteboat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

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

        // Get list of races
        races = Parcels.unwrap(
                getIntent()
                        .getParcelableExtra(Race.class.getSimpleName()));

        binding.rvRaces.setAdapter(new RaceAdapter(this, races));
        binding.rvRaces.setLayoutManager(new LinearLayoutManager(this));
    }
}