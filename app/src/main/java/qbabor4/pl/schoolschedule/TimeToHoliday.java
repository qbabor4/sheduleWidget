package qbabor4.pl.schoolschedule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.Date;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_to_holiday);
        setToolbar();
        changeTimeOnScreen( getStartTimeOfNextHoliday() );
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
            yearOfNextHoliday +=1;
        }

        Calendar startTimeOfHoliday = Calendar.getInstance();
        startTimeOfHoliday.set(Calendar.YEAR, yearOfNextHoliday);
        startTimeOfHoliday.set(Calendar.MONTH, Calendar.JUNE);
        startTimeOfHoliday.set(Calendar.DAY_OF_MONTH, 23 );

        return startTimeOfHoliday.getTimeInMillis();
    }

    private void changeTimeOnScreen(long startTimeOfNextHolidat){
        // dodac textviews
        // dostac poszczególne textview na dni, godziny...

    }



}
