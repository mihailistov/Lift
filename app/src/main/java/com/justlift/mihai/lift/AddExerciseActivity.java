package com.justlift.mihai.lift;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by mihai on 16-04-21.
 */
public class AddExerciseActivity extends AppCompatActivity {
    public static DatabaseHelper myDbHelper;
    ArrayAdapter<String> adapter;
    public static ArrayList<String> categoryList, exercisesAdded, exerciseList;
    public static CustomListView listView;
    public static boolean exercisesDisp = false;
    public static Toolbar toolbar;
    public static String exerciseClicked;
    public static Context myContext;

    @Override
    protected void onPause(){
        super.onPause();
        myDbHelper = DatabaseHelper.getInstance(this);

        exercisesDisp = false;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_add_exercise);

        myContext = this;

        toolbar = (Toolbar) findViewById(R.id.dialog_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Choose category");

        myDbHelper = DatabaseHelper.getInstance(this);

        exercisesAdded = new ArrayList<String>();

        categoryList = new ArrayList<String>();
        categoryList = myDbHelper.getCategories();

        listView = (CustomListView) findViewById(R.id.list_layout);
        setAdapterToList(categoryList);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int len = listView.getCount();
//
//                SparseBooleanArray checked = listView.getCheckedItemPositions();
//                for (int i = 0; i < len; i++) {
//                    if (checked.get(i)) {
//                        String item = exerciseList.get(i);
//                        exercisesAdded.add(item);
//                    }
//                }
//
//                Log.e("AddExerciseActivity", "Exercises checked:\n" + exercisesAdded);

                toolbar.setNavigationIcon(null);
                setAdapterToList(categoryList);
                exercisesDisp = false;
                toolbar.setTitle("Choose category");
            }
        });

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                if (!exercisesDisp) {
//                    String catClicked = (String) parent.getItemAtPosition(position);
//                    toolbar.setTitle(catClicked);
//                    toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
//
//                    exerciseList = new ArrayList<String>();
//                    exerciseList = myDbHelper.getExercises(catClicked);
//
//                    setAdapterToMultipleList(exerciseList);
//
//                    exercisesDisp = true;
//
//                }
//            }
//        });

        Button doneButton = (Button) findViewById(R.id.done_button);
        Button addButton = (Button) findViewById(R.id.add_button);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("exercisesAdded", exercisesAdded);
                Log.e("AddExerciseActivity", "Passing exercises to MainActivity:\n" + exercisesAdded);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public static void setAdapterToList(ArrayList<String> list){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(myContext,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    public static void setAdapterToMultipleList(ArrayList<String> list){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(myContext,
                android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, list);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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
                        exerciseList = new ArrayList<String>();
                        exerciseList = myDbHelper.getSearchResults(newText);
                        setAdapterToMultipleList(exerciseList);
                        exercisesDisp = true;
                    }
                    return false;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }


}
