package ca.mihailistov.lift;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

/**
 * Created by mihai on 16-09-05.
 */
public class ExerciseExpandableAdapter extends ExpandableRecyclerAdapter<ExerciseParentViewHolder, ExerciseChildViewHolder> {

    LayoutInflater mInflater = null;

    public ExerciseExpandableAdapter(Context context, List<ParentListItem> parentItemList) {
        super(parentItemList);

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
    public void onBindParentViewHolder(ExerciseParentViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        Exercise exercise = (Exercise) parentListItem;
        parentViewHolder.mExerciseTitleTextView.setText(exercise.getTitle());
    }

    @Override
    public void onBindChildViewHolder(ExerciseChildViewHolder exerciseChildViewHolder, int i, Object childObject) {
        ExerciseChild exerciseChild = (ExerciseChild) childObject;

        exerciseChildViewHolder.mExerciseSetNumText.setText(String.format("%d",exerciseChild.getSetNum()));
        exerciseChildViewHolder.mExerciseWeightText.setText(String.format("%d",exerciseChild.getWeight()));
        exerciseChildViewHolder.mExerciseRepsText.setText(String.format("%d",exerciseChild.getReps()));
    }
}
