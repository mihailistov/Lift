package com.justlift.mihai.lift;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * Created by mihai on 16-04-28.
 */
public class CustomListView extends ListView {
        public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean performItemClick(View view, int position, long id) {
        boolean checkedBeforeClick = isItemChecked(position);
        super.performItemClick(view, position, id);

        if (!AddExerciseActivity.exercisesDisp) {
            String catClicked = (String) AddExerciseActivity.categoryList.get(position);

            AddExerciseActivity.toolbar.setTitle(catClicked);
            AddExerciseActivity.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);

            AddExerciseActivity.exerciseList = AddExerciseActivity.myDbHelper.getExercises(catClicked);

            AddExerciseActivity.setAdapterToMultipleList(AddExerciseActivity.exerciseList);

            for (int i=0;i<AddExerciseActivity.exercisesAdded.size();i++){
                for(int j=0;j<AddExerciseActivity.exerciseList.size();j++){
                    if (AddExerciseActivity.exercisesAdded.get(i).matches(AddExerciseActivity.exerciseList.get(j)))
                        setItemChecked(j, true);
                }
            }

            AddExerciseActivity.exercisesDisp = true;

        } else if (AddExerciseActivity.exercisesDisp) {
            String exerciseClicked = (String) AddExerciseActivity.exerciseList.get(position);
            Log.e("CustomListView", "Exercise clicked: " + exerciseClicked);

            if (!checkedBeforeClick) {
                Log.e("CustomListView", "Checking box... adding exercise clicked: " + exerciseClicked);
                AddExerciseActivity.exercisesAdded.add(exerciseClicked);
            } else if (checkedBeforeClick) {
                Log.e("CustomListView", "Already checked! Unchecking...");
                setItemChecked(position, false);

                for (int i = 0; i < AddExerciseActivity.exercisesAdded.size(); i++) {
                    if (AddExerciseActivity.exercisesAdded.get(i).matches(exerciseClicked)) {
                        AddExerciseActivity.exercisesAdded.remove(i);
                    }
                }
            }
        }
        return true;
    }
}
