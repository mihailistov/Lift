package ca.mihailistov.lift.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.GridLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ca.mihailistov.lift.R;
import ca.mihailistov.lift.activities.AddActionActivity;
import ca.mihailistov.lift.activities.AddExerciseActivity;
import ca.mihailistov.lift.adapters.ExerciseExpandableAdapter;
import ca.mihailistov.lift.models.Exercise;
import ca.mihailistov.lift.models.ExerciseChild;
import ca.mihailistov.lift.realm.RealmExercise;
import ca.mihailistov.lift.realm.RealmExerciseData;
import ca.mihailistov.lift.realm.RealmSet;
import ca.mihailistov.lift.realm.RealmWorkout;
import io.realm.Realm;

import static ca.mihailistov.lift.R.id.search_all;

/**
 * Created by mihai on 16-09-04.
 */
public class LiftFragmentPage extends Fragment {
    int mNum;
    private static final String TAG = "LiftFragmentPage";
    private RecyclerView rv;
    private ExerciseExpandableAdapter mExerciseExpandableAdapter;
    private ArrayList<ParentListItem> parentListItems;

    public static LiftFragmentPage newInstance(int num) {
        LiftFragmentPage f = new LiftFragmentPage();

        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK) {
                Log.e(TAG, "LiftFragmentPage mNum = " + mNum + " received onActivityResult");
                parentListItems.clear();
                ArrayList<ParentListItem> newParentListItems = generateExercises();
                parentListItems.addAll(newParentListItems);

                mExerciseExpandableAdapter = new ExerciseExpandableAdapter(getContext(), parentListItems);
                rv.setAdapter(mExerciseExpandableAdapter);

                mExerciseExpandableAdapter.expandAllParents();

                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(llm);

                mExerciseExpandableAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        Log.e(TAG, "mNum = " + mNum);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lift_fragment_page, container, false);

        parentListItems = generateExercises();

        rv = (RecyclerView) rootView.findViewById(R.id.lift_recycler_view);
        GridLayout gridLayout = (GridLayout) rootView.findViewById(R.id.gridLayout);

        final FloatingActionMenu menuMultipleActions = (FloatingActionMenu) rootView.findViewById(R.id.fabmenu);
        FloatingActionButton addButton = (FloatingActionButton) rootView.findViewById(R.id.add_button);
        createCustomAnimation(rootView);
        menuMultipleActions.setClosedOnTouchOutside(true);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx,int dy){
                super.onScrolled(recyclerView, dx, dy);

                if (dy >0) {
                    // Scroll Down
                    if (menuMultipleActions.isShown()) {
                        menuMultipleActions.hideMenu(true);
                    }
                }
                else if (dy <0) {
                    // Scroll Up
                    if (!menuMultipleActions.isShown()) {
                        menuMultipleActions.showMenu(true);
                    }
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuMultipleActions.close(true);
                Intent intent = new Intent(getActivity(), AddActionActivity.class);
                Bundle b = new Bundle();
                b.putInt("mNum",mNum);
                intent.putExtras(b);
                startActivityForResult(intent, 1001);
            }
        });

        if (parentListItems.size() != 0) {
            rv.setVisibility(View.VISIBLE);
            gridLayout.setVisibility(View.GONE);
            menuMultipleActions.showMenu(true);

            Log.e("LiftFragmentPage","Found parentObjects to be length" + parentListItems.size());
            rv.setHasFixedSize(true);

            mExerciseExpandableAdapter = new ExerciseExpandableAdapter(getContext(), parentListItems);

            rv.setAdapter(mExerciseExpandableAdapter);

            mExerciseExpandableAdapter.expandAllParents();

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            rv.setLayoutManager(llm);
        } else {
            menuMultipleActions.hideMenu(true);
            rv.setVisibility(View.GONE);
            gridLayout.setVisibility(View.VISIBLE);
        }

        TextView searchAll = (TextView) rootView.findViewById(search_all);
        searchAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddExerciseActivity.class);
                Bundle b = new Bundle();
                b.putInt("mNum",mNum);
                intent.putExtras(b);
                startActivityForResult(intent, 1001);
            }
        });

        return rootView;
    }

    private ArrayList<ParentListItem> generateExercises() {
        ArrayList<ParentListItem> parentObjects = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_WEEK, mNum-15);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        Realm realm = Realm.getDefaultInstance();

        RealmWorkout realmWorkout;
        try {
            realmWorkout = realm.where(RealmWorkout.class)
                    .equalTo("date", df.format(c.getTime())).findFirst();
            List<RealmExercise> realmExerciseList = realmWorkout.exercises;
            List<RealmSet> realmSetList;

            for (int i = 0; i < realmExerciseList.size(); i++) {
                RealmExerciseData realmExerciseData = realmExerciseList.get(i).realmExerciseData;
                realmSetList = realmExerciseList.get(i).realmSets;

                ArrayList<Object> childList = new ArrayList<>();
                if (realmSetList.size() == 0){
                    childList.add(new ExerciseChild(1, 0, 0));
                } else {
                    for (int j = 0; j < realmSetList.size(); j++) {
                        childList.add(new ExerciseChild(j + 1, realmSetList.get(j).weight, realmSetList.get(j).reps));
                    }
                }
                Exercise exercise = new Exercise(realmExerciseData.name);
                exercise.setChildObjectList(childList);
                parentObjects.add(exercise);
            }
        } catch (Exception e) {
            Log.e("json error", "error" + e);
        }
        return parentObjects;
    }

//    private ArrayList<ParentObject> generateExercises() {
//        ArrayList<ParentObject> parentObjects = new ArrayList<>();
//        List<Exercise> exercises = new ArrayList<Exercise>();
//
//        for(int i=0;i<4;i++){
//            exercises.add(new Exercise());
//        }
//        for (Exercise exercise : exercises) {
//            ArrayList<Object> childList = new ArrayList<>();
//            childList.add(new ExerciseChild("Sep 5, 2016", true));
//            exercise.setChildObjectList(childList);
//            parentObjects.add(exercise);
//        }
//        return parentObjects;
//    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        GridLayout gridLayout = (GridLayout) view.findViewById(R.id.gridLayout1);
//        gridLayout.setVisibility(View.GONE);
//        TextView textView = (TextView) view.findViewById(R.id.textView1);
//        textView.setText("Current frag # " + mNum);
    }

    private void createCustomAnimation(View rootView) {
        final FloatingActionMenu menuMultipleActions = (FloatingActionMenu) rootView.findViewById(R.id.fabmenu);
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
}
