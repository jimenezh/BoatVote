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

    public static final String KEY_OFFICE = "office";
    public static final String KEY_CANDIDATES = "candidates";

    public static final String TAG = "Race";


    public static Race fromJsonObject(JSONObject jsonObject) throws JSONException {
        Race race = new Race();
        if (jsonObject.has("office"))
            race.put(KEY_OFFICE, jsonObject.getString("office"));
        if (jsonObject.has("candidates")) {
            race.addCandidates(jsonObject.getJSONArray("candidates"));
        }

        return race;
    }

    public void addCandidates(JSONArray jsonArray) throws JSONException {
        Log.i(TAG, this.office+" has "+jsonArray.length()+" races");
        final Race race = this;
        final ParseRelation<Candidate> relation = this.getRelation(KEY_CANDIDATES);
        for (int i = 0; i < jsonArray.length(); i++) {
            // Create
            final Candidate c = Candidate.fromJsonObject(jsonArray.getJSONObject(i));
            c.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Could not save candidates");
                        return;
                    }
                    relation.add(c);
                    race.saveInBackground();
                }
            });
        }

    }


    public String getOffice() {
        return getString(KEY_OFFICE);
    }


    public ParseRelation<Candidate> getCandidates() {
        return getRelation(KEY_CANDIDATES);
    }

    public boolean hasCandidates() {
        return getCandidates() != null;
    }
}
