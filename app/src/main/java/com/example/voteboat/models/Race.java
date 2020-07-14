package com.example.voteboat.models;

import com.example.voteboat.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Race {
    String office;
    String level;
    String district;
    List<Candidate> candidates;

    public static Race fromJsonObject(JSONObject jsonObject) throws JSONException {
        Race race = new Race();
        race.office = jsonObject.getString("office");
        if(jsonObject.has("level"))
            race.level = jsonObject.getJSONArray("level").getString(0);
        if(jsonObject.has("district"))
            race.district = jsonObject.getJSONObject("district").getString("name");
        if(jsonObject.has("candidates"))
            race.candidates = Candidate.fromJsonArray(jsonObject.getJSONArray("candidates"));


        return  race;
    }

    public static List<Race> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Race> races = new ArrayList<>();
        for (int i = 0; i < jsonArray.length() ; i++) {
            races.add(Race.fromJsonObject(jsonArray.getJSONObject(i)));
        }
        return races;
    }

    public String getOffice() {
        return office;
    }

    public String getLevel() {
        return level;
    }

    public String getDistrict() {
        return district;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }
}
