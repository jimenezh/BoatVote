package com.example.voteboat.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import com.example.voteboat.models.User;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class EditUsernameFragment extends DialogFragment {
    public static final String TAG = "EditUsernameFragment";
    public static final String USERNAME = "New username";

    public EditUsernameFragment() {
        // Required empty public constructor
    }

    public static EditUsernameFragment newInstance(String title) {
        EditUsernameFragment frag = new EditUsernameFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


    public interface EditNameDialogListener {
        void onFinishEditDialog(String inputText);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult(String data) {
        EditNameDialogListener listener = (EditNameDialogListener) getTargetFragment();
        listener.onFinishEditDialog(data);
        dismiss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = getArguments().getString("title");

        final EditText editText = new EditText(getContext());
        // Hides the input in case of password
        if (!title.equals(USERNAME))
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setView(editText);
        alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Change username/password
                if (title.equals(USERNAME)) {
                    changeUsername(editText.getText().toString());
                } else {
                    changePassword(editText.getText().toString());
                }

            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

        });

        return alertDialogBuilder.create();
    }

    private void changePassword(String password) {
        User.setPassword(password);
        dismiss();
    }

    private void changeUsername(String username) {
        User.setUsername(username);
        sendBackResult(username);
    }
}