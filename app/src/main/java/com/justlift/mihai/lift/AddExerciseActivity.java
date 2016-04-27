package com.justlift.mihai.lift;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by mihai on 16-04-21.
 */
public class AddExerciseActivity extends AppCompatActivity {
    private DatabaseHelper myDbHelper;
    ArrayAdapter<String> adapter;
    ArrayList<String> categoryList;
    ListView listView;
    boolean exercisesDisp = false;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_add_exercise);

        toolbar = (Toolbar) findViewById(R.id.dialog_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Choose category");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exercisesDisp) {
                    toolbar.setNavigationIcon(null);
                    setAdapterToList(categoryList);
                    exercisesDisp = false;
                    toolbar.setTitle("Add exercise");
                }
            }
        });

        myDbHelper = DatabaseHelper.getInstance(this);

        listView = (ListView) findViewById(R.id.list_layout);

        categoryList = new ArrayList<String>();
        ArrayList<String> exercisesToAdd = new ArrayList<String>();

        categoryList = myDbHelper.getCategories();

//        adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, android.R.id.text1, categoryList);
//
//        listView.setAdapter(adapter);

        setAdapterToList(categoryList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!exercisesDisp) {
                    String catClicked = (String) parent.getItemAtPosition(position);
                    toolbar.setTitle(catClicked);
                    toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);

                    ArrayList<String> exerciseList = new ArrayList<String>();
                    exerciseList = myDbHelper.getExercises(catClicked);

                    setAdapterToList(exerciseList);

                    //                Log.e("AddExerciseActivity","Category selected: " + catClicked);
                    //                Intent intent = new Intent(AddExerciseActivity.this, ExerciseListActivity.class);
                    //                intent.putExtra("category", catClicked);
                    //                startActivityForResult(intent, 1);

                    exercisesDisp = true;

                } else if (exercisesDisp) {
                    String exerciseClicked = (String) parent.getItemAtPosition(position);
                    Intent intent = new Intent();
                    intent.putExtra("exercise", exerciseClicked);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    public void setAdapterToList(ArrayList<String> list){
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listView.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_bar, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) AddExerciseActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(AddExerciseActivity.this.getComponentName()));
        }
        return super.onCreateOptionsMenu(menu);
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if(resultCode == RESULT_OK){
//                String exerciseClicked = data.getStringExtra("exercise");
//                Intent intent = new Intent();
//                intent.putExtra("exercise", exerciseClicked);
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        }
//    }
//
}
