package com.justlift.mihai.lift;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    public ViewPager viewPager;
    private static MainActivity instance;
    private String exerciseName;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        instance = this;

        PrefManager.getInstance().Initalize(getApplicationContext());

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.fabmenu);
        final FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuMultipleActions.collapse();
                AlertDialog.Builder alert = new AlertDialog.Builder(instance);

                alert.setTitle("New Exercise");
                alert.setMessage("Enter name");

                final EditText input = new EditText(instance);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exerciseName = input.getText().toString();

                        String emptyChild[] = {""};
                        String fullChild[] = {"Set 1: 20x45", "Set 2: 12x95", "Set 3: 8x135", "Set 4: 8x185", "Set 5: 5x225"};

                        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:"
                                + R.id.viewpager + ":" + viewPager.getCurrentItem());

                        Log.i("MainActivity", "getView() - get item number " + viewPager.getCurrentItem());

                        ElvDataHandler.addEntry(viewPager.getCurrentItem(), exerciseName, fullChild);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();

            }
        });

//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.ic_action_icon);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentPage().newInstance(0), "SUN\n" + dayOfWeek(-7));    // Tu W Th F Sa Su M
        adapter.addFragment(new FragmentPage().newInstance(1), "MON\n" + dayOfWeek(-6));;
        adapter.addFragment(new FragmentPage().newInstance(2), "TUE\n" + dayOfWeek(-5));;
        adapter.addFragment(new FragmentPage().newInstance(3), "WED\n" + dayOfWeek(-4));
        adapter.addFragment(new FragmentPage().newInstance(4), "THU\n" + dayOfWeek(-3));
        adapter.addFragment(new FragmentPage().newInstance(5), "FRI\n" + dayOfWeek(-2));
        adapter.addFragment(new FragmentPage().newInstance(6), "SAT\n" + dayOfWeek(-1));
        adapter.addFragment(new FragmentPage().newInstance(7), "SUN\n" + dayOfWeek(0));    // Tu W Th F Sa Su M
        adapter.addFragment(new FragmentPage().newInstance(8), "MON\n" + dayOfWeek(1));;
        adapter.addFragment(new FragmentPage().newInstance(9), "TUE\n" + dayOfWeek(2));;
        adapter.addFragment(new FragmentPage().newInstance(10), "WED\n" + dayOfWeek(3));
        adapter.addFragment(new FragmentPage().newInstance(11), "THU\n" + dayOfWeek(4));
        adapter.addFragment(new FragmentPage().newInstance(12), "FRI\n" + dayOfWeek(5));
        adapter.addFragment(new FragmentPage().newInstance(13), "SAT\n" + dayOfWeek(6));

        viewPager.setAdapter(adapter);

        CustomTabLayout tabLayout = (CustomTabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // determine the day of the week it is today
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        int tabNumber = 0;
        switch (day) {
            case Calendar.SUNDAY:
                tabNumber = 7;
                break;
            case Calendar.MONDAY:
                tabNumber = 8;
                break;
            case Calendar.TUESDAY:
                tabNumber = 9;
                break;
            case Calendar.WEDNESDAY:
                tabNumber = 10;
                break;
            case Calendar.THURSDAY:
                tabNumber = 11;
                break;
            case Calendar.FRIDAY:
                tabNumber = 12;
                break;
            case Calendar.SATURDAY:
                tabNumber = 13;
                break;
        }

        // set tab to today's day of the week
        tabLayout.getTabAt(tabNumber).select();

        // title bar titles for each tab
        final String[] titleStrings = {"Lift.Rest",
                "Lift.Chest",
                "Lift.Back",
                "Lift.Chest/Cardio",
                "Lift.Legs",
                "Lift.Shoulders+Chest",
                "Lift.Rest",
                "Lift.Rest",
                "Lift.Chest",
                "Lift.Back",
                "Lift.Chest/Cardio",
                "Lift.Legs",
                "Lift.Shoulders+Chest",
                "Lift.Rest"};

        // set title bar title based on today's day of the week
        setActionBarTitle(titleStrings[tabNumber]);

        // update title bar based on tab/view changes
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position);
                setActionBarTitle(titleStrings[position]);
            }
        });

    }

    public static MainActivity getInstance() {
        return instance;
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
