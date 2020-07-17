package com.example.voteboat.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
@ParseClassName("ToDoItem")
public class ToDoItem extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_VOTED = "hasVoted";
    public static final String KEY_HAS_REGISTERED = "hasRegistered";
    public static final String KEY_GATHERED_DOCS = "hasGatheredDocuments";
    public ToDoItem() {
    }

    public String getName(){
        return (String) get(KEY_NAME);
    }

    boolean hasVoted(){
        return (boolean) get(KEY_VOTED);
    }

    boolean isRegistered(){
        return  (boolean) get(KEY_HAS_REGISTERED);
    }

    boolean hasDocuments(){
        return  (boolean) get(KEY_GATHERED_DOCS);
    }
}
