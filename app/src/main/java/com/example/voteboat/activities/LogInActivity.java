package com.example.voteboat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.voteboat.R;
import com.example.voteboat.databinding.ActivityLogInBinding;
import com.parse.ParseUser;

public class LogInActivity extends AppCompatActivity {

    public static final String TAG="LogInActivity";
    ActivityLogInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // If user is already signed in, redirect to MainActivity
        if(ParseUser.getCurrentUser() != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // So that user can't go back to login page
        }
    }
}