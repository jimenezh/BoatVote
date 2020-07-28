package com.example.voteboat.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
@ParseClassName("Race")
public class Race extends ParseObject {
    String office;

    List<Candidate> candidates;

    public static final String KEY_OFFICE="office";
    public static final String KEY_LEVEL="level";
    public static final String KEY_DISTRICT= "district";
    public static final String KEY_CANDIDATES="candidates";

    public static final String TAG = "Race";


    public static Race fromJsonObject(JSONObject jsonObject) throws JSONException {
        Race race = new Race();
        if(jsonObject.has("office"))
            race.put(KEY_OFFICE, jsonObject.getString("office"));
//        if(jsonObject.has("candidates"))
//            race.candidates = Candidate.fromJsonArray(jsonObject.getJSONArray("candidates"));
        return  race;
    }



    public String getOffice() {
        return office;
    }


    public List<Candidate> getCandidates() {
        return candidates;
    }

    public boolean hasCandidates() {
        return getCandidates() != null;
    }
}
