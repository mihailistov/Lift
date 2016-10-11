package ca.mihailistov.lift;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AddActionActivity extends AppCompatActivity {

    private static final String TAG = "AddActionActivity";
    private int mNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_add_action);

        Intent intent = getIntent();
        mNum = intent.getIntExtra("mNum", -2);
        Log.e(TAG, "mNum = " + mNum);

//        Realm realm = Realm.getDefaultInstance();
//
//        RealmResults<RealmCategory> dataQuery = realm.where(RealmCategory.class).findAll();
//
//        /*
//        * Add default categories if they don't exist.
//        * */
//        if (dataQuery.size() == 0) {
//
//            List<RealmCategory> defaultRealmCategoryList = new ArrayList<RealmCategory>();
//
//            List<String> newCategoryList = new ArrayList<String>(
//                    Arrays.asList("Abs", "Back", "Biceps",
//                            "Chest", "Legs", "Shoulders", "Triceps"));
//
//
//            Log.e("RealmManager", newCategoryList.toString());
//
//            for (int i = 0; i < newCategoryList.size(); i++) {
//                RealmCategory newRealmCategory = new RealmCategory();
//                newRealmCategory.id = i + 1;
//                newRealmCategory.name = newCategoryList.get(i);
//                defaultRealmCategoryList.add(newRealmCategory);
//            }
//            final List<RealmCategory> finalDefaultRealmCategoryList = defaultRealmCategoryList;
//
//            realm.executeTransaction(new Realm.Transaction() {
//                @Override
//                public void execute(Realm realm) {
//                    realm.copyToRealm(finalDefaultRealmCategoryList);
//                }
//            });
//        }

        RelativeLayout spacerOne = (RelativeLayout) findViewById(R.id.spacer_one);
        RelativeLayout spacerTwo = (RelativeLayout) findViewById(R.id.spacer_two);
        ImageView closeButton = (ImageView) findViewById(R.id.add_close_button);
        TextView searchAll = (TextView) findViewById(R.id.search_all);

        searchAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddActionActivity.this, AddExerciseActivity.class);
                Bundle b = new Bundle();
                b.putInt("mNum",mNum);
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        });

        spacerOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        spacerTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
