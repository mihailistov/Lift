package ca.mihailistov.lift;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.daimajia.swipe.SwipeLayout;

/**
 * Created by mihai on 16-09-05.
 */
public class ExerciseChildViewHolder extends ChildViewHolder {
    public TextView mExerciseWeightText;
    public TextView mExerciseRepsText;
    public TextView mExerciseSetNumText;

    public ExerciseChildViewHolder(View itemView) {
        super(itemView);

        mExerciseSetNumText = (TextView) itemView.findViewById(R.id.childSetNum);
        mExerciseRepsText = (TextView) itemView.findViewById(R.id.childSetReps);
        mExerciseWeightText = (TextView) itemView.findViewById(R.id.childSetWeight);

        SwipeLayout childSwipe = (SwipeLayout) itemView.findViewById(R.id.childSwipe);
        childSwipe.setShowMode(SwipeLayout.ShowMode.LayDown);
        childSwipe.addDrag(SwipeLayout.DragEdge.Right, childSwipe.findViewWithTag("childBottom"));

    }
}
