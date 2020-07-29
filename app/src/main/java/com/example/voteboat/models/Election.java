package com.example.voteboat.models;


import android.util.Log;

import androidx.annotation.Nullable;

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

@ParseClassName("Election")
public class Election extends ParseObject {

    public static final String TAG = "Election";

    String title;
    String googleId;
    String electionDate;
    Poll electionDayPoll;
    String registrationUrl;
    String electionInfoUrl;
    String absenteeBallotUrl;
    String electionRulesUrl;
    String ocdId;

    // Parse Keys
    public static final String KEY_NAME = "name";
    public static final String KEY_GOOGLE_ID = "googleId";
    public static final String KEY_ELECTION_DATE = "electionDay";
    public static final String KEY_OCD_ID = "ocdDivisionId";
    public static final String KEY_HAS_PASSED = "hasPassed";
    public static final String KEY_RACES = "races";
    public static final String KEY_HAS_RACES = "hasRaces";
    public static final String KEY_HAS_DETAILS = "hasDetails";

    public ParseRelation<Race> getRaces() {
        return getRelation(KEY_RACES);
    }

    public static Election basicInformationFromJson(JSONObject json) throws JSONException {
        Election election = new Election();
        election.title = json.getString("name");
        election.electionDate = json.getString("electionDay");
        election.googleId = json.getString("id");
        election.ocdId = json.getString("ocdDivisionId");

        Log.i(TAG, "Election " + election.googleId);

        return election;
    }

    public void addDetails(JSONObject jsonObject, SaveCallback saveCallback) throws JSONException {
        JSONObject state = jsonObject
                .getJSONArray("state")
                .getJSONObject(0)
                .getJSONObject("electionAdministrationBody");
        // Adding this when online
        this.registrationUrl = checkifExistsAndAdd("electionRegistrationUrl", state);
        this.electionInfoUrl = checkifExistsAndAdd("electionInfoUrl", state);
        this.absenteeBallotUrl = checkifExistsAndAdd("absenteeVotingInfoUrl", state);
        this.electionRulesUrl = checkifExistsAndAdd("electionRulesUrl", state);
        if (jsonObject.has("pollingLocations"))
            this.electionDayPoll = Poll.fromJsonArray(jsonObject.getJSONArray("pollingLocations"));
//         This is the info we synchronize with Parse
        if (jsonObject.has("contests")) {
            this.addRaces(jsonObject.getJSONArray("contests"));

        } else {
            Log.i(TAG, "No elections for " + this.getGoogleId());
        }


        // Saving once more time
        this.saveInBackground(saveCallback);


    }

    public void addRaces(JSONArray jsonArray) throws JSONException {

        final Election election = this;
        final ParseRelation relation = election.getRelation(Election.KEY_RACES);
        final List<Race> races = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            // Creating race
            final Race r = Race.fromJsonObject(jsonArray.getJSONObject(i));
            // Saving
            races.add(r);
        }

        ParseObject.saveAllInBackground(races, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if( e !=null) {
                    Log.e(TAG, "Could not save races", e);
                    return;
                }
                for(Race r: races)
                    relation.add(r);
                election.put(KEY_HAS_RACES, true); // Marking as true
                election.put(KEY_HAS_DETAILS, true);
                election.saveInBackground();
                Log.i(TAG, "Finished saving "+election.getGoogleId());
            }
        });
    }

    private static String checkifExistsAndAdd(String field, JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(field))
            return jsonObject.getString(field);
        else
            return "N/A";
    }

    public String getOcdId() {
        if (ocdId == null)
            return getString(KEY_OCD_ID);
        return ocdId;
    }

    public String getTitle() {
        if (title == null)
            return getString(KEY_NAME);
        return title;
    }

    public String getGoogleId() {
        if (googleId == null) {
            return getString(KEY_GOOGLE_ID);
        }
        return googleId;
    }

    public String getElectionDate() {
        if (electionDate == null) {
            return getString(KEY_ELECTION_DATE);
        }
        return electionDate;
    }

    public Poll getElectionDayPoll() {
        return electionDayPoll;
    }

    public String getRegistrationUrl() {
        return registrationUrl;
    }

    public String getElectionInfoUrl() {
        return electionInfoUrl;
    }

    public String getAbsenteeBallotUrl() {
        return absenteeBallotUrl;
    }

    public String getElectionRulesUrl() {
        return electionRulesUrl;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj.getClass() != Election.class)
            return false;
        Election otherElection = (Election) obj;
        return otherElection.getGoogleId().equals(this.getGoogleId());
    }

    public void putInParse(SaveCallback saveCallback) {
        put(KEY_NAME, this.getTitle());
        put(KEY_GOOGLE_ID, this.getGoogleId());
        put(KEY_ELECTION_DATE, this.getElectionDate());
        put(KEY_OCD_ID, this.getOcdId());

        this.saveInBackground(saveCallback);
    }


    public void setElectionHasPassed() {
        put(KEY_HAS_PASSED, true);
        saveInBackground();
    }

    public boolean getHasPassed() {
        return getBoolean(KEY_HAS_PASSED);
    }

    public boolean hasDetails() {
        return getBoolean(KEY_HAS_DETAILS);
    }

    public boolean hasRaces() {
        return getBoolean(KEY_HAS_RACES);
    }
}
