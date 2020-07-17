package com.example.voteboat.models;

import android.util.Log;
import android.widget.ScrollView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class User {

    public static final String TAG = "User";
    public static final String KEY_TO_DO = "toDo";

    public final static ParseUser user = ParseUser.getCurrentUser();
    public static final String KEY_STARRED_ELECTIONS = "elections";

    // Gettter for updates list of elections
    public static ArrayList<String> getStarredElections() {
        return (ArrayList<String>) user.get(KEY_STARRED_ELECTIONS);
    }

    public static void starElection(Election election) {
        // First, we add the election Id to the user's list of starred election
        user.add(User.KEY_STARRED_ELECTIONS, election.getGoogleId());
        // Now, we construct a new ToDoItem + add it to the user's list of ToDoItems
        ToDoItem toDoItem = new ToDoItem();
        toDoItem.put("name", election.getTitle());
        toDoItem.put("googleId", election.getGoogleId());
        toDoItem.put("user", User.user);
        user.add(KEY_TO_DO, toDoItem);
        // Finally, we save the user
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    Log.e(TAG, "Could not star election", e);
                else
                    Log.i(TAG, "Starred election");
            }
        });
    }


    public static void unstarElection(final Election election) {
        // First we need to find which ToDoItem has the same id for the user and or the election
        ParseQuery<ToDoItem> query = new ParseQuery<ToDoItem>("ToDoItem");
        query.whereEqualTo("googleId", election.getGoogleId());
        query.whereEqualTo("user", user);
        query.findInBackground(new FindCallback<ToDoItem>() {
            @Override
            public void done(List<ToDoItem> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not unstar election", e);
                    return;
                }
                // Now we remove the election id from the user and the toDoItems
                user.removeAll(KEY_STARRED_ELECTIONS, Collections.singleton(election.getGoogleId()));
                user.removeAll(KEY_TO_DO, objects);
                // Finally we save the user
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Could not unstar election", e);
                            return;
                        } else
                            Log.i(TAG, "Unstarred " + election.getGoogleId());
                    }
                });
            }
        });

    }
}
