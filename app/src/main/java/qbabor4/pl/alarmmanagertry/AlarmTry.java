package qbabor4.pl.alarmmanagertry;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * TODO
 * wybieranie koloru w dodawaniu zajęć albo same sie beda zmieniac
 * klawisze prawo i lewo zeby zobaczy kolojne tygodnie
 * wybór koloru
 * pokazywanie danych jak sie kliknie na rectangla z guzikami delete, edit
 * jak sie przytrzyma to dać tylko mozliwosc edit
 * zrobic oznaczenie gdzie jestesmy teraz na planie (wedłóg godziny i dnia tygodnia)
 * colorpicker przy ddoawaniu zajęć
 *
 * TODO IFTIME:
 * zmiana nagólwka z dniami i czasami dynamicznie (robienie wedłód procentów
 * <p>
 * Created by Jakub on 09-Dec-17.
 */

public class AlarmTry extends AppCompatActivity {


    public static final String EXTRA_MESSAGE = "qbabor4.pl.alarmmanagertry.MESSAGE";

    Button btnSet;
    EditText etTime;
    TextView tvUpdate;

    private static SqlLiteHelper mDB;
    private static AlarmTry ins;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_try);

        /** Layout */
        setWidgets();
        setButtonListeners();
        setToolbar();

        /** Instances */
        setInstance();
        setDBInstance();
    }

    private void setWidgets() {
        btnSet = (Button) findViewById(R.id.btnAlarm);
        etTime = (EditText) findViewById(R.id.etAlarm);
        tvUpdate = (TextView) findViewById(R.id.tv_update); //TODO
    }

    private void setButtonListeners() {
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int time = Integer.parseInt(etTime.getText().toString());
                createAlarmIntent(time);
            }
        });
    }

    private void setToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // strzałka z powrotem
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ikona powrotu
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu3, menu);
        return true;
    }

    private void setInstance() {
        ins = this;
    }

    private void setDBInstance() {
        mDB = new SqlLiteHelper(this); //this jako context
    }

    public static AlarmTry getInstace() {
        return ins;
    }


    protected void showTableData(Cursor cursor) {
        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            buffer.append("ID " + cursor.getString(0) + "\n");
            buffer.append("StartTime " + cursor.getString(1) + "\n");
            buffer.append("EndTime " + cursor.getString(2) + "\n");
            buffer.append("Day of week " + cursor.getString(3) + "\n");
            buffer.append("Subject " + cursor.getString(4) + "\n");
            buffer.append("Classroom " + cursor.getString(5) + "\n");
            buffer.append("Teacher " + cursor.getString(6) + "\n");
            buffer.append("Description " + cursor.getString(7) + "\n");
            buffer.append("Color " + cursor.getString(8) + "\n");
            buffer.append("Frequency " + cursor.getString(9) + "\n\n"); // moze 0/1/2?
        }
        //cursor.close();
        showData("data", buffer.toString());
    }

    private void showData(String tile, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);// we can cancel it
        builder.setTitle(tile);
        builder.setMessage(message);
        builder.create().show();

    }

    public void createAlarmIntent(int time) {
        Intent intent = new Intent(ins, Alarm.class);
        intent.setAction(Intent.ACTION_ANSWER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ins.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) ins.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time * 1000, pendingIntent); // podaje w sekundach
    }

    /** Changes text in textView in thread */
    public void updateTheTextView(final String t) {
        AlarmTry.this.runOnUiThread(new Runnable() {
            public void run() {
                tvUpdate.setText(t);
            }
        });
    }
}
