package com.justlift.mihai.lift;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.justlift.mihai.lift.Realm.RealmExercise;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MainActivity extends AppCompatActivity {
    private static DatabaseHelper myDbHelper;
    private Realm realm;
    private CustomTabLayout tabLayout;
    private MenuItem calendarMenu;
    public static ViewPager viewPager;
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
    private Toolbar toolbar;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle drawerToggle;
    public static CoordinatorLayout coordinatorLayout;
    public static Snackbar snackbarRemove, sbEditMode, sbUpdated;

    @Override
    protected void onResume(){
        super.onResume();

        if (EditExerciseActivity.noSets){
            myDbHelper.addSet(EditExerciseActivity.fragmentNum, EditExerciseActivity.exerciseNum,
                    EditExerciseActivity.exerciseName, 0, 0);
        }

        if (setUpdated) {
            refreshFragment();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        instance = this;

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        realm = Realm.getInstance(realmConfiguration);

        myDbHelper = DatabaseHelper.getInstance(this);
        List<RealmExercise> realmExerciseList = new ArrayList<RealmExercise>();

        try {
            realmExerciseList = getRealmExercises();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("MainActivity","All exercises: " + realmExerciseList);

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

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        // Configure edit mode snackbar
        sbEditMode = Snackbar
                .make(MainActivity.coordinatorLayout, "Edit mode", Snackbar.LENGTH_INDEFINITE)
                .setAction("DONE", new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        MainActivity.menuMultipleActions.showMenu(true);
                        MainActivity.refreshFragment();
                    }
                });
        sbEditMode.setActionTextColor(Color.WHITE);

        View sbEditModeView = sbEditMode.getView();
        sbEditModeView.setBackgroundColor(getResources().getColor(R.color.redDelete));

        // Configure update notifcation snackbar
        sbUpdated = Snackbar
                .make(coordinatorLayout, "Updated workout", Snackbar.LENGTH_LONG);
        View sbUpdatedView = sbUpdated.getView();
        sbUpdatedView.setBackgroundColor(getResources().getColor(R.color.greenUpdate));

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
                        editEnabled = false;
                        removeEnabled = false;
                        menuMultipleActions.showMenu(true);

                        String workoutTitle = input.getText().toString();

                        myDbHelper.setWorkoutTitle(fragNum, workoutTitle);
                        setActionBarTitle("Lift." + myDbHelper.getWorkoutTitle(fragNum));

                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Updated title", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(getResources().getColor(R.color.greenUpdate));
                        snackbar.show();
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

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = (FragmentActivity)viewPager.getContext();
                FragmentManager manager = activity.getSupportFragmentManager();

                FragmentPage page = adapter.getItem(viewPager.getCurrentItem());

                if (page != null && page.isAdded()) {
                    page.setEditModeEnabled();
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuMultipleActions.close(true);

                int fragNum = viewPager.getCurrentItem();

                Intent intent = new Intent(MainActivity.getInstance(), AddExerciseActivity.class);
                intent.putExtra("fragNum", fragNum);
                startActivityForResult(intent, 1);

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

        tabLayout = (CustomTabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSmoothScrollingEnabled(true);

        // determine the day of the week it is today
        setTabToCurrentDate();

//        MenuItem dateMenuItem = menu.findItem(R.id.calendar);

        // update title bar based on tab/view changes
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position);

                String exerciseTitle = myDbHelper.getWorkoutTitle(position);

                if (exerciseTitle.matches("")) {
                    Log.e("MainActivity","No workout title!");
                    exerciseTitle = "Lift.<font color='#95d5dd'>Tap to title</font>";
                    toolbar.setTitle(Html.fromHtml(exerciseTitle));
                } else
                    setActionBarTitle("Lift." + exerciseTitle);
            }
        });

    }

    public List<RealmExercise> getRealmExercises() throws IOException {
        loadJsonFromStream();
//        loadJsonFromJsonObject();
//        loadJsonFromString();

        return realm.where(RealmExercise.class).findAll();
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("exerciseBible.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void loadJsonFromStream() throws IOException {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("exercises"); // exercises array (1066 total)
            ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> m_li;

            for (int i=0; i < m_jArry.length(); i++){
                JSONObject jo_inside = m_jArry.getJSONObject(i); // gets a single exercise
                RealmExercise realmExercise = new RealmExercise();
                realmExercise.name = jo_inside.getString("name");
                realmExercise.rating = jo_inside.getString("rating");
                realmExercise.type = jo_inside.getString("type");
                realmExercise.muscle = jo_inside.getString("muscle");
                realmExercise.otherMuscles = jo_inside.getString("otherMuscles");
                realmExercise.equipment = jo_inside.getString("equipment");
                realmExercise.mechanics = jo_inside.getString("mechanics");
                realmExercise.level = jo_inside.getString("level");
                realmExercise.force = jo_inside.getString("force");

//                JSONArray m_kArry = obj.getJSONArray("guideimgurls");
//
//                for (int j=0; j < m_kArry.length(); j++){
//                    String test2 = m_kArry.get(j).toString();
//                    Log.d("tag", test2);
//                }

//                JSONArray m_kArry = obj.getJSONArray("guide_imgurls");
//                realmExercise.guide_imgurls = jo_inside.getString("guide_imgurls");
//                public RealmList<RealmString> guide_items;
//                public String note_title;
//                public RealmList<RealmString> notes;
//                public String sport;
//                public String url;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        realm.beginTransaction();
//        try {
//            realm.createObjectFromJson(RealmExercise.class, stream);
//            realm.commitTransaction();
//        } catch (IOException e) {
//            realm.cancelTransaction();
//        } finally {
//            if (stream != null) {
//                stream.close();
//            }
//        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                ArrayList<String> exercisesAdded;
                exercisesAdded = data.getStringArrayListExtra("exercisesAdded");

                int fragNum = viewPager.getCurrentItem();

                for (int i=0;i<exercisesAdded.size();i++) {
                    myDbHelper.addExercise(fragNum, exercisesAdded.get(i));
                }
//                refreshFragment();
            }
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
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
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        menuMultipleActions.setIconToggleAnimatorSet(set);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public static void updatedSet(){
        setUpdated = true;
    }

    public static void refreshFragment(){
        FragmentActivity activity = (FragmentActivity)viewPager.getContext();
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentPage page = adapter.getItem(viewPager.getCurrentItem());

        if (page.updatedOrder && page != null && page.isAdded()) {
            int updatedFragNum = page.mNum;
            myDbHelper.updateExerciseNum(updatedFragNum, page.listDataHeader, page.updatedHeader);
            Log.e("MainActivity", "New exercise order:\n" + page.updatedHeader);
            page.updatedOrder = false;
        }

        if(sbEditMode.isShown())
            sbEditMode.dismiss();

        if (menuMultipleActions.isMenuHidden())
            menuMultipleActions.showMenu(true);

        setUpdated=false;
        removeEnabled = false;
        editEnabled = false;

        sbUpdated.show();

        if (page != null && page.isAdded()) {
            manager.beginTransaction()
                    .detach(page)
                    .attach(page)
                    .commit();
        }
    }

    public static boolean getEditState(){
        return editEnabled;
    }

    public static void setEditEnabled(){
        editEnabled = true;
    }

    public static void setEditDisabled(){editEnabled = false;}

    public static boolean getRemoveState(){ return removeEnabled; }

    public static void setRemoveEnabled() { removeEnabled = true;}

    public static void setRemoveDisabled() { removeEnabled = false;}

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

    public void setTabToCurrentDate(){
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

        // set title bar title based on today's day of the week
        String exerciseTitle = myDbHelper.getWorkoutTitle(tabNumber);

        if (exerciseTitle.matches("")) {
            exerciseTitle = "Lift.<font color='#95d5dd'>Tap to title</font>";
            toolbar.setTitle(Html.fromHtml(exerciseTitle));
        } else
            setActionBarTitle("Lift." + exerciseTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        calendarMenu = menu.findItem(R.id.calendar_menu);

        Bitmap calendarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_calendar);
        Bitmap mutableBitmap = calendarBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setTextSize(45);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.CENTER);

        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;
        yPos += 7.5;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("d");
        canvas.drawText(df.format(c.getTime()), xPos, yPos, paint);

        Drawable calIcon = new BitmapDrawable(getResources(), mutableBitmap);
        calendarMenu.setIcon(calIcon);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home){
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.edit_title) {
            toolbar.performClick();
            return true;
        } else if (id == R.id.calendar_menu) {
            setTabToCurrentDate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
