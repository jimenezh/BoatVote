package com.example.voteboat.clients;

import android.content.Intent;
import android.util.Log;

import com.example.voteboat.models.Election;
import com.parse.ParseCloud;
import com.parse.ParseException;
import java.util.HashMap;
import java.text.DateFormatSymbols;


public class PushNotificationClient {

    public static final String TAG = "PushNotificationClient";

    public static String getFormattedDate(String electionDate) {
        // Google client election Date is formatted yyyy-mm-dd

        String month = (new DateFormatSymbols().getShortMonths())[Integer.parseInt(electionDate.substring(5, 7))-1];
        String day = electionDate.substring(8, electionDate.length());
        String year = electionDate.substring(0, 4);

        String time = "00:00:00";

        return month + " " + day + ", " + year + " " + time;


    }

    public static void addChannel(Election election) {
        String date = getFormattedDate(election.getElectionDate());
        HashMap<String, String> params = new HashMap<>();
        params.put("date", date);
        params.put("channel", election.getGoogleId());
        params.put("election", election.getTitle());
        params.put("before", "week");

        Log.i(TAG, "Params are " + params.toString());

        try {
            ParseCloud.callFunction("schedule", params);
            Log.i(TAG, "Scheduled " + election.getGoogleId());
        } catch (ParseException e) {
            Log.e(TAG, "Could not schedule " + election.getGoogleId(), e);
        }
    }
}
