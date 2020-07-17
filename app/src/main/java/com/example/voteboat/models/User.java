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
    public final static ParseUser user = ParseUser.getCurrentUser();
    public static final String KEY_STARRED_ELECTIONS = "elections";

    /*HashSets to prevent duplicates
    * Logic: everytime a user stars or unstars an election, it will be added to the
    * appropriate HashSet. These will then be used to update the user (when not empty)
    * when the fragment is destroyed.
    * Note that we only add elections to star/unstar when they were originally unstarred/starred
    * This prevents tons of callbacks + possible weird async behavior
    */
    static HashSet<String> toAdd = new HashSet<>();
    static HashSet<String> toRemove = new HashSet<>();

    // Gettter for updates list of elections
    public static ArrayList<String> getStarredElections() {
        return (ArrayList<String>) user.get(KEY_STARRED_ELECTIONS);
    }

    // Adding of star/unstar elections
    public static void addToStarredElections(String electionId) {
        toAdd.add(electionId);
    }
    public static void removeFromStarredElections(String electionId) {
        toRemove.add(electionId);
    }

    // Called when fragment is destroyed
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
                    // Now we can update the user with the elections removed
                    if (!toRemove.isEmpty()) {
                        saveUnstarredElections(tag, data);
                    }
                }
            }
        });
    }

    public static void starElection(Election election){

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
