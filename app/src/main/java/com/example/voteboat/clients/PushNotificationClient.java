package com.example.voteboat.clients;

import android.content.Intent;
import android.util.Log;

import com.example.voteboat.models.Election;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParsePush;

import java.util.Date;
import java.util.HashMap;


public class PushNotificationClient {

    public static final String TAG = "PushNotificationClient";

    public static String getFormattedDate(String electionDate) {
        // Google client election Date is formatted yyyy-mm-dd
        String month = electionDate.substring(8,electionDate.length());
        String day = electionDate.substring(5,7);
        String year = electionDate.substring(0, 4);

        // Now we want to turn it into a UTC Date
        Date date = new Date(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));

        Log.i(TAG, date.toString());

        return "Jul 31, 2020 15:45:00";
    }

    public static void addChannel(Election election) {
        String date = getFormattedDate(election.getElectionDate());
        HashMap<String, String> params = new HashMap<>();
        params.put("date", date);
        params.put("channel", election.getGoogleId());
        params.put("election", election.getTitle());
        params.put("before", "week");

        Log.i(TAG, "Params are "+params.toString());

        try {
            ParseCloud.callFunction("schedule", params);
            Log.i(TAG, "Scheduled "+election.getGoogleId());
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Could not schedule "+election.getGoogleId());
        }
    }
}
