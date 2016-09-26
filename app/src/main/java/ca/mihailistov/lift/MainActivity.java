package ca.mihailistov.lift;

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
import android.view.Menu;
import android.view.MenuItem;

import ca.mihailistov.lift.Realm.RealmExercise;
import ca.mihailistov.lift.Realm.RealmExerciseData;
import ca.mihailistov.lift.Realm.RealmManager;
import ca.mihailistov.lift.Realm.RealmSet;
import ca.mihailistov.lift.Realm.RealmWorkout;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

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

        RealmResults<RealmExerciseData> dataQuery = realm.where(RealmExerciseData.class).findAll();

        if (dataQuery.size() == 0) {
            Intent intent = new Intent(this, RealmManager.class);
            intent.putExtra("SOME_KEY", "NOT NULL");
            this.startService(intent);
        }

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
                final RealmWorkout realmWorkout = new RealmWorkout();

                RealmResults<RealmExerciseData> realmExerciseDataList = realm.where(RealmExerciseData.class)
                        .findAll();

                RealmList<RealmExercise> realmExerciseList = new RealmList<>();

                for (int i=0;i<3;i++){
                    RealmExercise realmExercise = new RealmExercise();
                    RealmExerciseData realmExerciseData = realmExerciseDataList.get(i);
                    realmExercise.realmExerciseData = realmExerciseData;

                    RealmList<RealmSet> realmSetList = new RealmList<>();
                    for (int j=0;j<4;j++){
                        RealmSet realmSet = new RealmSet();
                        realmSet.reps = 5;
                        realmSet.weight = 135;
                        realmSetList.add(realmSet);
                    }

                    realmExercise.realmSets =  realmSetList;
                    realmExerciseList.add(realmExercise);
                }
                realmWorkout.exercises = realmExerciseList;

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(realmWorkout);
                    }
                });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
