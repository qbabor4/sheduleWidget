package qbabor4.pl.alarmmanagertry;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Jakub on 14-Oct-17.
 */

public class Alarm extends BroadcastReceiver {

    SqlLiteTry myDb = MainActivity.getDatabaseInstance();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {
            if (intent.getAction().equals(Intent.ACTION_ANSWER)) {
                Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
                MainActivity mActivity = MainActivity.getInstace();

                if (mActivity != null) {
                    MainActivity.getInstace().updateTheTextView("Updated"); // when app is closed this is null
                }


                Vibrator v = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(500);
                //MainActivity.createAlarmIntent(5000); // nie odpala sie w ogole TODO

                // jak nie ma MainActivity to alarm sie nie ustawi (ustawiac na tą klase ?) Moze w klasie od widgeta? Ciezko powiedziec :/
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mActivity.getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT); // tu zobaczyc tam gdzie numery

                AlarmManager alarmManager = (AlarmManager) mActivity.getSystemService(ALARM_SERVICE); // co robi alarm_service ALARM_SERVICE
                // TODO: pobierac z bazy kolejny czas (Pobrać czas, który jest pierwszym wiekszym czasem od tego teraz)
                // todo 3: dostac z bazy pierwszy wiekszy wynik w tym samym dniu
                // todo 4: jak nie ma w tym dniu, to w kolejnym


                int timeInMinutes = getCurrentTimeInMInutes();
                int dayInWeek = getDayInWeek();

                Toast.makeText(context, String.valueOf(dayInWeek), Toast.LENGTH_SHORT).show();
                Toast.makeText(context, String.valueOf(timeInMinutes), Toast.LENGTH_SHORT).show();
                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 5*1000 , pendingIntent ); // RTC _WAKEUP budzi nawet jak jest zablokowany telefon // poczytac o typach
                // TODO stworzyc kolejy alarm, który odpali się za jakiś czas; Tworzy sie, ale jak zamknie sie aplikację, to jest błąd (przypisac do widgeta a nie do mainActivity)
            }

        }

    }

    private int getNextSubjectId(){
        int timeInMinutes = getCurrentTimeInMInutes();
        int dayInWeek = getDayInWeek();
        // zapytanie do bazy z
        return myDb.getNextSubjectId(timeInMinutes, dayInWeek);
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
