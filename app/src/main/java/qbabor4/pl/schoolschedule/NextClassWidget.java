package qbabor4.pl.schoolschedule;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * TODO
 * wywołać alarm
 * odebrać alarm w onReceive, pobrać dane z bazy i zmienic dane na widgecie
 * skasować alarm
 * podmienić textView na godziny
 * metoda dostajaca klasę i zmieniająca dane klasy na widgecie
 * Ma ustawiać alarm w momencie rozpoczęcia kolejnych zajęć a nie zakończenia tych co są
 * Jak jest widget na ekarnie i sie odpala jeszcze raz aplikację, to wywala błąd
 *
 */
public class NextClassWidget extends AppWidgetProvider {

    public static final String OPEN_APP_ACTION = "qbabor4.pl.schoolschedule.OPEN_APP";

    @Override
    public void onReceive(Context context, Intent intent) {
        // zrobic własną nazwę akcji do intenta alarmującego coś jak android.appwidget.action.APPWIDGET_UPDATE tylko dac do zmiennej i wstawic do intenta
        // pobierac dane z bazy tylko raz a nie dla kazdego widgeta w updateAppWidget
        Log.d("time", intent.getAction() + "out");


        if (intent != null) {
            /** When got intent from alarm or when new widget is added */ // moze to rozbic i jak bedzie dodawany, to bedzie usuwany alarm i tworzony nowy?
            if (intent.getAction().equals(Intent.ACTION_ANSWER) || intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")) { //usunac poprzedni alarm jak jest
                Alarm alarm = new Alarm(context);
                Cursor cursor = alarm.getNextSubjectData();

                updateAllWidgets(context, AppWidgetManager.getInstance(context), AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, NextClassWidget.class)), alarm.getDataFromCursor(cursor));
                alarm.setNewAlarm(context, intent, alarm.getTimeOfNextAlarm(cursor));

            } else if(intent.getAction().equals(OPEN_APP_ACTION)){
                openApp(context); // nie działa
            }
        }
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
//        ComponentName comp = new ComponentName(context.getPackageName(), MainActivity.class.getName());
//        appWidgetManager.updateAppWidget(comp, views);
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
    }

    public void changeLayoutData(RemoteViews views, HashMap<SqlDataEnum, String> classData){ // trzeba będzie kążdemy widgetowi updatować text (chyba bedzie w onUpdate
        views.setTextViewText(R.id.tv_start_time,  TimeTools.getClockFormatTime(classData.get(SqlDataEnum.START_TIME)));
        views.setTextViewText(R.id.tv_end_time, TimeTools.getClockFormatTime(classData.get(SqlDataEnum.END_TIME)));
        views.setTextViewText(R.id.tv_subject, "Przedmiot: " + (classData.get(SqlDataEnum.SUBJECT) == null ? "brak" : classData.get(SqlDataEnum.SUBJECT)));
        //views.setTextViewText(R.id.tv_classroom, "Sala: " + classData.get(SqlDataEnum.CLASSROOM));
        views.setTextViewText(R.id.tv_day_of_week, "Dzień: " + getFullNameOfDayOfWeek(classData.get(SqlDataEnum.DAY_OF_WEEK)));
    }

}

