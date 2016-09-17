package ca.mihailistov.lift;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;

public class AddExerciseActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_add_exercise);

        toolbar = (Toolbar) findViewById(R.id.dialog_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Choose category");
    }
}
