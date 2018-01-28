package qbabor4.pl.schoolschedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.UnicodeSetSpanner;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.sql.Date;
import java.util.HashMap;

import static android.content.Context.ALARM_SERVICE;

/**
 * ustawiac intent w widgecie a nie w MainActivity
 * jakby w kolejnym dniu nie było tych zajęć, to inaczej zrobić (przy szukaniu czasukolejnego alarmu)
 * po dodaniu zajęć do planu zmienić widget i stworzyć nowy alarm, anulując poprzedni
 * dac ten onRecive do
 *
 * <p>
 * Created by Jakub on 14-Oct-17.
 */

//public class Alarm extends BroadcastReceiver {
public class Alarm {

    public static final String UPDATE_WIDGET = "qbabor4.pl.schoolschedule.UPDATE_WIDGET";
    SqlLiteHelper mDB; // brac z widgeta

    public Alarm(Context context){
        mDB = new SqlLiteHelper(context);
    }

//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        if (intent != null) {
//            if (intent.getAction().equals(Intent.ACTION_ANSWER)) {
//                Log.d("lol4", intent.getAction() + "lolAlarm");
//                int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, NextClassWidget.class));
//                if ( appWidgetIds.length != 0) { /* There are my widgets on screen */
//
//                    //shows that alarm do something
//                    AlarmTry mActivity = AlarmTry.getInstace();
//                    if (mActivity != null) {
//                        AlarmTry.getInstace().updateTheTextView("Updated"); // when app is closed this is null (dac do widgeta, to bedzie zawsze działac)
//                    }
//                    // Vibrate for 500 milliseconds
////                    Vibrator v = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
////                    v.vibrate(500);
//
//                    Cursor cursor = getNextSubjectData();
//                    if (cursor != null) { /** got next class data */
////                        mActivity.showTableData(cursor);
////                        Toast.makeText(context, "data", Toast.LENGTH_SHORT).show();
//
//                        /// zobaczyc na co ustawia (wylogować w konsoli)
//                        long time = getTimeOfNextAlarm(cursor);
//                        Date date = new Date(time);
//                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                        Log.d("data1", dateFormat.format(date));
//
//                        HashMap<SqlDataEnum, String> classData = getDataFromCursor(cursor);
//
////                        Toast.makeText(context, "Number of widgets: " + appWidgetIds.length, Toast.LENGTH_LONG).show();
//
//                        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
//                        views.setTextViewText(R.id.tv_start_time, classData.get(SqlDataEnum.START_TIME));
//                        views.setTextViewText(R.id.tv_end_time, classData.get(SqlDataEnum.END_TIME));
//                        views.setTextViewText(R.id.tv_subject, classData.get(SqlDataEnum.SUBJECT));
//                        views.setTextViewText(R.id.tv_classroom, classData.get(SqlDataEnum.CLASSROOM));
//
//                        AppWidgetManager manager = AppWidgetManager.getInstance(context);
//                        manager.updateAppWidget(appWidgetIds, views);
//
////                    setNewAlarm(intent, getTimeOfNextAlarm(cursor)); // jak sie konczą zajecia z cursora
//
//
//                    } else { /** no subjects added to timetable */
//                        Toast.makeText(context, "No data", Toast.LENGTH_SHORT).show();
//                        // nie ustawiać kolejnego alarmu
//                    }
//
//
//                    // zmienić dane na widgecie (na jakiekolwiek)
//
//                } else {
//                    Toast.makeText(context, "Number of widgets: " + "nie ma widgetow", Toast.LENGTH_LONG).show();
//                    // nie ma widgetów
//                }
//
//            }
//        }
//    }

    public HashMap<SqlDataEnum, String> getDataFromCursor(Cursor cursor){
        HashMap<SqlDataEnum, String> classData = new HashMap<>();
        if (cursor != null) {
            cursor.moveToFirst(); // to bedzie mozna wywalic jak nic nie bede z tym robił

            SqlDataEnum[] rowNames = SqlDataEnum.values();
            for (int i = 0; i < rowNames.length; i++) {
                classData.put(rowNames[i], cursor.getString(i));
            }
        }
        return classData;
    }

    /**
     * Returns begine time of next class
     * @param classData
     * @return
     */
    public Long getTimeOfNextAlarm(Cursor classData){ // zobaczyc czy zwraca początek kolejnych zajęć (zobaczyc co to zwraca) log (NEXT) sprawdzic czy dziala. Jak nie działa, to naprawic
        if (classData == null){
            return null;
        }
        classData.moveToFirst();
        int classStartTime = Integer.parseInt(classData.getString(1)); // start
        int classHour = classStartTime / 60;
        int classMinute = classStartTime % 60;
        int classDayOfWeek = Integer.parseInt(classData.getString(3));

        Calendar now = Calendar.getInstance();
        int timeNow = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE); // min and hours
        // podaje 7 :41 a nie 19

        Log.d("data1", timeNow+"");
        if (classDayOfWeek == TimeTools.getDayInCurrentWeek() && classStartTime <= timeNow ){
            now.add(Calendar.DATE, 7); /* You should add 7 days to get next time of classes that are earlier in time and today */
            Log.d("alarm1", "7");
        } else {
            now.add(Calendar.DATE,(classDayOfWeek - TimeTools.getDayInCurrentWeek() + 7)%7); /* that many days you should add to get how many days to add, to set alarm on correct day */
        }

        now.set(Calendar.HOUR_OF_DAY, classHour);
        now.set(Calendar.MINUTE, classMinute);
        now.set(Calendar.SECOND, 0);

        return now.getTimeInMillis();
    }

    /**
     * Creates new pendingIntent and sets new Alarm
     *
     * @param intent intent
     * @param time   time from now after this alarm will make alarm
     */
    public void setNewAlarm(Context context, Intent intent, Long time) {
        if (time != null) {
            /** Seting up pendingIntent and AlarmManager */
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            /** setsAnother alarm looking on start time of next classes */
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent); /* RTC _WAKEUP works even when phone is locked */
        }
    }

    public Cursor getNextSubjectData() {
        Cursor retCursor = null;

        int timeInMinutes = TimeTools.getCurrentTimeInMinutes();
        int dayInWeek = TimeTools.getDayInCurrentWeek();
        int thisDayWeekAfter = dayInWeek + 8; /* looks in whole week includnig current day from 0:00 */

        for (int i = dayInWeek; i < thisDayWeekAfter; i++) {
            Log.d("lol3",i + "");
            Cursor cursor = mDB.getNextSubjectData(timeInMinutes, i % 7);
            if (cursor.getCount() != 0) { /* if data in cursor */
                retCursor = cursor;
                Log.d("cur", "znalazł");
                break;
            }
            timeInMinutes = 0; // when in current day there is no class found, algorithm looks for class in next day from 0:00 hour
        }
        return retCursor;
    }

    public static void createAlarmIntent(Context context, AddNewClass addNewClass) {
//        Intent intent = new Intent(ins, Alarm.class);
        Intent intent = new Intent(context, NextClassWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        addNewClass.startActivity(intent);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT); /* you dont have to cancel previous alarm, because of FLAG_UPDATE_CURRENT that will update alarm*/
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1, pendingIntent);
    }

    public static void updateWidget(Context context) {
        Intent intent = new Intent(context, NextClassWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, NextClassWidget.class));
        if (ids != null && ids.length > 0) {
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            context.sendBroadcast(intent);
        }
    }



}
