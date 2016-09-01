package com.justlift.mihai.lift.Realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mihai on 16-08-30.
 */
public class RealmSet extends RealmObject {
    @PrimaryKey
    public int id;
    public RealmExercise exercise;

    // match weights to reps e.g. 135 lbs = weight(i), and 8 reps = reps(i)
    public RealmList<RealmInteger> weight;
    public RealmList<RealmInteger> reps;
}
