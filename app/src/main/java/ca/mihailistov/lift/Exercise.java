package ca.mihailistov.lift;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.List;

/**
 * Created by mihai on 16-09-05.
 */
public class Exercise implements ParentObject {
    private List<Object> mSetList;
    private String name;

    public Exercise(String name){
        this.name = name;
    }

    @Override
    public List<Object> getChildObjectList() {
        return mSetList;
    }

    @Override
    public void setChildObjectList(List<Object> list) {
        mSetList = list;
    }

    public String getTitle(){
        return name;
    }
}
