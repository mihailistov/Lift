package com.justlift.mihai.lift;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

/**
 * Created by mihai on 16-03-26.
 */
public class FourFragment extends Fragment {
    View rootView;
    ExpandableListView elv;
    private String[] groups;
    private String[][] children;
//    String[] list_items;

    public FourFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ((MainActivity) getActivity()).setActionBarTitle("Lift.Legs");
        groups = new String[] {"Bench Press","Push-ups","Cardio","Russian KB Twist","Swiss Ball Crunch","Hanging Leg Raise"};
        children = new String[][] {
                {"Set 1: 20x45", "Set 2: 12x95", "Set 3: 8x135", "Set 4: 8x135", "Set 5: 8x135"},
                {"Set 1: 20xBW", "Set 2: 20xBW", "Set 3: 20xBW"},
                {"Interval 1: 4 x 1/4 mile", "Interval 2: 4 x 1/2 mile", "Interval 3: 4 x 1 mile"},
                {"Set 1: 10 reps", "Set 2: 20 reps", "Set 3: 20 reps", "Set 4: 20 reps", "Set 5: AMRAP"},
                {"Set 1: 10 reps", "Set 2: 20 reps", "Set 3: 20 reps", "Set 4: 20 reps", "Set 5: AMRAP"},
                {"Set 1: 10 reps", "Set 2: 20 reps", "Set 3: 20 reps", "Set 4: 20 reps", "Set 5: AMRAP"},
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Lift.Legs");
//        ((MainActivity) getActivity()).setActionBarTitle("Lift.Legs");
//        list_items = getResources().getStringArray(R.array.list);
//        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list_items));
        rootView = inflater.inflate(R.layout.fragment_one, container, false);
//        v.setPadding(20,20,20,20);
//        ExpandableListView elv = (ExpandableListView) v.findViewById(R.id.list);
//        elv.setAdapter(new SavedTabsListAdapter());

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        elv = (ExpandableListView) view.findViewById(R.id.expListView);
        elv.setAdapter(new ExpandableListAdapter(groups, children));
//        elv.setGroupIndicator(null);

        // Move indicator to right
        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            elv.setIndicatorBounds(width - GetPixelFromDips(50), width - GetPixelFromDips(10));
        } else {
            elv.setIndicatorBoundsRelative(width - GetPixelFromDips(50), width - GetPixelFromDips(10));
        }
    }

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter{

        private final LayoutInflater inf;
        private String[] groups;
        private String[][] children;

        public ExpandableListAdapter(String[] groups, String[][] children){
            this.groups = groups;
            this.children = children;
            inf = LayoutInflater.from(getActivity());
        }

        @Override
        public int getGroupCount(){
            return groups.length;
        }

        @Override
        public int getChildrenCount(int i){
            return children[i].length;}


        @Override
        public Object getGroup(int i){
            return groups[i];
        }

        @Override
        public Object getChild(int i, int i1){
            return children[i][i1];
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
                view = inf.inflate(R.layout.group_view, viewGroup, false);

                holder = new ViewHolder();
                holder.text = (TextView) view.findViewById(R.id.lblListHeader);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }


            holder.text.setText(getGroup(i).toString());
            return view;

//            TextView textView = new TextView(OneFragment.this.getActivity());
//            textView.setText(getGroup(i).toString());
//            return textView;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup){

            ViewHolder holder;
            if (view == null){
                holder = new ViewHolder();
                view = inf.inflate(R.layout.child_view, viewGroup, false);

                holder.text = (TextView) view.findViewById(R.id.lblListItem);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.text.setText(getChild(i, i1).toString());
            return view;

//            TextView textView = new TextView(OneFragment.this.getActivity());
//            textView.setText(getChild(i, i1).toString());
//            return textView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1){
            return true;
        }

        private class ViewHolder {
            TextView text;
        }
    }

}