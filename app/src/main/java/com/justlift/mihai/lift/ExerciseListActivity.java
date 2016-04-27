package com.justlift.mihai.lift;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by mihai on 16-04-26.
 */
public class ExerciseListActivity extends AppCompatActivity {
    private DatabaseHelper myDbHelper;
    String category;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_exercise_list);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");

        myDbHelper = DatabaseHelper.getInstance(this);

        ListView listView = (ListView) findViewById(R.id.exercise_list);

        ArrayList<String> exerciseList = new ArrayList<String>();
        exerciseList = myDbHelper.getExercises(category);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, exerciseList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String exerciseClicked = (String) parent.getItemAtPosition(position);
                Log.e("AddExerciseActivity", "Exercise selected: " + exerciseClicked);
                Intent intent = new Intent();
                intent.putExtra("exercise", exerciseClicked);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}