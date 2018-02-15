package qbabor4.pl.schoolschedule;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static qbabor4.pl.schoolschedule.AddNewClass.ADD_NEW_CLASS;

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
 * jak se kliknie na widget, to dać do alarm_try
 * kolory do kazdego przedmiotu
 * Dodać widget
 * nie robic 2 razy Calendar rightNow = Calendar.getInstance();
 * zrobić klasę do danych z kalędarza ?
 * zamnąc drawer jak sie przejdzie do nowego layouta
 * patrzec na dni od do a nie tylko do bo jak jest cos w srode, to poniedziałek widac
 * zmianic wymiary stałe na procenty canvasa i wymiary tekstu
 * przerwa jak sie pokazuje dane jak dodałem pszyre (moze na koncu był enter (trimować inputy)
 * pokazywac gdzie teraz jestes na planie
 * ustawic czas update na taki, żeby sie nie robiło
 * przechodzenie do aplikacji jak sie kliknie na widget
 * jak edytuje zajecia to powinno updatowac alarm
 * wpakowac wsztstko jakoś (teraz nie widac sali)
 * nie zmienia danych zaj\e\c na widgeci po właczeniu alarmu (zmieniło po jakims czasie)
 * sqlitehelper jak propertymanager statycznie getInstance i od razu przydzielic konstruktor do zmiennej jak sie da (moze byc problem z contextem
 * przy
 *  narysowac nazwy zajęć na prostokątach (kolor tekstu bedzie podawany przez uzytkownika
 *  patrzec na pixele od krawedzi w rectanglu i jak tekst bedzie pasował, to dac, jak nie, to zmiejszyc
 *
 * TODO IFTIME:
 * dodać mój color picker do wyboru koloru
 */
public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnTouchListener {
    /**
     * Drawer menu
     */
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    /**
     * Class instances
     */
    private static SqlLiteHelper mDB;
    private static MainActivity thisInstance;
    /**
     * Rectangles on screen
     */
    private List<Rect> rectangleClasses = new ArrayList<>();
    /**
     * Data of classes (start time, end time, day of week etc.
     */
    private List<HashMap<SqlDataEnum, String>> classesData = new ArrayList<>();
    private Canvas canvas;
    private SurfaceView canvasSurfaceView;
    /**
     * canvas data got when canvas is made
     */
    private int canvasSurfaceViewWidth;
    private int canvasSurfaceViewHeight;
    private int scheduleWidth;
    private int scheduleHeight;

    private int startTimeDisplayed;
    private int endTimeDisplayed;
    private int numOfTimesDisplayed;
    /** Gap between times on schedule can be 15, 30 min, 1 or 2 hours */
    private int gapBetweenTimesDisplayed;
    private int firstLineYValue; // moze to lokalnie w drawDefault? albo drawRectangleClasses
    private int lastLineYValue; // ^
    private int rowWidth;
    /**
     * Data from DB
     */
    private String[] daysOfWeek;
    /** Needed to move rectangle */
    private int minDay;
    /**
     * Constant canvas data
     */
    private static final int TIME_SECTION_SIZE = 150;
    private static final int DAYS_SECTION_SIZE = 80;
    private static final int NO_LINE_SIZE = 15;
    private static final int RECTANGLE_HORIZONTAL_PADDING = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(qbabor4.pl.schoolschedule.R.layout.main_activity);
        setInstance();
        setDatabaseInstance();
        setToolbar();
        setNavigationDrawer();
        setNavigationViewListener();
        getDataFromDB();
        setSurfaceView();
    }


    private void setInstance() {
        thisInstance = this;
    }

    public static Context getContext(){
        return thisInstance.getApplicationContext();
    }

    private void setDatabaseInstance() {
        // moze tu ostawiac zmienną statyczną na poczatku z tym contextem, a potem tylko brac tamtą instancje? TODO
        mDB = new SqlLiteHelper(this); //this jako context
    }

    private void setToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_main_activity);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.toolbar_main_activity, menu);
        return true;
    }

    private void setNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) { // plus icon
            addNewClassActivity();
        } else if (mActionBarDrawerToggle.onOptionsItemSelected(item)) { // when drawer is opened by clicking on button
//            Toast.makeText(MainActivity.this, "opening/closing drawer", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setNavigationViewListener() {
        NavigationView navigation = (NavigationView) findViewById(R.id.navigationView);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_add_new_class:
                        addNewClassActivity();
                        mDrawerLayout.closeDrawers();
                        return true;
//                    case R.id.navigation_add_free_days:
//                        // dodawanie wolnych dni TODO
//                        return true;
                    case R.id.navigation_time_to_holiday:
                        timeToHolidayActivity();
                        return true;
//                    case R.id.navigation_alarm_try:
//                        alarmTryActivity();
//                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void getDataFromDB() {
        Cursor result = mDB.getAllData();
        if (result.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "no data", Toast.LENGTH_SHORT).show();
            // pusty plan od 8 do 16 plan TODO

        } else {
            setClassesData(result);
        }
    }

    private void setSurfaceView() {
        canvasSurfaceView = (SurfaceView) findViewById(R.id.surface);
        canvasSurfaceView.getHolder().addCallback(this);
        ViewTreeObserver observer = canvasSurfaceView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                canvasSurfaceViewHeight = canvasSurfaceView.getHeight();
                canvasSurfaceViewWidth = canvasSurfaceView.getWidth();
                setScheduleSize();
                removeOnGlobalLayoutListener(canvasSurfaceView, this);
            }
        });
        setOnTouchListenerOnCanvas();
    }

    private void setOnTouchListenerOnCanvas() {
        canvasSurfaceView.setOnTouchListener(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // jakby tu pobieracdane z bazy zobaczyc TODO
        setCanvas(holder);
        drawDefault();
        drawRectangleClasses();
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("sur", "CHANGED");
        // tu sie robi jak jest rotate
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("sur", "DESTROYED");
    }


    // ------------- TODO pozmianiac kolejnosc

    /** Reads data from database and sets variable classesData */
    private void setClassesData(Cursor cursor) {
        while (cursor.moveToNext()) {
            HashMap<SqlDataEnum, String> classData = new HashMap<>();
            SqlDataEnum[] rowNames = SqlDataEnum.values();
            for (int i = 0; i < rowNames.length; i++) {
                classData.put(rowNames[i], cursor.getString(i));
            }
            classesData.add(classData);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    private void setScheduleSize() {
        this.scheduleHeight = canvasSurfaceViewHeight - DAYS_SECTION_SIZE;
        this.scheduleWidth = canvasSurfaceViewWidth - TIME_SECTION_SIZE;
    }

    private void drawDefault() {
        // co któraś linia w innym kolorze? moze kazda inna co 3 w innym kolorze (odcienie szarego)
        int minStartTime = mDB.getMinStartTime();
        int maxEndTime = mDB.getMaxEndTime();
        minDay = mDB.getMinDay();
        int maxDay = mDB.getMaxDay();
        setDays(minDay, maxDay);

        setGlobalGapAndStartAndStopTimeDisplayed(minStartTime, maxEndTime);
        setGlobalValuesOfFirstAndLastLineOnYaxis(); // to jakos inaczej, bez globalnych jak sie da

        drawLineUnderDays();

        // TODO pobierane na podstawie bazy i języka (pol/ang) (brać kawałek jak sie dowiem z bazy
        setGlobalVariables();
        drawDaysNames(daysOfWeek); // daysOfWeek chyba beda globalnie, albo tylko długość arrraya
        drawTimesWithLines();
    }

    private void setDays(int firstDay, int lastDay) { //TODO
        daysOfWeek = Arrays.copyOfRange(TimeTools.DAYS_OF_WEEK_PL, firstDay, lastDay + 1);
    }

    private void drawRectangleClasses() {
        for (Map<SqlDataEnum, String> classData : classesData) {
            drawRectangle(classData);
        }
    }

    private int getRowWidth() {
        return scheduleWidth / daysOfWeek.length;
    }

    private void setGlobalVariables() { // dodac inne zmienne
        rowWidth = getRowWidth();
    }

    private void drawRectangle(Map<SqlDataEnum, String> classData) {
        Log.d("lol34", "lol3" + startTimeDisplayed + " " + endTimeDisplayed + " " + gapBetweenTimesDisplayed);
        int day = Integer.parseInt(classData.get(SqlDataEnum.DAY_OF_WEEK)) - minDay;
        int startTime = Integer.parseInt(classData.get(SqlDataEnum.START_TIME));
        int stopTime = Integer.parseInt(classData.get(SqlDataEnum.END_TIME));

        // globalnie czas pomiedzy poczatkiem i koncem
        float percentageRectangleStartY = (startTime - startTimeDisplayed) / (float) (endTimeDisplayed - startTimeDisplayed);
        float percentageRectangleEndY = (stopTime - startTimeDisplayed) / (float) (endTimeDisplayed - startTimeDisplayed);
        // odliegłosc czasów plus procentstart * wysokosc w pixelach
        Log.d("lol34", "draw: " + firstLineYValue + " " + lastLineYValue  + " " + percentageRectangleStartY + " " + percentageRectangleEndY );
        int y1 = firstLineYValue + Math.round(percentageRectangleStartY * (lastLineYValue - firstLineYValue));
        int y2 = firstLineYValue + Math.round(percentageRectangleEndY * (lastLineYValue - firstLineYValue));

        int x1 = TIME_SECTION_SIZE + rowWidth * day + RECTANGLE_HORIZONTAL_PADDING;
        int x2 = TIME_SECTION_SIZE + rowWidth * (day + 1) - RECTANGLE_HORIZONTAL_PADDING;

        Log.d("lol7",getColorFromData(classData));
        Log.d("lol34", x1 + " " + x2 + " " + y1 + " " + y2);
        drawRectangle(x1, y1, x2, y2, getColorFromData(classData), classData.get(SqlDataEnum.SUBJECT));
    }

    private String getColorFromData(Map<SqlDataEnum, String> classData){
        String color = classData.get(SqlDataEnum.COLOR);
        Log.d("color", color);
        if (color.equals("")){
            color =  "#0093B2";
        } else {
            color = "#" + color;
        }
        return color;
    }

    private void drawRectangle(int left, int top, int right, int bottom, String hexColor, String text) {
        Rect rect = new Rect(left, top, right, bottom);
        rectangleClasses.add(rect);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        Log.d("lol4", hexColor+"");
        paint.setColor(Color.parseColor(hexColor));
        canvas.drawRect(rect, paint);
        drawText(text, left, top, right, bottom);
    }

    private void drawTimesWithLines() {
        Paint paintOfTime = getPaintOfTimeToDisplay();
        for (int i = 0; i < numOfTimesDisplayed; i++) { // 17 czasów (moze dac do zmiennej ile wyświetlać)
            String timeToDraw = getTimeToDisplay(i);
            Rect bounds = new Rect();
            paintOfTime.getTextBounds(timeToDraw, 0, timeToDraw.length(), bounds);
            canvas.drawLine(TIME_SECTION_SIZE, getYaxisOfLine(i), canvasSurfaceViewWidth, getYaxisOfLine(i), paintOfTime);
            canvas.drawText(timeToDraw, getXaxisOfTimeDisplayed(bounds), getYaxisOfTimeDisplayed(i, bounds), paintOfTime);
        }
    }

    /**
     * Draws days on top of schedule after getting data about days from database
     * @param daysOfWeek
     */
    private void drawDaysNames(String[] daysOfWeek) { // moze tu sie da podawać dni a nie globalnie
        Paint paintDay = getPaintOfDaysToDisplay();
        for (int i = 0; i < daysOfWeek.length; i++) {
            String day = daysOfWeek[i];
            Rect bounds = new Rect();
            paintDay.getTextBounds(day, 0, day.length(), bounds);
            canvas.drawText(day, TIME_SECTION_SIZE + (i * 2 + 1) * ((scheduleWidth / (daysOfWeek.length * 2))) - bounds.width() / 2, DAYS_SECTION_SIZE / 2 + paintDay.getTextSize() / 4, paintDay); // getTextSize is /4 becouse it looks better, and positions ok
        }
    }

    /**
     * Draws 1 line under days of week to make it better looking
     */
    private void drawLineUnderDays() {
        Paint paintOfLineUnderDays = getPaintOfLineUnderDays();
        canvas.drawLine(NO_LINE_SIZE, DAYS_SECTION_SIZE, canvasSurfaceViewWidth - NO_LINE_SIZE, DAYS_SECTION_SIZE, paintOfLineUnderDays); // horisontal line under days of week
    }

    /**
     * Gets Paint with color and style to draw line under days
     * @return
     */
    private Paint getPaintOfLineUnderDays() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#aaaaaa"));
        return paint;
    }

    private Paint getPaintOfDaysToDisplay() {
        Paint paintDay = new Paint();
        paintDay.setColor(Color.BLACK);
        paintDay.setTextSize(50);
        return paintDay;
    }

    private Paint getPaintOfTimeToDisplay() {
        Paint paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(50);
        return paintText;
    }

    private String getTimeToDisplay(int index) {
        int minutesTotal = startTimeDisplayed + gapBetweenTimesDisplayed * index;
        int hours = minutesTotal / 60;
        String minutes = String.valueOf(minutesTotal % 60);
        if (minutes.length() == 1) {
            minutes = '0' + minutes;
        }
        return hours + ":" + minutes;
    }

    private int getXaxisOfTimeDisplayed(Rect bounds) {
        return TIME_SECTION_SIZE / 2 - bounds.width() / 2;
    }

    private int getYaxisOfTimeDisplayed(int index, Rect bounds) {
        return getYaxisOfLine(index) + bounds.height() / 2;
    }

    private int getYaxisOfLine(int indexOfLinOnScreen) {
        return DAYS_SECTION_SIZE + (indexOfLinOnScreen * 2 + 1) * ((scheduleHeight / ((numOfTimesDisplayed) * 2))); // czy ja bede potrzebowal w innych miejscach numOfTimesDisplayed? (moze podawac do funkcji TODO
    }

    private void setGlobalValuesOfFirstAndLastLineOnYaxis() {
        firstLineYValue = getYaxisOfLine(0);
        lastLineYValue = getYaxisOfLine(numOfTimesDisplayed - 1);
    }

    private void setGlobalGapAndStartAndStopTimeDisplayed(int startTime, int stopTime) {
            Log.d("lol1", startTime + stopTime + "lol");
        if (startTime + stopTime == 0){
            setGapAndStartAndStopTimeDisplayed(480, 960, 15);
        } else {
            setGapAndStartAndStopTimeDisplayed(startTime, stopTime, 15); // starts looking with 15 min gap
        }
    }

    /**
     * Sets global variables: startTimeDisplayed, gapBetweenTimesDisplayed, endTimeDisplayed
     * @param startTime start time of earliest class in week
     * @param stopTime stop time of earliest class in week
     * @param gap gap between times to be displayed. If there will be more than 17 times displayed gap is multipied by 2
     */
    private void setGapAndStartAndStopTimeDisplayed(int startTime, int stopTime, int gap) { // setuje czas poczatkowy i koncowy
        startTimeDisplayed = startTime - startTime % gap;
        int stopTimeCounted = stopTime;
        if (stopTime % gap != 0) {
            stopTimeCounted += gap - stopTime % gap; /* f.e.: When there is 8:07 it will add 8 when gap is 15 and there will be 8:15 */
        }
        numOfTimesDisplayed = (stopTimeCounted - startTimeDisplayed) / gap + 1;
        gapBetweenTimesDisplayed = gap;
        if (numOfTimesDisplayed > 17) {   /* you can change here maximum number of times that can be displayed on screen */
            setGapAndStartAndStopTimeDisplayed(startTime, stopTime, gap * 2);
        } else {
            endTimeDisplayed = startTimeDisplayed + (numOfTimesDisplayed-1) * gap;
            if (endTimeDisplayed > 1440) { /* when more than 24:00 */
                endTimeDisplayed = 1440;
            }
        }
    }

    private void setCanvas(SurfaceHolder holder) {
        canvas = holder.lockCanvas();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
    }

    private void drawText(String text, int x1, int y1, int x2, int y2) { /// kto to panu tak spierdo**ł... TODO
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(39);  //set text size
        float w = paint.measureText(text) / 2;
        // długosc textu
        float textSize = paint.getTextSize();
        if (text != null && text.length() > 4 ) {
            text = text.substring(0, 5);
        }
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, x1 + (x2-x1)/2 - bounds.width()/2, y1 + (y2 - y1)/2 - bounds.height() /2 , paint);
        // wpisywanie textu w rectangle (patrzenie na x, y rectangla i na tej podstawie text)
        // upierdalać text jak wychodzi poza obszar
        // wpisywac na środku
    }

    /**
     * on touch listener used when user touches rectangle
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) { // zrobic on hold jak bedzie przytrzymywał
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                for (int i = 0; i < rectangleClasses.size(); i++) {
                    if (rectangleClasses.get(i).contains(touchX, touchY)) {
                        showClassData(classesData.get(i));
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
//                Toast.makeText(AlarmTry.getInstance(), "up", Toast.LENGTH_SHORT).show();
                break;
            case MotionEvent.ACTION_MOVE:
//                Toast.makeText(AlarmTry.getInstance(), "sliding", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    /**
     * The same activity ad adding new class but with given data
     * After confirmation data is updated, not inserted
     * @param classData
     */
    private void startEditClassActivity(HashMap<SqlDataEnum, String> classData) {
        Intent intent = new Intent(this, AddNewClass.class);
        Log.d("llol", classData.toString());
        intent.putExtra("classData", classData);
        startActivity(intent);
    }

    /**
     * Data of class shown when user touches on rectangle to show him info of this class
     * @param classData
     * @return
     */
    private String getClassDataToDisplay(HashMap<SqlDataEnum, String> classData) {
        String out = "";
        SqlDataEnum[] sqlDataEnumValues = SqlDataEnum.values();
        for (SqlDataEnum sqlDataEnum : sqlDataEnumValues) {
            if (sqlDataEnum == SqlDataEnum.START_TIME || sqlDataEnum == SqlDataEnum.END_TIME ){
                out += sqlDataEnum.getDescriptionPL() + ": " + TimeTools.getClockFormatTime(classData.get(sqlDataEnum)) + "\n";
            } else if (sqlDataEnum == SqlDataEnum.DAY_OF_WEEK ) {
                out += sqlDataEnum.getDescriptionPL() + ": " + TimeTools.DAYS_OF_WEEK_PL_FULL[Integer.parseInt(classData.get(sqlDataEnum))] + "\n";
            } else if (sqlDataEnum != SqlDataEnum.ID && sqlDataEnum != SqlDataEnum.COLOR){
                out += sqlDataEnum.getDescriptionPL() + ": " + classData.get(sqlDataEnum) + "\n";
            }
        }
        return out;
    }

    /**
     * Deletes data from database
     * @param classData
     * @return
     */
    private boolean deleteClass(HashMap<SqlDataEnum, String> classData) {
        return mDB.deleteData(classData.get(SqlDataEnum.ID));
    }


    private void showClassData(final HashMap<SqlDataEnum, String> classData) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.class_data_dialog, null);
        final AlertDialog alertD = new AlertDialog.Builder(this).create();

        TextView tvClassData = (TextView) promptView.findViewById(R.id.textView);
        tvClassData.setText(getClassDataToDisplay(classData));
        Button btnDelete = (Button) promptView.findViewById(R.id.delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                confirmDeleteDialog(classData);
            }
        });

        Button btnEdit = (Button) promptView.findViewById(R.id.edit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startEditClassActivity(classData);
            }
        });

        alertD.setView(promptView);
        alertD.show();
    }

    private void confirmDeleteDialog(final HashMap<SqlDataEnum, String> classData) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        Alarm.updateWidget(getApplicationContext());
                        deleteClass(classData);
                        finish();
                        startActivity(getIntent());
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Czy na pewno chcesz usunąć te zajęcia?").setPositiveButton("Tak", dialogClickListener)
                .setNegativeButton("Nie", dialogClickListener).show();
    }

    private void addNewClassActivity() {
        Intent intent = new Intent(this, AddNewClass.class);
        startActivity(intent);
    }

    private void timeToHolidayActivity() {
        Intent intent = new Intent(this, TimeToHoliday.class);
        startActivity(intent);
    }

    private void alarmTryActivity() {
        Intent intent = new Intent(this, AlarmTry.class);
        startActivity(intent);
    }
}



