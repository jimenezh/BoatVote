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
        race.level = jsonObject.getJSONArray("level").getString(0);
        race.district = jsonObject.getJSONObject("district").getString("name");
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
}
