package com.justlift.mihai.lift;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton addButton = (ImageButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v){
                Toast.makeText(MainActivity.this, "Updated",Toast.LENGTH_SHORT).show();
                OneFragment.groups[0] = "Barbell Front Squat";
                OneFragment.elv.invalidateViews();
            }
        });

//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.ic_action_icon);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        CustomTabLayout tabLayout = (CustomTabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

//        for (int ii = 0; ii < tabLayout.getTabCount(); ii++) {
//            tabLayout.getTabAt(ii).setCustomView(R.layout.tab_view);
//        }

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        int tabNumber = 0;

        switch(day)
        {
            case Calendar.SUNDAY:
                tabNumber = 0;
                break;
            case Calendar.MONDAY:
                tabNumber = 1;
                break;
            case Calendar.TUESDAY:
                tabNumber = 2;
                break;
            case Calendar.WEDNESDAY:
                tabNumber = 3;
                break;
            case Calendar.THURSDAY:
                tabNumber = 4;
                break;
            case Calendar.FRIDAY:
                tabNumber = 5;
                break;
            case Calendar.SATURDAY:
                tabNumber = 6;
                break;
        }

        tabLayout.getTabAt(tabNumber).select();

        final String[] titleStrings = {"Lift.Rest",
                                       "Lift.Chest",
                                       "Lift.Back",
                                       "Lift.Chest/Cardio",
                                       "Lift.Legs",
                                       "Lift.Shoulders+Chest",
                                       "Lift.Rest"};

        setActionBarTitle(titleStrings[tabNumber]);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position);
                setActionBarTitle(titleStrings[position]);
            }
        });

    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    public static String dayOfWeek(int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, days);
        SimpleDateFormat df = new SimpleDateFormat("d");
        return df.format(cal.getTime());
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "SUN\n" + dayOfWeek(0));    // Tu W Th F Sa Su M
        adapter.addFragment(new TwoFragment(), "MON\n" + dayOfWeek(1));;
        adapter.addFragment(new ThreeFragment(), "TUE\n" + dayOfWeek(2));;
        adapter.addFragment(new FourFragment(), "WED\n" + dayOfWeek(3));
        adapter.addFragment(new FiveFragment(), "THU\n" + dayOfWeek(4));
        adapter.addFragment(new SixFragment(), "FRI\n" + dayOfWeek(5));
        adapter.addFragment(new SevenFragment(), "SAT\n" + dayOfWeek(6));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
