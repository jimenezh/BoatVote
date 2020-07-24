package com.example.voteboat.models;

import com.multilevelview.models.RecyclerViewItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Item extends RecyclerViewItem {
    public static final int TODO = 0;
    public static final int REP = 1;
    public static final int LABEL = 2;

    List<RecyclerViewItem> children;
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
        this.children = new ArrayList<>();
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

    public void addChild(Item child) {
        this.children.add(child);
    }

    public List<RecyclerViewItem> getChildren() {
        return children;
    }

    @Override
    public void addChildren(List<RecyclerViewItem> children) {
        Collections.reverse(children);
        this.children = children;
    }

    @Override
    public boolean hasChildren() {
        return !children.isEmpty();
    }
}
