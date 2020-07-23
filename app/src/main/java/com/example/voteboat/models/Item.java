package com.example.voteboat.models;

import com.multilevelview.models.RecyclerViewItem;

public class Item extends RecyclerViewItem {
    static Enum TODO;
    static Enum REP;
    static Enum LABEL;

    ToDoItem toDoItem;
    Representative representative;
    String label;
    Enum type;



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
}
