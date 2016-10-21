package ca.mihailistov.lift.helpers;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.daimajia.swipe.SwipeLayout;

import ca.mihailistov.lift.R;

/**
 * Created by mihai on 16-09-05.
 */
public class ExerciseChildViewHolder extends ChildViewHolder {
    public TextView mExerciseWeightText;
    public TextView mExerciseRepsText;
    public TextView mExerciseSetNumText;

    public TextView lbsTv;
    public TextView repsTv;

    public ExerciseChildViewHolder(View itemView) {
        super(itemView);

        lbsTv = (TextView) itemView.findViewById(R.id.lbsTv);
        repsTv = (TextView) itemView.findViewById(R.id.repsTv);

        mExerciseSetNumText = (TextView) itemView.findViewById(R.id.childSetNum);
        mExerciseRepsText = (TextView) itemView.findViewById(R.id.childSetReps);
        mExerciseWeightText = (TextView) itemView.findViewById(R.id.childSetWeight);

        SwipeLayout childSwipe = (SwipeLayout) itemView.findViewById(R.id.childSwipe);
        childSwipe.setShowMode(SwipeLayout.ShowMode.LayDown);
        childSwipe.addDrag(SwipeLayout.DragEdge.Right, childSwipe.findViewWithTag("childBottom"));

        ImageView editImage = (ImageView) itemView.findViewById(R.id.list_exercise_child_edit);
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
