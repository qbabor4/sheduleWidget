package qbabor4.pl.schoolschedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import static android.content.Context.ALARM_SERVICE;

/**
 * TODO
 * zrobić widget z własnym service (bo nie powinno to wywalać alarmu wtedy)
 * alarm puszczas z mojego service
 * Jak sie klika na widget, to czasami tworzy nowe activity i jak sie cofa, to cofa do tego samego
 */
public class NextClassWidget extends AppWidgetProvider {

    public static final String OPEN_APP_ACTION = "qbabor4.pl.schoolschedule.OPEN_APP";

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE); /* to wake up device and recive intent when is locked */
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG");
        wl.acquire();
        Log.d("time", intent.getAction() + "out");

//        final Intent i = new Intent(context, UpdateWidgetService.class);


        /** When got intent from alarm or when new widget is added or when phone is booted up*/
        String intentAction = intent.getAction();
        if (intentAction.equals(Intent.ACTION_ANSWER) || intentAction.equals("android.appwidget.action.APPWIDGET_UPDATE") || intentAction.equals("android.intent.action.BOOT_COMPLETED")) {
            Toast.makeText(context, intentAction, Toast.LENGTH_LONG).show();
            Log.d("time3", context.getApplicationContext() + "out");
            Alarm alarm = new Alarm(context);
            Cursor cursor = alarm.getNextSubjectData();

            updateAllWidgets(context, AppWidgetManager.getInstance(context), AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, NextClassWidget.class)), alarm.getDataFromCursor(cursor));
            alarm.setNewAlarm(context, intent, alarm.getTimeOfNextAlarm(cursor));
//            alarm.setNewAlarm(context, i , alarm.getTimeOfNextAlarm(cursor));

        } else if(intent.getAction().equals(OPEN_APP_ACTION)){
            openApp(context); // nie działa
        }

        wl.release();

    }

    /**
     * Ma otwierac główną aplikacje jak sie kliknie na widget
     * @param context

     * @return
     */
    public static boolean openApp(Context context) {
        // TODO otworzyc tu aplikację ( bedzie jak sie kliknie na widget )
        Intent i = new Intent(context, MainActivity.class);
        context.startActivity(i);
        return false;
    }

    private void updateAllWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, HashMap<SqlDataEnum, String> classData){
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Log.d("time", classData.toString());
            updateAppWidget(context, appWidgetManager, appWidgetId, classData);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId,  HashMap<SqlDataEnum, String> classData) {
        /* Change data on widget */
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        changeLayoutData(views, classData);
        Log.d("updateMe", "okl");
        createWidgetOnClickListener(context, views, appWidgetManager);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private String getFullNameOfDayOfWeek(String index){
        if (index == null){
            return "brak";
        }
        String[] daysOfWeek = new String[]{"Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"};
        return daysOfWeek[Integer.parseInt(index)];
    }

    private void createWidgetOnClickListener(Context context, RemoteViews views, AppWidgetManager appWidgetManager){
        Intent openAppIntent = new Intent(context, this.getClass());
        openAppIntent.setAction(OPEN_APP_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, openAppIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_linearLayout, pendingIntent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) { /* to nie działa jak sie dodaje widget */ // nic z tym nie robic
        // There may be multiple widgets active, so update all of them
        Log.d("lol4", "onUpdate");
        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
            // tu chyba nic nie robić
            // może tylko kasować alarmy jak skasują wszystkie widgety
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Alarm.updateWidget(context);
        // jak ktos dodaje widget to dac mu onClickEvent
//        Intent intent = new Intent(context, this.getClass());
//        intent.setAction(YOUR_AWESOME_ACTION);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//        remoteView.setOnClickPendingIntent(R.id.widgetFrameLayout, pendingIntent);
        // stworzyć alarm
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Alarm.cancelAlarm(context);
    }

    public void changeLayoutData(RemoteViews views, HashMap<SqlDataEnum, String> classData){ // trzeba będzie kążdemy widgetowi updatować text (chyba bedzie w onUpdate
        views.setTextViewText(R.id.tv_start_time,  TimeTools.getClockFormatTime(classData.get(SqlDataEnum.START_TIME)));
        views.setTextViewText(R.id.tv_end_time, TimeTools.getClockFormatTime(classData.get(SqlDataEnum.END_TIME)));
        views.setTextViewText(R.id.tv_subject, "Przedmiot: " + (classData.get(SqlDataEnum.SUBJECT) == null ? "brak" : classData.get(SqlDataEnum.SUBJECT)));
        //views.setTextViewText(R.id.tv_classroom, "Sala: " + classData.get(SqlDataEnum.CLASSROOM));
        views.setTextViewText(R.id.tv_day_of_week, "Dzień: " + getFullNameOfDayOfWeek(classData.get(SqlDataEnum.DAY_OF_WEEK)));
    }

}

