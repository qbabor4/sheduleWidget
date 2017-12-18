package qbabor4.pl.alarmmanagertry;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Vibrator;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Jakub on 14-Oct-17.
 */

public class Alarm extends BroadcastReceiver {

    SqlLiteHelper myDb = MainActivity.getDatabaseInstance();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {
            if (intent.getAction().equals(Intent.ACTION_ANSWER)) {
                //Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
                AlarmTry mActivity = AlarmTry.getInstace();
                if (mActivity != null) {
                    AlarmTry.getInstace().updateTheTextView("Updated"); // when app is closed this is null
                }
                Vibrator v = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(500);

                // ustawiac intent w widgecie a nie w MainActivity
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mActivity.getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT); // tu zobaczyc tam gdzie numery

                AlarmManager alarmManager = (AlarmManager) mActivity.getSystemService(ALARM_SERVICE); // co robi alarm_service ALARM_SERVICE
                // TODO: pobierac z bazy kolejny czas (Pobrać czas, który jest pierwszym wiekszym czasem od tego teraz)
                // todo 4: jak nie ma w tym dniu, to w kolejnym

                int timeInMinutes = getCurrentTimeInMInutes();
                int dayInWeek = getDayInWeek();

                Cursor cursor = getNextSubjectData(context);

                if (cursor != null) {

                    mActivity.showTableData(cursor);
                    Toast.makeText(context, "data", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "No data", Toast.LENGTH_SHORT).show();
                    // no subjects added
                }
                //cursor.moveToFirst();
                //getNextSubjectData(cursor, context);

                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5*1000 , pendingIntent ); // RTC _WAKEUP budzi nawet jak jest zablokowany telefon //
            }
        }
    }

    private void getNextSubjectData(Cursor cursor, Context context){
        String startTime = cursor.getString(1);
        Toast.makeText(context, String.valueOf(startTime), Toast.LENGTH_SHORT).show();
    }

    private Cursor getNextSubjectData(Context context){ //TODO trzeba potem zmienic jak nie bedzie nic w kolejnym tygodniu a w nastepnym bedzie) zapisywac jakos inaczej do bazy
        int timeInMinutes = getCurrentTimeInMInutes();
        int dayInWeek = getDayInWeek();
        // for do dni do while
        Cursor retCursor = null;
        int weekAfter = dayInWeek +7;

        for (int i=dayInWeek; i<weekAfter; i++){ // iterating through all days in week (TODO trzeba potem zmienic jak nie bedzie nic w kolejnym tygodniu a w nastepnym bedzie) zapisywac jakos inaczej do bazy
            int dayInWeekParsed = i;
            if (i > 7){ dayInWeekParsed = i%7; }
            if (i > dayInWeek){
                timeInMinutes = -1;
            }
            Cursor cursor  = myDb.getNextSubjectData(timeInMinutes, dayInWeekParsed); // patrzy tylko na te z wyzszyą godziną
            Toast.makeText(context, i  +" "+ cursor.getCount()  , Toast.LENGTH_SHORT).show();
            if (cursor.getCount() != 0) { // data in cursor
                retCursor = cursor;
                break;
            }
        }
        return retCursor;
    }

    private int getMinutes(int hours, int minutes){
        return minutes + hours*60;
    }

    private int getCurrentTimeInMInutes(){
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int minutes = rightNow.get(Calendar.MINUTE);
        return getMinutes(hour, minutes);
    }

    private int getDayInWeek(){
        Calendar rightNow = Calendar.getInstance();
        int dayInWeek = rightNow.get(Calendar.DAY_OF_WEEK);
        int dayOfWeekFromMonday = dayInWeek -1;
        if (dayOfWeekFromMonday == 0){
            dayOfWeekFromMonday = 7;
        }
        return dayOfWeekFromMonday; // 1 as monday, not sunday
    }
}
