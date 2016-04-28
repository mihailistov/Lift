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
                toolbar.setNavigationIcon(null);
                setAdapterToList(categoryList);
                exercisesDisp = false;
                toolbar.setTitle("Choose category");
            }
        });

        myDbHelper = DatabaseHelper.getInstance(this);

        categoryList = new ArrayList<String>();
        categoryList = myDbHelper.getCategories();

        listView = (ListView) findViewById(R.id.list_layout);
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

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.equals("")){
                        setAdapterToList(categoryList);
                        exercisesDisp = false;
                    } else {
                        ArrayList<String> resultsList = new ArrayList<String>();
                        resultsList = myDbHelper.getSearchResults(newText);
                        setAdapterToList(resultsList);
                        exercisesDisp = true;
                    }
                    return false;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }


}
