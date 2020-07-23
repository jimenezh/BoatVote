package com.example.voteboat.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.voteboat.activities.LogInActivity;
import com.example.voteboat.adapters.ElectionAdapter;
import com.example.voteboat.adapters.PastElectionsAdapter;
import com.example.voteboat.databinding.FragmentProfileBinding;
import com.example.voteboat.models.Election;
import com.example.voteboat.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements EditUsernameFragment.EditNameDialogListener {
    public static final String TAG = "ProfileFragment";
    FragmentProfileBinding binding;

    List<Election> pastElections;
    PastElectionsAdapter adapter;

    boolean useCustomAddress;

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
        // User past elections RV
        pastElections = new ArrayList<>();
        adapter = new PastElectionsAdapter(getContext(), pastElections);
        binding.rvPastElections.setAdapter(adapter);
        binding.rvPastElections.setLayoutManager(new LinearLayoutManager(getContext()));
        // Query for past elections
        populatePastElectionsRV();


        // Get current address
        getCurrentAddress();

        // Custom address
        binding.switchAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    setAddressFormVisibility(View.VISIBLE);
                } else {
                    setAddressFormVisibility(View.GONE);
                    User.setUseCustomAddress(false);
                }
            }
        });

        binding.btnSetAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAddress();
            }
        });

        return binding.getRoot();
    }

    private void setAddress() {
        String address = binding.etAddress.getText().toString();
        if (address.isEmpty())
            Toast.makeText(getContext(), "Address cannot be empty", Toast.LENGTH_SHORT).show();
        else {
            User.setAddress(address);
            binding.tvCurrentAddress.setText(address);
            binding.etAddress.setText("");
            User.setUseCustomAddress(true);
        }
    }

    private void getCurrentAddress() {
        if( User.useCustomAddress()){
            String currentAddress = User.getCurrentAddress();
            binding.tvCurrentAddress.setText(currentAddress);
            binding.switchAddress.setChecked(true);
            setAddressFormVisibility(View.VISIBLE);
        }
    }

    private void setAddressFormVisibility(int visibility) {
        binding.tvCurrentAddress.setVisibility(visibility);
        binding.etAddress.setVisibility(visibility);
        binding.btnSetAddress.setVisibility(visibility);
    }

    private void populatePastElectionsRV() {
        User.getPastElections(new FindCallback<Election>() {
            @Override
            public void done(List<Election> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get past elections");
                    return;
                }
                Log.i(TAG, "Got " + objects.size() + " past elections");
                pastElections.addAll(objects);
                adapter.notifyDataSetChanged();
                binding.tvNumElections.setText(String.valueOf(pastElections.size()));
            }
        });

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