package ca.mihailistov.lift.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ca.mihailistov.lift.R;
import ca.mihailistov.lift.fragments.LiftFragment;
import ca.mihailistov.lift.realm.RealmCategory;
import ca.mihailistov.lift.realm.RealmExercise;
import ca.mihailistov.lift.realm.RealmExerciseData;
import ca.mihailistov.lift.realm.RealmSet;
import ca.mihailistov.lift.realm.RealmString;
import ca.mihailistov.lift.realm.RealmWorkout;
import ca.mihailistov.lift.sync.VolleySingleton;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Realm realm;
    private Menu menu;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();

        realm = Realm.getDefaultInstance();
        RealmResults<RealmExerciseData> dataQuery = realm.where(RealmExerciseData.class).findAll();

        if (dataQuery.size() == 0)
            loadJSONFromWeb();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
        selectDrawerItem(nvDrawer.getMenu().getItem(0));
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_lift_fragment:
                fragmentClass = LiftFragment.class;
                break;
            default:
                fragmentClass = LiftFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
            case R.id.create_data:
                Realm realm = Realm.getDefaultInstance();

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
                final String currentDate = df.format(c.getTime());

                RealmWorkout realmWorkout = null;
                try {
                    realmWorkout = realm.where(RealmWorkout.class).equalTo("date", currentDate).findFirst();
                } catch (Exception e) {
                    Log.e(TAG, "realm error: " + e);
                }

                if (realmWorkout == null){
                    // create new workout
                    realmWorkout = new RealmWorkout();
                }

                RealmResults<RealmExerciseData> realmExerciseDataList = realm.where(RealmExerciseData.class)
                        .findAll();

                for (int i=0;i<3;i++){
                    RealmExerciseData realmExerciseData = null;
                    try {
                        realmExerciseData = realmExerciseDataList.get(i+30);
                    } catch (Exception e) {
                        Log.e(TAG, "realm error: " + e);
                    }

                    final RealmExercise realmExercise = new RealmExercise();
                    realmExercise.realmExerciseData = realmExerciseData;

                    RealmList<RealmSet> realmSetList = new RealmList<>();
                    for (int j=0;j<4;j++){
                        RealmSet realmSet = new RealmSet();
                        realmSet.reps = 5;
                        realmSet.weight = 135;
                        realmSetList.add(realmSet);
                    }

                    realmExercise.realmSets =  realmSetList;

                    if (realmWorkout.exercises == null)
                        realmWorkout.exercises = new RealmList<RealmExercise>();

                    final RealmWorkout finalRealmWorkout = realmWorkout;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            finalRealmWorkout.exercises.add(realmExercise);
                        }
                    });
                }

//                final RealmWorkout finalRealmWorkout = realmWorkout;
//                realm.executeTransaction(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//                        realm.copyToRealm(finalRealmWorkout);
//                    }
//                });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadJSONFromWeb() {
        final String categoriesUrl = getResources().getString(R.string.url_categories);
        JsonArrayRequest categoriesReq = new JsonArrayRequest(categoriesUrl, new Response.Listener<JSONArray> () {
            @Override
            public void onResponse(JSONArray response) {
                Log.e(TAG, response.toString());

                List<RealmCategory> defaultRealmCategoryList = new ArrayList<RealmCategory>();
                for (int i = 0; i < response.length(); i++) {
                    RealmCategory newRealmCategory = new RealmCategory();
                    newRealmCategory.id = i + 1;
                    try {
                        newRealmCategory.name = response.getString(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    defaultRealmCategoryList.add(newRealmCategory);
                }
                final List<RealmCategory> finalDefaultRealmCategoryList = defaultRealmCategoryList;

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(finalDefaultRealmCategoryList);
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        VolleySingleton.getInstance().getRequestQueue().add(categoriesReq);

        final String exercisesUrl = getResources().getString(R.string.url_exercises);
        JsonArrayRequest exercisesReq = new JsonArrayRequest(exercisesUrl, new Response.Listener<JSONArray> () {
            @Override
            public void onResponse(JSONArray response) {
                Log.e(TAG, response.toString());
                List<RealmExerciseData> realmExerciseDataList = new ArrayList<RealmExerciseData>();
                try {
                    JSONArray exercises = response;

//            Log.wtf("wtf","# of exercises: "+ exercises.length());
                    for (int i=0; i < exercises.length(); i++)
                    {
                        RealmExerciseData realmExerciseData = new RealmExerciseData();
                        JSONObject exercise = new JSONObject();
                        try {
                            exercise = exercises.getJSONObject(i); // gets a single exercise

//                    Log.wtf("wtf",String.format("\"id\": %d", i+1));
                            realmExerciseData.id = i+1;

                            // if the exercise has no name, there's no point in importing it into the db
                            if (exercise.has("name") && !exercise.getString("name").isEmpty() &&
                                    exercise.getString("name") != null) {
                                realmExerciseData.name = exercise.getString("name");

                                if (exercise.has("rating") && !exercise.getString("rating").isEmpty() &&
                                        exercise.getString("muscle") != null) {
                                    realmExerciseData.rating = Double.parseDouble(exercise.getString("rating"));
                                }

                                if (exercise.has("type") && !exercise.getString("type").isEmpty() &&
                                        exercise.getString("type") != null) {
                                    realmExerciseData.type = exercise.getString("type");
                                }

                                if (exercise.has("muscle") && !exercise.getString("muscle").isEmpty() &&
                                        exercise.getString("muscle") != null) {
                                    realmExerciseData.muscle = exercise.getString("muscle");

                                    String muscle = exercise.getString("muscle");

                                    if (muscle.contains("Back") || muscle.contains("Lats"))
                                        realmExerciseData.category = "Back";
                                    else if (muscle.contains("Abductors") || muscle.contains("Adductors") ||
                                            muscle.contains("Calves") || muscle.contains("Glutes") ||
                                            muscle.contains("Hamstrings") || muscle.contains("Quadriceps"))
                                        realmExerciseData.category = "Legs";
                                    else if (muscle.contains("Traps") || muscle.contains("Neck"))
                                        realmExerciseData.category = "Shoulders";
                                    else
                                        realmExerciseData.category = muscle;
                                }

                                if (exercise.has("other_muscles") && !exercise.getString("other_muscles").isEmpty() &&
                                        exercise.getString("other_muscles") != null) {
                                    realmExerciseData.other_muscles = exercise.getString("other_muscles");
                                }

                                if (exercise.has("equipment") && !exercise.getString("equipment").isEmpty() &&
                                        exercise.getString("equipment") != null) {
                                    realmExerciseData.equipment = exercise.getString("equipment");
                                }

                                if (exercise.has("mechanics") && !exercise.getString("mechanics").isEmpty() &&
                                        exercise.getString("mechanics") != null) {
                                    realmExerciseData.mechanics = exercise.getString("mechanics");
                                }

                                if (exercise.has("level") && !exercise.getString("level").isEmpty() &&
                                        exercise.getString("muscle") != null) {
                                    realmExerciseData.level = exercise.getString("level");
                                }

                                if (exercise.has("force") && !exercise.getString("force").isEmpty() &&
                                        exercise.getString("muscle") != null) {
                                    realmExerciseData.force = exercise.getString("force");
                                }

                                if (exercise.has("guide_imgurls") && !exercise.getString("guide_imgurls").isEmpty() &&
                                        exercise.get("guide_imgurls") != null) {
                                    realmExerciseData.guide_imgurls = exercise.getJSONArray("guide_imgurls").getString(0);
                                }

                                realmExerciseData.sport = exercise.getString("sport");
                                realmExerciseData.url = exercise.getString("url");

                                RealmList<RealmString> realmGuideItems = new RealmList<RealmString>(); // loop thorugh string arrays for
                                // guide_items, imgurls,

                                JSONArray guide_items = new JSONArray();
                                try {
                                    guide_items = exercise.getJSONArray("guide_items");

                                    for (int j = 0; j < guide_items.length(); j++) {
                                        RealmString guide_item = new RealmString();

                                        if (!guide_items.getString(j).isEmpty() && guide_items.get(j) != null &&
                                                !guide_items.getString(j).equals("null") && !guide_items.getString(j).contains("\n")) {
                                            guide_item.val = guide_items.getString(j);
                                            realmGuideItems.add(guide_item);
                                        }
                                    }
                                } catch (Exception e) {
//                            Log.e("json error", "error" + e);
                                } finally {
                                    realmExerciseData.guide_items = realmGuideItems;
                                }

                                RealmList<RealmString> realmImgurls = new RealmList<RealmString>(); // loop thorugh string arrays for
                                // guide_items, imgurls,

                                JSONArray imgurls = new JSONArray();
                                try {
                                    imgurls = exercise.getJSONArray("imgurls");

                                    for (int j = 0; j < imgurls.length(); j++) {
                                        RealmString imgurl = new RealmString();

                                        if (!imgurls.getString(j).isEmpty() && imgurls.get(j) != null &&
                                                !imgurls.getString(j).equals("null") && !imgurls.getString(j).contains("\n")) {
                                            imgurl.val = imgurls.getString(j);
                                            realmImgurls.add(imgurl);
                                        }
                                    }
                                } catch (Exception e) {
//                            Log.e("json error", "error" + e);
                                } finally {
                                    realmExerciseData.imgurls = realmImgurls;
                                }

                                RealmList<RealmString> realmNotes = new RealmList<RealmString>(); // loop thorugh string arrays for
                                // guide_items, imgurls,

                                JSONArray notes = new JSONArray();
                                try {
                                    notes = exercise.getJSONArray("notes");

                                    for (int j = 0; j < notes.length(); j++) {
                                        RealmString note = new RealmString();

                                        if (!notes.getString(j).isEmpty() && notes.get(j) != null &&
                                                !notes.getString(j).equals("null") && !notes.getString(j).contains("\n")) {
                                            note.val = notes.getString(j);
                                            realmNotes.add(note);
                                        }
                                    }
                                } catch (Exception e) {
//                            Log.e("json error", "error" + e);
                                } finally {
                                    realmExerciseData.notes = realmNotes;
                                }


                                realmExerciseDataList.add(realmExerciseData);
                            }


                        } catch (Exception e) {
                            Log.e("realm error", "error" + e + " number: " + i);
                        }
                    }

                } finally {
                    final List<RealmExerciseData> finalRealmExerciseDataList = realmExerciseDataList;
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction(){
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealm(finalRealmExerciseDataList);
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        VolleySingleton.getInstance().getRequestQueue().add(exercisesReq);
    }
}
