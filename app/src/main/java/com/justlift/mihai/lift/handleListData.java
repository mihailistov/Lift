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
public class handleListData {
    private List<String> _listDataHeader, listHeaderLoaded;
    private HashMap<String, List<String>> _listDataChild, listChildLoaded;
    String listDataChildStr;
    String listDataHeaderStr;
    String headerKey;
    String childKey;
    private Gson gson;
    private SharedPreferences pref;

    public handleListData(int fragmentNum, List<String> listDataHeader,
                          HashMap<String, List<String>> listChildData){
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;

        // create unique key for shared pref based on fragmentNum
        this.headerKey = "listDataHeader" + fragmentNum;
        this.childKey = "listDataChild" + fragmentNum;
    }

    public void loadData(){
        listDataHeaderStr = prefManager.getInstance().getPref(headerKey, null);
        listDataChildStr = prefManager.getInstance().getPref(childKey, null);

        gson = new Gson();
        Type typeHeader = new TypeToken<List<String>>(){}.getType();
        Type typeChild = new TypeToken<HashMap<String, List<String>>>(){}.getType();

        listHeaderLoaded = gson.fromJson(listDataHeaderStr, typeHeader);
        listChildLoaded = gson.fromJson(listDataChildStr, typeChild);

        if (listHeaderLoaded != null) this._listDataHeader = listHeaderLoaded;
        if (listChildLoaded != null) this._listDataChild = listChildLoaded;
    }

    public void addEntry(String exerciseTitle, String[] exerciseChild){
        _listDataHeader.add(exerciseTitle);
        List<String> exercise = new ArrayList<String>();

        for (int i=0; i < exerciseChild.length; i++) {
            exercise.add(exerciseChild[i]);
        }

        _listDataChild.put(_listDataHeader.get(_listDataHeader.size()-1), exercise);

        gson = new Gson();
        String listDataChildStr = gson.toJson(_listDataChild);
        String listDataHeaderStr = gson.toJson(_listDataHeader);

        prefManager.getInstance().writePref(childKey, listDataChildStr);
        prefManager.getInstance().writePref(headerKey, listDataHeaderStr);

    }

    public List<String> returnHeader(){
        return _listDataHeader;
    }

    public HashMap<String, List<String>> returnChildren(){
        return _listDataChild;
    }
}
