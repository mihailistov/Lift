package ca.mihailistov.lift.Realm;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by mihai on 16-09-05.
 */
public class RealmExercise extends RealmObject {
    public RealmExerciseData realmExerciseData;
    public RealmList<RealmSet> realmSets;
}
