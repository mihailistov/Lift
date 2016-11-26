package ca.mihailistov.lift.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.INameableAdapter;
import com.turingtechnologies.materialscrollbar.TouchScrollBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ca.mihailistov.lift.R;
import ca.mihailistov.lift.interfaces.RecyclerViewClickListener;
import ca.mihailistov.lift.helpers.SimpleDividerItemDecoration;
import ca.mihailistov.lift.realm.RealmCategory;
import ca.mihailistov.lift.realm.RealmExercise;
import ca.mihailistov.lift.realm.RealmExerciseData;
import ca.mihailistov.lift.realm.RealmWorkout;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class AddExerciseActivity extends AppCompatActivity implements RecyclerViewClickListener {
    private static final String TAG = "AddExerciseActivity";
    private Toolbar toolbar;
    private ImageView toolbarNavArrow;
    private RelativeLayout relativeLayout;
    private RecyclerView mRecyclerView;
    private ExerciseAdapter exerciseAdapter;
    private Realm realm;
    private RealmResults<RealmCategory> allRealmCategories;
    private RealmResults<RealmExerciseData> exercisesInCat;
    private RealmResults<RealmExerciseData> exercisesQueried;

    /*
    * if DEPTH = 0: displaying CATEGORIES
    * else if DEPTH = 1: displaying EXERCISES
    * else if DEPTH = -1: displaying SEARCH
    * */
    private int DEPTH = 0;
    private int mNum;

    private ArrayList<String> categoryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_add_exercise);

        Intent intent = getIntent();
        mNum = intent.getIntExtra("mNum",-1);

        toolbar = (Toolbar) findViewById(R.id.dialog_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Choose category");

        relativeLayout = (RelativeLayout) findViewById(R.id.add_exercise_relative_layout);
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

        exerciseAdapter = new ExerciseAdapter(this, this);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        mRecyclerView.setAdapter(exerciseAdapter);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DEPTH = 0;
                final float scale = getResources().getDisplayMetrics().density;
                int pixels = (int) (376 * scale + 0.5f);
                relativeLayout.getLayoutParams().height = pixels;
                getSupportActionBar().setTitle("Choose category");
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                exerciseAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        TextView textView = (TextView) v.findViewById(R.id.category_text);
        if (DEPTH == 0) {
            String category = textView.getText().toString();
            exercisesInCat = realm.where(RealmExerciseData.class)
                    .equalTo("category",category)
                    .greaterThan("rating",8.0)
                    .findAllSorted("name");

            getSupportActionBar().setTitle(category);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            exerciseAdapter.notifyDataSetChanged();

            final float scale = getResources().getDisplayMetrics().density;
            int pixels = (int) (496 * scale + 0.5f);
            relativeLayout.getLayoutParams().height = pixels;

            TouchScrollBar materialScrollBar = new TouchScrollBar(this, mRecyclerView, true)
                    .setHandleColourRes(R.color.colorPrimary)
                    .setIndicator(new AlphabetIndicator(this), false);

            DEPTH = 1;
        } else if (DEPTH == 1) {
            final String exercise = textView.getText().toString();
            final RealmExercise newRealmExercise = new RealmExercise();
            RealmExerciseData realmExerciseData = realm.where(RealmExerciseData.class)
                    .equalTo("name",exercise)
                    .findFirst();
            newRealmExercise.realmExerciseData = realmExerciseData;

            RealmWorkout realmWorkout = null;

            Calendar c = Calendar.getInstance();
            Log.e(TAG, "mNum = " + mNum);
            c.add(Calendar.DAY_OF_WEEK, mNum-15);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);

            final String currentDate = df.format(c.getTime());

            try {
                realmWorkout = realm.where(RealmWorkout.class).equalTo("date", currentDate).findFirst();
            } catch (Exception e) {
                Log.e(TAG, "realm error: " + e);
            }

            if (realmWorkout != null){
                // add to existing workout
                final RealmWorkout updatedRealmWorkout = realmWorkout;

                realm.executeTransaction(new Realm.Transaction(){
                    @Override
                    public void execute(Realm realm) {

                        RealmExercise realmExercise = realm.copyToRealm(newRealmExercise);
                        updatedRealmWorkout.exercises.add(realmExercise);
                    }
                });
            } else {
                // create new workout

                final RealmWorkout newRealmWorkout = new RealmWorkout();

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        RealmExercise realmExercise = realm.copyToRealm(newRealmExercise);
                        newRealmWorkout.date = currentDate;
                        newRealmWorkout.exercises = new RealmList<>();
                        newRealmWorkout.exercises.add(realmExercise);
                        realm.copyToRealm(newRealmWorkout);
                    }
                });
            }

            Toast.makeText(this, "Added exercise: " + exercise, Toast.LENGTH_LONG).show();

            Log.e(TAG, "Finishing AddExerciseActivity with result");
            Intent resultIntent = new Intent();
            resultIntent.putExtra("exerciseAdded",true);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseHolder> implements INameableAdapter {
        private Context context;
        private RecyclerViewClickListener itemListener;

        public ExerciseAdapter(Context context, RecyclerViewClickListener itemListener){
            this.context = context;
            this.itemListener = itemListener;
        }

        @Override
        public ExerciseHolder onCreateViewHolder(ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_add_exercise, parent, false);

            return new ExerciseHolder(view);
        }

        @Override
        public void onBindViewHolder(ExerciseHolder holder, int position) {
            String category = null;
            String exercise = null;
            String exerciseResult = null;
            if (DEPTH == 0) {
                category = allRealmCategories.get(position).name.toString();
                holder.bindCategoryList(category);
            }
            else if (DEPTH == 1) {
                exercise = exercisesInCat.get(position).name.toString();
                holder.bindCategoryList(exercise);
            } else if (DEPTH == -1) {
                exerciseResult = exercisesQueried.get(position).name.toString();
                holder.bindCategoryList(exerciseResult);
            }
//            final String finalCategory = category;
//            final String finalExercise = exercise;
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (DEPTH == 0) {
//                        Toast.makeText(getApplication(), "Clicked on: " + finalCategory,
//                                Toast.LENGTH_SHORT).show();
//
//                        exercisesInCat = realm.where(RealmExerciseData.class)
//                                .equalTo("category", finalCategory).findAllSorted("name");
//                        Log.e(TAG, "Exercises found in category: " + exercisesInCat.size());
//                        DEPTH = 1; // Set DEPTH = 1 to incidate displaying EXERCISES
//                        exerciseAdapter.notifyDataSetChanged();
//                        mRecyclerView.invalidate();
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Clicked on: " + finalExercise,
//                                Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
        }

        @Override
        public int getItemCount() {
            if (DEPTH == 0)
                return allRealmCategories.size();
            else if (DEPTH == 1)
                return exercisesInCat.size();
            else
                return exercisesQueried.size();
        }

        @Override
        public Character getCharacterForElement(int element) {
            Log.e(TAG, "getCharForElement: int element = " + element);
            Character c = exercisesInCat.get(element).name.charAt(0);
            return c;
        }

        public class ExerciseHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            private final TextView textView;

            public ExerciseHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.category_text);
                itemView.setOnClickListener(this);
            }

            public void bindCategoryList (String category) {
                textView.setText(category);
            }

            @Override
            public void onClick(View v) {
                itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
            }
        }
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
                        getSupportActionBar().setDisplayShowHomeEnabled(false);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        DEPTH = 0;
                        exerciseAdapter.notifyDataSetChanged();
                    } else {
                        exercisesQueried = realm.where(RealmExerciseData.class)
                                .greaterThan("rating",8.0)
                                .beginGroup()
                                    .contains("name",newText, Case.INSENSITIVE)
                                    .or()
                                    .contains("muscle",newText, Case.INSENSITIVE)
                                    .or()
                                    .contains("other_muscles",newText, Case.INSENSITIVE)
                                    .or()
                                    .contains("equipment",newText, Case.INSENSITIVE)
                                    .or()
                                    .contains("force",newText, Case.INSENSITIVE)
                                .endGroup()
                                .findAll();
                        DEPTH = -1;
                        exerciseAdapter.notifyDataSetChanged();
                    }
                    return false;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }
}
