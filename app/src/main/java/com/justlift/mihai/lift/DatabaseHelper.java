package com.justlift.mihai.lift;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mihai on 16-04-10.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private static String DB_PATH = "/data/data/com.justlift.mihai.lift/databases/";
    private static String DB_NAME = "liftData";
    private SQLiteDatabase myDatabase;
    private final Context myContext;

    private static List<String> listDataHeader;
    private static HashMap<String, List<String>> listDataChild;

    // Database Info
    private static final String DATABASE_NAME = "liftData";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_WORKOUT_LOG = "workoutLog";

    // Exercises Table Columns
    private static final String KEY_WORKOUT_LOG_ID = "_id";
    private static final String KEY_WORKOUT_LOG_DATE = "date";
    private static final String KEY_WORKOUT_LOG_EXERCISE_NUM = "exerciseNum";
    private static final String KEY_WORKOUT_LOG_EXERCISE_NAME = "exerciseName";
    private static final String KEY_WORKOUT_LOG_SET_NUM = "setNum";
    private static final String KEY_WORKOUT_LOG_REPS = "reps";
    private static final String KEY_WORKOUT_LOG_WEIGHT = "weight";

    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    public void createDatabase() throws IOException{

        boolean dbExist = checkDatabase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            File f = new File(DB_PATH);
            if (!f.exists()) {
                f.mkdir();
            }
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {
                this.close();
                copyDatabase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    private boolean checkDatabase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    private void copyDatabase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDatabase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDatabase != null)
            myDatabase.close();

        super.close();

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String getDate(int fragmentNum){
        int dayWrtFirstDayOfWeek = fragmentNum - 7;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, dayWrtFirstDayOfWeek);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        // Get yyyy-MM-dd format for current fragment's date
        return df.format(cal.getTime());
    }

    public void getExerciseStats(int fragmentNum, int exerciseNum, final List<Integer> setNum,
                                    final List<Integer> setReps, final List<Integer> setWeight){
//        List<String> exerciseSets = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'" + " AND "
                + KEY_WORKOUT_LOG_EXERCISE_NUM + " = " + exerciseNum;

        Cursor c = db.rawQuery(selectQuery, null);

        while(c.moveToNext()){
            setNum.add(c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_SET_NUM)));
            setReps.add(c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_REPS)));
            setWeight.add(c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_WEIGHT)));
        }
        c.close();
    }

    public void setSetStats(int fragmentNum, int exerciseNum, int setNum, int setReps, int setWeight){
        SQLiteDatabase db = this.getReadableDatabase();

        setNum += 1;

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'" + " AND "
                + KEY_WORKOUT_LOG_EXERCISE_NUM + " = " + exerciseNum + " AND "
                + KEY_WORKOUT_LOG_SET_NUM + " = " + setNum;

        Log.e("DatabaseHelper", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        long id = 0;
        if (c != null && c.getCount() == 1) {
            c.moveToFirst();
            id = c.getLong(c.getColumnIndex(KEY_WORKOUT_LOG_ID));
        } else
            Log.e("DatabaseHelper","Matched more than one entry for set number");

        c.close();

        ContentValues values = new ContentValues();
        values.put(KEY_WORKOUT_LOG_REPS, setReps);
        values.put(KEY_WORKOUT_LOG_WEIGHT, setWeight);
        db.update(TABLE_WORKOUT_LOG, values, KEY_WORKOUT_LOG_ID + "=" + id, null);
        MainActivity.updatedSet();
//        MainActivity.adapter.notifyDataSetChanged();
    }

    public void addSet(int fragmentNum, int exerciseNum, int setReps, int setWeight){
        SQLiteDatabase db = this.getReadableDatabase();

        int lastSetNum = getLastSetNum(fragmentNum, exerciseNum);
        int newSetNum = lastSetNum + 1;

        ContentValues values = new ContentValues();
        values.put(KEY_WORKOUT_LOG_DATE, getDate(fragmentNum));
        values.put(KEY_WORKOUT_LOG_EXERCISE_NUM, exerciseNum);
        values.put(KEY_WORKOUT_LOG_EXERCISE_NAME, getExerciseName(fragmentNum, exerciseNum));
        values.put(KEY_WORKOUT_LOG_SET_NUM, newSetNum);
        values.put(KEY_WORKOUT_LOG_REPS, setReps);
        values.put(KEY_WORKOUT_LOG_WEIGHT, setWeight);
        db.insert(TABLE_WORKOUT_LOG, null, values);
        MainActivity.updatedSet();
    }

    public String getExerciseName(int fragmentNum, int exerciseNum){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'" + " AND "
                + KEY_WORKOUT_LOG_EXERCISE_NUM + " = " + exerciseNum;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        String exerciseName = c.getString(c.getColumnIndex(KEY_WORKOUT_LOG_EXERCISE_NAME));

        c.close();

        return exerciseName;
    }

    public int getLastSetNum(int fragmentNum, int exerciseNum){
        int curNum, lastNum;

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'" + " AND "
                + KEY_WORKOUT_LOG_EXERCISE_NUM + " = " + exerciseNum;

        Cursor c = db.rawQuery(selectQuery, null);

        lastNum = 0;
        curNum = 0;

        while(c.moveToNext()){
            curNum = c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_SET_NUM));

            if (curNum > lastNum)
                lastNum = curNum;
        }
        c.close();

        Log.e("DatabaseHelper", "Last set found was: " + lastNum);

        return lastNum;
    }

    public void removeSet(int fragmentNum, int exerciseNum, int setNum){
        SQLiteDatabase db = this.getReadableDatabase();

        // setNum will from 0-4 for example
        // lastSetNum will be from 1-5 for example
        String exerciseName = getExerciseName(fragmentNum, exerciseNum);
        int lastSetNum = getLastSetNum(fragmentNum, exerciseNum);
        int setRemoveNum = setNum + 1;
        Log.e("DatabaseHelper", "Given set to remove: " + setRemoveNum + " Last set num: " + lastSetNum);

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'" + " AND "
                + KEY_WORKOUT_LOG_EXERCISE_NUM + " = " + exerciseNum + " AND "
                + KEY_WORKOUT_LOG_SET_NUM + " = " + setRemoveNum;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.getCount() == 1)
            c.moveToFirst();
        else {
            Log.e("DatabaseHelper", "Found more than one set to delete");
            return;
        }

        long rowId = c.getLong(c.getColumnIndex(KEY_WORKOUT_LOG_ID));

        db.delete(TABLE_WORKOUT_LOG, KEY_WORKOUT_LOG_ID + "=" + rowId, null);

//        if (setRemoveNum == lastSetNum){
//            Log.e("DatabaseHelper", "THIS IS THE LAST SET TO DELETE");
//            selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
//                    + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'" + " AND "
//                    + KEY_WORKOUT_LOG_EXERCISE_NUM + " = " + exerciseNum;
//
//            c = db.rawQuery(selectQuery, null);
//
//            if (c != null && c.getCount() == 1){
//                Log.e("DatabaseHelper", "LAST AND ONLY SET, UPDATE TO NEW");
//                ContentValues values = new ContentValues();
//                values.put(KEY_WORKOUT_LOG_SET_NUM, 1);
//                values.put(KEY_WORKOUT_LOG_WEIGHT, 0);
//                values.put(KEY_WORKOUT_LOG_REPS, 0);
//                db.update(TABLE_WORKOUT_LOG, values, KEY_WORKOUT_LOG_ID + "=" + rowId, null);
//            } else {
//                db.delete(TABLE_WORKOUT_LOG, KEY_WORKOUT_LOG_ID + "=" + rowId, null);
//            }
//        }

//        if (setRemoveNum == lastSetNum){
//            Log.e("DatabaseHelper", "THIS IS THE LAST SET TO DELETE");
//            c = db.rawQuery(selectQuery, null);
//
//            if (c.getCount() == 0) {
//                Log.e("DatabaseHelper", "LAST AND ONLY SET, CREATE A NEW ONE");
//                ContentValues values = new ContentValues();
//                values.put(KEY_WORKOUT_LOG_DATE, getDate(fragmentNum));
//                values.put(KEY_WORKOUT_LOG_EXERCISE_NUM, exerciseNum);
//                values.put(KEY_WORKOUT_LOG_EXERCISE_NAME, exerciseName);
//                values.put(KEY_WORKOUT_LOG_SET_NUM, 1);
//                values.put(KEY_WORKOUT_LOG_REPS, 0);
//                values.put(KEY_WORKOUT_LOG_WEIGHT, 0);
//                db.insert(TABLE_WORKOUT_LOG, null, values);
//            }
//        }

        if (setRemoveNum < lastSetNum){
//            db.delete(TABLE_WORKOUT_LOG, KEY_WORKOUT_LOG_ID + "=" + rowId, null);

            Log.e("DatabaseHelper","RemoveSet Note: THIS IS NOT THE LAST SET");
            for (int i=1;i<=(lastSetNum-setRemoveNum);i++){
                int nextSetNum = setRemoveNum + i;
                int newSetNum = nextSetNum - 1;

                selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                        + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'" + " AND "
                        + KEY_WORKOUT_LOG_EXERCISE_NUM + " = " + exerciseNum + " AND "
                        + KEY_WORKOUT_LOG_SET_NUM + " = " + nextSetNum;

                c = db.rawQuery(selectQuery, null);

                if (c != null && c.getCount() == 1)
                    c.moveToFirst();
                else {
                    Log.e("DatabaseHelper", "Found more than one set to delete");
                    return;
                }

                rowId = c.getLong(c.getColumnIndex(KEY_WORKOUT_LOG_ID));

                ContentValues values = new ContentValues();
                values.put(KEY_WORKOUT_LOG_SET_NUM, newSetNum);
                db.update(TABLE_WORKOUT_LOG, values, KEY_WORKOUT_LOG_ID + "=" + rowId, null);
            }
        }
        MainActivity.updatedSet();
    }

    public int getLastExerciseNum(int fragmentNum){
        int curNum, lastNum;

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'";

        Cursor c = db.rawQuery(selectQuery, null);

        lastNum = 0;
        curNum = 0;

        while(c.moveToNext()){
            curNum = c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_EXERCISE_NUM));

            if (curNum > lastNum)
                lastNum = curNum;
        }
        c.close();

        return lastNum;
    }

    public void addExercise(int fragmentNum, String exerciseName){
        SQLiteDatabase db = this.getReadableDatabase();

        int lastExerciseNum = getLastExerciseNum(fragmentNum);

        ContentValues values = new ContentValues();
        values.put(KEY_WORKOUT_LOG_DATE, getDate(fragmentNum));
        values.put(KEY_WORKOUT_LOG_EXERCISE_NUM, lastExerciseNum+1);
        values.put(KEY_WORKOUT_LOG_EXERCISE_NAME, exerciseName);
        values.put(KEY_WORKOUT_LOG_SET_NUM, 1);
        values.put(KEY_WORKOUT_LOG_REPS, 0);
        values.put(KEY_WORKOUT_LOG_WEIGHT, 0);
        db.insert(TABLE_WORKOUT_LOG, null, values);

        Log.e("DatabaseHelper","Adding entry: " + getDate(fragmentNum) + " " + lastExerciseNum+1
            + " " + exerciseName);
    }

    public List<String> getExerciseHeaders(int fragmentNum){
        List<String> exerciseList = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'";

        Cursor c = db.rawQuery(selectQuery, null);

        int i = 1;

        while(c.moveToNext()){
            int currExerciseNum = c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_EXERCISE_NUM));

            if(currExerciseNum == i) {
                exerciseList.add(c.getString(c.getColumnIndex(KEY_WORKOUT_LOG_EXERCISE_NAME)));
                i++;
            }
        }

        c.close();
        Log.e("DatabaseHelper", "List of all exercises saved for this workout:" + exerciseList);

        return exerciseList;
    }

    public HashMap<String, List<String>> getExerciseChildren(int fragmentNum){
        List<String> exerciseSets;
        HashMap<String, List<String>> exercises = new HashMap<String, List<String>>();

        SQLiteDatabase db = this.getReadableDatabase();

        int lastNum = getLastExerciseNum(fragmentNum);
        Log.e("DatabaseHelper", "Last exercise number is " + lastNum);

        String selectQuery, exerciseName;
        Cursor c;
        for(int currNum = 1; currNum <= lastNum; currNum++) {
            selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                    + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'" + " AND "
                    + KEY_WORKOUT_LOG_EXERCISE_NUM + " = " + currNum;

            c = db.rawQuery(selectQuery, null);

            exerciseSets = new ArrayList<String>();

            if (c != null) {
                c.moveToNext();
                exerciseName = c.getString(c.getColumnIndex(KEY_WORKOUT_LOG_EXERCISE_NAME));
                c.moveToPrevious();

                while (c.moveToNext()) {
                    if (c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_REPS)) > 0) {
//                        exerciseSets.add("Set "
//                                + c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_SET_NUM)) + ": "
//                                + c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_REPS)) + "x"
//                                + c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_WEIGHT)) + "lbs");
                        exerciseSets.add(""
                                + c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_SET_NUM)) + ":"
                                + c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_REPS)) + ":"
                                + c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_WEIGHT)) + "");
                    } else {
                        exerciseSets.add("1:0:0");
                    }
                }
                    exercises.put(exerciseName, exerciseSets);
            }
            c.close();
        }

        Log.e("DatabaseHelper", "Hashmap of all sets saved for this workout:" + exercises);
        return exercises;
    }

}
