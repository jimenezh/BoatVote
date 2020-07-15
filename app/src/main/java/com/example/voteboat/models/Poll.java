package com.example.voteboat.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
@Parcel
public class Poll {

    String location;
    String datesOpen;
    String pollingHours;

    public static Poll fromJson(JSONObject jsonObject) throws JSONException {
        Poll poll = new Poll();
        JSONObject addressInformation = jsonObject.getJSONObject("address");
        poll.location = addressInformation.getString("locationName");
        // TODO: add more information on location
        poll.pollingHours = jsonObject.getString("pollingHours");
        return poll;
    }

    public static List<Poll> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Poll> polls = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            polls.add(Poll.fromJson(jsonArray.getJSONObject(i)));
        }
        return polls;
    }

    public String getLocation() {
        return location;
    }

    public String getDatesOpen() {
        return datesOpen;
    }

}
