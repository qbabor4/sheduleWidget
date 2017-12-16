package qbabor4.pl.alarmmanagertry;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import android.view.ViewTreeObserver;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * TODO
 * wybieranie koloru w dodawaniu zajęć albo same sie beda zmieniac
 * klawisze prawo i lewo zeby zobaczy kolojne tygodnie
 * <p>
 * TODO IFTIME:
 * zmiana nagólwka z dniami i czasami dynamicznie (robienie wedłód procentów
 * <p>
 * Created by Jakub on 09-Dec-17.
 */

public class TimetableCanvas extends AppCompatActivity implements SurfaceHolder.Callback {

    private List<Rect> rectangleClasses;
    private static TimetableCanvas ins;
    private CanvasTouchListener canvasTouchListener;
    private int canvasSurfaceViewWidth;
    private int canvasSurfaceViewHeight;
    private int scheduleWidth;
    private int scheduleHeight;
    private Canvas canvas;

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

    private SqlLiteHelper mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_canvas);
        setInstance();
        setToolbar();

        setDBInstance();
        // wczytac dane z bazy (wszystkie) TODO NEXT
        setSurfaceView();
    }

    private void setDBInstance() {
        mDB = new SqlLiteHelper(this); //this jako context
    }


    private void setSurfaceView() {
        final SurfaceView canvasSurfaceView = (SurfaceView) findViewById(R.id.surface);
        canvasSurfaceView.getHolder().addCallback(this);
        ViewTreeObserver observer = canvasSurfaceView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                canvasSurfaceViewHeight = canvasSurfaceView.getHeight();
                canvasSurfaceViewWidth = canvasSurfaceView.getWidth();
                setScheduleSize();
                Log.d("surlol", "lol");
                canvasSurfaceView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        canvasTouchListener = new CanvasTouchListener(rectangleClasses);
        canvasSurfaceView.setOnTouchListener(canvasTouchListener);
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
        // tu bedzie pobierał z bazy i na tej podstawie rysował rectangle
        rectangleClasses = new ArrayList<>();

        setCanvas(holder);

        drawDefault();

        drawRectangleClasses();

        Toast.makeText(TimetableCanvas.getInstance(), rectangleClasses.toString(), Toast.LENGTH_SHORT).show();

        canvasTouchListener.setRectangles(rectangleClasses);
        holder.unlockCanvasAndPost(canvas);
    }

    private void drawDefault() {

        // dostac pierszą godzinę i ostatnią wyciagajac z bazy i patrząc na min i max ze wszystkich
        // dosta tak samo dni
        // jak nic nie ma w bazie, to wyswietlic od 8 do 16 plan
        // co któraś linia w innym kolorze? moze kazda inna co 3 w innym kolorze (odcienie szarego)

        setGlobalGapAndStartAndStopTimeDisplayed(540, 960); // 9 do 16 // pierwsza i ostatnia godzina w planie
        setGlobalValuesOfFirstAndLastLineOnYaxis(); // to jakos inaczej, bez globalnych jak sie da

        drawLineUnderDays();

        daysOfWeek = getDays(0); // pobierane na podstawie bazy i języka (pol/ang) (brać kawałek jak sie dowiem z bazy
        setGlobalVariables();
        drawDaysNames(daysOfWeek); // daysOfWeek chyba beda globalnie, albo tylko długość arrraya
        drawTimesWithLines();
    }

    private String[] getDays(int lastDay) { //TODO
        return new String[]{"Pon", "Wt", "Śr", "Czw", "Pt"};
    }

    private void drawRectangleClasses() { // podawac liste z mapami
        // testowo
        // 10:00 - 13:00 we wtorek
        Map<SqlDataEnum, String> classData = new HashMap<>();
        classData.put(SqlDataEnum.DAY_OF_WEEK, "1");
        classData.put(SqlDataEnum.START_TIME, "600");
        classData.put(SqlDataEnum.END_TIME, "780");
        drawRectangle(classData);

        Map<SqlDataEnum, String> classData2 = new HashMap<>();
        classData2.put(SqlDataEnum.DAY_OF_WEEK, "4");
        classData2.put(SqlDataEnum.START_TIME, "900");
        classData2.put(SqlDataEnum.END_TIME, "960");
        drawRectangle(classData2);

        Map<SqlDataEnum, String> classData3 = new HashMap<>();
        classData3.put(SqlDataEnum.DAY_OF_WEEK, "3");
        classData3.put(SqlDataEnum.START_TIME, "900");
        classData3.put(SqlDataEnum.END_TIME, "960");
        drawRectangle(classData3);
    }

    private int getRowWidth() {
        return scheduleWidth / daysOfWeek.length;
    }

    private void setGlobalVariables(){ // dodac inne zmienne
        rowWidth = getRowWidth();
    }



    private void drawRectangle(Map<SqlDataEnum, String> classData) {
        int day = Integer.parseInt(classData.get(SqlDataEnum.DAY_OF_WEEK)); // numer dnia (1)
        int startTime = Integer.parseInt(classData.get(SqlDataEnum.START_TIME)); // numer dnia (1)
        int stopTime = Integer.parseInt(classData.get(SqlDataEnum.END_TIME)); // numer dnia (1)

        Log.d("start", "" + startTimeDisplayed );
        Log.d("start", "" + endTimeDisplayed );

        // globalnie czas pomiedzy poczatkiem i koncem
        float percentageRectangleStartY = (startTime - startTimeDisplayed) / (float)(endTimeDisplayed - startTimeDisplayed);
        float percentageRectangleEndY = (stopTime - startTimeDisplayed) / (float)(endTimeDisplayed - startTimeDisplayed);
        // odliegłosc czasów plus procentstart * wysokosc w pixelach
        int y1 = firstLineYValue + Math.round(percentageRectangleStartY * (lastLineYValue - firstLineYValue)); // nie ok firstLinevalue
        int y2 = firstLineYValue + Math.round(percentageRectangleEndY * (lastLineYValue - firstLineYValue)); // nie ok

        int x1 = TIME_SECTION_SIZE + rowWidth * day + RECTANGLE_HORIZONTAL_PADDING; //ok
        int x2 = TIME_SECTION_SIZE + rowWidth * (day+1) - RECTANGLE_HORIZONTAL_PADDING; // ok
        // wyznaczyć x1 i x2 gdzie sie ma pokazac
//        drawRectangle(50, 100, 500, 400, "#000345");
        drawRectangle(x1, y1, x2, y2, "#000345");
        Log.d("lol4", ""+ percentageRectangleEndY);
        Log.d("lol4", x1+ " " + x2 + " " + y1 + " " + y2);
    }

    private void getXofRectangleStart(){

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
}
