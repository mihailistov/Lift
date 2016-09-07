package ca.mihailistov.lift;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.daimajia.swipe.SwipeLayout;

/**
 * Created by mihai on 16-09-05.
 */
public class ExerciseParentViewHolder extends ParentViewHolder {
    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    public TextView mExerciseTitleTextView;
    public ImageView mArrowExpandImageView;

    public ExerciseParentViewHolder(View itemView) {
        super(itemView);


        mArrowExpandImageView = (ImageView) itemView.findViewById(R.id.parent_list_item_expand_arrow);
        mExerciseTitleTextView = (TextView) itemView.findViewById(R.id.parent_list_item_exercise_title_text_view);

        mArrowExpandImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded()) {
                    collapseView();
                } else {
                    expandView();
                }
            }
        });

        SwipeLayout parentSwipe = (SwipeLayout) itemView.findViewById(R.id.parentSwipe);
        parentSwipe.setShowMode(SwipeLayout.ShowMode.LayDown);
        parentSwipe.addDrag(SwipeLayout.DragEdge.Right, parentSwipe.findViewWithTag("parentBottom"));
    }

    @SuppressLint("NewApi")
    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (expanded) {
                mArrowExpandImageView.setRotation(ROTATED_POSITION);
            } else {
                mArrowExpandImageView.setRotation(INITIAL_POSITION);
            }
        }
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            RotateAnimation rotateAnimation;
            if (expanded) { // rotate clockwise
                rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            } else { // rotate counterclockwise
                rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            }

            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            mArrowExpandImageView.startAnimation(rotateAnimation);
        }
    }

    @Override
    public boolean shouldItemViewClickToggleExpansion() {
        return false;
    }
}
