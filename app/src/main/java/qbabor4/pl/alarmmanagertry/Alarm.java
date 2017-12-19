package qbabor4.pl.alarmmanagertry;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.sql.Date;

import static android.content.Context.ALARM_SERVICE;

/**
 * ustawiac intent w widgecie a nie w MainActivity
 * jakby w kolejnym dniu nie było tych zajęć, to inaczej zrobić (przy szukaniu czasukolejnego alarmu)
 *
 * <p>
 * Created by Jakub on 14-Oct-17.
 */

public class Alarm extends BroadcastReceiver {

    SqlLiteHelper myDb = MainActivity.getDatabaseInstance(); // brac z widgeta

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {
            if (intent.getAction().equals(Intent.ACTION_ANSWER)) {


                //shows that alarm do something
                AlarmTry mActivity = AlarmTry.getInstace();
                if (mActivity != null) {
                    AlarmTry.getInstace().updateTheTextView("Updated"); // when app is closed this is null (dac do widgeta, to bedzie zawsze działac)
                }
                // Vibrate for 500 milliseconds
                Vibrator v = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
                v.vibrate(500);


                Cursor cursor = getNextSubjectData();
                if (cursor != null) { /** got next class data */
                    mActivity.showTableData(cursor);
                    Toast.makeText(context, "data", Toast.LENGTH_SHORT).show();

                    setNewAlarm(intent, getTimeOfNextAlarm(cursor));


                } else { /** no subjects added to timetable */
                    Toast.makeText(context, "No data", Toast.LENGTH_SHORT).show();
                    // nie ustawiać kolejnego alarmu
                }
            }
        }
    }

    private long getTimeOfNextAlarm(Cursor classData){
        classData.moveToFirst();
        int classEndTime = Integer.parseInt(classData.getString(2));
        int classHour = classEndTime / 60;
        int classMinute = classEndTime % 60;
        int classDayOfWeek = Integer.parseInt(classData.getString(3));

        Calendar now = Calendar.getInstance();
        int timeNow = now.get(Calendar.HOUR) * 60 + now.get(Calendar.MINUTE); // min and hours

        if (classDayOfWeek == TimeTools.getDayInCurrentWeek()){
            if (classEndTime <= timeNow){
                now.add(Calendar.DATE,7); // that many days you should add to get next day after now
                now.set(Calendar.HOUR, classHour);
                now.set(Calendar.MINUTE, classMinute);
            } else {
                now.set(Calendar.HOUR, classHour);
                now.set(Calendar.MINUTE, classMinute);
            }
        } else {
            now.add(Calendar.DATE,(classDayOfWeek-TimeTools.getDayInCurrentWeek())%7); // that many days you should add to get next day after now
            now.set(Calendar.HOUR, classHour);
            now.set(Calendar.MINUTE, classMinute);
        }

        return now.getTimeInMillis();
    }

    /**
     * Creates new pendingIntent and sets new Alarm
     *
     * @param intent intent
     * @param time   time from now after this alarm will make alarm
     */
    private void setNewAlarm(Intent intent, long time) { // moze podawac cały timestap?

        AlarmTry mActivity = AlarmTry.getInstace(); // TODO zmianic jak widget bedzie
        if (mActivity != null) {
            AlarmTry.getInstace().updateTheTextView("Updating"); // when app is closed this is null (dac do widgeta, to bedzie zawsze działac)
        }
        /** Seting up pendingIntent and AlarmManager */
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mActivity.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT); // tu zobaczyc tam gdzie numery
        AlarmManager alarmManager = (AlarmManager) mActivity.getSystemService(ALARM_SERVICE); // co robi alarm_service ALARM_SERVICE
        /** setsAnother alarm looking on end time of next classes */
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent); // RTC _WAKEUP budzi nawet jak jest zablokowany telefon //
    }

    private Cursor getNextSubjectData() { //TODO trzeba potem zmienic jak nie bedzie nic w kolejnym tygodniu a w nastepnym bedzie) zapisywac jakos inaczej do bazy
        Cursor retCursor = null;

        int timeInMinutes = TimeTools.getCurrentTimeInMinutes();
        int dayInWeek = TimeTools.getDayInCurrentWeek();
        int weekAfter = dayInWeek + 8; // looks in whole week includnig current day from 0:00

        for (int i = dayInWeek; i < weekAfter; i++) {
            Cursor cursor = myDb.getNextSubjectData(timeInMinutes, i % 7); // patrzy tylko na te z wyzszą godziną
            if (cursor.getCount() != 0) { // if data in cursor
                retCursor = cursor;
                Log.d("cur", "znalazł");
                break;
            }
            timeInMinutes = 0; // when in current day there is no class found, algorithm looks for class in next day from 0:00 hour
        }
        return retCursor;
    }


}
