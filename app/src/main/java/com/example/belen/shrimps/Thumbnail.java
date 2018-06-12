package com.example.belen.shrimps;

import java.util.ArrayList;

public class Thumbnail {

    String name;
    ArrayList<ArrayList<Integer>> tags;

    public Thumbnail(String name) {
        this.name = name;
        this.tags = new ArrayList<ArrayList<Integer>>();
    }

    public ArrayList<ArrayList<Integer>> getTags() { return tags; }

    public void setTags(ArrayList<ArrayList<Integer>> tags) { this.tags = tags; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
