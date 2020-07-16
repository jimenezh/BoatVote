package com.example.voteboat.models;

import android.util.Log;
import android.widget.ScrollView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class User {
    /*
     * Rather than having to create another User object, this User class
     * gets one instance of the current user (user)
     * and uses ParseUser methods to handle custom fields from the database
     */
    final static ParseUser user = ParseUser.getCurrentUser();
    public static final String KEY_STARRED_ELECTIONS = "elections";

    static HashSet<String> toAdd = new HashSet<String>();
    static HashSet<String> toRemove = new HashSet<String>();


    public static ArrayList<String> getStarredElections() {
        return (ArrayList<String>) user.get(KEY_STARRED_ELECTIONS);
    }

    public static void addToStarredElections(String electionId) {
        toAdd.add(electionId);
        Log.i("USER", "To add now has " + toAdd.size());
    }

    public static void removeFromStarredElections(String electionId) {
        toRemove.add(electionId);
        Log.i("USER", "To remove now has " + toRemove.size());

//        user.removeAll(KEY_STARRED_ELECTIONS, Collections.singleton(electionId));
    }

    public static void saveUserStarredElections(final String data, final String tag) {
        // If need to add elecs, then we go ahead and try to save these
        // this method also then attempt to remove the elects we want to remove
        // Nested callbacks will be slower but then will avoid error of operation not allowed
        if (!toAdd.isEmpty())
            saveNewlyStarredElections(data, tag);
        // Because of the above, use else if
        // This is in case toAdd is empty but toRemove is not
        else if(!toRemove.isEmpty())
            saveUnstarredElections(tag,data);
    }

    private static void saveNewlyStarredElections(final String data, final String tag) {
        user.addAllUnique(KEY_STARRED_ELECTIONS, toAdd);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(tag, "Could not save added in" + data);
                } else {
                    toAdd.clear();
                    if (!toRemove.isEmpty()) {
                        saveUnstarredElections(tag, data);
                    }
                }
            }
        });
    }

    private static void saveUnstarredElections(final String tag, final String data) {
        user.removeAll(KEY_STARRED_ELECTIONS, toRemove);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    Log.e(tag, "Could not save removed in" + data);
                else {
                    Log.i(tag, "Saved " + data);
                    toRemove.clear();
                }

            }
        });
    }


}
