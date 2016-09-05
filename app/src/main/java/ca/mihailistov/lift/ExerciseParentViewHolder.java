package ca.mihailistov.lift;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

/**
 * Created by mihai on 16-09-05.
 */
public class ExerciseParentViewHolder extends ParentViewHolder {
    public TextView mExerciseTitleTextView;
    public ImageButton mParentDropDownArrow;

    public ExerciseParentViewHolder(View itemView) {
        super(itemView);

        mExerciseTitleTextView = (TextView) itemView.findViewById(R.id.parent_list_item_exercise_title_text_view);
//        mParentDropDownArrow = (ImageButton) itemView.findViewById(R.id.parent_list_item_expand_arrow);
    }
}
