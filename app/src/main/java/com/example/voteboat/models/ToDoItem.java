package com.example.voteboat.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
@ParseClassName("ToDoItem")
public class ToDoItem extends ParseObject {
    public static final String KEY_NAME = "name";
    public ToDoItem() {
    }

    public String getName(){
        return (String) get(KEY_NAME);
    }
}
