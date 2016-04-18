package com.justlift.mihai.lift;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mihai on 16-03-26.
 */
public class FragmentPage extends Fragment {
    View rootView;
    public static ExpandableListView elv;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    int mNum;

    static FragmentPage newInstance(int num) {
        FragmentPage f = new FragmentPage();

        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_one, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        elv = (ExpandableListView) view.findViewById(R.id.expListView);

//        listDataHeader = ElvDataHandler.returnHeader(mNum);
//        listDataChild = ElvDataHandler.returnChildren(mNum);

        final DatabaseHelper myDbHelper;
        myDbHelper = new DatabaseHelper(MainActivity.getInstance());

        try {

            myDbHelper.createDatabase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            myDbHelper.createDatabase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            myDbHelper.openDatabase();

        }catch(SQLException sqle){

            try {
                throw sqle;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        listDataHeader = myDbHelper.getExerciseHeaders(mNum);
        listDataChild = myDbHelper.getExerciseChildren(mNum);
        elv.setAdapter(new ExpandableListAdapter(listDataHeader, listDataChild));

        elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (MainActivity.getEditState()) {

                    Log.e("FragmentPage", "Clicked group #: " + groupPosition);

                    Intent intent = new Intent(MainActivity.getInstance(), EditExerciseActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("fragmentNum", mNum);
                    b.putInt("exerciseNum", groupPosition + 1);
                    intent.putExtras(b);

                    startActivity(intent);

//                    MainActivity.setEditDisabled();

                    MainActivity.cancelAction();

                    if (parent.isGroupExpanded(groupPosition))
                        return true;
                    else
                        return false;
                } else
                    return false;
            }
        });

        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.e("FragmentPage", "Clicked child: " + childPosition + " group: " + groupPosition);
                List<Integer> setNum = new ArrayList<Integer>();
                List<Integer> setReps = new ArrayList<Integer>();
                List<Integer> setWeight = new ArrayList<Integer>();

                myDbHelper.getExerciseStats(mNum, groupPosition+1, setNum, setReps, setWeight);

                if (setNum.size() == 1 && setReps.get(0) == 0 && setWeight.get(0) == 0 || MainActivity.getEditState())
                {
                    Intent intent = new Intent(MainActivity.getInstance(), EditExerciseActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("fragmentNum", mNum);
                    b.putInt("exerciseNum", groupPosition+1);
                    b.putInt("setNumClicked",childPosition+1);
                    intent.putExtras(b);

                    startActivity(intent);

                    if (MainActivity.getEditState())
                        MainActivity.cancelAction();
                } else if (MainActivity.getRemoveState()){

                    final int removeExerciseNum = groupPosition + 1;
                    final int removeSetNum = childPosition;

                    DialogInterface.OnClickListener removeDialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    myDbHelper.removeSet(mNum, removeExerciseNum, removeSetNum);
                                    MainActivity.adapter.notifyDataSetChanged();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
                    builder.setMessage("Are you sure you want to delete?").setPositiveButton("Yes", removeDialogClickListener)
                            .setNegativeButton("No", removeDialogClickListener).show();

                    MainActivity.cancelAction();
                }


                return true;
            }
        });

        elv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                int childPosition = ExpandableListView.getPackedPositionChild(id);
                int itemType = ExpandableListView.getPackedPositionType(id);

                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

                    Log.e("FragmentPage","Long press detected on child item: " + childPosition + " of group: " + groupPosition);
                    Intent intent = new Intent(MainActivity.getInstance(), EditExerciseActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("fragmentNum", mNum);
                    b.putInt("exerciseNum", groupPosition+1);
                    b.putInt("setNumClicked",childPosition+1);
                    intent.putExtras(b);

                    startActivity(intent);

                    // Return true as we are handling the event.
                    return true;
                } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP)
                {
                    Log.e("FragmentPage","Long press detected on group item: " + groupPosition);
                    Intent intent = new Intent(MainActivity.getInstance(), EditExerciseActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("fragmentNum", mNum);
                    b.putInt("exerciseNum", groupPosition+1);
                    intent.putExtras(b);

                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });

        // Move indicator to right
        DisplayMetrics metrics = new DisplayMetrics();
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
        private List<String> _listDataHeader;
        private HashMap<String, List<String>> _listDataChild;

        public ExpandableListAdapter(List<String> listDataHeader,
                                     HashMap<String, List<String>> listChildData){
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
            inf = LayoutInflater.from(getActivity());
        }

        @Override
        public int getGroupCount(){
            return this._listDataHeader.size();
        }

        @Override
        public int getChildrenCount(int i){
            return this._listDataChild.get(this._listDataHeader.get(i))
                    .size();}


        @Override
        public Object getGroup(int i){
            return this._listDataHeader.get(i);
        }

        @Override
        public Object getChild(int i, int i1){
            return this._listDataChild.get(this._listDataHeader.get(i))
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
                view = inf.inflate(R.layout.group_view, viewGroup, false);

                holder = new ViewHolder();
                holder.text = (TextView) view.findViewById(R.id.lblListHeader);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.text.setText(getGroup(i).toString());
            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup){

            ViewHolder holder;
            if (view == null){
//                holder = new ViewHolder();
//                view = inf.inflate(R.layout.child_view, viewGroup, false);
                view = inf.inflate(R.layout.child_view, null);

//                holder.text = (TextView) view.findViewById(R.id.lblListItem);
//                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            String childString = getChild(i, i1).toString();
            String[] seperated = childString.split(":");
            String setNum = seperated[0];
            String repNum = seperated[1];
            String weightNum = seperated[2];
//            Log.e("FragmentPage","Children, repNum: " + repNum + ", weightNum: " + weightNum);

            TextView tv = (TextView) view.findViewById(R.id.childSetNum);
            TextView ltv = (TextView) view.findViewById(R.id.childSetReps);
            TextView ktv = (TextView) view.findViewById(R.id.childSetWeight);
            TextView lbsTv = (TextView) view.findViewById(R.id.lbsTv);
            TextView repsTv = (TextView) view.findViewById(R.id.repsTv);

            if (!repNum.equals("0") && !weightNum.equals("0")){
                tv.setVisibility(View.VISIBLE);
                ltv.setVisibility(View.VISIBLE);
                ktv.setVisibility(View.VISIBLE);
                lbsTv.setVisibility(View.VISIBLE);
                repsTv.setVisibility(View.VISIBLE);

                tv.setText(setNum);
                ltv.setText(repNum);
                ktv.setText(weightNum);
            } else {
                tv.setVisibility(View.GONE);
                ltv.setVisibility(View.GONE);
                lbsTv.setVisibility(View.GONE);
                repsTv.setVisibility(View.GONE);

                ktv.setText("Click to add sets");
            }
//            holder.text.setText(getChild(i, i1).toString());
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

}