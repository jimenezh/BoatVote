package com.example.voteboat.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.voteboat.R;
import com.example.voteboat.databinding.FragmentEditDialogueBinding;
import com.example.voteboat.models.User;

import androidx.fragment.app.DialogFragment;


public class EditDialogueFragment extends DialogFragment {
    public static final String TAG = "EditUsernameFragment";
    FragmentEditDialogueBinding binding;
    private EditText mEditText;


    public EditDialogueFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentEditDialogueBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    public static EditDialogueFragment newInstance(String title) {
        EditDialogueFragment frag = new EditDialogueFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mEditText = (EditText) view.findViewById(R.id.etUsername);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().cancel();
            }
        });
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.etUsername.getText().toString();
                if(!text.isEmpty() || text != null) {
                    User.setUsername(text);
                }
            }
        });
    }
}