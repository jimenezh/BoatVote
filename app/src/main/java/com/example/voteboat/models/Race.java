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

    public static final String KEY_OFFICE = "office";
    public static final String KEY_CANDIDATES = "candidates";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_SCORE = "score";

    public static final String TAG = "Race";


    public static Race fromJsonObject(JSONObject jsonObject) throws JSONException {

        Race race = new Race();
        if (jsonObject.has("office"))
            race.put(KEY_OFFICE, jsonObject.getString("office"));
        if (jsonObject.has("level"))
            race.put(KEY_LEVEL, jsonObject.getString("level"));
        if (jsonObject.has("candidates")) {
            race.addCandidates(jsonObject.getJSONArray("candidates"));
        }

        race.calculateScore(jsonObject);
        race.saveInBackground();

        return race;
    }

    private void calculateScore(JSONObject jsonObject) throws JSONException {
        int score = this.getInt(KEY_SCORE);

        if (jsonObject.has("level")) {
            JSONArray array = jsonObject.getJSONArray("level");
            for (int i = 0; i < array.length(); i++) {
                score += levelValue(array.getString(i));
            }
        }

        if (jsonObject.has("district")) {
            if (((JSONObject) jsonObject.get("district")).has("scope")) {
                String scope = ((JSONObject) jsonObject.get("district")).getString("scope");
                score += scopeValue(scope);
            }
        }

        // Update in parse
        put(KEY_SCORE, score);

    }

    private static int scopeValue(String scope) {
        switch (scope) {
            case "congressional":
            case "judicial":
            case "national":
                return 8;
            case "stateLower":
            case "stateUpper":
            case "statewide":
                return 6;
            case "cityCouncil":
            case "citywide":
                return 5;
            case "countyCouncil":
            case "countywide":
                return 4;
            case "township":
            case "ward":
                return 2;
            case "special":
            case "schoolBoard":
                return 1;
            default:
                return 0;
        }
    }

    private static int levelValue(String level) {
        switch (level) {
            case "country":
                return 5;
            case "administrativeArea1":
            case "administrativeArea2":
            case "regional":
                return 4;
            case "locality":
                return 3;
            case "subLocality1":
            case "subLocality2":
                return 2;
            case "special":
                return 1;
            default:
                return 0;
        }
    }

    public void addCandidates(JSONArray jsonArray) throws JSONException {
        Log.i(TAG, this + " has " + jsonArray.length() + " races");
        final Race race = this;
        final ParseRelation<Candidate> relation = this.getRelation(KEY_CANDIDATES);

        final List<Candidate> candidates = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            // Create
            Candidate c = Candidate.fromJsonObject(jsonArray.getJSONObject(i));
            candidates.add(c); // This will be used to save all candidates
        }

        ParseObject.saveAllInBackground(candidates, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not save candidates");
                    return;
                }
                for(Candidate c : candidates)
                    relation.add(c);

                race.saveInBackground();
                race.addToScore(candidates); // Adding to race score

            }
        });

    }

    private void addToScore(List<Candidate> candidates) {
        int score = this.getInt("score");
        List<String> parties = new ArrayList<>();
        for (int i = 0; i < candidates.size(); i++) {
            Candidate candidate = candidates.get(i);
            if (!candidate.getWebsiteUrl().isEmpty()) {
                score += 5;
            }

            if (!parties.contains(candidate.getParty())) {
                score += 5;
            }

        }
        this.put("score", score);

    }

    public String getOffice() {
        return getString(KEY_OFFICE);
    }

    public String getLevel() {
        return getString(KEY_LEVEL);
    }

    public ParseRelation<Candidate> getCandidates() {
        return getRelation(KEY_CANDIDATES);
    }
}
