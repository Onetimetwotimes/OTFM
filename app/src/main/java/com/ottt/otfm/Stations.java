package com.ottt.otfm;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Map;

public class Stations {
    private SharedPreferences stationMap;
    private SharedPreferences.Editor editor;

    public Stations(Context context) {
        if (context.equals(null))
            throw new IllegalArgumentException("Context cannot be null");
        stationMap = context.getSharedPreferences("OTFM",0);
        editor = stationMap.edit();
    }

    public String getStationURL(String key) {
        editor.putString(".latest",stationMap.getString(key,""));
        return stationMap.getString(key,"");
    }

    public void addStation(String key, String url) {
        if(key.equals(null) || key.isEmpty())
            throw new IllegalArgumentException("Key cannot be null or empty!");
        if (stationMap.contains(key))
            throw new InvalidParameterException("Key already exists!");
        if (url.isEmpty() || url.equals(null))
            throw new IllegalArgumentException("URL cannot be null or empty!");

        editor.putString(key,url);
        editor.apply();

    }

    public void setStation(String key, String url) {
        if (url == null || url.isEmpty())
            throw new IllegalArgumentException("URL cannot be null or empty!");
        if(!stationMap.contains(key))
            throw new InvalidParameterException("Station does not exist!");
        editor.putString(key, url);
        editor.apply();
    }

    public void removeStation(String key) {
        if (key.equals(null))
            throw new IllegalArgumentException("Key cannot be null!");

        editor.remove(key);
        editor.apply();


    }

    public ArrayList<String> getKeySet() {
        Map m = stationMap.getAll();
        m.remove(".latest");
        ArrayList<String> al = new ArrayList<>();

        for (Object s: m.keySet().toArray()) {
            al.add((String) s);
        }
        return al;
    }

    public String getLatest() {

        return stationMap.getString(".latest","no_dest");
    }
    public void setLatest(String key) {
        editor.putString(".latest",key);
        editor.apply();
    }
}
