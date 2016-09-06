package ca.mihailistov.lift;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

/**
 * Created by mihai on 16-09-05.
 */
public class Exercise implements ParentListItem {
    private List<Object> mSetList;
    private String name;

    public Exercise(String name){
        this.name = name;
    }

    public void setChildObjectList(List<Object> list) {
        mSetList = list;
    }

    public String getTitle(){
        return name;
    }

    @Override
    public List<Object> getChildItemList() {
        return mSetList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
