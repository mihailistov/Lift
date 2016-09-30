package ca.mihailistov.lift;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ca.mihailistov.lift.Realm.RealmCategory;
import ca.mihailistov.lift.Realm.RealmExerciseData;
import io.realm.Realm;
import io.realm.RealmResults;

public class AddExerciseActivity extends AppCompatActivity implements  RecyclerViewClickListener {
    private static final String TAG = "AddExerciseActivity";
    private Toolbar toolbar;
    private TextView toolbarText;
    private ImageView toolbarIcon;
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
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarText = (TextView) findViewById(R.id.add_exercise_title);
        toolbarText.setText("Choose category");
        toolbarIcon = (ImageView) findViewById(R.id.add_exercise_icon);

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

    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        TextView textView = (TextView) v.findViewById(R.id.category_text);
        if (DEPTH == 0) {
            String category = textView.getText().toString();

            toolbarText.setText(category);


        }
    }

    public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseHolder> {
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
            if (DEPTH == 0) {
                category = allRealmCategories.get(position).name.toString();
                holder.bindCategoryList(category);
            }
            else if (DEPTH == 1) {
                exercise = exercisesInCat.get(position).name.toString();
                holder.bindCategoryList(exercise);
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
            else
                return exercisesInCat.size();
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
}
