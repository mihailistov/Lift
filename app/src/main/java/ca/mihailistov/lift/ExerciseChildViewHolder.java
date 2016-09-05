package ca.mihailistov.lift;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

/**
 * Created by mihai on 16-09-05.
 */
public class ExerciseChildViewHolder extends ChildViewHolder {
    public TextView mExerciseCompletedText;
    public CheckBox mExerciseCompletedCheckBox;

    public ExerciseChildViewHolder(View itemView) {
        super(itemView);

        mExerciseCompletedText = (TextView) itemView.findViewById(R.id.child_list_item_exercise_text_view);
        mExerciseCompletedCheckBox = (CheckBox) itemView.findViewById(R.id.child_list_item_exercise_completed_check_box);
    }
}
