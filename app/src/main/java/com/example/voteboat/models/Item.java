package com.example.voteboat.models;

import com.multilevelview.models.RecyclerViewItem;

public class Item extends RecyclerViewItem {
    public static final int TODO = 0;
    public static final int REP = 1;
    public static final int LABEL = 2;

    ToDoItem toDoItem;
    Representative representative;
    String label;
    int type;



    public Item(int level, ToDoItem toDoItem) {
        super(level);
        this.type = TODO;
        this.toDoItem = toDoItem;
    }

    public Item(int level, String label) {
        super(level);
        this.type = LABEL;
        this.label = label;
    }

    public Item(int level, Representative representative) {
        super(level);
        this.representative = representative;
        this.type = REP;
    }

    public ToDoItem getToDoItem() {
        return toDoItem;
    }

    public Representative getRepresentative() {
        return representative;
    }

    public String getLabel() {
        return label;
    }

    public int getType() {
        return type;
    }
}
