package com.justlift.mihai.lift.Realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mihai on 16-08-27.
 */
public class RealmWorkout extends RealmObject {

    @PrimaryKey
    private int id;
    private int userId;
    private RealmList<RealmSet> sets;
}
