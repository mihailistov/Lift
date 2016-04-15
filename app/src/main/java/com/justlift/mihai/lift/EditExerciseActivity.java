package com.justlift.mihai.lift;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    int currSetNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);

        Intent intent = getIntent();
        final int fragmentNum = intent.getIntExtra("fragmentNum", 0);
        final int exerciseNum = intent.getIntExtra("exerciseNum", 0);

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

        myDbHelper.getExerciseStats(fragmentNum, exerciseNum, setNum, setReps, setWeight);

        Log.e("EditExerciseActivity", "Number of sets to show: " + setNum.size());
        Log.e("EditExerciseActivity", "Set numbers: \n" + setNum
                + "\nSet reps: \n" + setReps
                + "\nSet weights: \n" + setWeight);

        String styledTitle = "<big>Edit.<font color='#fd5621'>Select/Add</font></big>";
        setTitle(Html.fromHtml(styledTitle));

//        currWeight = setWeight.get(0);
//        currRepNum = setReps.get(0);
        displayWeight(0);
        displayReps(0);
        currSetNum = 0;

        if (currSetNum == 0){

        }

        final int numberOfSets = setNum.size();

        final TableLayout table = (TableLayout) EditExerciseActivity.this.findViewById(R.id.tableLayoutList);

        View.OnClickListener tableRowOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currSetNum = (Integer)v.getTag();
                currWeight = setWeight.get(currSetNum);
                currRepNum = setReps.get(currSetNum);
                displayWeight(currWeight);
                displayReps(currRepNum);

                Log.e("EditExerciseActivity", "Selected set number is: " + currSetNum);

                for(int j=0;j<table.getChildCount();j++) {
                    TableRow otherRows = (TableRow) table.getChildAt(j);
                    otherRows.setBackgroundColor(Color.rgb(255,255,255));
                }

                TableRow currRow = (TableRow) table.findViewWithTag(currSetNum);
                TableRow currDivider = (TableRow) table.findViewWithTag(currSetNum+numberOfSets);

                currRow.setBackgroundColor(Color.rgb(237, 237, 237));

                if (currSetNum < numberOfSets-1)
                {
                    currDivider.setBackgroundColor(Color.rgb(237, 237, 237));
                }

                String styledTitle = "<big>Edit.<font color='#fd5621'>Set " + setNum.get(currSetNum) + "</font></big>";
                setTitle(Html.fromHtml(styledTitle));
            }
        };

        for(int i=0; i<numberOfSets;i++){
            final TableRow row = (TableRow)LayoutInflater.from(EditExerciseActivity.this).inflate(R.layout.row_layout, null);
            ((TextView)row.findViewById(R.id.setNum)).setText("" + setNum.get(i));
            ((TextView)row.findViewById(R.id.setReps)).setText("" + setReps.get(i));
            ((TextView)row.findViewById(R.id.setWeight)).setText("" + setWeight.get(i));
            row.setTag(Integer.valueOf(i));
            table.addView(row);

//            final int currSetNum = i;
            final TableRow divider = (TableRow) LayoutInflater.from(EditExerciseActivity.this).inflate(R.layout.row_divider, null);
            divider.setTag(i+numberOfSets);

            if (i < numberOfSets-1) {
                table.addView(divider);
            }

            row.setOnClickListener(tableRowOnClick);
//            row.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    currSetNum = (Integer)v.getTag();
//                    currWeight = setWeight.get(currSetNum);
//                    currRepNum = setReps.get(currSetNum);
//                    displayWeight(currWeight);
//                    displayReps(currRepNum);
//
//                    Log.e("EditExerciseActivity", "Selected set number is: " + currSetNum);
//
//                    for(int j=0;j<table.getChildCount();j++) {
//                        TableRow otherRows = (TableRow) table.getChildAt(j);
//                        otherRows.setBackgroundColor(Color.rgb(255,255,255));
//                    }
//
//                    row.setBackgroundColor(Color.rgb(237, 237, 237));
//
//                    if (currSetNum < numberOfSets-1)
//                    {
//                        divider.setBackgroundColor(Color.rgb(237, 237, 237));
//                    }
//
//                    String styledTitle = "<big>Edit.<font color='#fd5621'>Set " + setNum.get(currSetNum) + "</font></big>";
//                    setTitle(Html.fromHtml(styledTitle));
//                }
//            });
        }

       Button saveButton = (Button) findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get values of setRep and setWeight
                // write current values to database
                EditText mRepNum = (EditText)findViewById(R.id.repNum);
                EditText mWeightNum = (EditText) findViewById(R.id.weightNum);

                currRepNum = Integer.parseInt(mRepNum.getText().toString());
                currWeight = Integer.parseInt(mWeightNum.getText().toString());

                myDbHelper.setSetStats(fragmentNum, exerciseNum, currSetNum, currRepNum, currWeight);

                displayWeight(currWeight);
                displayReps(currRepNum);

                TableRow currRow = (TableRow) table.findViewWithTag(currSetNum);
                ((TextView)currRow.findViewById(R.id.setReps)).setText("" + currRepNum);
                ((TextView)currRow.findViewById(R.id.setWeight)).setText("" + currWeight);
            }
        });

    }

    public void increaseWeight(View view) {
        currWeight = currWeight + 5;
        displayWeight(currWeight);

    }public void decreaseWeight(View view) {
        currWeight = currWeight - 5;
        displayWeight(currWeight);
    }

    private void displayWeight(int weight) {
        EditText displayReps = (EditText) findViewById(
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
        EditText displayReps = (EditText) findViewById(
                R.id.repNum);
        displayReps.setText("" + currRepNum);
    }

}
