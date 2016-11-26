package ca.mihailistov.lift.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import ca.mihailistov.lift.R;
import ca.mihailistov.lift.realm.RealmExercise;
import ca.mihailistov.lift.realm.RealmExerciseData;
import ca.mihailistov.lift.realm.RealmSet;
import io.realm.Realm;

/**
 * Created by mihai on 2016-11-26.
 */

public class LiftExpandableAdapter extends BaseExpandableListAdapter {

    private Realm realm;
    private final LayoutInflater inf;
    private List<RealmExercise> listExerciseData;
    private HashMap<RealmExercise, List<RealmSet>> listSetData;

    public LiftExpandableAdapter(Context context, List<RealmExercise> listExerciseData,
                                     HashMap<RealmExercise, List<RealmSet>> listSetData){
        realm = Realm.getDefaultInstance();
        this.listExerciseData = listExerciseData;
        this.listSetData = listSetData;
        inf = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount(){
        return this.listExerciseData.size();
    }

    @Override
    public int getChildrenCount(int i){
        return this.listSetData.get(this.listExerciseData.get(i))
                .size();}


    @Override
    public RealmExercise getGroup(int i){
        return this.listExerciseData.get(i);
    }

    @Override
    public RealmSet getChild(int i, int i1){
        return this.listSetData.get(this.listExerciseData.get(i))
                .get(i1);
    }

    @Override
    public long getGroupId(int i){
        return i;
    }

    @Override
    public long getChildId(int i, int i1){
        return i1;
    }

    @Override
    public boolean hasStableIds(){
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        if(view == null){
            view = inf.inflate(R.layout.list_item_exercise, viewGroup, false);

            holder = new ViewHolder();
            holder.text = (TextView) view.findViewById(R.id.exercise_title);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.text.setText(getGroup(i).realmExerciseData.name);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup){

        ViewHolder holder;
        if (view == null){
            view = inf.inflate(R.layout.list_item_set, null);
        }

        TextView childSetNum = (TextView) view.findViewById(R.id.childSetNum);
        TextView childSetReps = (TextView) view.findViewById(R.id.childSetReps);
        TextView childSetWeight = (TextView) view.findViewById(R.id.childSetWeight);
        TextView lbsTv = (TextView) view.findViewById(R.id.lbsTv);
        TextView repsTv = (TextView) view.findViewById(R.id.repsTv);

        RealmSet childSet;
        if (getChild(i, i1) != null) {
            childSet = getChild(i, i1);
            childSetNum.setText(i1);
            childSetWeight.setText(childSet.weight);
            childSetReps.setText(childSet.reps);
        } else {
            childSetNum.setText("+");
            childSetWeight.setText("Click to add set.");
            childSetReps.setVisibility(View.GONE);
            lbsTv.setVisibility(View.GONE);
            repsTv.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1){
        return true;
    }

    private class ViewHolder {
        TextView text;
    }
}