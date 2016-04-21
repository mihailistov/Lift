package com.justlift.mihai.lift;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mihai on 16-03-26.
 */
public class FragmentAdd extends ListFragment {
    private DatabaseHelper myDbHelper;
    private ListView addList;
    String[] list_items;
    View rootView;
    int mNum;

    static FragmentAdd newInstance(int num) {
        FragmentAdd f = new FragmentAdd();

        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;

        myDbHelper = DatabaseHelper.getInstance(MainActivity.getInstance());

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add, container, false);
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        new CreateArrayListTask().execute();
    }

    private class CreateArrayListTask extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... params) {
//            List<String> linkArray = myDbHelper.getExerciseHeaders(mNum+9);
            List<String> linkArray = new ArrayList<String>();;
            linkArray.add("KB Overhead Lunge");
            linkArray.add("KB Lunge");
            linkArray.add("Barbell Front Squat");
            linkArray.add("KB One-Legged Deadlift");
            linkArray.add("Barbell Squat");
            linkArray.add("Stiff-Legged Deadlift");
            linkArray.add("Barbell Lunge");
            linkArray.add("KB Swing");
            linkArray.add("KB Forward/Backward Travelling Swing");
            linkArray.add("KB Goblet Squat");
            linkArray.add("KB Side Travelling Swing");
            linkArray.add("Barbell Lunge");
            linkArray.add("KB Deadlift");
            linkArray.add("Standing Calf Raises");
            linkArray.add("KB Overhead Lunge");
            linkArray.add("KB Lunge");
            linkArray.add("Barbell Front Squat");
            linkArray.add("KB One-Legged Deadlift");
            linkArray.add("Barbell Squat");
            linkArray.add("Stiff-Legged Deadlift");
            linkArray.add("Barbell Lunge");
            linkArray.add("KB Swing");
            linkArray.add("KB Forward/Backward Travelling Swing");
            linkArray.add("KB Goblet Squat");
            linkArray.add("KB Side Travelling Swing");
            linkArray.add("Barbell Lunge");
            linkArray.add("KB Deadlift");
            linkArray.add("Standing Calf Raises");
            return linkArray;
        }

        protected void onPostExecute(List<String> linkArray) {
            ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, linkArray);
            setListAdapter(a);
        }
    }

}