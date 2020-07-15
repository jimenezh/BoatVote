package com.example.voteboat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.voteboat.R;
import com.example.voteboat.databinding.ActivityLogInBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

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
            launchMainActivity();
        }

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                if(username.isEmpty() || password.isEmpty())
                    Toast.makeText(LogInActivity.this,"Please fill out all fields",Toast.LENGTH_LONG).show();
                else
                    logInUser(username, password);
            }
        });

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                if(username.isEmpty() || password.isEmpty())
                    Toast.makeText(LogInActivity.this,"Please fill out all fields",Toast.LENGTH_LONG).show();
                else
                    signUpUser(username,password);
            }
        });
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // So that user can't go back to login page
    }

    public void logInUser(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.i(TAG, user.getUsername() + " is signed in");
                    Toast.makeText(LogInActivity.this, user.getUsername() + " is signed in", Toast.LENGTH_SHORT);
                    launchMainActivity();
                } else {
                    Log.e(TAG, "Parse Log In exception", e);
                    Toast.makeText(LogInActivity.this, "Could not sign in", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signUpUser(final String username, final String password) {
        Log.i(TAG,"in Sign up user");
        // Create the ParseUser
        final ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);

        Log.i(TAG,"User has "+username+" "+password);

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {

                if (e == null) {
                    // Hooray! Let them use the app now.
                    Toast.makeText(LogInActivity.this, "User signed up!",Toast.LENGTH_LONG).show();
                    logInUser(username, password);
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.e(TAG, "Error SignUp", e);
                    Toast.makeText(LogInActivity.this, "Could not sign up",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}