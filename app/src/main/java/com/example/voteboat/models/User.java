package com.example.voteboat.models;

import android.util.Log;
import android.widget.ScrollView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {
    /*
     * Rather than having to create another User object, this User class
     * gets one instance of the current user (user)
     * and uses ParseUser methods to handle custom fields from the database
     */
    final static ParseUser user = ParseUser.getCurrentUser();
    public static final String KEY_STARRED_ELECTIONS = "elections";

    static ArrayList<String> starredElections;
    static ArrayList<String> toAdd = new ArrayList<>();
    static ArrayList<String> toRemove = new ArrayList<>();

    public User() {
        starredElections = (ArrayList<String>) user.get(KEY_STARRED_ELECTIONS);
    }

    public static ArrayList<String> getStarredElections() {
        return starredElections;
    }

    public static void addToStarredElections(String electionId) {
        toAdd.add(electionId);
        Log.i("USER", "To add now has " + toAdd.size());
    }

    public static void removeFromStarredElections(String electionId) {
        toRemove.add(electionId);
        Log.i("USER", "To remove now has " + toAdd.size());

//        user.removeAll(KEY_STARRED_ELECTIONS, Collections.singleton(electionId));
    }

    public static void saveUser(final String data, final String tag) {
        if(!toAdd.isEmpty())
            user.addAllUnique(KEY_STARRED_ELECTIONS, toAdd);
        if(!toRemove.isEmpty())
            user.removeAll(KEY_STARRED_ELECTIONS,toRemove);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    Log.e(tag, "Could not save " + data);
                else
                    Log.i(tag, "Saved " + data);
            }
        });
    }


}
