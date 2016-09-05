package ca.mihailistov.lift;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.List;

/**
 * Created by mihai on 16-09-05.
 */
public class ExerciseExpandableAdapter extends ExpandableRecyclerAdapter<ExerciseParentViewHolder, ExerciseChildViewHolder> {

    LayoutInflater mInflater = null;

    public ExerciseExpandableAdapter(Context context, List<ParentObject> parentItemList) {
        super(context, parentItemList);

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ExerciseParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.list_item_exercise_parent, viewGroup, false);

        return new ExerciseParentViewHolder(view);
    }

    @Override
    public ExerciseChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.list_item_exercise_child, viewGroup, false);

        return new ExerciseChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(ExerciseParentViewHolder exerciseParentViewHolder, int i, Object parentObject) {
        Exercise exercise = (Exercise) parentObject;
        exerciseParentViewHolder.mExerciseTitleTextView.setText("Exercise Title # " + i);

    }

    @Override
    public void onBindChildViewHolder(ExerciseChildViewHolder exerciseChildViewHolder, int i, Object childObject) {
        ExerciseChild exerciseChild = (ExerciseChild) childObject;
        exerciseChildViewHolder.mExerciseCompletedText.setText("Exercise child set # " + i);
    }
}
