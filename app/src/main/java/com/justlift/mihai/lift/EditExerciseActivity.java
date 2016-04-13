package com.justlift.mihai.lift;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditExerciseActivity extends AppCompatActivity {

    int repNum = 0;
    int weight = 0;

    int repNum2 = 0;
    int weight2 = 0;

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

        Button close_button = (Button) findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        List<Integer> setNum = new ArrayList<Integer>();
        List<Integer> setReps = new ArrayList<Integer>();
        List<Integer> setWeight = new ArrayList<Integer>();

        myDbHelper.getExerciseSetStats(fragmentNum, exerciseNum, setNum, setReps, setWeight);

        Log.e("EditExerciseActivity", "Set numbers: \n" + setNum
                + "\nSet reps: \n" + setReps
                + "\nSet weights: \n" + setWeight);

        repNum = setReps.get(0);
        weight = setWeight.get(0);

        repNum2 = setReps.get(1);
        weight2 = setWeight.get(1);

        displayReps(repNum);
        displayWeight(weight);

        displayReps2(repNum2);
        displayWeight2(weight2);


    }

    public void increaseReps(View view) {
        repNum = repNum + 1;
        displayReps(repNum);

    }public void decreaseReps(View view) {
        repNum = repNum - 1;
        displayReps(repNum);
    }

    public void increaseWeight(View view) {
        weight = weight + 5;
        displayWeight(weight);

    }public void decreaseWeight(View view) {
        weight = weight - 5;
        displayWeight(weight);
    }

    private void displayReps(int reps) {
        TextView displayReps = (TextView) findViewById(
                R.id.repNumber);
        displayReps.setText("" + reps);
    }

    private void displayWeight(int weight) {
        TextView displayReps = (TextView) findViewById(
                R.id.weightLbs);
        displayReps.setText("" + weight);
    }

    // second set
    public void increaseReps2(View view) {
        repNum2 = repNum2 + 1;
        displayReps2(repNum2);

    }public void decreaseReps2(View view) {
        repNum2 = repNum2 - 1;
        displayReps2(repNum2);
    }

    public void increaseWeight2(View view) {
        weight2 = weight2 + 5;
        displayWeight2(weight2);

    }public void decreaseWeight2(View view) {
        weight2 = weight2 - 5;
        displayWeight2(weight2);
    }

    private void displayReps2(int reps2) {
        TextView displayReps = (TextView) findViewById(
                R.id.repNumber2);
        displayReps.setText("" + reps2);
    }

    private void displayWeight2(int weight2) {
        TextView displayReps = (TextView) findViewById(
                R.id.weightLbs2);
        displayReps.setText("" + weight2);
    }
}
