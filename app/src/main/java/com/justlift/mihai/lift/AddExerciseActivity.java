package com.justlift.mihai.lift;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

/**
 * Created by mihai on 16-04-21.
 */
public class AddExerciseActivity extends AppCompatActivity {
    public ViewPager viewPager;
    public ViewPagerAdapter adapter;
    private CustomTabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_exercise);

        viewPager = (ViewPager) findViewById(R.id.viewpager_add);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        String[] titles = {"Legs","Back","Chest","Arms"};

        for (int i = 0; i < 4; i++) {
            FragmentAdd newFrag = new FragmentAdd().newInstance(i);
            adapter.addFragment(newFrag, titles[i]);
        }

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);
        tabLayout = (CustomTabLayout) findViewById(R.id.tabs_add);
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.setSmoothScrollingEnabled(true);
    }
}
