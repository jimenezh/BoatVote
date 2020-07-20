package com.example.voteboat.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.voteboat.activities.LogInActivity;
import com.example.voteboat.databinding.FragmentProfileBinding;
import com.example.voteboat.models.User;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment implements EditUsernameFragment.EditNameDialogListener {
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
        // User details
        binding.tvUsername.setText(User.getUsername());
        binding.btnUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditUsernameDialog("New username");
            }
        });
        binding.btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditUsernameDialog("New password");
            }
        });
        return binding.getRoot();
    }

    private void showEditUsernameDialog(String title) {
        FragmentManager fm = getParentFragmentManager();
        EditUsernameFragment editDialogFragment = EditUsernameFragment.newInstance(title);
        editDialogFragment.setTargetFragment(this, 300);
        editDialogFragment.show(fm, "EditUsernameFragment");
    }

    private void launchLogInActivity() {
        Toast.makeText(getContext(), "User logged out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(), LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
        getActivity().finish();
    }

    // This is called when the dialog is completed and the results have been passed
    @Override
    public void onFinishEditDialog(String inputText) {
        Toast.makeText(getContext(), "Hi, " + inputText, Toast.LENGTH_SHORT).show();
        binding.tvUsername.setText(inputText);
    }
}