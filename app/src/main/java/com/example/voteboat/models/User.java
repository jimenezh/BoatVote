package com.example.voteboat.models;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

public class User {
    /*
     * Rather than having to create another User object, this User class
     * gets one instance of the current user (user)
     * and uses ParseUser methods to handle custom fields from the database
     */
    final static ParseUser user = ParseUser.getCurrentUser();
    public static final String KEY_STARRED_ELECTIONS="elections";

    public static ArrayList<String> getStarredElections(){
        return (ArrayList<String>)  user.get(KEY_STARRED_ELECTIONS);
    }


}
