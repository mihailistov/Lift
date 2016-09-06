package ca.mihailistov.lift.Realm;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by mihai on 16-08-27.
 */
public class RealmWorkout extends RealmObject {

    public RealmList<RealmExercise> exercises;
    public String date;

    public RealmWorkout() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        date = df.format(c.getTime());
    }

}
