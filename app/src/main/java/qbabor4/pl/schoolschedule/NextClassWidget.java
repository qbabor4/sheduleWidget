package qbabor4.pl.schoolschedule;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
 *
 */
public class NextClassWidget extends AppWidgetProvider {

    public static String YOUR_AWESOME_ACTION = "YourAwesomeAction";


    @Override
    public void onReceive(Context context, Intent intent) {
        // tu dać rzeczy z alarmu TODO
        // zobaczyc czy tu odbiera
        // jak sie dodaje na ekran to dostaje android.appwidget.action.APPWIDGET_UPDATEout
        // zrobic własną nazwę akcji do intenta alarmującego coś jak android.appwidget.action.APPWIDGET_UPDATE tylko dac do zmiennej i wstawic do intenta
        // pobierac dane z bazy tylko raz a nie dla kazdego widgeta w updateAppWidget
        Log.d("lol4", intent.getAction() + "out");

        if (intent != null) {
            if (intent.getAction().equals(Intent.ACTION_ANSWER)) {
                Log.d("lol4", intent.getAction());

                Alarm alarm = new Alarm();
                Cursor cursor = alarm.getNextSubjectData();


                Toast.makeText(context, intent.getAction() + "lol3", Toast.LENGTH_LONG).show();
                updateAllWidgets(context, AppWidgetManager.getInstance(context), AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, NextClassWidget.class)), alarm.getDataFromCursor(cursor));
//                onUpdate(context, AppWidgetManager.getInstance(context), AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, NextClassWidget.class)));
                // zobaczyc czy to na gorze działa (chyba dziala
                // przekazać mapę do zmiany danych do onUpdate
                // jak działą, to dać updatowanie do updateAppWidget
                // nie uzywac updatwowania
            }
            else if(intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")){
                Log.d("lol4", "OK");
                // ustawić onClick
            }
        }

    }

    private void updateAllWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, HashMap<SqlDataEnum, String> classData){
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, classData);
            // tu chyba nic nie robić
            // może tylko kasować alarmy jak skasują wszystkie widgety
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId,  HashMap<SqlDataEnum, String> classData) {
        /* Change data on widget */
        // zmienic czasy na czytelne TODO
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.tv_start_time, TimeTools.getClockFormatTime(classData.get(SqlDataEnum.START_TIME)));
        views.setTextViewText(R.id.tv_end_time, TimeTools.getClockFormatTime(classData.get(SqlDataEnum.END_TIME)));
        views.setTextViewText(R.id.tv_subject, classData.get(SqlDataEnum.SUBJECT));
        views.setTextViewText(R.id.tv_classroom, classData.get(SqlDataEnum.CLASSROOM));

        // jak puste pola, to napisać, że brak

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
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

