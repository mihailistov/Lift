package com.justlift.mihai.lift;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mihai on 16-03-30.
 */
public class ElvDataHandler {
    private static List<String> _listDataHeader, listHeaderLoaded;
    private static HashMap<String, List<String>> _listDataChild, listChildLoaded;
    static String listDataChildStr;
    static String listDataHeaderStr;
    static String headerKey;
    static String childKey;
    private static Gson gson;
    private SharedPreferences pref;

    public ElvDataHandler(){

    }

//    public void loadData(){
//        listDataHeaderStr = prefManager.getInstance().getPref(headerKey, null);
//        listDataChildStr = prefManager.getInstance().getPref(childKey, null);
//
//        gson = new Gson();
//        Type typeHeader = new TypeToken<List<String>>(){}.getType();
//        Type typeChild = new TypeToken<HashMap<String, List<String>>>(){}.getType();
//
//        listHeaderLoaded = gson.fromJson(listDataHeaderStr, typeHeader);
//        listChildLoaded = gson.fromJson(listDataChildStr, typeChild);
//
//        if (listHeaderLoaded != null) this._listDataHeader = listHeaderLoaded;
//        if (listChildLoaded != null) this._listDataChild = listChildLoaded;
//    }

    public static void addEntry(int fragmentNum, String exerciseTitle, String[] exerciseChild){
        headerKey = "listDataHeader" + fragmentNum;
        childKey = "listDataChild" + fragmentNum;

        listDataHeaderStr = PrefManager.getInstance().getPref(headerKey, null);
        listDataChildStr = PrefManager.getInstance().getPref(childKey, null);

        gson = new Gson();
        Type typeHeader = new TypeToken<List<String>>(){}.getType();
        Type typeChild = new TypeToken<HashMap<String, List<String>>>(){}.getType();

        listHeaderLoaded = gson.fromJson(listDataHeaderStr, typeHeader);
        listChildLoaded = gson.fromJson(listDataChildStr, typeChild);

        if (listHeaderLoaded != null) {
            _listDataHeader = listHeaderLoaded;
        } else _listDataHeader = new ArrayList<String>();

        if (listChildLoaded != null) {
            _listDataChild = listChildLoaded;
        } else _listDataChild = new HashMap<String, List<String>>();

        _listDataHeader.add(exerciseTitle);
        List<String> exercise = new ArrayList<String>();

        for (int i=0; i < exerciseChild.length; i++) {
            exercise.add(exerciseChild[i]);
        }

        _listDataChild.put(_listDataHeader.get(_listDataHeader.size()-1), exercise);

        listDataChildStr = gson.toJson(_listDataChild);
        listDataHeaderStr = gson.toJson(_listDataHeader);

        PrefManager.getInstance().writePref(childKey, listDataChildStr);
        PrefManager.getInstance().writePref(headerKey, listDataHeaderStr);

    }

    public static List<String> returnHeader(int fragmentNum){
        headerKey = "listDataHeader" + fragmentNum;

        listDataHeaderStr = PrefManager.getInstance().getPref(headerKey, null);

        gson = new Gson();
        Type typeHeader = new TypeToken<List<String>>(){}.getType();
        listHeaderLoaded = gson.fromJson(listDataHeaderStr, typeHeader);

        if (listHeaderLoaded != null) {
            _listDataHeader = listHeaderLoaded;
            return _listDataHeader;
        } else {
            return new ArrayList<String>();
        }
    }

    public static HashMap<String, List<String>> returnChildren(int fragmentNum){
        childKey = "listDataChild" + fragmentNum;
        listDataChildStr = PrefManager.getInstance().getPref(childKey, null);

        gson = new Gson();
        Type typeChild = new TypeToken<HashMap<String, List<String>>>(){}.getType();
        listChildLoaded = gson.fromJson(listDataChildStr, typeChild);

        if (listChildLoaded != null) {
            _listDataChild = listChildLoaded;
            return _listDataChild;
        } else {
            return new HashMap<String, List<String>>();
        }
    }
}
