package qbabor4.pl.alarmmanagertry;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Brak klawiatury jak sie klika na edittext
 * wybieranie czasu co 5 min
 * zmiana koloru pickera czasu
 * picker wygladow tak jak u gościa
 * brak wibracji przy zmianie
 * podswietlanie edittextów na inny kolor
 * ustawianie koloru zajęć
 * przedziały czasowe
 * wybór z listy juz instniejacych przedmiotów
 * moze byc kilka razy w tygodniu
 * przycisk zatwierdzenia w prawym, górnym rogu
 * sprawdzac czy czas rozpoczecia jest wczesniej od zakonczenia albo jak nie ma rozpoczecia, to sprawdzac na odwrót (ustawic na taki sam czas jak źle poda
 * wysuwana lista jak nacisnie dzien tygodnia
 *
 * <p>
 * Created by Jakub on 07-Dec-17.
 */

public class AddNewClass extends AppCompatActivity implements View.OnClickListener {

    EditText etStartTime, etEndTime, etSubject, etDayOfWeek, etTeacher, etClassroom, etDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_class);
        setToolbar();
        setButtons();
        dontShowKeyboardOnStart();
    }

    private void setButtons() {
        etStartTime = (EditText) findViewById(R.id.et_start_time);
        etStartTime.setOnClickListener(this);
        etEndTime = (EditText) findViewById(R.id.et_end_time);
        etEndTime.setOnClickListener(this);
        etSubject = (EditText) findViewById(R.id.et_subject);
        etSubject.setOnClickListener(this);
        etDayOfWeek = (EditText) findViewById(R.id.et_day_of_week);
        etDayOfWeek.setOnClickListener(this);
        etTeacher = (EditText) findViewById(R.id.et_teacher);
        etTeacher.setOnClickListener(this);
        etClassroom = (EditText) findViewById(R.id.et_classroom);
        etClassroom.setOnClickListener(this);
        etDescription = (EditText) findViewById(R.id.et_description);
        etDescription.setOnClickListener(this);
    }

    private void dontShowKeyboardOnStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * TOOLBAR
     */
    private void setToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // strzałka z powrotem
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                // ikona powrotu
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {
            Toast.makeText(this, "Clicked action menu", Toast.LENGTH_SHORT).show();
        }

        // dodac ikonę powrotu i dac finish jak kliknie na nią
        // dać ikonę z lewej

        return super.onOptionsItemSelected(item);
    }

    /**
     * end Toolbar
     */

    @Override
    public void onClick(View v) {
        if (v.equals(etStartTime) || v.equals(etEndTime)) {
            showTimePicker((EditText) v);
            // nie działa
            Toast.makeText(getApplicationContext(), "LOL", Toast.LENGTH_SHORT).show();
        } else if (v.equals(etTeacher)) {
            Toast.makeText(getApplicationContext(), "te", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTimePicker(final EditText v) {
        int hourOfDay = 12;
        int minute = 0;
        boolean is24HourView = true;

        TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewClass.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                v.setText(hourOfDay + ":" + minute);
            }
        }, hourOfDay, minute, is24HourView);
        timePickerDialog.show();
    }

}
