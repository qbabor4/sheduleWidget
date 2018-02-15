package qbabor4.pl.schoolschedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;

import static qbabor4.pl.schoolschedule.SqlDataEnum.*;

/**
 * TODO Normalnie bedzie patrzyło na czas urzadzenia i według tego wyciagało rzeczy z bazy (trzeba uwzględnić przerwy)
 * Czy alarm moze się usunąc jak wywalam aplikację? (Raczej nie)
 * dać enuma na czestotliwośc
 * zmienic indexy kolumn
 * Created by Jakub on 19-Oct-17.
 */

public class SqlLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "STUDENT.db";
    public static final String TABLE_NAME = "timetable";
    public static final String COL_0 = ID.name(); // id
    public static final String COL_1 = START_TIME.name(); // Time in minutes
    public static final String COL_2 = END_TIME.name(); // Time in minutes
    public static final String COL_3 = DAY_OF_WEEK.name(); // {0|1|2|3|4|5|6}
    public static final String COL_4 = SUBJECT.name(); // TODO albo nazwa zeby nie bylo tylko pod szkołe? Moze wybór na początku typ rzeczy: szkoła/zwykła czynnosc?
    public static final String COL_5 = CLASSROOM.name();
    public static final String COL_6 = TEACHER.name();
    public static final String COL_7 = DESCRIPTION.name();
    public static final String COL_8 = COLOR.name();
//    public static final String COL_9 = FREQUENCY.name();

    public SqlLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 11); // numerki to wersje (jak sie zmieni wersje na wieksza, to updatuje
    }

    /**
     * Runs when there is no DB created
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_String = "CREATE TABLE " + TABLE_NAME + "(" + COL_0 + " INTEGER PRIMARY KEY AUTOINCREMENT  ," + COL_1 + " INTEGER NOT NULL, " + COL_2 + " INTEGER NOT NULL," + COL_3 + " INTEGER NOT NULL, " + COL_4 + " TEXT, " + COL_5 + " TEXT, " + COL_6 + " TEXT, " + COL_7 + " TEXT," + COL_8 + " TEXT" + ")";
        // ," + COL_9 + " TEXT"
        db.execSQL(SQL_String);
    }

    /**
     * Runs when version in constructor is bigger than last time
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * @param startTime   Time in minutes
     * @param subject
     * @param classroom
     * @param teacher
     * @param description
     * @return
     */
    public boolean insertData(int startTime, int endTime, int dayOfWeek, String subject, String classroom, String teacher, String description, String color, String frequency) {
        SQLiteDatabase db = this.getWritableDatabase(); //
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_1, startTime);
        contentValues.put(COL_2, endTime);
        contentValues.put(COL_3, dayOfWeek);
        contentValues.put(COL_4, subject);
        contentValues.put(COL_5, classroom);
        contentValues.put(COL_6, teacher);
        contentValues.put(COL_7, description);
        contentValues.put(COL_8, color);
//        contentValues.put(COL_9, frequency);

        long result = db.insert(TABLE_NAME, null, contentValues); // if data is not inserted this method returns -1 (false)
        return result != -1; // true when isn't -1
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_NAME, null); //zobaczyc jak działą rawquery i czy nie zamienic na query
        return result;
    }

    public boolean insertData(HashMap<SqlDataEnum, String> classData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        SqlDataEnum[] rowNames = SqlDataEnum.values();
        rowNames = Arrays.copyOfRange(rowNames, 1, rowNames.length);

        for (SqlDataEnum rowName: rowNames){
            Log.d("lol5", rowName.name() + classData.get(rowName));
            contentValues.put(rowName.name(), classData.get(rowName));
        }

        return db.insert(TABLE_NAME, null, contentValues) != -1; // true when isn't -1 // if data is not inserted this method returns -1 (false)
    }

    public boolean updateData(HashMap<SqlDataEnum, String> classData) { // dać classData TODO
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        SqlDataEnum[] rowNames = SqlDataEnum.values();
        for (SqlDataEnum rowName: rowNames){
            contentValues.put(rowName.name(), classData.get(rowName));
        }

        int result = db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{classData.get(SqlDataEnum.ID)});
        return result != -1;
    }

    public boolean deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, "ID = ?", new String[]{id});
        return result != 0; // returns rows affected (when returns 0 it is false)
    }

    // powinno zwracać inaczej z alarmu
    public Cursor getNextSubjectData(int timeInMinutes, int dayInWeek) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM (SELECT * FROM " + TABLE_NAME + " ORDER BY "
                + COL_1 + " ) t WHERE " + COL_1 + " >= " + timeInMinutes + " AND " + COL_3 +
                " = " + dayInWeek + " limit 1", null);
    }

    // ^ zrobić pętlę jak nic nie zwróci dodająca 1 do dayInWeek ale jak wiecej niz 7 to zrobic od 1 ^

    public int getMinStartTime() { // co jak nie bedzie nic?
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select min(" + COL_1 + ") from " + TABLE_NAME, null); //zobaczyc jak działą rawquery i czy nie zamienic na query
        result.moveToFirst();
        return result.getInt(0);
    }

    public int getMaxEndTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select max(" + COL_2 + ") from " + TABLE_NAME, null);
        result.moveToFirst();
        return result.getInt(0);
    }

    public int getMaxDay() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select max(" + COL_3 + ") from " + TABLE_NAME, null);
        result.moveToFirst();
        return result.getInt(0);
    }

    public int getMinDay() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select min(" + COL_3 + ") from " + TABLE_NAME, null);
        result.moveToFirst();
        return result.getInt(0);
    }

}
