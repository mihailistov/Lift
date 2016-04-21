package com.justlift.mihai.lift;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by mihai on 16-04-21.
 */
public class AddExerciseActivity extends AppCompatActivity {
    public ViewPager viewPager;
    public ViewPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
    }
}
