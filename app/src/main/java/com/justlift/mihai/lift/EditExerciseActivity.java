package com.justlift.mihai.lift;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditExerciseActivity extends AppCompatActivity {

    boolean noSets = false;
    int fragmentNum = 0;
    int exerciseNum = 0;
    int setNumClicked = 0;
    String exerciseName;
    final List<Integer> setNum = new ArrayList<Integer>();
    final List<Integer> setReps = new ArrayList<Integer>();
    final List<Integer> setWeight = new ArrayList<Integer>();

    int currSetNum = 0;
    int currRepNum = 0;
    int currWeight = 0;

    @Override
    protected void onPause(){
        super.onPause();
        final DatabaseHelper myDbHelper;
        myDbHelper = new DatabaseHelper(this);

        if (noSets){
            myDbHelper.addSet(fragmentNum, exerciseNum, exerciseName, 0, 0);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);

        Intent intent = getIntent();
        fragmentNum = intent.getIntExtra("fragmentNum", 0);
        exerciseNum = intent.getIntExtra("exerciseNum", 0);
        setNumClicked = intent.getIntExtra("setNumClicked", 0);

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

        exerciseName = myDbHelper.getExerciseName(fragmentNum, exerciseNum);
        myDbHelper.getExerciseStats(fragmentNum, exerciseNum, setNum, setReps, setWeight);

        if (setNum.size() == 1 && setReps.get(0) == 0 && setWeight.get(0) == 0) {
            setNumClicked = 1;
        }

        Log.e("EditExerciseActivity", "Number of sets to show: " + setNum.size());
        Log.e("EditExerciseActivity", "Set numbers: \n" + setNum
                + "\nSet reps: \n" + setReps
                + "\nSet weights: \n" + setWeight);

        displayWeight(0);
        displayReps(0);

        final Button saveButton = (Button) findViewById(R.id.save_button);
        final Button clearButton = (Button) findViewById(R.id.clear_button);
        setButtonDefaults();

        if (setNumClicked != 0){
            currSetNum = setNumClicked-1;
            currWeight = setWeight.get(currSetNum);
            currRepNum = setReps.get(currSetNum);

            displayWeight(currWeight);
            displayReps(currRepNum);

            setButtonUpdateMode();

            Log.e("EditExerciseActivity", "Long press on child detected, editing Set: " + currSetNum);
        }

        final TableLayout table = (TableLayout) EditExerciseActivity.this.findViewById(R.id.tableLayoutList);

        final View.OnClickListener tableRowOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // determine which set was selected & get set information
                currSetNum = (Integer)v.getTag();
                currWeight = setWeight.get(currSetNum);
                currRepNum = setReps.get(currSetNum);

                // set buttons to show "Update" and "Delete"
                setButtonUpdateMode();

                // display numbers in EditText for the set
                displayWeight(currWeight);
                displayReps(currRepNum);

                Log.e("EditExerciseActivity", "Selected set number is: " + currSetNum);

                // clear highlight from any previously selected rows
                for(int j=0;j<table.getChildCount();j++) {
                    TableRow otherRows = (TableRow) table.getChildAt(j);
                    otherRows.setBackgroundColor(Color.rgb(255,255,255));
                }

                // highlight the selected row
                TableRow currRow = (TableRow) table.findViewWithTag(currSetNum);
                currRow.setBackgroundColor(Color.rgb(237, 237, 237));
                Log.e("EditExerciseHelper","Set background to gray on row tag: " + currRow.getTag());

                // if selected row is not the last row, then highlight the divider row as well
                if (currSetNum < setNum.size()-1)
                {
                    int dividerTag = currSetNum + 100;
                    TableRow currDivider = (TableRow) table.findViewWithTag(dividerTag);
                    Log.e("EditExerciseActivity","Attempting to set divider on set: " + currSetNum + " divider tag is: " + dividerTag);
                    currDivider.setBackgroundColor(Color.rgb(237, 237, 237));
                }
            }
        };

        // generates table rows for each set on create of activity
        for(int i=0; i<setNum.size();i++){
            final TableRow row = (TableRow)LayoutInflater.from(EditExerciseActivity.this).inflate(R.layout.row_layout, null);
            ((TextView)row.findViewById(R.id.setNum)).setText("" + setNum.get(i));
            ((TextView)row.findViewById(R.id.setReps)).setText("" + setReps.get(i));
            ((TextView)row.findViewById(R.id.setWeight)).setText("" + setWeight.get(i));
            row.setTag(Integer.valueOf(i)); // set row tag 0-5 for 6 sets for example
            table.addView(row);

            final TableRow divider = (TableRow) LayoutInflater.from(EditExerciseActivity.this).inflate(R.layout.row_divider, null);
            int dividerTag = i+100;
            divider.setTag(dividerTag); // set divider tag to 100-105 for 6 sets for example

            // add dividers for all but the last set
            if (i < setNum.size()-1) {
                table.addView(divider);
            }

            row.setOnClickListener(tableRowOnClick);
        }

        // "Clear" or "Delete" button on click listener
        clearButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText mRepNum = (EditText)findViewById(R.id.repNum);
                EditText mWeightNum = (EditText) findViewById(R.id.weightNum);

                // currSetNum will from 0-4 for example
                // lastSetNum will be from 1-5 for example
                final int lastSetNum = myDbHelper.getLastSetNum(fragmentNum, exerciseNum);
                final int removeSetNum = currSetNum + 1; // puts it in a more comparable format (1-5 like lastSetNum)

                // if no sets selected clear the EditText boxes
                if (currSetNum == -1){
                    mRepNum.setText("" + 0);
                    mWeightNum.setText("" + 0);
                } else {

                    DialogInterface.OnClickListener removeDialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    // if a row is selected then delete that row
                                    Log.e("EditExerciseActivity", "Deleting row tag: " + currSetNum);

                                    // call database function to remove information from db
                                    myDbHelper.removeSet(fragmentNum, exerciseNum, currSetNum);

                                    // remove deleted row from table view
                                    TableRow deletedRow = (TableRow) table.findViewWithTag(currSetNum);
                                    table.removeView(deletedRow);

                                    Log.e("EditExerciseActivity", "Deleting... Last set num: " + lastSetNum);

                                    // if the removed set is not the last set then remove the divider associated with it
                                    // as well fix the row/divider tags for the sets after the deleted set
                                    if (removeSetNum < lastSetNum) {
                                        int dividerTag = currSetNum+100;
                                        TableRow deletedDivider = (TableRow) table.findViewWithTag(dividerTag);
                                        table.removeView(deletedDivider);

                                        for (int i=1;i<=(lastSetNum-removeSetNum);i++) {
                                            int rowTag = currSetNum + i;
                                            int newRowTag = rowTag - 1;

                                            dividerTag = rowTag+100;
                                            int newDividerTag = newRowTag + 100;

                                            TableRow row = (TableRow) table.findViewWithTag(rowTag);
                                            row.setTag(Integer.valueOf(newRowTag));

                                            // update divider tags on all sets but the last one b/c is doesn't exist
                                            if (i < (lastSetNum-removeSetNum)) {
                                                TableRow rowDivider = (TableRow) table.findViewWithTag(dividerTag);
                                                rowDivider.setTag(newDividerTag);
                                            }
                                        }
                                    } else {
                                        // if the removed set is the last one simple remove the previous rows divider
                                        // and remove the set row
                                        int dividerTag = (currSetNum+100)-1;
                                        TableRow deletedDivider = (TableRow) table.findViewWithTag(dividerTag);
                                        table.removeView(deletedDivider);
                                    }

                                    // refresh db information
                                    setNum.clear();
                                    setReps.clear();
                                    setWeight.clear();
                                    myDbHelper.getExerciseStats(fragmentNum, exerciseNum, setNum, setReps, setWeight);

                                    Log.e("EditExerciseActivity", "Number of sets after delete: " + setNum.size());

                                    if(setNum.size() == 0)
                                        noSets = true;

                                    // update row set numbers
                                    for(int i=0;i<setNum.size();i++){
                                        TableRow row = (TableRow) table.findViewWithTag(i);
                                        ((TextView)row.findViewById(R.id.setNum)).setText("" + setNum.get(i));
                                    }

                                    currSetNum = -1;
                                    setButtonDefaults();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(EditExerciseActivity.this);
                    builder.setMessage("Are you sure you want to delete?")
                            .setPositiveButton("Yes", removeDialogClickListener)
                            .setNegativeButton("No", removeDialogClickListener).show();



                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get values of setRep and setWeight
                // write current values to database
                EditText mRepNum = (EditText)findViewById(R.id.repNum);
                EditText mWeightNum = (EditText) findViewById(R.id.weightNum);

                currRepNum = Integer.parseInt(mRepNum.getText().toString());
                currWeight = Integer.parseInt(mWeightNum.getText().toString());


                if (currSetNum != -1) {
                    if (currRepNum == 0 || currWeight == 0){
                        Toast.makeText(EditExerciseActivity.this, "Please enter non-zero values", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    myDbHelper.setSetStats(fragmentNum, exerciseNum, currSetNum, currRepNum, currWeight);

                    TableRow currRow = (TableRow) table.findViewWithTag(currSetNum);
                    ((TextView) currRow.findViewById(R.id.setReps)).setText("" + currRepNum);
                    ((TextView) currRow.findViewById(R.id.setWeight)).setText("" + currWeight);
                } else {
                    if (currRepNum == 0 || currWeight == 0){
                        Toast.makeText(EditExerciseActivity.this, "Please enter non-zero values", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int lastSetNum = myDbHelper.getLastSetNum(fragmentNum, exerciseNum);
                    int newSetNum = lastSetNum + 1;

                    myDbHelper.addSet(fragmentNum, exerciseNum, exerciseName, currRepNum, currWeight);
                    noSets = false;

                    final TableRow divider = (TableRow) LayoutInflater.from(EditExerciseActivity.this).inflate(R.layout.row_divider, null);

                    int dividerTag = (lastSetNum + 100)-1;

                    if (dividerTag >= 100) {
                        divider.setTag(dividerTag);
                        Log.e("EditExerciseActivity", "Setting divider tag to: " + dividerTag);
                        table.addView(divider);
                    }

                    final TableRow row = (TableRow)LayoutInflater.from(EditExerciseActivity.this).inflate(R.layout.row_layout, null);
                    ((TextView)row.findViewById(R.id.setNum)).setText("" + newSetNum);
                    ((TextView)row.findViewById(R.id.setReps)).setText("" + currRepNum);
                    ((TextView)row.findViewById(R.id.setWeight)).setText("" + currWeight);
                    row.setTag(Integer.valueOf(lastSetNum));
                    Log.e("EditExerciseTag","Setting row tag to: " + lastSetNum);
                    table.addView(row);

                    row.setOnClickListener(tableRowOnClick);
                }

                setNum.clear();
                setReps.clear();
                setWeight.clear();

                myDbHelper.getExerciseStats(fragmentNum, exerciseNum, setNum, setReps, setWeight);

                for(int j=0;j<table.getChildCount();j++) {
                    TableRow otherRows = (TableRow) table.getChildAt(j);
                    otherRows.setBackgroundColor(Color.rgb(255,255,255));
                }

                setButtonDefaults();
            }
        });

    }

    private void setButtonDefaults(){
        currSetNum = -1;
        Button saveButton = (Button) findViewById(R.id.save_button);
        Button clearButton = (Button) findViewById(R.id.clear_button);

        saveButton.getBackground().setColorFilter(0xFF23A96E, PorterDuff.Mode.MULTIPLY);
        saveButton.setTextAppearance(R.style.ButtonEditSet);
        saveButton.setText("New");

        clearButton.getBackground().setColorFilter(0xFF009DD7, PorterDuff.Mode.MULTIPLY);
        clearButton.setTextAppearance(R.style.ButtonEditSet);
        clearButton.setText("Clear");

        String styledTitle = "<big>Edit.<font color='#33aebe'>Add/select a set</font></big>";
        setTitle(Html.fromHtml(styledTitle));
    }

    private void setButtonUpdateMode(){
        Button saveButton = (Button) findViewById(R.id.save_button);
        Button clearButton = (Button) findViewById(R.id.clear_button);

        saveButton.setText("Update");
        clearButton.getBackground().setColorFilter(0xFFFA3D3D, PorterDuff.Mode.MULTIPLY);
        clearButton.setText("Delete");

        // set title to "Edit.Set #"
        String styledTitle = "<big>Edit.<font color='#33aebe'>Set " + setNum.get(currSetNum) + "</font></big>";
        setTitle(Html.fromHtml(styledTitle));
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
