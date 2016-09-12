package ca.mihailistov.lift.Realm;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by mihai on 16-09-11.
 */
public class RealmCategory extends RealmObject {
    public int id;
    @Required
    public String name;
}
