package com.example.voteboat.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Iterator;

public class Poll {

    String location;
    String datesOpen;
    String pollingHours;

    public static Poll fromJsonArray(JSONObject jsonObject) throws JSONException {
        Poll poll = new Poll();
        JSONObject addressInformation = jsonObject.getJSONObject("address");
        poll.location = addressInformation.getString("locationName");
        // TODO: add more information on location
        poll.pollingHours = jsonObject.getString("pollingHours");
        return poll;
    }

    public String getLocation() {
        return location;
    }

    public String getDartesOpen() {
        return datesOpen;
    }

}
