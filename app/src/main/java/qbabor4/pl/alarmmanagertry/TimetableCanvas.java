package qbabor4.pl.alarmmanagertry;


import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import android.view.ViewTreeObserver;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * TODO
 * wybieranie koloru w dodawaniu zajęć albo same sie beda zmieniac
 * klawisze prawo i lewo zeby zobaczy kolojne tygodnie
 * wybór koloru
 * pokazywanie danych jak sie kliknie na rectangla z guzikami delete, edit
 * jak sie przytrzyma to dać tylko mozliwosc edit
 * <p>
 * TODO IFTIME:
 * zmiana nagólwka z dniami i czasami dynamicznie (robienie wedłód procentów
 * <p>
 * Created by Jakub on 09-Dec-17.
 */

public class TimetableCanvas extends AppCompatActivity implements SurfaceHolder.Callback, View.OnTouchListener {

    private List<Rect> rectangleClasses = new ArrayList<>();

    private static TimetableCanvas ins;
//    private CanvasTouchListener canvasTouchListener;
    private int canvasSurfaceViewWidth;
    private int canvasSurfaceViewHeight;
    private int scheduleWidth;
    private int scheduleHeight;
    private Canvas canvas;
    private SurfaceView canvasSurfaceView;

    private int startTimeDisplayed;
    private int endTimeDisplayed;
    private int numOfTimesDisplayed;
    private int gapBetweenTimesDisplayed;
    private int firstLineYValue; // moze to lokalnie w drawDefault? albo drawRectangleClasses
    private int lastLineYValue; // ^
    private String[] daysOfWeek;

    private int rowWidth;

    private static final int TIME_SECTION_SIZE = 150; // moze to sie zmieni na procenty v v v
    private static final int DAYS_SECTION_SIZE = 80;
    private static final int NO_LINE_SIZE = 15;
    private static final int RECTANGLE_HORIZONTAL_PADDING = 10;

    private SqlLiteHelper mDB  = new SqlLiteHelper(this);
    private List<HashMap<SqlDataEnum, String>> classesData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_canvas);
        setInstance();
        setToolbar();
        getDataFromDB();
        setSurfaceView();
        canvasSurfaceView.setOnTouchListener(this);
    }

    private void getDataFromDB(){
        Cursor result = mDB.getAllData();
        if (result.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "no data", Toast.LENGTH_SHORT).show();
        } else {
            setClassesData(result);
        }
    }

    private void setClassesData(Cursor cursor){
        while (cursor.moveToNext()) {
            HashMap<SqlDataEnum, String> classData = new HashMap<>();
            SqlDataEnum[] rowNames = SqlDataEnum.values();
            for (int i = 0; i < rowNames.length; i++) {
                classData.put(rowNames[i], cursor.getString(i));
            }
            classesData.add(classData);
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
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
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

    private void setInstance() {
        ins = this;
    }

    public static TimetableCanvas getInstance() {
        return ins;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.home) {
//            Toast.makeText(this, "Clicked action menu", Toast.LENGTH_SHORT).show();
//        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setCanvas(holder);
        drawDefault();
        drawRectangleClasses();
        holder.unlockCanvasAndPost(canvas);
    }

    private void drawDefault() {

        // dosta tak samo dni
        // jak nic nie ma w bazie, to wyswietlic od 8 do 16 plan
        // co któraś linia w innym kolorze? moze kazda inna co 3 w innym kolorze (odcienie szarego)
        int minStartTime = mDB.getMinStartTime();
        int maxEndTime = mDB.getMaxEndTime();
        int maxDay = mDB.getMaxDay();
        setDays(maxDay);
//        setGlobalGapAndStartAndStopTimeDisplayed(540, 960); // 9 do 16 // pierwsza i ostatnia godzina w planie
        setGlobalGapAndStartAndStopTimeDisplayed(minStartTime, maxEndTime);
        setGlobalValuesOfFirstAndLastLineOnYaxis(); // to jakos inaczej, bez globalnych jak sie da

        drawLineUnderDays();

         // pobierane na podstawie bazy i języka (pol/ang) (brać kawałek jak sie dowiem z bazy
        setGlobalVariables();
        drawDaysNames(daysOfWeek); // daysOfWeek chyba beda globalnie, albo tylko długość arrraya
        drawTimesWithLines();
    }

    private void setDays(int lastDay) { //TODO
        String[] days =  {"Pon", "Wt", "Śr", "Czw", "Pt", "Sob", "Ndz"};
        daysOfWeek =  Arrays.copyOfRange(days, 0, lastDay+1);
    }

    private void drawRectangleClasses() {
        for(Map<SqlDataEnum, String> classData: classesData){
            drawRectangle(classData);
        }
    }

    private int getRowWidth() {
        return scheduleWidth / daysOfWeek.length;
    }

    private void setGlobalVariables(){ // dodac inne zmienne
        rowWidth = getRowWidth();
    }



    private void drawRectangle(Map<SqlDataEnum, String> classData) {
        int day = Integer.parseInt(classData.get(SqlDataEnum.DAY_OF_WEEK));
        int startTime = Integer.parseInt(classData.get(SqlDataEnum.START_TIME));
        int stopTime = Integer.parseInt(classData.get(SqlDataEnum.END_TIME));

        // globalnie czas pomiedzy poczatkiem i koncem
        float percentageRectangleStartY = (startTime - startTimeDisplayed) / (float)(endTimeDisplayed - startTimeDisplayed);
        float percentageRectangleEndY = (stopTime - startTimeDisplayed) / (float)(endTimeDisplayed - startTimeDisplayed);
        // odliegłosc czasów plus procentstart * wysokosc w pixelach
        int y1 = firstLineYValue + Math.round(percentageRectangleStartY * (lastLineYValue - firstLineYValue)); // nie ok firstLinevalue
        int y2 = firstLineYValue + Math.round(percentageRectangleEndY * (lastLineYValue - firstLineYValue)); // nie ok

        int x1 = TIME_SECTION_SIZE + rowWidth * day + RECTANGLE_HORIZONTAL_PADDING; //ok
        int x2 = TIME_SECTION_SIZE + rowWidth * (day+1) - RECTANGLE_HORIZONTAL_PADDING; // ok

        drawRectangle(x1, y1, x2, y2, "#000345");
    }


    private void drawRectangle(int left, int top, int right, int bottom, String hexColor) {
        Rect rect = new Rect(left, top, right, bottom);
        rectangleClasses.add(rect);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor(hexColor));
        canvas.drawRect(rect, paint);
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

    private void drawDaysNames(String[] daysOfWeek) {
        Paint paintDay = getPaintOfDaysToDisplay();
        for (int i = 0; i < daysOfWeek.length; i++) {

            String day = daysOfWeek[i];
            Rect bounds = new Rect();
            paintDay.getTextBounds(day, 0, day.length(), bounds);

            canvas.drawText(day, TIME_SECTION_SIZE + (i * 2 + 1) * ((scheduleWidth / (daysOfWeek.length * 2))) - bounds.width() / 2, DAYS_SECTION_SIZE / 2 + paintDay.getTextSize() / 4, paintDay); // getTextSize is /4 becouse it looks better, and positions ok
        }
    }

    private void drawLineUnderDays() {
        Paint paintOfLineUnderDays = getPaintOfLineUnderDays();
        canvas.drawLine(NO_LINE_SIZE, DAYS_SECTION_SIZE, canvasSurfaceViewWidth - NO_LINE_SIZE, DAYS_SECTION_SIZE, paintOfLineUnderDays); // horisontal line under days of week
    }

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
        return DAYS_SECTION_SIZE + (indexOfLinOnScreen * 2 +1) * ((scheduleHeight / ((numOfTimesDisplayed) * 2))); // czy ja bede potrzebowal w innych miejscach numOfTimesDisplayed? (moze podawac do funkcji
    }

    private void setGlobalValuesOfFirstAndLastLineOnYaxis() {
        firstLineYValue = getYaxisOfLine(0);
        lastLineYValue = getYaxisOfLine(numOfTimesDisplayed - 1);
    }

    private void setGlobalGapAndStartAndStopTimeDisplayed(int startTime, int stopTime) {
        setGapAndStartAndStopTimeDisplayed(startTime, stopTime, 15); // starts looking with 15 min gap
    }

    private void setGapAndStartAndStopTimeDisplayed(int startTime, int stopTime, int gap) { // setuje czas poczatkowy i koncowy
        startTimeDisplayed = startTime - startTime % gap;
        int stopTimeCounted = stopTime; // jak bedzie 8:07 to doda 8 i bedzie 8 :15
        if (stopTime % gap != 0) {
            stopTimeCounted += gap - stopTime % gap;
        }
        numOfTimesDisplayed = (stopTimeCounted - startTimeDisplayed) / gap + 1;
        gapBetweenTimesDisplayed = gap;

        if (numOfTimesDisplayed > 17) {                                                 /// mozna podawac ile ma sie wyswietlać
            setGapAndStartAndStopTimeDisplayed(startTime, stopTime, gap * 2);
        } else {
            endTimeDisplayed = startTimeDisplayed + 16 * gap;  // 17 -1
            numOfTimesDisplayed = 17;
            if (endTimeDisplayed > 1440) { // jak wiecej od 24
                endTimeDisplayed = 1440;
                Log.d("LOLnum", "lol");
                numOfTimesDisplayed = (endTimeDisplayed - startTimeDisplayed) / gap + 1;
                // podać liczbę godzin, które się wyświetlają (normalnie jest zawsze 17)
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

    private void drawText(String text) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);  //set text size
        float w = paint.measureText(text) / 2;
        float textSize = paint.getTextSize();
        canvas.drawText(text, 50, 100, paint);
        // wpisywanie textu w rectangle (patrzenie na x, y rectangla i na tej podstawie text)
        // upierdalać text jak wychodzi poza obszar
        // wpisywac na środku
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("sur", "CHANGED");
        // tu sie robi jak jest rotate
        // przedrawować defoult
        // czy trzeba znowu ustwaic width i height?
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("sur", "DESTROYED");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) { // zrobic on hold jak bedzie przytrzymywał
        int touchX = (int)event.getX();
        int touchY = (int)event.getY();
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:

                for (int i = 0; i < rectangleClasses.size(); i++ ){
                    if(rectangleClasses.get(i).contains(touchX,touchY)){
                        Toast.makeText(TimetableCanvas.getInstance(), "rectangle", Toast.LENGTH_SHORT).show();

                        // pokazac okinko z danymi lekcji

//                        Intent intent = new Intent(this, AddNewClass.class);
//                        Log.d("llol", classesData.toString());
//                        intent.putExtra("classData", classesData.get(i));
//                        startActivity(intent);
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
//                Toast.makeText(TimetableCanvas.getInstance(), "up", Toast.LENGTH_SHORT).show();
                break;
            case MotionEvent.ACTION_MOVE:
//                Toast.makeText(TimetableCanvas.getInstance(), "sliding", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}
