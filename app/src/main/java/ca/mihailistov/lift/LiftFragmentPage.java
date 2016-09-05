package ca.mihailistov.lift;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mihai on 16-09-04.
 */
public class LiftFragmentPage extends Fragment {
    int mNum;

    static LiftFragmentPage newInstance(int num) {
        LiftFragmentPage f = new LiftFragmentPage();

        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lift_fragment_page, container, false);
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        rv.setHasFixedSize(true);

        ExerciseExpandableAdapter mExerciseExpandableAdapter = new ExerciseExpandableAdapter(getActivity(), generateExercises());
        mExerciseExpandableAdapter.setCustomParentAnimationViewId(R.id.parent_list_item_expand_arrow);
        mExerciseExpandableAdapter.setParentClickableViewAnimationDefaultDuration();
        mExerciseExpandableAdapter.setParentAndIconExpandOnClick(true);
        rv.setAdapter(mExerciseExpandableAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        return rootView;
    }

    private ArrayList<ParentObject> generateExercises() {
        ArrayList<ParentObject> parentObjects = new ArrayList<>();
        List<Exercise> exercises = new ArrayList<Exercise>();

        for(int i=0;i<4;i++){
            exercises.add(new Exercise());
        }
        for (Exercise exercise : exercises) {
            ArrayList<Object> childList = new ArrayList<>();
            childList.add(new ExerciseChild("Sep 5, 2016", true));
            exercise.setChildObjectList(childList);
            parentObjects.add(exercise);
        }
        return parentObjects;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        GridLayout gridLayout = (GridLayout) view.findViewById(R.id.gridLayout1);
//        gridLayout.setVisibility(View.GONE);
//        TextView textView = (TextView) view.findViewById(R.id.textView1);
//        textView.setText("Current frag # " + mNum);
    }
}
