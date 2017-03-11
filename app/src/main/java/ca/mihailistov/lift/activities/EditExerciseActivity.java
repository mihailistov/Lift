package ca.mihailistov.lift.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ca.mihailistov.lift.R;
import ca.mihailistov.lift.adapters.EditSetListAdapter;
import ca.mihailistov.lift.realm.RealmExercise;
import ca.mihailistov.lift.realm.RealmSet;
import ca.mihailistov.lift.realm.RealmWorkout;
import io.realm.Realm;
import io.realm.RealmList;

public class EditExerciseActivity extends AppCompatActivity {

    private static final String TAG = "EditExerciseActivity";
    private int mNum;
    private int groupPos;
    private int childPos;
    private Realm realm;
    private Button saveButton;
    private Button clearButton;
    private EditSetListAdapter editSetListAdapter;
    private ListView editSetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_edit_exercise);

        Intent intent = getIntent();
        mNum = intent.getIntExtra("mNum",-1);
        groupPos = intent.getIntExtra("groupPos",-1);
        childPos = intent.getIntExtra("childPos",-1);

        realm = Realm.getDefaultInstance();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_WEEK, mNum-15);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        final String currentDate = df.format(c.getTime());

        RealmWorkout realmWorkout = null;

        try {
            realmWorkout = realm.where(RealmWorkout.class).equalTo("date", currentDate).findFirst();
        } catch (Exception e) {
            Log.e(TAG, "realm error: " + e);
        }

        RealmList<RealmExercise> realmExercises = realmWorkout.exercises;
        RealmExercise realmExercise = realmExercises.get(groupPos);
        RealmList<RealmSet> realmSets = realmExercise.realmSets;
        RealmSet realmSet = realmSets.get(childPos);

        Log.e(TAG, "realmExercise = " + realmExercise.realmExerciseData.name);
        Log.e(TAG, "realmSet reps = " + realmSet.reps + " weight = " + realmSet.weight);


        editSetList = (ListView) findViewById(R.id.editSetList);
        editSetListAdapter = new EditSetListAdapter(this, realmSets);
        editSetList.setAdapter(editSetListAdapter);
        editSetList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        editSetList.setItemChecked(childPos, true);

        setButtonDefaults();
        setButtonUpdateMode();
    }

    private void setButtonDefaults(){
        saveButton = (Button) findViewById(R.id.save_button);
        clearButton = (Button) findViewById(R.id.clear_button);

        saveButton.getBackground().setColorFilter(0xFF23A96E, PorterDuff.Mode.MULTIPLY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            saveButton.setTextAppearance(R.style.ButtonEditSet);
        }
        saveButton.setText("New");

        clearButton.getBackground().setColorFilter(0xFF009DD7, PorterDuff.Mode.MULTIPLY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            clearButton.setTextAppearance(R.style.ButtonEditSet);
        }
        clearButton.setText("Clear");

        String styledTitle = "<big>Edit.<font color='#33aebe'>Add/select a set</font></big>";
        setTitle(Html.fromHtml(styledTitle));
    }

    private void setButtonUpdateMode(){
        saveButton.setText("Update");
        clearButton.getBackground().setColorFilter(0xFFFA3D3D, PorterDuff.Mode.MULTIPLY);
        clearButton.setText("Delete");

        // set title to "Edit.Set #"
        String styledTitle = "<big>Edit.<font color='#33aebe'>Set " + (groupPos+1) + "</font></big>";
        setTitle(Html.fromHtml(styledTitle));
    }
}
