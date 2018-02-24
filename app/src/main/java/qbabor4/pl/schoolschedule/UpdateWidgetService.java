package qbabor4.pl.schoolschedule;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by Jakub on 24-Feb-18.
 */

public class UpdateWidgetService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
//
        int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
//
//        for (int widgetId : allWidgetIds) {
            RemoteViews views = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.new_app_widget);
//
//            //updatował widget
//        }
        Toast.makeText(this.getApplicationContext(), intent.getAction(), Toast.LENGTH_LONG);


        Alarm alarm = new Alarm(this.getApplicationContext());
        Cursor cursor = alarm.getNextSubjectData();
        appWidgetManager.updateAppWidget(allWidgetIds, views);
        alarm.setNewAlarm(this.getApplicationContext(), intent, alarm.getTimeOfNextAlarm(cursor));

        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
