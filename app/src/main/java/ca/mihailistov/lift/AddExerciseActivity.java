package ca.mihailistov.lift;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;

import ca.mihailistov.lift.Realm.RealmCategory;
import io.realm.Realm;
import io.realm.RealmResults;

public class AddExerciseActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;

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

        Realm realm = Realm.getDefaultInstance();

        RealmResults<RealmCategory> allRealmCategories = realm.where(RealmCategory.class).findAll();

        categoryList = new ArrayList<>();
        for (int i = 0; i < allRealmCategories.size(); i++){
            categoryList.add(allRealmCategories.get(i).name);
        }

        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        mRecyclerView.setAdapter(new ExerciseAdapter());

    }

    private class ExerciseHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private String category;

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
        @Override
        public ExerciseHolder onCreateViewHolder(ViewGroup parent, int pos) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_add_exercise, parent, false);
            return new ExerciseHolder(view);
        }

        @Override
        public void onBindViewHolder(ExerciseHolder holder, int position) {
            String category = categoryList.get(position);
            holder.bindCategoryList(category);
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }
    }
}
