package ca.mihailistov.lift.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mihai on 16-08-27.
 */
public class RealmExerciseData extends RealmObject {

    @PrimaryKey
    public int id;
    public String name;
    public String category;
    public double rating;
    public String type;
    public String muscle;
    public String other_muscles;
    public String equipment;
    public String mechanics;
    public String level;
    public String force;
    public RealmList<RealmString> imgurls;
    public RealmList<RealmString> guide_items;
    public String guide_imgurls;
    public RealmList<RealmString> notes;
    public String sport;
    public String url;
}
