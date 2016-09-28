package ca.mihailistov.lift;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.mihailistov.lift.Realm.RealmCategory;
import ca.mihailistov.lift.Realm.RealmExerciseData;
import io.realm.Realm;
import io.realm.RealmResults;

public class AddExerciseActivity extends AppCompatActivity {
    private static final String TAG = "AddExerciseActivity";
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private ExerciseAdapter exerciseAdapter;
    private Realm realm;
    private RealmResults<RealmCategory> allRealmCategories;
    private RealmResults<RealmExerciseData> exercisesInCat;

    /*
    * if DEPTH = 0: displaying CATEGORIES
    * else if DEPTH = 1: displaying EXERCISES
    * else if DEPTH = -1: displaying SEARCH
    * */
    private int DEPTH = 0;

    private ArrayList<String> categoryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_add_exercise);

        toolbar = (Toolbar) findViewById(R.id.dialog_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Choose category");

        mRecyclerView = (RecyclerView) findViewById(R.id.add_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        realm = Realm.getDefaultInstance();

        allRealmCategories = realm.where(RealmCategory.class).findAll();

//        RealmResults<RealmCategory> allRealmCategories = realm.where(RealmCategory.class).findAll();
//
//        categoryList = new ArrayList<>();
//        for (int i = 0; i < allRealmCategories.size(); i++){
//            categoryList.add(allRealmCategories.get(i).name);
//        }

        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        exerciseAdapter = new ExerciseAdapter();
        mRecyclerView.setAdapter(exerciseAdapter);

    }


    private class ExerciseHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private String category;
        private Context context;

        public ExerciseHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.category_text);
        }



        public void bindCategoryList (String category) {
            this.category = category;
            textView.setText(category);
        }
    }


    private class ExerciseAdapter extends RecyclerView.Adapter<ExerciseHolder> {
        private View view;

        @Override
        public ExerciseHolder onCreateViewHolder(ViewGroup parent, int position) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_add_exercise, parent, false);

            return new ExerciseHolder(view);
        }

        @Override
        public void onBindViewHolder(ExerciseHolder holder, int position) {
            String category = null;
            String exercise = null;
            if (DEPTH == 0) {
                category = allRealmCategories.get(position).name.toString();
                holder.bindCategoryList(category);
            }
            else if (DEPTH == 1) {
                exercise = exercisesInCat.get(position).name.toString();
                holder.bindCategoryList(exercise);
            }
            final String finalCategory = category;
            final String finalExercise = exercise;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (DEPTH == 0) {
                        Toast.makeText(getApplication(), "Clicked on: " + finalCategory,
                                Toast.LENGTH_SHORT).show();

                        exercisesInCat = realm.where(RealmExerciseData.class)
                                .equalTo("category", finalCategory).findAllSorted("name");
                        Log.e(TAG, "Exercises found in category: " + exercisesInCat.size());
                        DEPTH = 1; // Set DEPTH = 1 to incidate displaying EXERCISES
                        exerciseAdapter.notifyDataSetChanged();
                        mRecyclerView.invalidate();
                    } else {
                        Toast.makeText(getApplicationContext(), "Clicked on: " + finalExercise,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (DEPTH == 1)
                return exercisesInCat.size();
            else
                return allRealmCategories.size();
        }
    }
}
