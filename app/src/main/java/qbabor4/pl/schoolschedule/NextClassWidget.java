package qbabor4.pl.schoolschedule;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * TODO
 * wywołać alarm
 * odebrać alarm w onReceive, pobrać dane z bazy i zmienic dane na widgecie
 * skasować alarm
 * podmienić textView na godziny
 * metoda dostajaca klasę i zmieniająca dane klasy na widgecie
 *
 */
public class NextClassWidget extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

//        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget); // to globalnie???
//        views.setTextViewText(R.id.appwidget_text, widgetText);

        // globalnie context setować? (trzeba dać do zmiany layouta
        views.setTextViewText(R.id.textView3, "lol" );


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            // tu chyba nic nie robić
            // może tylko kasować alarmy jak skasują wszystkie widgety
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public void changeLayoutData(){ // trzeba będzie kążdemy widgetowi updatować text
        // dosta\c tu listę z id wszystkich widgetów i przeiterować jak w onUpdate

    }

}

