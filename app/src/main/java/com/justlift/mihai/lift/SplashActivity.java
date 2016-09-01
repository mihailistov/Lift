package com.justlift.mihai.lift;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.justlift.mihai.lift.Realm.RealmExercise;
import com.justlift.mihai.lift.Realm.RealmString;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

/**
 * Created by mihai on 16-03-25.
 */
public class SplashActivity extends AppCompatActivity {
    private JSONObject jsonObjFromFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.deleteRealm(realmConfiguration); // Clean slate
        Realm.setDefaultConfiguration(realmConfiguration); // Make this Realm the default

        SharedPreferences prefs = null;
        prefs = getSharedPreferences("com.justlift.mihai.lift", MODE_PRIVATE);

        if(prefs.getBoolean("firstrun", true)) {
            // FIRST RUN STUFF
            new loadJSON().execute();
            prefs.edit().putBoolean("firstrun", false).commit();
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private class loadJSON extends AsyncTask<Void, Void, String> {
        String json = null;

        protected String doInBackground(Void... params) {
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


            Log.e("SplashActivity", "Done reading inputstream");
            return json;
        }

        protected void onPostExecute(String jsonObjStr){
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(jsonObjStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray exercises = null; // exercises array (1066 total)
            try {
                exercises = jsonObj.getJSONArray("exercises");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new ParseJSON().execute(exercises);
        }
    }

    private class ParseJSON extends AsyncTask<JSONArray, Void, List<RealmExercise>> {
        @Override
        protected List<RealmExercise> doInBackground(JSONArray... params) {
            Realm realm = Realm.getDefaultInstance();

            JSONArray exercises = params[0];
            final List<RealmExercise> realmExerciseList = new ArrayList<RealmExercise>();

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
                            exercise.get("name") != null) {
                        realmExercise.name = exercise.getString("name");

                        if (exercise.has("rating") && !exercise.getString("rating").isEmpty() &&
                                exercise.get("rating") != null) {
                            realmExercise.rating = exercise.getString("rating");
                        }

                        if (exercise.has("type") && !exercise.getString("type").isEmpty() &&
                                exercise.get("type") != null) {
                            realmExercise.type = exercise.getString("type");
                        }

                        if (exercise.has("muscle") && !exercise.getString("muscle").isEmpty() &&
                                exercise.get("muscle") != null) {
                            realmExercise.muscle = exercise.getString("muscle");
                        }

                        if (exercise.has("other_muscles") && !exercise.getString("other_muscles").isEmpty() &&
                                exercise.get("other_muscles") != null) {
                            realmExercise.other_muscles = exercise.getString("other_muscles");
                        }

                        if (exercise.has("equipment") && !exercise.getString("equipment").isEmpty() &&
                                exercise.get("equipment") != null) {
                            realmExercise.equipment = exercise.getString("equipment");
                        }

                        if (exercise.has("mechanics") && !exercise.getString("mechanics").isEmpty() &&
                                exercise.get("mechanics") != null) {
                            realmExercise.mechanics = exercise.getString("mechanics");
                        }

                        if (exercise.has("level") && !exercise.getString("level").isEmpty() &&
                                exercise.get("muscle") != null) {
                            realmExercise.level = exercise.getString("level");
                        }

                        if (exercise.has("force") && !exercise.getString("force").isEmpty() &&
                                exercise.get("muscle") != null) {
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
                    }

                } catch (Exception e) {
                    Log.e("realm error", "error" + e);
                } finally {
                    realmExerciseList.add(realmExercise);
                }
            }

            realm.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {
                    realm.copyToRealm(realmExerciseList);
                }
            });

            return realmExerciseList;
        }

//        @Override
//        protected  void onPostExecute(List<RealmExercise> realmExerciseList) {
//            super.onPostExecute(realmExerciseList);
//
//            realm.beginTransaction();
//            realm.copyToRealm(realmExerciseList);
//            realm.commitTransaction();
//        }
    }


//    private void loadJsonFromStream() throws IOException, JSONException {
//        JSONArray exercises = jsonObjFromFile.getJSONArray("exercises"); // exercises array (1066 total)
//        ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();
//        HashMap<String, String> m_li;
//
//        new ParseJSON().execute(exercises);
//    }
}