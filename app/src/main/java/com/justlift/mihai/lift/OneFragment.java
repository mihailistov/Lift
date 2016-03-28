package com.justlift.mihai.lift;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

/**
 * Created by mihai on 16-03-26.
 */
public class OneFragment extends Fragment {
//    String[] list_items;

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ((MainActivity) getActivity()).setActionBarTitle("Lift.Legs");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Lift.Legs");
//        ((MainActivity) getActivity()).setActionBarTitle("Lift.Legs");
//        list_items = getResources().getStringArray(R.array.list);
//        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list_items));
        View v = inflater.inflate(R.layout.fragment_one, container, false);
//        v.setPadding(20,20,20,20);
        ExpandableListView elv = (ExpandableListView) v.findViewById(R.id.list);
        elv.setAdapter(new SavedTabsListAdapter());

        // Inflate the layout for this fragment
        return v;
    }

    public class SavedTabsListAdapter extends BaseExpandableListAdapter{
        private String[] groups = {"Barbell Squat","Stiff-Legged Deadlift","Barbell Lunge","KB Swing"};
        private String[][] children = {
                {"Set 1: 10x45", "Set 2: 8x95", "Set 3: 8x135", "Set 4: 6x185", "Set 5: 5x225"},
                {"Set 1: 10x45", "Set 2: 8x95", "Set 3: 8x135", "Set 4: 6x185", "Set 5: 5x225"},
                {"Set 1: 10x45", "Set 2: 8x95", "Set 3: 8x135", "Set 4: 6x185", "Set 5: 5x225"},
                {"Set 1: 10x45", "Set 2: 8x95", "Set 3: 8x135", "Set 4: 6x185", "Set 5: 5x225"},
        };

        @Override
        public int getGroupCount(){return groups.length;}

        @Override
        public int getChildrenCount(int i){return children[i].length;}

        @Override
        public Object getGroup(int i){return groups[i];}

        @Override
        public Object getChild(int i, int i1){return children[i][i1];}

        @Override
        public long getGroupId(int i){return i;}

        @Override
        public long getChildId(int i, int i1){return i1;}

        @Override
        public boolean hasStableIds(){return true;}

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(OneFragment.this.getActivity());
            textView.setText(getGroup(i).toString());
            return textView;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup){
            TextView textView = new TextView(OneFragment.this.getActivity());
            textView.setText(getChild(i, i1).toString());
            return textView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1){
            return true;
        }
    }

}