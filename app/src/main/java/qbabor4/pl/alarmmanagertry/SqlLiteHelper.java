package qbabor4.pl.alarmmanagertry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * TODO Normalnie bedzie patrzyło na czas urzadzenia i według tego wyciagało rzeczy z bazy (trzeba uwzględnić przerwy)
 * Czy alarm moze się usunąc jak wywalam aplikację? (Raczej nie)
 *
 * Created by Jakub on 19-Oct-17.
 */

public class SqlLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "STUDENT.db";
    public static final String TABLE_NAME = "timetable_table";
    public static final String COL_1 = "id"; // id
    public static final String COL_2 = "start_time"; // Time in minutes
    public static final String COL_3 = "end_time"; // Time in minutes
    public static final String COL_4 = "day_of_week"; // {1|2|3|4|5|6|7}
    public static final String COL_5 = "subject"; // TODO albo nazwa zeby nie bylo tylko pod szkołe? Moze wybór na początku typ rzeczy: szkoła/zwykła czynnosc?
    public static final String COL_6 = "classroom";
    public static final String COL_7 = "teacher";
    public static final String COL_8 = "description";

    public SqlLiteHelper(Context context ) {
        super(context, DATABASE_NAME, null, 3); // numerki to chyba wersje (jak sie zmieni wersje na wieksza, to updatuje chyba
    }

    /** Runs when there is no DB created */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_String = "CREATE TABLE " + TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT  ," + COL_2 + " INTEGER, " + COL_3 + " INTEGER," + COL_4 +" INTEGER, " + COL_5 + " TEXT, " + COL_6 + " TEXT, " + COL_7 + " TEXT, " + COL_8 + " TEXT" + ")";
        db.execSQL(SQL_String);
    }

    /** Runs when version in constructor is bigger than last time */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    /**
     *
     * @param startTime Time in minutes
     * @param subject
     * @param classroom
     * @param teacher
     * @param description
     * @return
     */
    public boolean insertData(int startTime, int endTime, int dayOfWeek, String subject, String classroom, String teacher, String description){
        //SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db = this.getWritableDatabase(); //
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_2, startTime);
        contentValues.put(COL_3, endTime);
        contentValues.put(COL_4, dayOfWeek);
        contentValues.put(COL_5, subject);
        contentValues.put(COL_6, classroom);
        contentValues.put(COL_7, teacher);
        contentValues.put(COL_8, description);

        long result = db.insert(TABLE_NAME, null, contentValues); // if data is not inserted this method returns -1 (false)
        return result != -1; // true when isn't -1
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_NAME, null); //zobaczyc jak działą rawquery i czy nie zamienic na query
        return result;
    }

    public boolean updateData(String id, int startTime, int endTime, int dayOfWeek, String subject, String classroom, String teacher, String description){
        SQLiteDatabase db = this.getWritableDatabase(); // czym to sie rózni od readable ? TODO
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_1, id);
        contentValues.put(COL_2, startTime);
        contentValues.put(COL_3, endTime);
        contentValues.put(COL_4, dayOfWeek);
        contentValues.put(COL_5, subject);
        contentValues.put(COL_6, classroom);
        contentValues.put(COL_7, teacher);
        contentValues.put(COL_8, description);

        int result = db.update(TABLE_NAME, contentValues, "ID = ?" ,new String[]{id} );
        return result != -1;
    }

    public boolean deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, "ID = ?", new String[]{id});
        return result != 0; // returns rows affected (when returns 0 it is false)
    }

    public Cursor getNextSubjectData(int timeInMinutes, int dayInWeek) { // zrobić pętlę jak nic nie zwróci dodająca 1 do dayInWeek ale jak wiecej niz 7 to zrobic od 1
        SQLiteDatabase db = this.getReadableDatabase();
        // dostać z Cursora czy jest coś
        // TODO zwiekszac day in week jak nie ma (7 razy)
        return  db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_2 + " >= " + timeInMinutes + " AND " + COL_4 + " = " + dayInWeek + " limit 1", null);
    }
}
