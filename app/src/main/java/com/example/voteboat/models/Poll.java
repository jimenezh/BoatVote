package com.example.voteboat.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Parcel
public class Poll {

    String location;
    String openDate;
    String closeDate;
    String pollingHours;

    public static Poll fromJson(JSONObject jsonObject) throws JSONException {
        Poll poll = new Poll();
        // Getting full address
        poll.location = getFullAddress(jsonObject);
        Log.i("POLL", "Full address is " + poll.location);
        // TODO: add more information on location
        poll.pollingHours = checkifExistsAndAdd("pollingHours", jsonObject);
        poll.openDate = checkifExistsAndAdd("startDate", jsonObject);
        poll.closeDate = checkifExistsAndAdd("endDate", jsonObject);
        return poll;
    }

    private static String checkifExistsAndAdd(String field, JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(field))
            return jsonObject.getString(field);
        else
            return "";
    }

    private static String getFullAddress(JSONObject jsonObject) throws JSONException {
        String address = "";
        JSONObject addressInformation = jsonObject.getJSONObject("address");
        Iterator<String> stringIterator = addressInformation.keys();
        for (Iterator<String> it = stringIterator; it.hasNext(); ) {
            String key = it.next();
            address = address + "\n" + addressInformation.getString(key);
        }
        return address;
    }

    public static List<Poll> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Poll> polls = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            polls.add(Poll.fromJson(jsonArray.getJSONObject(i)));
        }
        return polls;
    }

    public String getPollingHours() {
        return pollingHours;
    }

    public String getLocation() {
        return location;
    }

    public String getOpenDate() {
        return openDate;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public boolean hasDates() {
        return !openDate.isEmpty() && !closeDate.isEmpty();
    }
}
