package com.example.voteboat.models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class User {

    public static final String TAG = "User";
    public static final String KEY_TO_DO = "toDo";

    public static final String KEY_STARRED_ELECTIONS = "starredElections";
    public static final String KEY_PAST_ELECTIONS = "pastElections";

    // Gettter for updates list of elections
    public static void getStarredElections(FindCallback findCallback) {
        ParseRelation<Election> starredElections = ParseUser.getCurrentUser().getRelation(KEY_STARRED_ELECTIONS);
        starredElections.getQuery().findInBackground(findCallback);
    }

    public static void starElection(Election election) {
        final ParseUser user = ParseUser.getCurrentUser();
        // First, we add the election Id to the user's list of starred election
        ParseRelation<Election> elections = user.getRelation(KEY_STARRED_ELECTIONS);
        elections.add(election);
        // Now, we construct a new ToDoItem + add it to the user's list of ToDoItems
        final ToDoItem toDoItem = createToDoItem(election, user);
        toDoItem.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG,"Could not save toDoItem");
                } else{
                    Log.i(TAG, "Saved toDoItem");
                    addItemToUser(user, toDoItem);
                    saveUser("Could not star election", "Starred election");
                }
            }
        });
    }

    private static void addItemToUser(ParseUser user, ToDoItem toDoItem) {
        ParseRelation<ToDoItem> toDoItems = user.getRelation(KEY_TO_DO);
        toDoItems.add(toDoItem);
    }

    private static ToDoItem createToDoItem(Election election, ParseUser user) {
        final ToDoItem toDoItem = new ToDoItem();
        toDoItem.put("name", election.getTitle());
        toDoItem.put("googleId", election.getGoogleId());
        toDoItem.put("election", election);
        toDoItem.put("user", user);
        return toDoItem;
    }


    public static void unstarElection(final Election election) {
        final ParseUser user = ParseUser.getCurrentUser();

        // First we get the user's to do items and remove the proper one
        final ParseRelation<ToDoItem> todoItems = user.getRelation(KEY_TO_DO);
        todoItems.getQuery()
                .whereEqualTo("election", election)
                .findInBackground(new FindCallback<ToDoItem>() {
                    @Override
                    public void done(List<ToDoItem> objects, ParseException e) {
                        if(e != null){
                            Log.e(TAG, "Could not get toDoItem", e);
                            return;
                        }
                        if(!objects.isEmpty()){
                            removeItemFromUser(objects, todoItems);
                            removeElectionFromUser(user, election);
                            saveUser("Could not unstar election","Unstarred election");
                        }
                    }
                });

    }

    private static void removeElectionFromUser(ParseUser user, Election election) {
        ParseRelation<Election> starredElections = user.getRelation(KEY_STARRED_ELECTIONS);
        starredElections.remove(election);
    }

    private static void removeItemFromUser(List<ToDoItem> objects, ParseRelation<ToDoItem> todoItems) {
        // First we remove the 1 todoitem
        ToDoItem toRemove = objects.get(0);
        todoItems.remove(toRemove);
        // Then we delete it
        toRemove.deleteInBackground();
    }

    public static String getUsername() {
        return ParseUser.getCurrentUser().getUsername();
    }

    public static void setUsername(String text) {
        ParseUser.getCurrentUser().setUsername(text);
        saveUser("Could not save username", "Saved username");
    }

    public static void saveUser(final String failureMessage, final String successMessage) {
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    Log.e(TAG, failureMessage, e);
                else
                    Log.i(TAG, successMessage);
            }
        });
    }

    public static void setPassword(String password) {
        ParseUser.getCurrentUser().setPassword(password);
        saveUser("Could not save password", "Saved password");
    }

    public static void getPastElections(FindCallback findCallback) {
        ParseRelation<Election> elections = ParseUser.getCurrentUser().getRelation(KEY_PAST_ELECTIONS);
        elections.getQuery().findInBackground(findCallback);
    }

    public static void addToPastElections(Election e) {
        ParseRelation<Election> elections = ParseUser.getCurrentUser().getRelation(KEY_PAST_ELECTIONS);
        elections.add(e);
        saveUser("Could not add past election", "Added past election");
    }

    public static void getToDo(FindCallback findCallback) {
        ParseRelation<ToDoItem> toDoItemParseRelation = ParseUser.getCurrentUser().getRelation(KEY_TO_DO);
        toDoItemParseRelation.getQuery().findInBackground(findCallback);
    }
}
