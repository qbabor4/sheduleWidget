package qbabor4.pl.alarmmanagertry;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

import android.database.Cursor;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    /**
     * TODO
     * statystki ile ktos juz chodził
     * ile miał jakich zajęc
     * ile czasu spedził w szkole
     * ile jeszcze do chodzenia
     * ile do wakacji
     * jak wywala sie widget, to trzeba usunąć alarm
     * pobieranie danych czasu tak, ze pokazuje sie zegar
     * podawanie czasu w pm/am czy tylko 24h?
     * pokazac kolene pole nad tym ekranem co jest z wyborem czasu na zegarze
     * rozwijane munu na dni tygodnia (albo na górze, to zanaczania jako remoteButton)
     * zobaczyc czy da sie lepiej odczytywac dane od StringBufera z Cursera
     * jak se kliknie na widget, to dać do activity_main
     * kolory do kazdego przedmiotu
     * Dodać widget
     * nie robic 2 razy Calendar rightNow = Calendar.getInstance();
     * zrobić klasę do danych z kalędarza ?
     */

    Button btnSet;
    Button btnAddToDB;
    Button btnShowAllDB;
    Button btnUpdateDB;
    Button btnDeleteDB;

    EditText etTime;
    EditText etId;
    EditText etStartTime;
    EditText etEndTime;
    EditText etDayOfWeek;
    EditText etSubject;
    EditText etClassroom;
    EditText etTeacher;
    EditText etDescription;

    TextView tvUpdate;

    static SqlLiteHelper myDB;
    private static MainActivity ins;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Layout */
        setToolbar();
        setNavigationDrawer();

        setInstance();
        setWidgets();
        setButtonListeners();
        setDBInstance();


    }

    private void setNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setToolbar(){
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {
            Toast.makeText(MainActivity.this, "Clicked action menu", Toast.LENGTH_SHORT).show();
        } else if (mActionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setDBInstance() {
        myDB = new SqlLiteHelper(this); //this jako context
    }

    private void setInstance() {
        ins = this;
    }

    private void setButtonListeners() {
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int time = Integer.parseInt(etTime.getText().toString());
                createAlarmIntent(time);
            }
        });

        btnAddToDB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (insertDataToDB()) {
                    Toast.makeText(getApplicationContext(), "adding data", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnShowAllDB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Cursor result = myDB.getAllData();
                if (result.getCount() == 0) {
                    Toast.makeText(getApplicationContext(), "no data", Toast.LENGTH_SHORT).show();
                } else {
                    showTableData(result);
                }
            }
        });

        btnUpdateDB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (updateDataDB()) {
                    Toast.makeText(getApplicationContext(), "updated data", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDeleteDB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (deleteDataDB()) {
                    Toast.makeText(getApplicationContext(), "deleted data", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setWidgets() {
        btnSet = (Button) findViewById(R.id.btnAlarm);
        btnAddToDB = (Button) findViewById(R.id.btn_add_to_DB);
        btnShowAllDB = (Button) findViewById(R.id.btn_show_DB);
        btnUpdateDB = (Button) findViewById(R.id.btn_update_DB);
        btnDeleteDB = (Button) findViewById(R.id.btn_delete_DB);

        etTime = (EditText) findViewById(R.id.etAlarm);
        etId = (EditText) findViewById(R.id.et_id);
        etStartTime = (EditText) findViewById(R.id.start_time);
        etEndTime = (EditText) findViewById(R.id.end_time);
        etDayOfWeek = (EditText) findViewById(R.id.day_of_week);
        etSubject = (EditText) findViewById(R.id.subject);
        etClassroom = (EditText) findViewById(R.id.classroom);
        etTeacher = (EditText) findViewById(R.id.teacher);
        etDescription = (EditText) findViewById(R.id.description);

        tvUpdate = (TextView) findViewById(R.id.tv_update); //TODO
    }

    public static SqlLiteHelper getDatabaseInstance() {
        return myDB;
    }

    public boolean deleteDataDB() {
        EditText etId = (EditText) findViewById(R.id.et_id);
        String id = String.valueOf(etId.getText());
        return myDB.deleteData(id);
    }

    protected void showTableData(Cursor cursor) {

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) { // zobaczyc jak to działą z tym bufferem TODO
            buffer.append("ID " + cursor.getString(0) + "\n");
            buffer.append("StartTime " + cursor.getString(1) + "\n");
            buffer.append("EndTime " + cursor.getString(2) + "\n");
            buffer.append("Day of week " + cursor.getString(3) + "\n");
            buffer.append("Subject " + cursor.getString(4) + "\n");
            buffer.append("Classroom " + cursor.getString(5) + "\n");
            buffer.append("Teacher " + cursor.getString(6) + "\n");
            buffer.append("Description " + cursor.getString(7) + "\n\n");
        }
        //cursor.close();
        showData("data", buffer.toString());
    }

    private void showData(String tile, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);// we can cancel it
        builder.setTitle(tile);
        builder.setMessage(message);
        builder.show();
    }

    public boolean updateDataDB() {
        String id = String.valueOf(etId.getText());
        String startTimeStr = String.valueOf(etStartTime.getText());
        int startTimeInMinutes = getTimeInMinutes(startTimeStr);
        int endTimeInMinutes = getTimeInMinutes(String.valueOf(etEndTime.getText()));
        int dayOfWeekInt = Integer.parseInt(String.valueOf(etDayOfWeek.getText()));
        String subjectStr = String.valueOf(etSubject.getText());
        String classroomStr = String.valueOf(etClassroom.getText());
        String teacherStr = String.valueOf(etTeacher.getText());
        String descriptionStr = String.valueOf(etDescription.getText());
        return myDB.updateData(id, startTimeInMinutes, endTimeInMinutes, dayOfWeekInt, subjectStr, classroomStr, teacherStr, descriptionStr);
    }

    public boolean insertDataToDB() {
        String startTimeStr = String.valueOf(etStartTime.getText());
        int startTimeInMinutes = getTimeInMinutes(startTimeStr);
        int endTimeInMinutes = getTimeInMinutes(String.valueOf(etEndTime.getText()));
        int dayOfWeekInt = Integer.parseInt(String.valueOf(etDayOfWeek.getText()));
        String subjectStr = String.valueOf(etSubject.getText());
        String classroomStr = String.valueOf(etClassroom.getText());
        String teacherStr = String.valueOf(etTeacher.getText());
        String descriptionStr = String.valueOf(etDescription.getText());
        return myDB.insertData(startTimeInMinutes, endTimeInMinutes, dayOfWeekInt, subjectStr, classroomStr, teacherStr, descriptionStr);
    }

    public void updateTheTextView(final String t) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                tvUpdate.setText(t);
            }
        });
    }

    // zakładam, ze poda dobre dane
    private int getTimeInMinutes(String time) {
        // TODO przeparsować dane z zegara na minuty
        return Integer.parseInt(time);
    }

    public void createAlarmIntent(int time) {
        Intent intent = new Intent(ins, Alarm.class);
        intent.setAction(Intent.ACTION_ANSWER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ins.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) ins.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + time * 1000, pendingIntent);
    }

    public static MainActivity getInstace() {
        return ins;
    }
}
