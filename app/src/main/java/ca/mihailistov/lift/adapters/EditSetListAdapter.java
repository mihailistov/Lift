package ca.mihailistov.lift.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import ca.mihailistov.lift.R;
import ca.mihailistov.lift.realm.RealmSet;
import io.realm.RealmList;

/**
 * Created by mihai on 2017-02-03.
 */

public class EditSetListAdapter extends ArrayAdapter<RealmSet> {

    RealmList<RealmSet> realmSetList;

    public EditSetListAdapter(Context context, RealmList<RealmSet> realmSetList) {
        super(context, 0, realmSetList);
        this.realmSetList = realmSetList;
        LayoutInflater inflater = LayoutInflater.from(getContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RealmSet realmSet = realmSetList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_edit_set, parent, false);
        }

        TextView setNumText = (TextView) convertView.findViewById(R.id.childSetNum);
        TextView setWeightText = (TextView) convertView.findViewById(R.id.childSetWeight);
        TextView setRepsText = (TextView) convertView.findViewById(R.id.childSetReps);

        setNumText.setText(String.valueOf(position+1));
        setWeightText.setText(String.valueOf(realmSet.weight));
        setRepsText.setText(String.valueOf(realmSet.reps));

        return convertView;
    }
}
