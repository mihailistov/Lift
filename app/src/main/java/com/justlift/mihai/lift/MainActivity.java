package com.justlift.mihai.lift;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    public ViewPager viewPager;
    public static boolean setUpdated = false;
    private static MainActivity instance;
    private static boolean editEnabled = false;
    private String exerciseName;
    ViewPagerAdapter adapter = null;

    @Override
    protected void onResume(){
        super.onResume();

        if (setUpdated) {
            adapter.notifyDataSetChanged();
            setUpdated=false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        instance = this;

        final DatabaseHelper myDbHelper;
        myDbHelper = new DatabaseHelper(this);

        try {

            myDbHelper.createDatabase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            myDbHelper.createDatabase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            myDbHelper.openDatabase();

        }catch(SQLException sqle){

            try {
                throw sqle;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        PrefManager.getInstance().Initalize(getApplicationContext());

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionMenu menuMultipleActions = (FloatingActionMenu) findViewById(R.id.fabmenu);
        createCustomAnimation();
        menuMultipleActions.setClosedOnTouchOutside(true);

        final FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_button);
        final FloatingActionButton editButton = (FloatingActionButton) findViewById(R.id.edit_button);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuMultipleActions.close(true);
                Toast.makeText(MainActivity.this, "Select exercise to edit", Toast.LENGTH_LONG).show();
                editEnabled = true;
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuMultipleActions.close(true);
                AlertDialog.Builder alert = new AlertDialog.Builder(instance);

                alert.setTitle("New Exercise");
                alert.setMessage("Enter name");

                final EditText input = new EditText(instance);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exerciseName = input.getText().toString();
//                        exerciseName = myDbHelper.addExercise(exerciseName);
//                        exerciseName = myDbHelper.returnExerciseName();

                        int fragNum = viewPager.getCurrentItem();
                        Log.e("MainActivity", "Current fragment number: " + fragNum);

                        myDbHelper.addExercise(fragNum, exerciseName);
//                        myDbHelper.getExerciseHeaders(fragNum);
//                        myDbHelper.getExerciseSets(fragNum);

//                        if (!myDbHelper.workoutExists(fragNum)){
//                            myDbHelper.createWorkout(fragNum);
//                        } else {
//                            long rowId = myDbHelper.getWorkoutId(fragNum);
//                        }

//                        String emptyChild[] = {""};
//                        String fullChild[] = {"Set 1: 20x45", "Set 2: 12x95", "Set 3: 8x135", "Set 4: 8x185", "Set 5: 5x225"};

//                        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:"
//                                + R.id.viewpager + ":" + viewPager.getCurrentItem());

//                        ElvDataHandler.addEntry(viewPager.getCurrentItem(), exerciseName, fullChild);
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

        Calendar cal = Calendar.getInstance();

        for (int i=-7; i<7;i++){
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            cal.add(Calendar.DAY_OF_WEEK, i);
            SimpleDateFormat df = new SimpleDateFormat("EEE");
            String dayOfWeekEEE =  df.format(cal.getTime());

            FragmentPage newFrag = new FragmentPage().newInstance(i + 7);

            adapter.addFragment(newFrag, dayOfWeekEEE + "\n" + dayOfWeek(i));
        }

//        adapter.addFragment(new FragmentPage().newInstance(0), "SUN\n" + dayOfWeek(-7));    // Tu W Th F Sa Su M
//        adapter.addFragment(new FragmentPage().newInstance(1), "MON\n" + dayOfWeek(-6));;
//        adapter.addFragment(new FragmentPage().newInstance(2), "TUE\n" + dayOfWeek(-5));;
//        adapter.addFragment(new FragmentPage().newInstance(3), "WED\n" + dayOfWeek(-4));
//        adapter.addFragment(new FragmentPage().newInstance(4), "THU\n" + dayOfWeek(-3));
//        adapter.addFragment(new FragmentPage().newInstance(5), "FRI\n" + dayOfWeek(-2));
//        adapter.addFragment(new FragmentPage().newInstance(6), "SAT\n" + dayOfWeek(-1));
//        adapter.addFragment(new FragmentPage().newInstance(7), "SUN\n" + dayOfWeek(0));    // Tu W Th F Sa Su M
//        adapter.addFragment(new FragmentPage().newInstance(8), "MON\n" + dayOfWeek(1));;
//        adapter.addFragment(new FragmentPage().newInstance(9), "TUE\n" + dayOfWeek(2));;
//        adapter.addFragment(new FragmentPage().newInstance(10), "WED\n" + dayOfWeek(3));
//        adapter.addFragment(new FragmentPage().newInstance(11), "THU\n" + dayOfWeek(4));
//        adapter.addFragment(new FragmentPage().newInstance(12), "FRI\n" + dayOfWeek(5));
//        adapter.addFragment(new FragmentPage().newInstance(13), "SAT\n" + dayOfWeek(6));

        viewPager.setAdapter(adapter);

        final CustomTabLayout tabLayout = (CustomTabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSmoothScrollingEnabled(true);

//        tabLayout.setOnTabSelectedListener(
//                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
//                    @Override
//                    public void onTabSelected(TabLayout.Tab tab) {
//                        super.onTabSelected(tab);
//                        int tabNum = viewPager.getCurrentItem();
////                        Display display = getWindowManager().getDefaultDisplay();
////                        Point size = new Point();
////                        display.getSize(size);
////                        int width = size.x;
//
//                        Log.e("MainActivity","Current tabNum: " + tabNum);
//                        if (tabNum >= 7) {
//                            tabLayout.smoothScrollTo(1078,0);
//                            Log.e("MainActivity", "Scroll position: " + tabLayout.getScrollX());
//                        }
//                    }
//                });


        // determine the day of the week it is today
        cal = Calendar.getInstance();
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
//                tabLayout.scrollTo(tabLayout.getChildAt(position), 0);
//                tabLayout.getTabAt(position).select();
//                if (position > 6)
//                    tabLayout.setScrollX(1078);
//                tabLayout.calculateScrollXForTab();
                setActionBarTitle(titleStrings[position]);
            }
        });

    }

    private void createCustomAnimation() {
        final FloatingActionMenu menuMultipleActions = (FloatingActionMenu) findViewById(R.id.fabmenu);

        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(menuMultipleActions.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(menuMultipleActions.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(menuMultipleActions.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(menuMultipleActions.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                menuMultipleActions.getMenuIconView().setImageResource(menuMultipleActions.isOpened()
                        ? R.drawable.ic_close : R.drawable.ic_list);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        menuMultipleActions.setIconToggleAnimatorSet(set);
    }

//    public static void editExercise(int fragmentNum, int groupPosition){
//        int exerciseNum = groupPosition + 1;
//
//        Intent intent = new Intent(MainActivity.this, EditExerciseActivity.class);
//        Bundle b = new Bundle();
//        b.putInt("fragmentNum", fragmentNum);
//        b.putInt("exerciseNum", exerciseNum);
//        intent.putExtras(b);
//
//        startActivity(intent);
//        finish();
//    }

    public static MainActivity getInstance() {
        return instance;
    }

    public static void updatedSet(){
        setUpdated = true;
    }

    public static boolean getEditState(){
        return editEnabled;
    }

    public static void setEditEnabled(){
        editEnabled = true;
    }

    public static void setEditDisabled(){
        editEnabled = false;
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
