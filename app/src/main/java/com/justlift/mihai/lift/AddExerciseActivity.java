package com.justlift.mihai.lift;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

/**
 * Created by mihai on 16-04-21.
 */
public class AddExerciseActivity extends AppCompatActivity {
    private String[] titles = {"Legs","Back","Chest","Arms"};

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_exercise);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_add);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (int i=0;i<4;i++) {
            FragmentAdd newFrag = new FragmentAdd().newInstance(i);
            adapter.addFragment(newFrag, titles[i]);
        }

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);

        CustomTabLayout tabLayout = (CustomTabLayout) findViewById(R.id.tabs_add);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSmoothScrollingEnabled(true);
    }
}
