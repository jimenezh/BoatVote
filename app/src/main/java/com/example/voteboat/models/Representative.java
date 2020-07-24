package com.example.voteboat.models;

import com.multilevelview.models.RecyclerViewItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Representative {
    String name;
    String party;
    String phoneNumber;
    String email;
    String url;

    public static List<RecyclerViewItem> fromJSONArray(JSONObject jsonObject) throws JSONException {
        List<RecyclerViewItem> representatives = new ArrayList<>();

        JSONArray array = jsonObject.getJSONArray("officials");
        for(int i = 0; i < array.length(); i++){
            representatives.add(Representative.fromJson(array.getJSONObject(i)));
        }
        return representatives;
    }

    private static RecyclerViewItem fromJson(JSONObject jsonObject) throws JSONException {
        Representative representative = new Representative();
        representative.name = jsonObject.getString("name");
        if(jsonObject.has("phones"))
            representative.phoneNumber = jsonObject.getJSONArray("phones").getString(0);
        representative.party = jsonObject.getString("party");
        if(jsonObject.has("emails"))
            representative.email = jsonObject.getJSONArray("emails").getString(0);
        if(jsonObject.has("urls"))
            representative.url = jsonObject.getJSONArray("urls").getString(0);
        return new Item(1, representative);
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getParty() {
        return party;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }
}
