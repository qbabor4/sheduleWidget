package qbabor4.pl.alarmmanagertry;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Time;

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
 * zamnąc drawer jak sie przejdzie do nowego layouta
 */
public class MainActivity extends AppCompatActivity {


    public static final String EXTRA_MESSAGE = "qbabor4.pl.alarmmanagertry.MESSAGE";

    Button btnSet, btnAddToDB, btnShowAllDB, btnUpdateDB, btnDeleteDB;

    EditText etTime, etId;

    TextView tvUpdate;

    private static SqlLiteHelper myDB;
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
        setNavigationViewListener();

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

    private void setToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    private void addNewClassActivity(){
        Intent intent = new Intent(this, AddNewClass.class);
        startActivity(intent);
    }

    private void timetableActivity(){
        Intent intent = new Intent(this, TimetableCanvas.class);
        startActivity(intent);
    }

    private void setNavigationViewListener() {
        NavigationView navigation = (NavigationView) findViewById(R.id.navigationView);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_add_new_class:
                        // Handle menu click
                        // nowy layout z dodawaniem zajęć (dodać do bazy)
                        addNewClassActivity();
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.navigation_add_free_days:
                        Toast.makeText(MainActivity.this, "add free days", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {
            timetableActivity();

        } else if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {

            Toast.makeText(MainActivity.this, "opening drawer", Toast.LENGTH_SHORT).show();
            // zmienic text w toolbarze na "Menu"
            return true;
        }
        // złapać jak zamyka navigacyjne menu
        // jak sam otwiera to też coś zrobić


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
        btnShowAllDB = (Button) findViewById(R.id.btn_show_DB);
        btnDeleteDB = (Button) findViewById(R.id.btn_delete_DB);

        etTime = (EditText) findViewById(R.id.etAlarm);
        etId = (EditText) findViewById(R.id.et_id);

        tvUpdate = (TextView) findViewById(R.id.tv_update); //TODO
    }

    public static SqlLiteHelper getDatabaseInstance() {
        return myDB;
    } // to bedzie instancja dla planu i dla widgeta

    public boolean deleteDataDB() {
        EditText etId = (EditText) findViewById(R.id.et_id);
        String id = String.valueOf(etId.getText());
        return myDB.deleteData(id);
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

        ////////////////////////////-------------
        builder.setMessage(message);
//        builder.setItems(new CharSequence[]
//                        {"button 1", "button 2", "button 3", "button 4"},
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // The 'which' argument contains the index position
//                        // of the selected item
//                        switch (which) {
//                            case 0:
//                                Toast.makeText(getInstace(), "clicked 1", Toast.LENGTH_SHORT).show();
//                                break;
//                            case 1:
//                                Toast.makeText(getInstace(), "clicked 2", Toast.LENGTH_SHORT).show();
//                                break;
//                            case 2:
//                                Toast.makeText(getInstace(), "clicked 3", Toast.LENGTH_SHORT).show();
//                                break;
//                            case 3:
//                                Toast.makeText(getInstace(), "clicked 4", Toast.LENGTH_SHORT).show();
//                                break;
//                        }
//                    }
//                });
        builder.create().show();

    }

//    public boolean updateDataDB() {
//        String id = String.valueOf(etId.getText());
//        String startTimeStr = String.valueOf(etStartTime.getText());
//        int startTimeInMinutes = getTimeInMinutes(startTimeStr);
//        int endTimeInMinutes = getTimeInMinutes(String.valueOf(etEndTime.getText()));
//        int dayOfWeekInt = Integer.parseInt(String.valueOf(etDayOfWeek.getText()));
//        String subjectStr = String.valueOf(etSubject.getText());
//        String classroomStr = String.valueOf(etClassroom.getText());
//        String teacherStr = String.valueOf(etTeacher.getText());
//        String descriptionStr = String.valueOf(etDescription.getText());
//        return myDB.updateData(id, startTimeInMinutes, endTimeInMinutes, dayOfWeekInt, subjectStr, classroomStr, teacherStr, descriptionStr);
//    }

//    public boolean insertDataToDB() {
//        String startTimeStr = String.valueOf(etStartTime.getText());
//        int startTimeInMinutes = getTimeInMinutes(startTimeStr);
//        int endTimeInMinutes = getTimeInMinutes(String.valueOf(etEndTime.getText()));
//        int dayOfWeekInt = Integer.parseInt(String.valueOf(etDayOfWeek.getText()));
//        String subjectStr = String.valueOf(etSubject.getText());
//        String classroomStr = String.valueOf(etClassroom.getText());
//        String teacherStr = String.valueOf(etTeacher.getText());
//        String descriptionStr = String.valueOf(etDescription.getText());
//        return myDB.insertData(startTimeInMinutes, endTimeInMinutes, dayOfWeekInt, subjectStr, classroomStr, teacherStr, descriptionStr);
//    }

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
