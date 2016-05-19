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

    private static DatabaseHelper mInstance = null;

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
    private static final String TABLE_WORKOUT_TITLE = "workoutTitle";
    private static final String TABLE_EXERCISE_LIST = "exerciseList";

    // Workout Log Columns
    private static final String KEY_WORKOUT_LOG_ID = "_id";
    private static final String KEY_WORKOUT_LOG_DATE = "date";
    private static final String KEY_WORKOUT_LOG_EXERCISE_NUM = "exerciseNum";
    private static final String KEY_WORKOUT_LOG_EXERCISE_NAME = "exerciseName";
    private static final String KEY_WORKOUT_LOG_SET_NUM = "setNum";
    private static final String KEY_WORKOUT_LOG_REPS = "reps";
    private static final String KEY_WORKOUT_LOG_WEIGHT = "weight";

    // Workout Title Columns
    private static final String KEY_WORKOUT_TITLE_DATE = "date";
    private static final String KEY_WORKOUT_TITLE_TITLE = "title";

    // Exercise List Columns
    private static final String KEY_EXERCISE_LIST_ID = "_id";
    private static final String KEY_EXERCISE_LIST_NAME = "name";
    private static final String KEY_EXERCISE_LIST_CATEGORY = "category";
    private static final String KEY_EXERCISE_LIST_TAGS = "tags";

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    private DatabaseHelper(Context context) {

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

    public void updateExerciseNum (int fragmentNum, List<String> oldHeader, List<String> updatedHeader) {
        SQLiteDatabase db = this.getReadableDatabase();

        Log.e("DatabaseHelper","Old table:\n" + oldHeader + "\nNew Table:\n" + updatedHeader);
        String selectQuery, currName;
        Cursor c;

        int oldNum, newNum;
        for (int i = 0; i < updatedHeader.size(); i++) {
            newNum = i + 1;
            oldNum = newNum;
            currName = updatedHeader.get(i);

            for (int j=0; j < oldHeader.size(); j++){
                if (currName.matches(oldHeader.get(j)))
                    oldNum = j + 1;
            }

            selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                    + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'" + " AND "
                    + KEY_WORKOUT_LOG_EXERCISE_NUM + " = " + oldNum + " AND "
                    + KEY_WORKOUT_LOG_EXERCISE_NAME + " = '" + currName + "'";

            c = db.rawQuery(selectQuery, null);

            while (c.moveToNext()) {
                long rowId = c.getLong(c.getColumnIndex(KEY_WORKOUT_LOG_ID));
                ContentValues values = new ContentValues();
                values.put(KEY_WORKOUT_LOG_EXERCISE_NUM, newNum);
                db.update(TABLE_WORKOUT_LOG, values, KEY_WORKOUT_LOG_ID + "=" + rowId, null);
            }
        }
    }

    public Cursor getHeaderCursor(int fragmentNum){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor headerCursor = db.query(
                true,
                TABLE_WORKOUT_LOG,
                new String[] {KEY_WORKOUT_LOG_EXERCISE_NAME},
                KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'",
                null,
                null, null, null, null);

        while(headerCursor.moveToNext())
            Log.e("DatabaseHelper","" + headerCursor.getString(headerCursor.getColumnIndex(KEY_WORKOUT_LOG_EXERCISE_NAME)));

        return headerCursor;
    }

    public ArrayList<String> getCategories(){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> categoryList = new ArrayList<String>();

        Cursor c = db.query(
                true,
                "exerciseList",
                new String[] {"category"},
                null,
                null,
                "category", null, null, null);

        while (c.moveToNext())
            categoryList.add(c.getString(c.getColumnIndex(KEY_EXERCISE_LIST_CATEGORY)));

        return categoryList;
    }

    public ArrayList<String> getExercises(String category){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> exerciseList = new ArrayList<String>();

        String selectQuery = "SELECT * FROM " + TABLE_EXERCISE_LIST + " WHERE "
                + KEY_EXERCISE_LIST_CATEGORY + " = '" + category + "'";

        Cursor c = db.rawQuery(selectQuery, null);

        while(c.moveToNext())
            exerciseList.add(c.getString(c.getColumnIndex(KEY_EXERCISE_LIST_NAME)));

        return exerciseList;
    }

    public ArrayList<String> getSearchResults(String search){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> resultsList = new ArrayList<String>();

        String selectQuery = "SELECT * FROM " + TABLE_EXERCISE_LIST + " WHERE "
                + KEY_EXERCISE_LIST_CATEGORY + " LIKE '%" + search + "%' OR "
                + KEY_EXERCISE_LIST_NAME + " LIKE '%" + search + "%' OR "
                + KEY_EXERCISE_LIST_TAGS + " LIKE '%" + search + "%'";

        Cursor c = db.rawQuery(selectQuery,null);

        while(c.moveToNext())
            resultsList.add(c.getString(c.getColumnIndex(KEY_EXERCISE_LIST_NAME)));

        return resultsList;
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

    public String getWorkoutTitle(int fragmentNum){
        SQLiteDatabase db = this.getReadableDatabase();

        String workoutTitle;

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_TITLE + " WHERE "
                + KEY_WORKOUT_TITLE_DATE + " = '" + getDate(fragmentNum) + "'";

        Cursor c = db.rawQuery(selectQuery, null);

        try {
            // if exists in db get string
            if (c != null && c.getCount() == 1) {
                c.moveToFirst();
                workoutTitle = c.getString(c.getColumnIndex(KEY_WORKOUT_TITLE_TITLE));
            } else // if doesn't exist, set title to blank
                workoutTitle = "";

        } finally {
            if (c != null && !c.isClosed())
                c.close();
        }
        return workoutTitle;
    }

    public void setWorkoutTitle(int fragmentNum, String workoutTitle){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_TITLE + " WHERE "
                + KEY_WORKOUT_TITLE_DATE + " = '" + getDate(fragmentNum) + "'";

        Cursor c = db.rawQuery(selectQuery, null);

        ContentValues values = new ContentValues();
        values.put(KEY_WORKOUT_TITLE_TITLE, workoutTitle);

        try {
            // if title already exists in db for current date
            if (c != null && c.getCount() == 1) {
                c.moveToFirst();
                db.update(TABLE_WORKOUT_TITLE, values, KEY_WORKOUT_TITLE_DATE + "= '" + getDate(fragmentNum) + "'", null);
            } else {
                // if doesn't exist in db
                values.put(KEY_WORKOUT_TITLE_DATE, getDate(fragmentNum));
                db.insert(TABLE_WORKOUT_TITLE, null, values);
            }
        } finally {
            if (c != null && !c.isClosed())
                c.close();
        }
    }

    public void getExerciseStats(int fragmentNum, int exerciseNum, final List<Integer> setNum,
                                    final List<Integer> setReps, final List<Double> setWeight){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'" + " AND "
                + KEY_WORKOUT_LOG_EXERCISE_NUM + " = " + exerciseNum;

        Cursor c = db.rawQuery(selectQuery, null);

        try {
            while(c.moveToNext()){
                setNum.add(c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_SET_NUM)));
                setReps.add(c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_REPS)));
                setWeight.add(c.getDouble(c.getColumnIndex(KEY_WORKOUT_LOG_WEIGHT)));
            }
        } finally {
            if (c != null && !c.isClosed())
                c.close();
        }
    }

    public int getNumofSets(int fragmentNum, int exerciseNum){
        int numOfSets = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'" + " AND "
                + KEY_WORKOUT_LOG_EXERCISE_NUM + " = " + exerciseNum;

        Cursor c = db.rawQuery(selectQuery, null);

        try {
            if(c != null)
                numOfSets = c.getCount();
        } finally {
            if (c != null && !c.isClosed())
                c.close();
        }

        return numOfSets;
    }

    public void setSetStats(int fragmentNum, int exerciseNum, int setNum, int setReps, double setWeight){
        SQLiteDatabase db = this.getReadableDatabase();

        setNum += 1;

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'" + " AND "
                + KEY_WORKOUT_LOG_EXERCISE_NUM + " = " + exerciseNum + " AND "
                + KEY_WORKOUT_LOG_SET_NUM + " = " + setNum;

        Cursor c = db.rawQuery(selectQuery, null);

        long id = 0;

        try {
            if (c != null && c.getCount() == 1) {
                c.moveToFirst();
                id = c.getLong(c.getColumnIndex(KEY_WORKOUT_LOG_ID));
            } else
                Log.e("DatabaseHelper", "Matched more than one entry for set number");
        } finally {
            if (c != null && !c.isClosed())
                c.close();
        }

        ContentValues values = new ContentValues();
        values.put(KEY_WORKOUT_LOG_REPS, setReps);
        values.put(KEY_WORKOUT_LOG_WEIGHT, setWeight);
        db.update(TABLE_WORKOUT_LOG, values, KEY_WORKOUT_LOG_ID + "=" + id, null);
        MainActivity.updatedSet();
    }

    public void addSet(int fragmentNum, int exerciseNum, String exerciseName, int setReps, double setWeight){
        SQLiteDatabase db = this.getReadableDatabase();

        int lastSetNum = getLastSetNum(fragmentNum, exerciseNum);
        int newSetNum = lastSetNum + 1;

        ContentValues values = new ContentValues();
        values.put(KEY_WORKOUT_LOG_DATE, getDate(fragmentNum));
        values.put(KEY_WORKOUT_LOG_EXERCISE_NUM, exerciseNum);
        values.put(KEY_WORKOUT_LOG_EXERCISE_NAME, exerciseName);
        values.put(KEY_WORKOUT_LOG_SET_NUM, newSetNum);
        values.put(KEY_WORKOUT_LOG_REPS, setReps);
        values.put(KEY_WORKOUT_LOG_WEIGHT, setWeight);
        db.insert(TABLE_WORKOUT_LOG, null, values);
        MainActivity.updatedSet();
    }

    public String getExerciseName(int fragmentNum, int exerciseNum){
        SQLiteDatabase db = this.getReadableDatabase();

        String exerciseName;

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'" + " AND "
                + KEY_WORKOUT_LOG_EXERCISE_NUM + " = " + exerciseNum;

        Cursor c = db.rawQuery(selectQuery, null);

        try {
            if (c != null)
                c.moveToFirst();

            exerciseName = c.getString(c.getColumnIndex(KEY_WORKOUT_LOG_EXERCISE_NAME));
        } finally {
            if (c != null && !c.isClosed())
                c.close();
        }

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

        try {
            while (c.moveToNext()) {
                curNum = c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_SET_NUM));

                if (curNum > lastNum)
                    lastNum = curNum;
            }
        } finally {
            if (c != null && !c.isClosed())
                c.close();
        }

        return lastNum;
    }

    public void removeExercise(int fragmentNum, int removeExerciseNum){
        SQLiteDatabase db = this.getReadableDatabase();
        // Notes: expects exerciseNum to start from 1

        int lastExerciseNum = getLastExerciseNum(fragmentNum);

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'" + " AND "
                + KEY_WORKOUT_LOG_EXERCISE_NUM + " = " + removeExerciseNum;

        Cursor c = db.rawQuery(selectQuery, null);

        try {
            while (c.moveToNext()) {
                long rowId = c.getLong(c.getColumnIndex(KEY_WORKOUT_LOG_ID));
                db.delete(TABLE_WORKOUT_LOG, KEY_WORKOUT_LOG_ID + "=" + rowId, null);
            }

            if (removeExerciseNum < lastExerciseNum) {
                for (int i = 1; i <= (lastExerciseNum - removeExerciseNum); i++) {
                    int exerciseNum = removeExerciseNum + i;
                    int newExerciseNum = exerciseNum - 1;

                    selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                            + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'" + " AND "
                            + KEY_WORKOUT_LOG_EXERCISE_NUM + " = " + exerciseNum;

                    c = db.rawQuery(selectQuery, null);

                    while (c.moveToNext()) {
                        long rowId = c.getLong(c.getColumnIndex(KEY_WORKOUT_LOG_ID));

                        ContentValues values = new ContentValues();
                        values.put(KEY_WORKOUT_LOG_EXERCISE_NUM, newExerciseNum);
                        db.update(TABLE_WORKOUT_LOG, values, KEY_WORKOUT_LOG_ID + "=" + rowId, null);
                    }
                }
            }
        } finally {
            if (c != null && !c.isClosed())
                c.close();
        }
    }

    public void removeSet(int fragmentNum, int exerciseNum, int setNum){
        SQLiteDatabase db = this.getReadableDatabase();

        // setNum will from 0-4 for example
        // lastSetNum will be from 1-5 for example
        String exerciseName = getExerciseName(fragmentNum, exerciseNum);
        int lastSetNum = getLastSetNum(fragmentNum, exerciseNum);
        int setRemoveNum = setNum + 1;

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'" + " AND "
                + KEY_WORKOUT_LOG_EXERCISE_NUM + " = " + exerciseNum + " AND "
                + KEY_WORKOUT_LOG_SET_NUM + " = " + setRemoveNum;

        Cursor c = db.rawQuery(selectQuery, null);

        try {
            if (c != null && c.getCount() == 1)
                c.moveToFirst();
            else {
                Log.e("DatabaseHelper", "Found more than one set to delete");
                return;
            }

            long rowId = c.getLong(c.getColumnIndex(KEY_WORKOUT_LOG_ID));

            db.delete(TABLE_WORKOUT_LOG, KEY_WORKOUT_LOG_ID + "=" + rowId, null);

            if (setRemoveNum < lastSetNum) {
                for (int i = 1; i <= (lastSetNum - setRemoveNum); i++) {
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
                        return;
                    }

                    rowId = c.getLong(c.getColumnIndex(KEY_WORKOUT_LOG_ID));

                    ContentValues values = new ContentValues();
                    values.put(KEY_WORKOUT_LOG_SET_NUM, newSetNum);
                    db.update(TABLE_WORKOUT_LOG, values, KEY_WORKOUT_LOG_ID + "=" + rowId, null);
                }
            }
        } finally {
            if (c != null && !c.isClosed())
                c.close();
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

        try {
            while (c.moveToNext()) {
                curNum = c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_EXERCISE_NUM));

                if (curNum > lastNum)
                    lastNum = curNum;
            }
        } finally {
            if (c != null && !c.isClosed())
                c.close();
        }

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
    }

    public List<String> getExerciseHeaders(int fragmentNum){
        List<String> exerciseList = new ArrayList<String>();
        HashMap<Integer, String> exercises = new HashMap<Integer, String>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'";

        Cursor c = db.rawQuery(selectQuery, null);

        int numOfExercises = getLastExerciseNum(fragmentNum);
        int lastExerciseNum = 0;

        try {
            while (c.moveToNext()) {
                int currExerciseNum = c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_EXERCISE_NUM));
                String exerciseName = c.getString(c.getColumnIndex(KEY_WORKOUT_LOG_EXERCISE_NAME));

                if (currExerciseNum != lastExerciseNum)
                    exercises.put(currExerciseNum, exerciseName);

                lastExerciseNum = currExerciseNum;
            }

            for (int i = 1; i <= numOfExercises; i++) {
                exerciseList.add(exercises.get(i));
            }
        } finally {
            if (c != null && !c.isClosed())
                c.close();
        }

        return exerciseList;
    }

    public HashMap<String, List<String>> getExerciseChildren(int fragmentNum){
        List<String> exerciseSets;
        HashMap<String, List<String>> exercises = new HashMap<String, List<String>>();

        SQLiteDatabase db = this.getReadableDatabase();

        int lastNum = getLastExerciseNum(fragmentNum);

        String selectQuery, exerciseName;
        Cursor c;
        for(int currNum = 1; currNum <= lastNum; currNum++) {
            selectQuery = "SELECT * FROM " + TABLE_WORKOUT_LOG + " WHERE "
                    + KEY_WORKOUT_LOG_DATE + " = '" + getDate(fragmentNum) + "'" + " AND "
                    + KEY_WORKOUT_LOG_EXERCISE_NUM + " = " + currNum;

            c = db.rawQuery(selectQuery, null);

            exerciseSets = new ArrayList<String>();

            try {
                if (c != null) {
                    c.moveToNext();
                    exerciseName = c.getString(c.getColumnIndex(KEY_WORKOUT_LOG_EXERCISE_NAME));
                    c.moveToPrevious();

                    while (c.moveToNext()) {
                        if (c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_REPS)) > 0) {
                            exerciseSets.add(""
                                    + c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_SET_NUM)) + ":"
                                    + c.getInt(c.getColumnIndex(KEY_WORKOUT_LOG_REPS)) + ":"
                                    + c.getDouble(c.getColumnIndex(KEY_WORKOUT_LOG_WEIGHT)) + "");
                        } else {
                            exerciseSets.add("1:0:0");
                        }
                    }
                    exercises.put(exerciseName, exerciseSets);
                }
            } finally {
                if (c != null && !c.isClosed())
                    c.close();
            }
        }
        return exercises;
    }

}
