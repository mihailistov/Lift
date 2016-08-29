//package com.justlift.mihai.lift.Realm;
//
//import android.content.Context;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.List;
//
//import io.realm.Realm;
//import io.realm.RealmConfiguration;
//
///**
// * Created by mihai on 16-08-28.
// */
//public class RealmExerciseManager {
//    Context mContext;
//    private Realm realm;
//
//    public RealmExerciseManager(Context context){
//        this.mContext = context;
//
//        RealmConfiguration realmConfiguration = new RealmConfiguration().Builder(mContext).build();
//
//    }
//
//    public List<RealmExercise> getRealmExercises() throws IOException {
//        loadJsonFromStream();
////        loadJsonFromJsonObject();
////        loadJsonFromString();
//
//        return realm.where(RealmExercise.class).findAll();
//    }
//
//    private void loadJsonFromStream() throws IOException {
//        InputStream stream = mContext.getAssets().open("exerciseBible.json");
//
//        realm.beginTransaction();
//        try {
//            realm.createAllFromJson(RealmExercise.class, stream);
//            realm.commitTransaction();
//        } catch (IOException e) {
//            realm.cancelTransaction();
//        } finally {
//            if (stream != null) {
//                stream.close();
//            }
//        }
//    }
//}
