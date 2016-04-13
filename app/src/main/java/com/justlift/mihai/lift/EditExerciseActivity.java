package com.justlift.mihai.lift;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditExerciseActivity extends AppCompatActivity {

    int currRepNum = 0;
    int currWeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);

        Intent intent = getIntent();
        int fragmentNum = intent.getIntExtra("fragmentNum", 0);
        int exerciseNum = intent.getIntExtra("exerciseNum", 0);

        final DatabaseHelper myDbHelper;
        myDbHelper = new DatabaseHelper(this);

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

        final List<Integer> setNum = new ArrayList<Integer>();
        final List<Integer> setReps = new ArrayList<Integer>();
        final List<Integer> setWeight = new ArrayList<Integer>();

        myDbHelper.getExerciseSetStats(fragmentNum, exerciseNum, setNum, setReps, setWeight);

        Log.e("EditExerciseActivity", "Number of sets to show: " + setNum.size());
        Log.e("EditExerciseActivity", "Set numbers: \n" + setNum
                + "\nSet reps: \n" + setReps
                + "\nSet weights: \n" + setWeight);

        String styledTitle = "Edit.<font color='#fd5621'>Set " + setNum.get(0) + "</font>";
        setTitle(Html.fromHtml(styledTitle));

        currWeight = setWeight.get(0);
        currRepNum = setReps.get(0);
        displayWeight(currWeight);
        displayReps(currRepNum);

        final int numberOfSets = setNum.size();

        final TableLayout table = (TableLayout) EditExerciseActivity.this.findViewById(R.id.tableLayoutList);

//        View.OnClickListener tableRowOnClick = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int tagValue = (Integer) v.getTag();
//                currWeight = setWeight.get(tagValue);
//                currRepNum = setReps.get(tagValue);
//                displayWeight(currWeight);
//                displayReps(currRepNum);
//
//                Log.e("EditExerciseActivity", "Tag value is: " + tagValue);
//
////                row.setBackgroundColor(Color.rgb(237, 237, 237));
////
////                if (tagValue < numberOfSets - 1) {
////                    divider.setBackgroundColor(Color.rgb(237, 237, 237));
////                }
//
//                String styledTitle = "Edit.<font color='#fd5621'>Set " + setNum.get(tagValue) + "</font>";
//                setTitle(Html.fromHtml(styledTitle));
//            }
//        };

        for(int i=0; i<numberOfSets;i++){
            final TableRow row = (TableRow)LayoutInflater.from(EditExerciseActivity.this).inflate(R.layout.row_layout, null);
            ((TextView)row.findViewById(R.id.setNum)).setText("" + setNum.get(i));
            ((TextView)row.findViewById(R.id.setReps)).setText("" + setReps.get(i));
            ((TextView)row.findViewById(R.id.setWeight)).setText("" + setWeight.get(i));
            row.setTag(Integer.valueOf(i));
            table.addView(row);

//            final int currSetNum = i;
            final TableRow divider = (TableRow) LayoutInflater.from(EditExerciseActivity.this).inflate(R.layout.row_divider, null);

            if (i < numberOfSets-1) {
                table.addView(divider);
            }

//            row.setOnClickListener(tableRowOnClick);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currSetNum = (Integer)v.getTag();
                    currWeight = setWeight.get(currSetNum);
                    currRepNum = setReps.get(currSetNum);
                    displayWeight(currWeight);
                    displayReps(currRepNum);

                    Log.e("EditExerciseActivity", "Tag value is: " + currSetNum);

                    for(int j=0;j<table.getChildCount();j++) {
                        TableRow otherRows = (TableRow) table.getChildAt(j);
                        otherRows.setBackgroundColor(Color.rgb(255,255,255));
                    }

                    row.setBackgroundColor(Color.rgb(237, 237, 237));

                    if (currSetNum < numberOfSets-1)
                    {
                        divider.setBackgroundColor(Color.rgb(237, 237, 237));
                    }

                    String styledTitle = "Edit.<font color='#fd5621'>Set " + setNum.get(currSetNum) + "</font>";
                    setTitle(Html.fromHtml(styledTitle));
                }
            });
        }



    }

    public void increaseWeight(View view) {
        currWeight = currWeight + 5;
        displayWeight(currWeight);

    }public void decreaseWeight(View view) {
        currWeight = currWeight - 5;
        displayWeight(currWeight);
    }

    private void displayWeight(int weight) {
        TextView displayReps = (TextView) findViewById(
                R.id.weightNum);
        displayReps.setText("" + currWeight);
    }

    public void increaseReps(View view) {
        currRepNum = currRepNum + 1;
        displayReps(currRepNum);

    }public void decreaseReps(View view) {
        currRepNum = currRepNum - 1;
        displayReps(currRepNum);
    }

    private void displayReps(int weight) {
        TextView displayReps = (TextView) findViewById(
                R.id.repNum);
        displayReps.setText("" + currRepNum);
    }

}
