package com.example.voteboat.models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {

    public static final String TAG = "User";
    public static final String KEY_TO_DO = "toDo";

    public static final String KEY_STARRED_ELECTIONS = "elections";

    // Gettter for updates list of elections
    public static ArrayList<String> getStarredElections() {
        return (ArrayList<String>) ParseUser.getCurrentUser().get(KEY_STARRED_ELECTIONS);
    }

    public static void starElection(Election election) {
        ParseUser user = ParseUser.getCurrentUser();
        // First, we add the election Id to the user's list of starred election
        user.add(User.KEY_STARRED_ELECTIONS, election.getGoogleId());
        // Now, we construct a new ToDoItem + add it to the user's list of ToDoItems
        ToDoItem toDoItem = new ToDoItem();
        toDoItem.put("name", election.getTitle());
        toDoItem.put("googleId", election.getGoogleId());
        toDoItem.put("user", user);
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
        final ParseUser user = ParseUser.getCurrentUser();

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
                objects.get(0).deleteInBackground();
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

    public static String getUsername() {
        return ParseUser.getCurrentUser().getUsername();
    }

    public static void setUsername(String text) {
        ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(text);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null)
                    Log.e(TAG, "Could not save username", e);
                else
                    Log.i(TAG, "Saved username");
            }
        });
    }
}
