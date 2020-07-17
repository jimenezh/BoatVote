package com.example.voteboat.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

@ParseClassName("ToDoItem")
public class ToDoItem extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_VOTED = "hasVoted";
    public static final String KEY_HAS_REGISTERED = "isRegistered";
    public static final String KEY_GATHERED_DOCS = "hasGatheredDocuments";
    public static final String TAG = "ToDoItem";
    public ToDoItem() {
    }

    public String getName(){
        return (String) get(KEY_NAME);
    }

    public boolean hasVoted(){
        return (boolean) get(KEY_VOTED);
    }

    public boolean isRegistered(){
        return  (boolean) get(KEY_HAS_REGISTERED);
    }

    public boolean hasDocuments(){
        return  (boolean) get(KEY_GATHERED_DOCS);
    }

    public void setRegistered(boolean isRegistered){
        put(KEY_HAS_REGISTERED, isRegistered);
        saveField(KEY_HAS_REGISTERED);
    }

    public void setDocuments(boolean hasDocuments) {
        put(KEY_GATHERED_DOCS, hasDocuments);
        saveField(KEY_GATHERED_DOCS);
    }

    public void setVoted(boolean hasVoted) {
        put(KEY_VOTED, hasVoted);
        saveField(KEY_VOTED);
    }

    private void saveField(final String field) {
        this.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null)
                    Log.e(TAG, "Error setting "+field);
                else
                    Log.i(TAG, "Saved "+field);
            }
        });
    }
}
