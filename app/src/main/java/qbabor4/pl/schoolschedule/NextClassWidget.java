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

    public static String OPEN_APP_ACTION = "qbabor4.pl.schoolschedule.OPEN_APP";

    @Override
    public void onReceive(Context context, Intent intent) {
        // tu dać rzeczy z alarmu TODO
        // zobaczyc czy tu odbiera
        // jak sie dodaje na ekran to dostaje android.appwidget.action.APPWIDGET_UPDATEout
        // zrobic własną nazwę akcji do intenta alarmującego coś jak android.appwidget.action.APPWIDGET_UPDATE tylko dac do zmiennej i wstawic do intenta
        // pobierac dane z bazy tylko raz a nie dla kazdego widgeta w updateAppWidget
        Log.d("lol4", intent.getAction() + "out");

        if (intent != null) {
            /** When got intent from alarm or when new widget is added */ // moze to rozbic i jak bedzie dodawany, to bedzie usuwany alarm i tworzony nowy?
            if (intent.getAction().equals(Intent.ACTION_ANSWER) || intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")) {
                Alarm alarm = new Alarm();
                Cursor cursor = alarm.getNextSubjectData();

                Toast.makeText(context, intent.getAction() + "lol3", Toast.LENGTH_LONG).show();
                updateAllWidgets(context, AppWidgetManager.getInstance(context), AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, NextClassWidget.class)), alarm.getDataFromCursor(cursor));

                // ustawić koleny alarm
            } else if(intent.getAction().equals(OPEN_APP_ACTION)){
//                openApp(context, "qbabor4.pl.schoolschedule"); // nie działa
            }
        }
    }

    /**
     * Nie działa
     * @param context
     * @param packageName
     * @return
     */
    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                return false;
                //throw new ActivityNotFoundException();
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    private void updateAllWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, HashMap<SqlDataEnum, String> classData){
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, classData);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId,  HashMap<SqlDataEnum, String> classData) {
        /* Change data on widget */

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.tv_start_time,  TimeTools.getClockFormatTime(classData.get(SqlDataEnum.START_TIME)));
        views.setTextViewText(R.id.tv_end_time, TimeTools.getClockFormatTime(classData.get(SqlDataEnum.END_TIME)));
        views.setTextViewText(R.id.tv_subject, "Przedmiot: " + classData.get(SqlDataEnum.SUBJECT));
        views.setTextViewText(R.id.tv_classroom, "Sala: " + classData.get(SqlDataEnum.CLASSROOM));
        views.setTextViewText(R.id.tv_day_of_week, "Dzień: " + getFullNameOfDayOfWeek(classData.get(SqlDataEnum.DAY_OF_WEEK)));

        createWidgetOnClickListener(context, views);
        // jak puste pola, to napisać, że brak

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private String getFullNameOfDayOfWeek(String index){
        String[] daysOfWeek = new String[]{"Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"};
        return daysOfWeek[Integer.parseInt(index)];
    }

    private void createWidgetOnClickListener(Context context, RemoteViews views){
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

    public void changeLayoutData(){ // trzeba będzie kążdemy widgetowi updatować text (chyba bedzie w onUpdate
        // dosta\c tu listę z id wszystkich widgetów i przeiterować jak w onUpdate

    }

}

