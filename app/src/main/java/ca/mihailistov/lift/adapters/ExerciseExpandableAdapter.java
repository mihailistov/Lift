package ca.mihailistov.lift.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

import ca.mihailistov.lift.R;
import ca.mihailistov.lift.helpers.ExerciseChildViewHolder;
import ca.mihailistov.lift.helpers.ExerciseParentViewHolder;
import ca.mihailistov.lift.models.Exercise;
import ca.mihailistov.lift.models.ExerciseChild;

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

        int setNum = exerciseChild.getSetNum();
        int weight = exerciseChild.getWeight();
        int reps = exerciseChild.getReps();

        if (setNum == 1 && weight == 0 && reps == 0){
            exerciseChildViewHolder.lbsTv.setText("");
            exerciseChildViewHolder.repsTv.setText("");
            exerciseChildViewHolder.mExerciseRepsText.setText("");
            exerciseChildViewHolder.mExerciseSetNumText.setText("+");
            exerciseChildViewHolder.mExerciseWeightText.setText("Click to add set.");
        } else {
            exerciseChildViewHolder.lbsTv.setText(" lbs");
            exerciseChildViewHolder.repsTv.setText(" reps");
            exerciseChildViewHolder.mExerciseSetNumText.setText(String.format("%d", exerciseChild.getSetNum()));
            exerciseChildViewHolder.mExerciseWeightText.setText(String.format("%d", exerciseChild.getWeight()));
            exerciseChildViewHolder.mExerciseRepsText.setText(String.format("%d", exerciseChild.getReps()));
        }
    }
}
