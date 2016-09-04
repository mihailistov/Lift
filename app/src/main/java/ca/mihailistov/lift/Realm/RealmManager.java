package ca.mihailistov.lift.Realm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by mihai on 16-09-04.
 */
public class RealmManager extends IntentService {

    public RealmManager() { super ("RealmManager"); }

    @Override
    protected  void onHandleIntent (Intent intent) {
        if (intent.getExtras() != null) {
            Realm realm = Realm.getDefaultInstance();
            List<RealmExercise> realmExerciseList = new ArrayList<RealmExercise>();
            try {
                realmExerciseList = loadJsonFromStream();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                final List<RealmExercise> finalRealmExerciseList = realmExerciseList;
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(finalRealmExerciseList);
                    }
                });
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Loaded exercises from JSON", Toast.LENGTH_LONG).show();

                    }
                });
                realm.close();
            }
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("exerciseBible.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private List<RealmExercise> loadJsonFromStream() throws IOException {
        List<RealmExercise> realmExerciseList = new ArrayList<RealmExercise>();

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray exercises = obj.getJSONArray("exercises"); // exercises array (1066 total)

//            Log.wtf("wtf","# of exercises: "+ exercises.length());
            for (int i=0; i < exercises.length(); i++)
            {
                RealmExercise realmExercise = new RealmExercise();
                JSONObject exercise = new JSONObject();
                try {
                    exercise = exercises.getJSONObject(i); // gets a single exercise

//                    Log.wtf("wtf",String.format("\"id\": %d", i+1));
                    realmExercise.id = i+1;

                    // if the exercise has no name, there's no point in importing it into the db
                    if (exercise.has("name") && !exercise.getString("name").isEmpty() &&
                            exercise.getString("name") != null) {
                        realmExercise.name = exercise.getString("name");

                        if (exercise.has("rating") && !exercise.getString("rating").isEmpty() &&
                                exercise.getString("muscle") != null) {
                            realmExercise.rating = exercise.getString("rating");
                        }

                        if (exercise.has("type") && !exercise.getString("type").isEmpty() &&
                                exercise.getString("type") != null) {
                            realmExercise.type = exercise.getString("type");
                        }

                        if (exercise.has("muscle") && !exercise.getString("muscle").isEmpty() &&
                                exercise.getString("muscle") != null) {
                            realmExercise.muscle = exercise.getString("muscle");
                        }

                        if (exercise.has("other_muscles") && !exercise.getString("other_muscles").isEmpty() &&
                                exercise.getString("other_muscles") != null) {
                            realmExercise.other_muscles = exercise.getString("other_muscles");
                        }

                        if (exercise.has("equipment") && !exercise.getString("equipment").isEmpty() &&
                                exercise.getString("equipment") != null) {
                            realmExercise.equipment = exercise.getString("equipment");
                        }

                        if (exercise.has("mechanics") && !exercise.getString("mechanics").isEmpty() &&
                                exercise.getString("mechanics") != null) {
                            realmExercise.mechanics = exercise.getString("mechanics");
                        }

                        if (exercise.has("level") && !exercise.getString("level").isEmpty() &&
                                exercise.getString("muscle") != null) {
                            realmExercise.level = exercise.getString("level");
                        }

                        if (exercise.has("force") && !exercise.getString("force").isEmpty() &&
                                exercise.getString("muscle") != null) {
                            realmExercise.force = exercise.getString("force");
                        }

                        if (exercise.has("guide_imgurls") && !exercise.getString("guide_imgurls").isEmpty() &&
                                exercise.get("guide_imgurls") != null) {
                            realmExercise.guide_imgurls = exercise.getJSONArray("guide_imgurls").getString(0);
                        }

                        realmExercise.sport = exercise.getString("sport");
                        realmExercise.url = exercise.getString("url");

                        RealmList<RealmString> realmGuideItems = new RealmList<RealmString>(); // loop thorugh string arrays for
                        // guide_items, imgurls,

                        JSONArray guide_items = new JSONArray();
                        try {
                            guide_items = exercise.getJSONArray("guide_items");

                            for (int j = 0; j < guide_items.length(); j++) {
                                RealmString guide_item = new RealmString();

                                if (!guide_items.getString(j).isEmpty() && guide_items.get(j) != null &&
                                        !guide_items.getString(j).equals("null") && !guide_items.getString(j).contains("\n")) {
                                    guide_item.val = guide_items.getString(j);
                                    realmGuideItems.add(guide_item);
                                }
                            }
                        } catch (Exception e) {
//                            Log.e("json error", "error" + e);
                        } finally {
                            realmExercise.guide_items = realmGuideItems;
                        }

                        RealmList<RealmString> realmImgurls = new RealmList<RealmString>(); // loop thorugh string arrays for
                        // guide_items, imgurls,

                        JSONArray imgurls = new JSONArray();
                        try {
                            imgurls = exercise.getJSONArray("imgurls");

                            for (int j = 0; j < imgurls.length(); j++) {
                                RealmString imgurl = new RealmString();

                                if (!imgurls.getString(j).isEmpty() && imgurls.get(j) != null &&
                                        !imgurls.getString(j).equals("null") && !imgurls.getString(j).contains("\n")) {
                                    imgurl.val = imgurls.getString(j);
                                    realmImgurls.add(imgurl);
                                }
                            }
                        } catch (Exception e) {
//                            Log.e("json error", "error" + e);
                        } finally {
                            realmExercise.imgurls = realmImgurls;
                        }

                        RealmList<RealmString> realmNotes = new RealmList<RealmString>(); // loop thorugh string arrays for
                        // guide_items, imgurls,

                        JSONArray notes = new JSONArray();
                        try {
                            notes = exercise.getJSONArray("notes");

                            for (int j = 0; j < notes.length(); j++) {
                                RealmString note = new RealmString();

                                if (!notes.getString(j).isEmpty() && notes.get(j) != null &&
                                        !notes.getString(j).equals("null") && !notes.getString(j).contains("\n")) {
                                    note.val = notes.getString(j);
                                    realmNotes.add(note);
                                }
                            }
                        } catch (Exception e) {
//                            Log.e("json error", "error" + e);
                        } finally {
                            realmExercise.notes = realmNotes;
                        }


                        realmExerciseList.add(realmExercise);
                    }


                } catch (Exception e) {
                    Log.e("realm error", "error" + e + " number: " + i);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return realmExerciseList;
        }
    }
}
