package ca.mihailistov.lift.realm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.mihailistov.lift.sync.VolleySingleton;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by mihai on 16-09-04.
 */
public class RealmManager extends IntentService {

    private static final String TAG = "RealmManager";

    public RealmManager() { super ("RealmManager"); }

    @Override
    protected void onHandleIntent (Intent intent) {
        Realm realm = Realm.getDefaultInstance();

        if (intent.getExtras() != null) {
//            List<RealmExerciseData> realmExerciseDataList = new ArrayList<RealmExerciseData>();
            loadJSONFromWebBeta();
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Loaded exercises from JSON", Toast.LENGTH_LONG).show();

                }
            });
//            try {
//                loadJSONFromWeb();
////                realmExerciseDataList = loadJsonFromStream();
////                final List<RealmExerciseData> finalRealmExerciseDataList = realmExerciseDataList;
////                realm.executeTransaction(new Realm.Transaction(){
////                    @Override
////                    public void execute(Realm realm) {
////                        realm.copyToRealm(finalRealmExerciseDataList);
////                    }
////                });
//                new Handler(getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "Loaded exercises from JSON", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    public void loadJSONFromWebBeta() {
        final String URL = "http://192.168.0.136:3000/exercises";
        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray> () {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        VolleySingleton.getInstance().getRequestQueue().add(req);
    }

    public void loadJSONFromWeb() {
        String url = "http://192.168.1.50:3000/exercises";

        Log.d(TAG, "Attempting to create JsonArrayRequest...");
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(TAG, response.toString());
//
//                        List<RealmExerciseData> realmExerciseDataList = new ArrayList<RealmExerciseData>();
//
//                        try {
//                            JSONArray exercises = response;
//
////            Log.wtf("wtf","# of exercises: "+ exercises.length());
//                            for (int i=0; i < exercises.length(); i++)
//                            {
//                                RealmExerciseData realmExerciseData = new RealmExerciseData();
//                                JSONObject exercise = new JSONObject();
//                                try {
//                                    exercise = exercises.getJSONObject(i); // gets a single exercise
//
////                    Log.wtf("wtf",String.format("\"id\": %d", i+1));
//                                    realmExerciseData.id = i+1;
//
//                                    // if the exercise has no name, there's no point in importing it into the db
//                                    if (exercise.has("name") && !exercise.getString("name").isEmpty() &&
//                                            exercise.getString("name") != null) {
//                                        realmExerciseData.name = exercise.getString("name");
//
//                                        if (exercise.has("rating") && !exercise.getString("rating").isEmpty() &&
//                                                exercise.getString("muscle") != null) {
//                                            realmExerciseData.rating = exercise.getString("rating");
//                                        }
//
//                                        if (exercise.has("type") && !exercise.getString("type").isEmpty() &&
//                                                exercise.getString("type") != null) {
//                                            realmExerciseData.type = exercise.getString("type");
//                                        }
//
//                                        if (exercise.has("muscle") && !exercise.getString("muscle").isEmpty() &&
//                                                exercise.getString("muscle") != null) {
//                                            realmExerciseData.muscle = exercise.getString("muscle");
//
//                                            String muscle = exercise.getString("muscle");
//
//                                            if (muscle.contains("Back") || muscle.contains("Lats"))
//                                                realmExerciseData.category = "Back";
//                                            else if (muscle.contains("Abductors") || muscle.contains("Adductors") ||
//                                                    muscle.contains("Calves") || muscle.contains("Glutes") ||
//                                                    muscle.contains("Hamstrings") || muscle.contains("Quadriceps"))
//                                                realmExerciseData.category = "Legs";
//                                            else if (muscle.contains("Traps") || muscle.contains("Neck"))
//                                                realmExerciseData.category = "Shoulders";
//                                            else
//                                                realmExerciseData.category = muscle;
//                                        }
//
//                                        if (exercise.has("other_muscles") && !exercise.getString("other_muscles").isEmpty() &&
//                                                exercise.getString("other_muscles") != null) {
//                                            realmExerciseData.other_muscles = exercise.getString("other_muscles");
//                                        }
//
//                                        if (exercise.has("equipment") && !exercise.getString("equipment").isEmpty() &&
//                                                exercise.getString("equipment") != null) {
//                                            realmExerciseData.equipment = exercise.getString("equipment");
//                                        }
//
//                                        if (exercise.has("mechanics") && !exercise.getString("mechanics").isEmpty() &&
//                                                exercise.getString("mechanics") != null) {
//                                            realmExerciseData.mechanics = exercise.getString("mechanics");
//                                        }
//
//                                        if (exercise.has("level") && !exercise.getString("level").isEmpty() &&
//                                                exercise.getString("muscle") != null) {
//                                            realmExerciseData.level = exercise.getString("level");
//                                        }
//
//                                        if (exercise.has("force") && !exercise.getString("force").isEmpty() &&
//                                                exercise.getString("muscle") != null) {
//                                            realmExerciseData.force = exercise.getString("force");
//                                        }
//
//                                        if (exercise.has("guide_imgurls") && !exercise.getString("guide_imgurls").isEmpty() &&
//                                                exercise.get("guide_imgurls") != null) {
//                                            realmExerciseData.guide_imgurls = exercise.getJSONArray("guide_imgurls").getString(0);
//                                        }
//
//                                        realmExerciseData.sport = exercise.getString("sport");
//                                        realmExerciseData.url = exercise.getString("url");
//
//                                        RealmList<RealmString> realmGuideItems = new RealmList<RealmString>(); // loop thorugh string arrays for
//                                        // guide_items, imgurls,
//
//                                        JSONArray guide_items = new JSONArray();
//                                        try {
//                                            guide_items = exercise.getJSONArray("guide_items");
//
//                                            for (int j = 0; j < guide_items.length(); j++) {
//                                                RealmString guide_item = new RealmString();
//
//                                                if (!guide_items.getString(j).isEmpty() && guide_items.get(j) != null &&
//                                                        !guide_items.getString(j).equals("null") && !guide_items.getString(j).contains("\n")) {
//                                                    guide_item.val = guide_items.getString(j);
//                                                    realmGuideItems.add(guide_item);
//                                                }
//                                            }
//                                        } catch (Exception e) {
////                            Log.e("json error", "error" + e);
//                                        } finally {
//                                            realmExerciseData.guide_items = realmGuideItems;
//                                        }
//
//                                        RealmList<RealmString> realmImgurls = new RealmList<RealmString>(); // loop thorugh string arrays for
//                                        // guide_items, imgurls,
//
//                                        JSONArray imgurls = new JSONArray();
//                                        try {
//                                            imgurls = exercise.getJSONArray("imgurls");
//
//                                            for (int j = 0; j < imgurls.length(); j++) {
//                                                RealmString imgurl = new RealmString();
//
//                                                if (!imgurls.getString(j).isEmpty() && imgurls.get(j) != null &&
//                                                        !imgurls.getString(j).equals("null") && !imgurls.getString(j).contains("\n")) {
//                                                    imgurl.val = imgurls.getString(j);
//                                                    realmImgurls.add(imgurl);
//                                                }
//                                            }
//                                        } catch (Exception e) {
////                            Log.e("json error", "error" + e);
//                                        } finally {
//                                            realmExerciseData.imgurls = realmImgurls;
//                                        }
//
//                                        RealmList<RealmString> realmNotes = new RealmList<RealmString>(); // loop thorugh string arrays for
//                                        // guide_items, imgurls,
//
//                                        JSONArray notes = new JSONArray();
//                                        try {
//                                            notes = exercise.getJSONArray("notes");
//
//                                            for (int j = 0; j < notes.length(); j++) {
//                                                RealmString note = new RealmString();
//
//                                                if (!notes.getString(j).isEmpty() && notes.get(j) != null &&
//                                                        !notes.getString(j).equals("null") && !notes.getString(j).contains("\n")) {
//                                                    note.val = notes.getString(j);
//                                                    realmNotes.add(note);
//                                                }
//                                            }
//                                        } catch (Exception e) {
////                            Log.e("json error", "error" + e);
//                                        } finally {
//                                            realmExerciseData.notes = realmNotes;
//                                        }
//
//
//                                        realmExerciseDataList.add(realmExerciseData);
//                                    }
//
//
//                                } catch (Exception e) {
//                                    Log.e("realm error", "error" + e + " number: " + i);
//                                }
//                            }
//
//                        } finally {
//                            final List<RealmExerciseData> finalRealmExerciseDataList = realmExerciseDataList;
//                            Realm realm = Realm.getDefaultInstance();
//                            realm.executeTransaction(new Realm.Transaction(){
//                                @Override
//                                public void execute(Realm realm) {
//                                    realm.copyToRealm(finalRealmExerciseDataList);
//                                }
//                            });
//                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e(TAG + " Error: ", error.toString());
                    }
                });

        VolleySingleton.getInstance().getRequestQueue().add(jsonObjectRequest);
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

    private List<RealmExerciseData> loadJsonFromStream() throws IOException {
        List<RealmExerciseData> realmExerciseDataList = new ArrayList<RealmExerciseData>();

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray exercises = obj.getJSONArray("exercises"); // exercises array (1066 total)

//            Log.wtf("wtf","# of exercises: "+ exercises.length());
            for (int i=0; i < exercises.length(); i++)
            {
                RealmExerciseData realmExerciseData = new RealmExerciseData();
                JSONObject exercise = new JSONObject();
                try {
                    exercise = exercises.getJSONObject(i); // gets a single exercise

//                    Log.wtf("wtf",String.format("\"id\": %d", i+1));
                    realmExerciseData.id = i+1;

                    // if the exercise has no name, there's no point in importing it into the db
                    if (exercise.has("name") && !exercise.getString("name").isEmpty() &&
                            exercise.getString("name") != null) {
                        realmExerciseData.name = exercise.getString("name");

                        if (exercise.has("rating") && !exercise.getString("rating").isEmpty() &&
                                exercise.getString("muscle") != null) {
                            realmExerciseData.rating = Double.parseDouble(exercise.getString("rating"));
                        }

                        if (exercise.has("type") && !exercise.getString("type").isEmpty() &&
                                exercise.getString("type") != null) {
                            realmExerciseData.type = exercise.getString("type");
                        }

                        if (exercise.has("muscle") && !exercise.getString("muscle").isEmpty() &&
                                exercise.getString("muscle") != null) {
                            realmExerciseData.muscle = exercise.getString("muscle");

                            String muscle = exercise.getString("muscle");

                            if (muscle.contains("Back") || muscle.contains("Lats"))
                                realmExerciseData.category = "Back";
                            else if (muscle.contains("Abductors") || muscle.contains("Adductors") ||
                                    muscle.contains("Calves") || muscle.contains("Glutes") ||
                                    muscle.contains("Hamstrings") || muscle.contains("Quadriceps"))
                                realmExerciseData.category = "Legs";
                            else if (muscle.contains("Traps") || muscle.contains("Neck"))
                                realmExerciseData.category = "Shoulders";
                            else
                                realmExerciseData.category = muscle;
                        }

                        if (exercise.has("other_muscles") && !exercise.getString("other_muscles").isEmpty() &&
                                exercise.getString("other_muscles") != null) {
                            realmExerciseData.other_muscles = exercise.getString("other_muscles");
                        }

                        if (exercise.has("equipment") && !exercise.getString("equipment").isEmpty() &&
                                exercise.getString("equipment") != null) {
                            realmExerciseData.equipment = exercise.getString("equipment");
                        }

                        if (exercise.has("mechanics") && !exercise.getString("mechanics").isEmpty() &&
                                exercise.getString("mechanics") != null) {
                            realmExerciseData.mechanics = exercise.getString("mechanics");
                        }

                        if (exercise.has("level") && !exercise.getString("level").isEmpty() &&
                                exercise.getString("muscle") != null) {
                            realmExerciseData.level = exercise.getString("level");
                        }

                        if (exercise.has("force") && !exercise.getString("force").isEmpty() &&
                                exercise.getString("muscle") != null) {
                            realmExerciseData.force = exercise.getString("force");
                        }

                        if (exercise.has("guide_imgurls") && !exercise.getString("guide_imgurls").isEmpty() &&
                                exercise.get("guide_imgurls") != null) {
                            realmExerciseData.guide_imgurls = exercise.getJSONArray("guide_imgurls").getString(0);
                        }

                        realmExerciseData.sport = exercise.getString("sport");
                        realmExerciseData.url = exercise.getString("url");

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
                            realmExerciseData.guide_items = realmGuideItems;
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
                            realmExerciseData.imgurls = realmImgurls;
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
                            realmExerciseData.notes = realmNotes;
                        }


                        realmExerciseDataList.add(realmExerciseData);
                    }


                } catch (Exception e) {
                    Log.e("realm error", "error" + e + " number: " + i);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return realmExerciseDataList;
        }
    }
}
