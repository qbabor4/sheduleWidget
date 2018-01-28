package qbabor4.pl.schoolschedule;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import java.util.Map;

/**
 * Brak klawiatury jak sie klika na edittext
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
 * przy cofaniu nie updatuje canvasa
 * jak sie cofa ikoną na dole po lewej, to zapytac czy na zapisac czy nie
 * pokazac kolor nie jako text tylko jako kółko z kolorem
 * <p>
 * Created by Jakub on 07-Dec-17.
 */

public class AddNewClass extends AppCompatActivity implements View.OnClickListener {

    public final static int ADD_NEW_CLASS = 1;

    EditText etStartTime, etEndTime, etSubject, etTeacher, etClassroom, etDescription, etColor;
    //, etFrequency;
    Spinner spDayOfWeek;

    private HashMap<SqlDataEnum, String> classData;
    private boolean updateOperation = false;

    private static SqlLiteHelper mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(qbabor4.pl.schoolschedule.R.layout.add_new_class);
        setToolbar();
        setEditTexts();
        setSpinner();
        setDefaultInputs();
        checkForUpdateOperation();
        setDBInstance();
        dontShowKeyboardOnStart();
    }

    private void checkForUpdateOperation() {
        classData = (HashMap<SqlDataEnum, String>) getIntent().getSerializableExtra("classData");
        if (classData != null) {
            updateOperation = true; // mozna sprawdxzac czy mapa jest null
            Log.d("lol4", classData.toString());
            setClassInputs();
        } else {
            classData = new HashMap<>(); // wywalic moze
        }
    }

    private void setClassInputs() {
        etSubject.setText(classData.get(SqlDataEnum.SUBJECT));
        etTeacher.setText(classData.get(SqlDataEnum.TEACHER));
        etClassroom.setText(classData.get(SqlDataEnum.CLASSROOM));
        etDescription.setText(classData.get(SqlDataEnum.DESCRIPTION));
        etColor.setText(classData.get(SqlDataEnum.COLOR));
//        etFrequency.setText(classData.get(SqlDataEnum.FREQUENCY));
        spDayOfWeek.setSelection(Integer.parseInt(classData.get(SqlDataEnum.DAY_OF_WEEK)));
        etStartTime.setText(TimeTools.getClockFormatTime(classData.get(SqlDataEnum.START_TIME)));
        etEndTime.setText(TimeTools.getClockFormatTime(classData.get(SqlDataEnum.END_TIME)));
    }

    private void setDBInstance() {
        mDB = new SqlLiteHelper(this); //this jako context
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
//        etFrequency = (EditText) findViewById(R.id.et_frequency);
//        etFrequency.setOnClickListener(this);
    }

    private void dontShowKeyboardOnStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void setSpinner() {
        spDayOfWeek = (Spinner) findViewById(R.id.sp_day_of_week);
        List<String> daysOfWeek = new ArrayList<>();
        daysOfWeek.add("Poniedziałek");
        daysOfWeek.add("Wtorek");
        daysOfWeek.add("Sroda");
        daysOfWeek.add("Czwartek");
        daysOfWeek.add("Piątek");
        daysOfWeek.add("Sobota");
        daysOfWeek.add("Niedziela");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysOfWeek);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDayOfWeek.setAdapter(dataAdapter);
    }

    private void validateStartTimeInput() {
        if (TimeTools.getTimeInMinutesFromTimePicker(etStartTime.getText().toString()) > TimeTools.getTimeInMinutesFromTimePicker(etEndTime.getText().toString())) {
            etEndTime.setText(etStartTime.getText());
        }
    }

    private void validateEndTimeInput() {
        if (TimeTools.getTimeInMinutesFromTimePicker(etEndTime.getText().toString()) < TimeTools.getTimeInMinutesFromTimePicker(etStartTime.getText().toString())) {
            etStartTime.setText(etEndTime.getText());
        }
    }

    private void setDefaultInputs() {
        etStartTime.setText("8:00");
        etEndTime.setText("10:00");
    }

    /**
     * TOOLBAR
     */
    private void setToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_add_new_class);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // strzałka z powrotem
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ikona powrotu
                // czy na chce dodać zajęcia okienko TODO
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.toolbar_add_new_class, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_add_new_class) { // biała parafka
            if (validateTimes()) {
                if (updateOperation) {
                    if (updateDataInDB()) {
                        Toast.makeText(getApplicationContext(), "Edytuje dane zajęć", Toast.LENGTH_SHORT).show();
                        Alarm.updateWidget(getApplicationContext());
                        finish();
                        goBackToTimetableActivity();
                    } else {
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (insertDataToDB()) {
                        Toast.makeText(getApplicationContext(), "Dodaje zajęcia", Toast.LENGTH_SHORT).show();
                        Log.d("lol34", classData.toString());
                        Alarm.updateWidget(getApplicationContext());
                        finish();
                        goBackToTimetableActivity();
                    } else {
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Czasy nie mogą być takie same", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void goBackToTimetableActivity() {
        Log.d("lol1",  "in" + "");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void addDataFromInputsToClassData() {
        classData.put(SqlDataEnum.SUBJECT, etSubject.getText().toString().trim());
        classData.put(SqlDataEnum.TEACHER, etTeacher.getText().toString().trim());
        classData.put(SqlDataEnum.CLASSROOM, etClassroom.getText().toString().trim());
        classData.put(SqlDataEnum.DESCRIPTION, etDescription.getText().toString().trim());
        classData.put(SqlDataEnum.COLOR, etColor.getText().toString().trim());
//        classData.put(SqlDataEnum.FREQUENCY, etFrequency.getText().toString().trim());
        classData.put(SqlDataEnum.DAY_OF_WEEK, String.valueOf(spDayOfWeek.getSelectedItemPosition()).trim());
        classData.put(SqlDataEnum.START_TIME, String.valueOf(TimeTools.getTimeInMinutesFromTimePicker(etStartTime.getText().toString())).trim());
        classData.put(SqlDataEnum.END_TIME, String.valueOf(TimeTools.getTimeInMinutesFromTimePicker(etEndTime.getText().toString())).trim());
    }

    private boolean updateDataInDB() {
        addDataFromInputsToClassData();
        return mDB.updateData(classData);
    }

    private boolean insertDataToDB() {
        addDataFromInputsToClassData();
        return mDB.insertData(classData);
    }
    /**
     * end Toolbar
     */

    @Override
    public void onClick(View v) {
        if (v.equals(etStartTime) || v.equals(etEndTime)) {
            showTimePicker((EditText) v);
        } else if (v.equals(etColor)) {
            showColorPicker();
        }
    }

    private static Map<Integer, Integer> getARGBfromHexNumber(String hexNumber) {
        Log.d("lol5", hexNumber);
        long dec = Long.parseLong(hexNumber, 16);
        Map<Integer, Integer> colors = new HashMap<>();
        colors.put(Color.BLACK, Color.alpha((int) dec)); // alfa
        colors.put(Color.RED, Color.red((int) dec));
        colors.put(Color.GREEN, Color.green((int) dec));
        colors.put(Color.BLUE, Color.blue((int) dec));
        return colors;
    }

    private void showColorPicker() {
        String colorNow = etColor.getText().toString();
        final ColorPicker colorPicker;
        if (colorNow.equals("")) {
            colorPicker = new ColorPicker(this, 255, 0, 147, 178);
        } else {
            Map<Integer, Integer> decColors = getARGBfromHexNumber(colorNow);
            colorPicker = new ColorPicker(this, decColors.get(Color.BLACK), decColors.get(Color.RED), decColors.get(Color.GREEN), decColors.get(Color.BLUE));
        }

        colorPicker.show();
        colorPicker.setCallback(new ColorPickerCallback() {

            @Override
            public void onColorChosen(@ColorInt int color) {
                etColor.setText(String.format("%08X", (0xffffffff & color)));

            }
        });
    }

    private boolean validateTimes() {
        if (etStartTime.getText().toString().equals(etEndTime.getText().toString())) {
            return false;
        }
        return true;
    }

    private void showTimePicker(final EditText v) {
        String time = v.getText().toString();
        int colonIndex = time.indexOf(':');
        int hour = Integer.parseInt(time.substring(0, colonIndex));
        int minutes = Integer.parseInt(time.substring(colonIndex + 1));

//        TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewClass.this, R.style.Dialog, new TimePickerDialog.OnTimeSetListener() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewClass.this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int pickerMinutes) {
                String pickerMinutersStr = String.valueOf(pickerMinutes);
                if (pickerMinutersStr.length() == 1) {
                    pickerMinutersStr = "0" + pickerMinutersStr;
                }
                v.setText(hourOfDay + ":" + pickerMinutersStr);
                if (v == etStartTime) {
                    validateStartTimeInput();
                } else if (v == etEndTime) {
                    validateEndTimeInput();
                }
            }
        }, hour, minutes, true);
        timePickerDialog.show();
    }

}
