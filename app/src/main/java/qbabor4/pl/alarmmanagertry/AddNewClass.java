package qbabor4.pl.alarmmanagertry;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
 * pozmieniac kolory w timepickerze
 * jak zrobic zeby brał nazwy dni z pliu (zeby mozna było ustawic inne języki
 * dodac ikonę parafki w prawym rogu jak chce dodać zajęcia
 * scrollwiev na layoucie
 *
 * <p>
 * Created by Jakub on 07-Dec-17.
 */

public class AddNewClass extends AppCompatActivity implements View.OnClickListener , AdapterView.OnItemSelectedListener {

    EditText etStartTime, etEndTime, etSubject, etTeacher, etClassroom, etDescription, etColor, etFrequency;
    Spinner spDayOfWeek;
    Button btnAddClass;

    private static SqlLiteHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_class);
        setToolbar();
        setEditTexts();
        setButtons();
        setSpinner();
        setDefaultInputs();
        setDBInstance();
        dontShowKeyboardOnStart();
    }

    private void setDBInstance() {
        myDB = new SqlLiteHelper(this); //this jako context
    }

    private void setEditTexts() {
        etStartTime = (EditText) findViewById(R.id.et_start_time);
        etStartTime.setOnClickListener(this);
        etEndTime = (EditText) findViewById(R.id.et_end_time);
        etEndTime.setOnClickListener(this);
        etSubject = (EditText) findViewById(R.id.et_subject);
        etSubject.setOnClickListener(this);
        etTeacher = (EditText) findViewById(R.id.et_teacher);
        etTeacher.setOnClickListener(this);
        etClassroom = (EditText) findViewById(R.id.et_classroom);
        etClassroom.setOnClickListener(this);
        etDescription = (EditText) findViewById(R.id.et_description);
        etDescription.setOnClickListener(this);
        etColor = (EditText) findViewById(R.id.et_color);
        etColor.setOnClickListener(this);
        etFrequency = (EditText) findViewById(R.id.et_frequency);
        etFrequency.setOnClickListener(this);
    }

    private void setButtons(){
        btnAddClass = (Button) findViewById(R.id.btn_add_new_class);
        btnAddClass.setOnClickListener(this);
    }

    private void dontShowKeyboardOnStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void setSpinner(){
        spDayOfWeek = (Spinner) findViewById(R.id.sp_day_of_week);
        spDayOfWeek.setOnItemSelectedListener(this);
        List<String> daysOfWeek = new ArrayList<>();
        daysOfWeek.add("Poniedziałek");
        daysOfWeek.add("Wtorek");
        daysOfWeek.add("Sroda");
        daysOfWeek.add("Czwartek");
        daysOfWeek.add("Piątek");
        daysOfWeek.add("Sobota");
        daysOfWeek.add("Niedziela");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, daysOfWeek);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDayOfWeek.setAdapter(dataAdapter);

        //String text = spDayOfWeek.getSelectedItem().toString();
    }

    public boolean insertDataToDB() {
        String startTimeStr = String.valueOf(etStartTime.getText());
        int startTimeInMinutes = getTimeInMinutesFromTimePicker(startTimeStr); // sprawdzac czy null (jak zły czas to dać info, że błedny czas
        int endTimeInMinutes = getTimeInMinutesFromTimePicker(String.valueOf(etEndTime.getText()));
        int dayOfWeekInt = spDayOfWeek.getSelectedItemPosition();
        String subjectStr = String.valueOf(etSubject.getText());
        String classroomStr = String.valueOf(etClassroom.getText());
        String teacherStr = String.valueOf(etTeacher.getText());
        String descriptionStr = String.valueOf(etDescription.getText());
        String color = String.valueOf(etColor.getText());
        String frequency = String.valueOf(etFrequency.getText());
//        return myDB.insertData(startTimeInMinutes, endTimeInMinutes, dayOfWeekInt, subjectStr, classroomStr, teacherStr, descriptionStr, color, frequency);
        return true;
    }

    private void validateStartTimeInput(){
        if (getTimeInMinutesFromTimePicker(etStartTime.getText().toString()) > getTimeInMinutesFromTimePicker(etEndTime.getText().toString()) ){
            etEndTime.setText(etStartTime.getText());
        }
    }

    private void validateEndTimeInput(){
        if (getTimeInMinutesFromTimePicker(etEndTime.getText().toString()) < getTimeInMinutesFromTimePicker(etStartTime.getText().toString())){
            etStartTime.setText(etEndTime.getText());
        }
        // sprawdzac tylko czasy i dzien, a tak to dodawac puste do bazy
        // jak ustawia czas pozniejszy na wczesniej to dac ten wczesniejszy na ten sam
    }

    private void setDefaultInputs(){
        etStartTime.setText("8:00");
        etEndTime.setText("10:00");
    }

    private int getTimeInMinutesFromTimePicker(String time) {
        int colonIndex = time.indexOf(':');
        int hour = Integer.parseInt(time.substring(0, colonIndex));
        int minutes = Integer.parseInt(time.substring(colonIndex+1));
        return hour*60 + minutes;
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

        return super.onOptionsItemSelected(item);
    }

    /**
     * end Toolbar
     */

    @Override
    public void onClick(View v) {
        if (v.equals(etStartTime) || v.equals(etEndTime)) {
            showTimePicker((EditText) v);
            Toast.makeText(getApplicationContext(), "LOL", Toast.LENGTH_SHORT).show();
        } else if (v.equals(etTeacher)) {
            Toast.makeText(getApplicationContext(), "te", Toast.LENGTH_SHORT).show();
        } else if (v.equals(btnAddClass)){
            // sprawdzic czy poprawne dane
            insertDataToDB();
        }
    }

    private void showTimePicker(final EditText v) {
        String time = v.getText().toString();
        int colonIndex = time.indexOf(':');
        int hour = Integer.parseInt(time.substring(0, colonIndex));
        int minutes = Integer.parseInt(time.substring(colonIndex+1));
        boolean is24HourView = true;

        TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewClass.this, R.style.Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int pickerMinutes) {
                String pickerMinutersStr = String.valueOf(pickerMinutes);
                if (pickerMinutersStr.length() == 1){
                    pickerMinutersStr = "0" + pickerMinutersStr;
                }
                v.setText(hourOfDay + ":" + pickerMinutersStr);
                if (v == etStartTime){
                    validateStartTimeInput();
                } else if (v == etEndTime){
                    validateEndTimeInput();
                }
            }
        }, hour, minutes, is24HourView);
        timePickerDialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // wybieranie z listy dnia
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
