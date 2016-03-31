package com.justlift.mihai.lift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mihai on 16-03-30.
 */
public class handleListData {
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
//    String title;
//    String child;

    public handleListData(List<String> listDataHeader,
                          HashMap<String, List<String>> listChildData){
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    public void addEntry(String exerciseTitle, String[] exerciseChild){
        _listDataHeader.add(exerciseTitle);
        List<String> exercise = new ArrayList<String>();

        for (int i=0; i < exerciseChild.length; i++) {
            exercise.add(exerciseChild[i]);
        }

        _listDataChild.put(_listDataHeader.get(_listDataHeader.size()-1), exercise);
    }

    public List<String> returnHeader(){
        return _listDataHeader;
    }

    public HashMap<String, List<String>> returnChildren(){
        return _listDataChild;
    }
}
