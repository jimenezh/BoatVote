package com.example.voteboat.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.voteboat.activities.LogInActivity;
import com.example.voteboat.databinding.FragmentProfileBinding;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {
    public static final String TAG ="ProfileFragment";
    FragmentProfileBinding binding;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        // Log out
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                launchLogInActivity();
            }
        });
        return binding.getRoot();
    }

    private void launchLogInActivity() {
        Toast.makeText(getContext(), "User logged out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(), LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
        getActivity().finish();
    }
}