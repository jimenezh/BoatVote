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
    List<Race> races;
    List<Poll> earlyPolls;
    List<Poll> electionDayPolls;
    List<Poll> absenteeBallotLocations;
    String registrationUrl;
    String electionInfoUrl;
    String absenteeBallotUrl;
    String electionRulesUrl;
    String ocdId;
    boolean isStarred;


    // Parse Keys
    public static final String KEY_NAME = "name";
    public static final String KEY_GOOGLE_ID = "googleId";
    public static final String KEY_ELECTION_DATE = "electionDay";
    public static final String KEY_OCD_ID = "ocdDivisionId";
    public static final String KEY_HAS_PASSED = "hasPassed";
    public static final String KEY_RACES = "races";
    public static final String KEY_HAS_RACES = "hasRaces";

    public Election() {
        earlyPolls = new ArrayList<>();
        electionDayPolls = new ArrayList<>();
        absenteeBallotLocations = new ArrayList<>();
    }

    public List<Race> getRaces() {
        return races;
    }

    public static Election basicInformationFromJson(JSONObject json) throws JSONException {
        Election election = new Election();
        election.title = json.getString("name");
        election.electionDate = json.getString("electionDay");
        election.googleId = json.getString("id");
        election.ocdId = json.getString("ocdDivisionId");

        Log.i("Election", election.googleId + " is starred:" + election.isStarred);

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
            this.electionDayPolls = Poll.fromJsonArray(jsonObject.getJSONArray("pollingLocations"));
        if (jsonObject.has("earlyVoteSites"))
            this.earlyPolls = Poll.fromJsonArray(jsonObject.getJSONArray("earlyVoteSites"));
        if (jsonObject.has("dropOffLocations"))
            this.absenteeBallotLocations = Poll.fromJsonArray(jsonObject.getJSONArray("dropOffLocations"));

//         This is the info we synchronize with Parse
        if (jsonObject.has("contests")) {
            this.addRaces(jsonObject.getJSONArray("contests"));
            this.saveInBackground(saveCallback);
        }

    }

    public void addRaces(JSONArray jsonArray) throws JSONException{
        final ParseRelation relation = this.getRelation(Election.KEY_RACES);
        for (int i = 0; i < jsonArray.length() ; i++) {
            // Creating race
            final Race r = Race.fromJsonObject(jsonArray.getJSONObject(i));
            // Saving
            r.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e!= null){
                        Log.e(TAG, "Could not save race");
                        return;
                    }
                    relation.add(r);
                }
            });
        }
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

    public List<Poll> getEarlyPolls() {
        return earlyPolls;
    }

    public List<Poll> getElectionDayPolls() {
        return electionDayPolls;
    }

    public List<Poll> getAbsenteeBallotLocations() {
        return absenteeBallotLocations;
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

        final String id = this.getGoogleId();
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
        return getBoolean(KEY_HAS_RACES);
    }
}
