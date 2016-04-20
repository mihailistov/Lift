package com.justlift.mihai.lift;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
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
    private static boolean removeEnabled = false;
    private String exerciseName;
    public static ViewPagerAdapter adapter = null;
    static FloatingActionMenu cancelAction;
    static FloatingActionMenu menuMultipleActions;
    static FloatingActionButton addButton;
    static FloatingActionButton editButton;
    static FloatingActionButton copyButton;
    static FloatingActionButton removeButton;
    Toolbar toolbar;

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int fragNum = viewPager.getCurrentItem();
                AlertDialog.Builder alert = new AlertDialog.Builder(instance);

                alert.setTitle("Edit Workout Title");
//                alert.setMessage("Enter name");

                final EditText input = new EditText(instance);
                input.setText(myDbHelper.getWorkoutTitle(fragNum));
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String workoutTitle = input.getText().toString();

                        myDbHelper.setWorkoutTitle(fragNum, workoutTitle);
                        setActionBarTitle("Lift." + myDbHelper.getWorkoutTitle(fragNum));

                        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                                .coordinatorLayout);

                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Updated title", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(getResources().getColor(R.color.greenUpdate));
                        snackbar.show();

//                        Configuration croutonConfig = new Configuration.Builder()
//                                .setInAnimation(android.R.anim.slide_in_left)
//                                .setOutAnimation(android.R.anim.slide_out_right)
//                                .build();
//
////                        Toast.makeText(MainActivity.this, "Updated workout title!", Toast.LENGTH_SHORT).show();
//                        Style updatedStyle = new Style.Builder()
//                                .setConfiguration(croutonConfig)
//                                .setBackgroundColor(R.color.greenUpdate)
//                                .setTextColor(R.color.white)
//                                .build();
//
//                        Crouton updatedTitleMsg = Crouton.makeText(MainActivity.this, "Updated title", updatedStyle, R.id.alternate_view_group);
//                        updatedTitleMsg.show();

//                        AppMsg appMsg = AppMsg.makeText(MainActivity.this, "Updated title", AppMsg.STYLE_INFO);
//                        appMsg.setDuration(AppMsg.LENGTH_SHORT);
//                        appMsg.setLayoutGravity(Gravity.BOTTOM);
//                        appMsg.show();
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

        menuMultipleActions = (FloatingActionMenu) findViewById(R.id.fabmenu);
        cancelAction = (FloatingActionMenu) findViewById(R.id.cancel_action);
        createCustomAnimation();
        menuMultipleActions.setClosedOnTouchOutside(true);

        addButton = (FloatingActionButton) findViewById(R.id.add_button);
        editButton = (FloatingActionButton) findViewById(R.id.edit_button);
        removeButton = (FloatingActionButton) findViewById(R.id.remove_button);
        copyButton = (FloatingActionButton) findViewById(R.id.copy_button);

//        cancelAction.setOnMenuButtonClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v){
//                cancelAction();
//            }
//        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addButton.setLabelVisibility(View.INVISIBLE);
//                editButton.setLabelVisibility(View.INVISIBLE);
//                removeButton.setLabelVisibility(View.INVISIBLE);
//                copyButton.setLabelVisibility(View.INVISIBLE);
//
//                menuMultipleActions.close(true);
//                menuMultipleActions.setAnimated(false);
//                menuMultipleActions.setIconAnimated(false);
//                menuMultipleActions.setVisibility(View.GONE);
//
//                Toast.makeText(MainActivity.this, "Select an exercise/set to remove", Toast.LENGTH_LONG).show();
//                removeEnabled = true;
//                cancelAction.setVisibility(View.VISIBLE);
//                cancelAction.setMenuButtonLabelText("Cancel remove");
//                cancelAction.open(true);

                menuMultipleActions.close(true);
//                menuMultipleActions.setVisibility(View.GONE);
                menuMultipleActions.hideMenu(true);
                removeEnabled = true;

                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                        .coordinatorLayout);

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Select exercise/set to remove", Snackbar.LENGTH_INDEFINITE)
                        .setAction("CANCEL", new View.OnClickListener(){
                            @Override
                            public void onClick(View view){
                                removeEnabled = false;
//                                menuMultipleActions.setVisibility(View.VISIBLE);
                                menuMultipleActions.showMenu(true);
                           }
                        });
                snackbar.setActionTextColor(Color.WHITE);

                View snackBarView = snackbar.getView();
                TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackBarView.setBackgroundColor(getResources().getColor(R.color.redDelete));
                snackbar.show();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addButton.setLabelVisibility(View.INVISIBLE);
//                editButton.setLabelVisibility(View.INVISIBLE);
//                removeButton.setLabelVisibility(View.INVISIBLE);
//                copyButton.setLabelVisibility(View.INVISIBLE);
//
//                menuMultipleActions.close(true);
//                menuMultipleActions.setAnimated(false);
//                menuMultipleActions.setIconAnimated(false);
//                menuMultipleActions.setVisibility(View.GONE);
//
//                Toast.makeText(MainActivity.this, "Select an exercise/set to edit", Toast.LENGTH_LONG).show();
//                editEnabled = true;
//                cancelAction.setVisibility(View.VISIBLE);
//                cancelAction.setMenuButtonLabelText("Cancel edit");
//                cancelAction.open(true);

                menuMultipleActions.close(true);
//                menuMultipleActions.setVisibility(View.GONE);
                menuMultipleActions.hideMenu(true);
                editEnabled = true;

                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                        .coordinatorLayout);

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Select exercise/set to edit", Snackbar.LENGTH_INDEFINITE)
                        .setAction("CANCEL", new View.OnClickListener(){
                            @Override
                            public void onClick(View view){
                                editEnabled = false;
//                                menuMultipleActions.setVisibility(View.VISIBLE);
                                menuMultipleActions.showMenu(true);
                            }
                        });
                snackbar.setActionTextColor(Color.WHITE);

                View snackBarView = snackbar.getView();
                TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackBarView.setBackgroundColor(getResources().getColor(R.color.greenUpdate));
                snackbar.show();
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

                        int fragNum = viewPager.getCurrentItem();
                        Log.e("MainActivity", "Current fragment number: " + fragNum);

                        myDbHelper.addExercise(fragNum, exerciseName);
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

        viewPager.setAdapter(adapter);

        final CustomTabLayout tabLayout = (CustomTabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSmoothScrollingEnabled(true);

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
        setActionBarTitle("Lift." + myDbHelper.getWorkoutTitle(tabNumber));

        // update title bar based on tab/view changes
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position);
                setActionBarTitle("Lift." + myDbHelper.getWorkoutTitle(position));
            }
        });

    }

    private void createCustomAnimation() {
        final FloatingActionMenu menuMultipleActions = (FloatingActionMenu) findViewById(R.id.fabmenu);
//        final FloatingActionMenu cancelAction = (FloatingActionMenu) findViewById(R.id.cancel_action);

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
//                cancelAction.getMenuIconView().setImageResource(removeEnabled
//                        ? R.drawable.ic_delete : R.drawable.ic_edit);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        menuMultipleActions.setIconToggleAnimatorSet(set);
//        cancelAction.setIconToggleAnimatorSet(set);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public static void updatedSet(){ setUpdated = true; }

    public static boolean getEditState(){
        return editEnabled;
    }

    public static void setEditEnabled(){
        editEnabled = true;
    }

    public static void setEditDisabled(){
        editEnabled = false;
    }

    public static boolean getRemoveState(){ return removeEnabled; }

    public static void setRemoveEnabled() { removeEnabled = true;}

    public static void setRemoveDisabled() { removeEnabled = false;}

    public static void cancelAction(){
        cancelAction.setVisibility(View.GONE);
        menuMultipleActions.close(true);
        menuMultipleActions.setVisibility(View.VISIBLE);
        removeEnabled = false;
        editEnabled = false;

        menuMultipleActions.setAnimated(true);
        menuMultipleActions.setIconAnimated(true);
        addButton.setLabelVisibility(View.VISIBLE);
        editButton.setLabelVisibility(View.VISIBLE);
        removeButton.setLabelVisibility(View.VISIBLE);
        copyButton.setLabelVisibility(View.VISIBLE);
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
        } else if (id == R.id.edit_title) {
            toolbar.performClick();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
