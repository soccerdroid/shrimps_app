package com.example.belen.shrimps;

import org.json.JSONException;
import org.json.JSONObject;

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

    public String stringify() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", this.name);

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static Thumbnail restore(String jsonObject){
        try {
            JSONObject obj = new JSONObject(jsonObject);
            Thumbnail thumbnail = new Thumbnail(obj.getString("name"));
            return thumbnail;
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return new Thumbnail("null");
    }

}
