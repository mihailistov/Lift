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
    private List<String> _listDataHeader, listHeaderLoaded; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild, listChildLoaded;
//    String title;
//    String child;
    String listDataChildStr;
    String listDataHeaderStr;
    private Gson gson;
    private SharedPreferences pref;

    public handleListData(List<String> listDataHeader,
                          HashMap<String, List<String>> listChildData){
//        pref = MainActivity.getInstance().getPreferences(Context.MODE_PRIVATE);
//        listDataChildStr = pref.getString("listDataChild", null);
//        listDataHeaderStr = pref.getString("listDataHeader",null);
//
//        gson = new Gson();
//        Type typeChild = new TypeToken<HashMap<String, List<String>>>(){}.getType();
//
//        this._listDataHeader = gson.fromJson(listDataHeaderStr,null);;
//        this._listDataChild = gson.fromJson(listDataChildStr,null);
        this._listDataChild = listChildData;
        this._listDataHeader = listDataHeader;
    }

    public void loadData(){
        listDataHeaderStr = prefManager.getInstance().getPref("listDataHeader", null);
        listDataChildStr = prefManager.getInstance().getPref("listDataChild", null);

        gson = new Gson();
        Type typeHeader = new TypeToken<List<String>>(){}.getType();
        Type typeChild = new TypeToken<HashMap<String, List<String>>>(){}.getType();

        listHeaderLoaded = gson.fromJson(listDataHeaderStr, typeHeader);
        listChildLoaded = gson.fromJson(listDataChildStr, typeChild);

        if (listHeaderLoaded != null) this._listDataHeader = listHeaderLoaded;
        if (listChildLoaded != null) this._listDataChild = listChildLoaded;
    }

//    public List<String> loadHeader(){
////        pref = MainActivity.getInstance().getPreferences(Context.MODE_PRIVATE);
////        listDataHeaderStr = pref.getString("listDataHeader", null);
//
//        listDataHeaderStr = prefManager.getInstance().getPref("listDataHeader", null);
//        gson = new Gson();
//        Type typeHeader = new TypeToken<List<String>>(){}.getType();
//
//        listHeaderLoaded = gson.fromJson(listDataHeaderStr, typeHeader);
//
//        if (listHeaderLoaded != null)
//            this._listDataHeader = listHeaderLoaded;
//
//        return this._listDataHeader;
//    }
//
//    public HashMap<String, List<String>> loadChild(){
////        pref = MainActivity.getInstance().getPreferences(Context.MODE_PRIVATE);
////        listDataChildStr = pref.getString("listDataChild", null);
//
//        listDataChildStr = prefManager.getInstance().getPref("listDataChild", null);
//        gson = new Gson();
//        Type typeChild = new TypeToken<HashMap<String, List<String>>>(){}.getType();
//
//        listChildLoaded = gson.fromJson(listDataChildStr, typeChild);
//
//        if (listChildLoaded != null)
//            this._listDataChild = listChildLoaded;
//
//        return this._listDataChild;
//    }

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

//        pref = MainActivity.getInstance().getPreferences(Context.MODE_PRIVATE);
//        pref.edit().putString("listDataChild", listDataChildStr);
//        pref.edit().putString("listDataHeader",listDataHeaderStr);
//        pref.edit().commit();

        prefManager.getInstance().writePref("listDataChild", listDataChildStr);
        prefManager.getInstance().writePref("listDataHeader", listDataHeaderStr);

    }

    public List<String> returnHeader(){
        return _listDataHeader;
    }

    public HashMap<String, List<String>> returnChildren(){
        return _listDataChild;
    }
}
