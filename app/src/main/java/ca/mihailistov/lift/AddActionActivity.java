package ca.mihailistov.lift;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AddActionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_add_action);

        RelativeLayout spacerOne = (RelativeLayout) findViewById(R.id.spacer_one);
        RelativeLayout spacerTwo = (RelativeLayout) findViewById(R.id.spacer_two);
        ImageView closeButton = (ImageView) findViewById(R.id.add_close_button);
        TextView searchAll = (TextView) findViewById(R.id.search_all);

        searchAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddActionActivity.this, AddExerciseActivity.class);
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
