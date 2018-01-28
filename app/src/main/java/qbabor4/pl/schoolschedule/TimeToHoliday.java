package qbabor4.pl.schoolschedule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TODO:
 * // aktywnosc zmieniajacego sie zegara TODO (dni, godziny, minuty, sekundy, do wakacji)
 * // dostac czas rozpoczecia wakacji (poczatek dnia wakacji)
 * // moziwosc zmiany czasu i dnia rozpoczecia wakacji
 *
 * IFTIME:
 * pobierac z sieci jakoś kiedy zaczynają się wakacje
 *
 * Clock that counts time to holiday
 * Created by Jakub on 28-Jan-18.
 */
public class TimeToHoliday extends AppCompatActivity {

    TextView tvDaysToHoliday, tvHoursToHoliday, tvMinutesToHoliday, tvSecondsToHoliday;

    long startTimeOfNextHoliday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_to_holiday);
        setToolbar();
        setTextViews();
        setStartTimeOfNextHoliday(getStartTimeOfNextHoliday());
        changeTimeOnScreen();
    }

    /**
     * TOOLBAR
     */
    private void setToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_time_to_holiday);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // strzałka z powrotem
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { /* ikona powrotu */
                finish();
            }
        });
    }

    private void setTextViews(){
        tvDaysToHoliday = (TextView) findViewById(R.id.tv_days_to_holiday);
        tvHoursToHoliday = (TextView) findViewById(R.id.tv_hours_to_holiday);
        tvMinutesToHoliday = (TextView) findViewById(R.id.tv_minutes_to_holiday);
        tvSecondsToHoliday = (TextView) findViewById(R.id.tv_seconds_to_holiday);
    }

    /**
     * Gets time in timestamp of next holiday
     * @return timestamp with time of next holiday
     */
    private long getStartTimeOfNextHoliday(){
        int yearOfNextHoliday = Calendar.getInstance().get(Calendar.YEAR);

        Calendar june23 = Calendar.getInstance(); /* june 23 of this year */
        june23.set(Calendar.MONTH, Calendar.JUNE);
        june23.set(Calendar.DAY_OF_MONTH, 23);
        if (june23.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()){
            yearOfNextHoliday += 1;
        }
        Calendar startTimeOfHoliday = Calendar.getInstance();
        Log.d("time2", startTimeOfHoliday.get(Calendar.HOUR) + "");
        startTimeOfHoliday.set(Calendar.YEAR, yearOfNextHoliday);
        startTimeOfHoliday.set(Calendar.MONTH, Calendar.JUNE);
        startTimeOfHoliday.set(Calendar.DAY_OF_MONTH, 23 );
        startTimeOfHoliday.set(Calendar.HOUR, 0);
        startTimeOfHoliday.set(Calendar.MINUTE, 0);
        startTimeOfHoliday.set(Calendar.SECOND, 0);
        startTimeOfHoliday.set(Calendar.MILLISECOND, 0);

        return startTimeOfHoliday.getTimeInMillis();
    }

    private void changeTimeOnScreen(){
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                changeTimeTextViews();
                            }
                        });
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        };

        t.start();
    }

    private void changeTimeTextViews(){
        Calendar timeToHoliday = Calendar.getInstance();
        timeToHoliday.setTimeInMillis(startTimeOfNextHoliday - timeToHoliday.getTimeInMillis());
        tvDaysToHoliday.setText(String.valueOf(timeToHoliday.get(Calendar.DAY_OF_YEAR)));
        tvHoursToHoliday.setText(String.valueOf(timeToHoliday.get(Calendar.HOUR)));
        tvMinutesToHoliday.setText(String.valueOf(timeToHoliday.get(Calendar.MINUTE)));
        tvSecondsToHoliday.setText(String.valueOf(timeToHoliday.get(Calendar.SECOND)));
    }

    private void setStartTimeOfNextHoliday(long startTimeOfNextHoliday){
        this.startTimeOfNextHoliday = startTimeOfNextHoliday;
    }

}
