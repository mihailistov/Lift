package com.justlift.mihai.lift;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mihai on 16-04-08.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static List<String> listDataHeader;
    private static HashMap<String, List<String>> listDataChild;

    // Database Info
    private static final String DATABASE_NAME = "liftData";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_EXERCISES = "exercises";
    private static final String TABLE_WORKOUTS = "workouts";
    private static final String TABLE_SETS = "sets";
    private static final String TABLE_WORKOUT_SETS = "workoutSets";
    private static final String TABLE_WORKOUT_TITLES = "workoutTitles";
    private static final String TABLE_TEMPLATE_TITLES = "templateTitles";
    private static final String TABLE_TEMPLATE_SETS = "templateSets";

    // Exercises Table Columns
    private static final String KEY_EXERCISE_ID = "id";
    private static final String KEY_EXERCISE_NAME = "name";
    private static final String KEY_EXERCISE_MUSCLE = "muscle";

    // Workouts Table Columns
    private static final String KEY_WORKOUT_ID = "id";
    private static final String KEY_WORKOUT_DATE = "date";

    // Sets Table Columns
    private static final String KEY_SET_ID = "id";
    private static final String KEY_SET_EXERCISE_ID_FK = "exerciseId";
    private static final String KEY_SET_NUM = "num";
    private static final String KEY_SET_REPS = "reps";
    private static final String KEY_SET_WEIGHT = "weight";

    // Workout Sets Table Columns
    private static final String KEY_WORKOUT_SET_WORKOUT_ID_FK = "workoutId";
    private static final String KEY_WORKOUT_SET_SET_ID_FK = "setId";

    // Workout Titles Table Columns
    private static final String KEY_WORKOUT_TITLE_WORKOUT_ID_FK = "workoutId";
    private static final String KEY_WORKOUT_TITLE = "title";

    // Template Titles Table Columns
    private static final String KEY_TEMPLATE_ID = "id";
    private static final String KEY_TEMPLATE_TITLE = "title";

    // Template Sets Table Columns
    private static final String KEY_TEMPLATE_SET_TEMPLATE_ID_FK = "templateId";
    private static final String KEY_TEMPLATE_SET_SET_ID_FK = "setId";

    private static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EXERCISES_TABLE = "CREATE TABLE " + TABLE_EXERCISES +
                "(" +
                    KEY_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    KEY_EXERCISE_NAME + " TEXT," +
                    KEY_EXERCISE_MUSCLE + " TEXT" +
                ")";

        String CREATE_WORKOUTS_TABLE = "CREATE TABLE " + TABLE_WORKOUTS +
                "(" +
                    KEY_WORKOUT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    KEY_WORKOUT_DATE + " DATE" +
                ")";

        String CREATE_SETS_TABLE = "CREATE TABLE " + TABLE_SETS +
                "(" +
                    KEY_SET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    KEY_SET_EXERCISE_ID_FK + " INTEGER REFERENCES " + TABLE_EXERCISES + "," +
                    KEY_SET_NUM + " INTEGER," +
                    KEY_SET_REPS + " INTEGER," +
                    KEY_SET_WEIGHT + " INTEGER" +
                ")";

        String CREATE_WORKOUT_SETS_TABLE = "CREATE TABLE " + TABLE_WORKOUT_SETS +
                "(" +
                    KEY_WORKOUT_SET_WORKOUT_ID_FK + " INTEGER REFERENCES " + TABLE_WORKOUTS + "," +
                    KEY_WORKOUT_SET_SET_ID_FK + " INTEGER REFERENCES " + TABLE_SETS +
                ")";

        String CREATE_WORKOUT_TITLES_TABLE = "CREATE TABLE " + TABLE_WORKOUT_TITLES +
                "(" +
                    KEY_WORKOUT_TITLE_WORKOUT_ID_FK + " INTEGER REFERENCES " + TABLE_WORKOUTS + "," +
                    KEY_WORKOUT_TITLE + " TEXT" +
                ")";

        String CREATE_TEMPLATE_TITLES_TABLE = "CREATE TABLE " + TABLE_TEMPLATE_TITLES +
                "(" +
                    KEY_TEMPLATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    KEY_TEMPLATE_TITLE + "TEXT" +
                ")";

        String CREATE_TEMPLATE_SETS_TABLE = "CREATE TABLE " + TABLE_TEMPLATE_SETS +
                "(" +
                    KEY_TEMPLATE_SET_TEMPLATE_ID_FK + " INTEGER REFERENCES " + TABLE_TEMPLATE_TITLES +
                    KEY_TEMPLATE_SET_SET_ID_FK + " INTEGER REFERENCES " + TABLE_SETS +
                ")";

        db.execSQL(CREATE_EXERCISES_TABLE);
        db.execSQL(CREATE_WORKOUTS_TABLE);
        db.execSQL(CREATE_SETS_TABLE);
        db.execSQL(CREATE_WORKOUT_SETS_TABLE);
        db.execSQL(CREATE_WORKOUT_TITLES_TABLE);
        db.execSQL(CREATE_TEMPLATE_TITLES_TABLE);
        db.execSQL(CREATE_TEMPLATE_SETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT_SETS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT_TITLES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMPLATE_TITLES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMPLATE_SETS);
            onCreate(db);
        }
    }

    // Collects exercise titles for a partciular fragment into an List of Strings
    public static List<String> returnHeader(int fragmentNum) {
        int dayWrtFirstDayOfWeek = fragmentNum - 7;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, dayWrtFirstDayOfWeek);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        // Get yyyy-MM-dd format for current fragment's date
        String date = df.format(cal.getTime());



        return listDataHeader;
    }
}
