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
import java.util.List;


/**
 * TODO
 * clickable rectangle
 * dostac wymiery layoutu
 * zrobić siatkę z dniami
 * wybieranie koloru w dodawaniu zajęć albo same sie beda zmieniac
 * klawisze prawo i lewo zeby zobaczy kolojne tygodnie
 * pokazywanie siatki godzinowej na podstawie minialnej godziny i maksymalnej w tygodniu
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

    private static final int TIME_SECTION_SIZE = 150;
    private static final int DAYS_SECTION_SIZE = 80;
    private static final int NO_LINE_SIZE = 10;
    private static final int RECTAGLE_HORISONTAL_PADDING = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_canvas);
        setInstance();
        setToolbar();
        setSurfaceView();

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
//        Log.d("in", String.valueOf(canvasSurfaceViewHeight));

        // zrobić siatkę z godzinami i dniami na podstawie szerokości canvasa

        // robić na width i height bez

        // ustawiac rectangle procentowo w zależnosci od długości zajęć
//        makeRectange(300, 200, 500, 400, "#f48342");
//        drawText("lol");


        Toast.makeText(TimetableCanvas.getInstance(), rectangleClasses.toString(), Toast.LENGTH_SHORT).show();

        canvasTouchListener.setRectangles(rectangleClasses);
        holder.unlockCanvasAndPost(canvas);
    }

    private int startTimeDisplayed;
    private int stopTimeDisplayed;
    private int numOfTimesDisplayed;
    private int gapBetweenTimesDisplayed;
    private int firstLineYValue;
    private int lastLineYValue;

    private void drawDefault() {

        // dostac pierszą godzinę i ostatnią wyciagajac z bazy i patrząc na min i max ze wszystkich
        // dosta tak samo dni
        // jak nic nie ma w bazie, to wyswietlic od 8 do 16 plan
        // co któraś linia w innym kolorze? moze kazda inna co 3 w innym kolorze (odcienie szarego)

        setGlobalGapAndStartAndStopTimeDisplayed(540, 960); // 9 do 16
        setGlobalValuesOfFirstAndLastLineOnYaxis();

//        Log.d("num1", "" + ""+ this.startTimeDisplayed);
//        Log.d("num2", "" + ""+ this.stopTimeDisplayed);
//        Log.d("num3", "" + ""+ this.numOfTimesDisplayed);
//        Log.d("num4", "" + ""+ this.gapBetweenTimesDisplayed);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#aaaaaa"));
        canvas.drawLine(NO_LINE_SIZE, DAYS_SECTION_SIZE, canvasSurfaceViewWidth - NO_LINE_SIZE, DAYS_SECTION_SIZE, paint); // horisontal line under days of week

        // srodkowanie w pionie nie nie podstawie bounds tylko bez ogonków i kresek robić jakoś


        String[] daysOfWeek = {"Pon", "Wt", "Śr", "Czw", "Pt", "Sob", "Ndz"}; // pobierane na podstawie bazy i języka (pol/ang) (brać kawałek jak sie dowiem z bazy

        int rectangleWidth = scheduleWidth / daysOfWeek.length; // bo 5 dni tygodnia (jak bedie wiecej, to zmienic na tyle ile jest dni

        for (int i = 0; i < daysOfWeek.length; i++) {
            String day = daysOfWeek[i];
            Paint paintText = new Paint();
            paintText.setColor(Color.BLACK);
            paintText.setTextSize(50);
            Rect bounds = new Rect();
            paintText.getTextBounds(day, 0, day.length(), bounds);
            canvas.drawText(day, TIME_SECTION_SIZE + (i * 2 + 1) * ((scheduleWidth / (daysOfWeek.length * 2))) - bounds.width() / 2, DAYS_SECTION_SIZE / 2 + bounds.height() / 2, paintText); //  /10 bo jest 5 dni /5 /2
            // 10
            Log.d("lol", String.valueOf(TIME_SECTION_SIZE + (scheduleWidth / (daysOfWeek.length * 2)) - bounds.width() / 2));
        }

//        int timeDifference = stopTimeDisplayed - startTimeDisplayed;

        for (int i = 0; i < numOfTimesDisplayed  ; i++) { // 17 czasów (moze dac do zmiennej ile wyświetlać?



            Paint paintOfTime = getPaintOfTimeToDisplay();
            String timeToDraw = getTimeToDisplay(i);
            Rect bounds = new Rect();
            paintOfTime.getTextBounds(timeToDraw, 0, timeToDraw.length(), bounds);

            // inaczej dostac te texty, zeby ostawiało w srodku bez ogonków

            canvas.drawLine(TIME_SECTION_SIZE,  getYaxisOfLine(i), canvasSurfaceViewWidth,  getYaxisOfLine(i), paintOfTime );
            canvas.drawText(timeToDraw, getXaxisOfTimeDisplayed(bounds), getYaxisOfTimeDisplayed(i, bounds), paintOfTime );
        }

        // narysowac prostokąt podając godziny i numer dnia (dzien)
//        int i = 0;  // 8:00
//        int y1 = DAYS_SECTION_SIZE + (i * 2 + 1) * ((scheduleHeight / ((numOfTimesDisplayed + 1) * 2))); // bez 1 ?
//        int j = 16; // 16:00
//        int y2 = DAYS_SECTION_SIZE + (j * 2 + 1) * ((scheduleHeight / ((numOfTimesDisplayed + 1) * 2)));
//        int k = 1;
//        int x1 = TIME_SECTION_SIZE + (scheduleWidth / (daysOfWeek.length)) * k + RECTAGLE_HORISONTAL_PADDING;
//        int l = 2;
//        int x2 = TIME_SECTION_SIZE + (scheduleWidth / (daysOfWeek.length)) * l - RECTAGLE_HORISONTAL_PADDING;
//        makeRectange(x1, y1, x2, y2, "#f48342");
    }

    private Paint getPaintOfTimeToDisplay(){
        Paint paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(50);
        return paintText;
    }

    private String getTimeToDisplay(int index){
        int minutesTotal = startTimeDisplayed + gapBetweenTimesDisplayed * index;
        int hours = minutesTotal / 60;
        String minutes = String.valueOf(minutesTotal % 60);
        if (minutes.length() == 1){
            minutes = '0' + minutes;
        }
        return hours + ":" + minutes;
    }

    private int getXaxisOfTimeDisplayed(Rect bounds){
        return TIME_SECTION_SIZE / 2 - bounds.width() / 2;
    }

    private int getYaxisOfTimeDisplayed(int index, Rect bounds){
        return getYaxisOfLine(index) + bounds.height() / 2;
    }

    private int getYaxisOfLine(int indexOfLinOnScreen){
       return DAYS_SECTION_SIZE + (indexOfLinOnScreen * 2 + 1) * ((scheduleHeight / ((numOfTimesDisplayed ) * 2))); // czy ja bede potrzebowal w innych miejscach numOfTimesDisplayed? (moze podawac do funkcji
    }

    private void setGlobalValuesOfFirstAndLastLineOnYaxis() {
        firstLineYValue = getYaxisOfLine(0);
        lastLineYValue = getYaxisOfLine(numOfTimesDisplayed -1);
    }


    private void setGlobalGapAndStartAndStopTimeDisplayed(int startTime, int stopTime){
        setGapAndStartAndStopTimeDisplayed(startTime, stopTime, 15); // starts looking with 15 min gap
    }


    private void setGapAndStartAndStopTimeDisplayed(int startTime, int stopTime, int gap){ // setuje czas poczatkowy i koncowy
        startTimeDisplayed = startTime - startTime % gap;
        int stopTimeCounted = stopTime ; // jak bedzie 8:07 to doda 8 i bedzie 8 :15
        if (stopTime%gap != 0) {
            stopTimeCounted += gap - stopTime % gap;
        }
        numOfTimesDisplayed = (stopTimeCounted - startTimeDisplayed)/gap +1;
        gapBetweenTimesDisplayed = gap;

        if (numOfTimesDisplayed > 17){                                                 /// mozna podawac ile ma sie wyswietlać
            setGapAndStartAndStopTimeDisplayed(startTime, stopTime, gap *2);
        } else {
            stopTimeDisplayed = startTimeDisplayed + 16 * gap;  // 17 -1
            numOfTimesDisplayed = 17;
            if (stopTimeDisplayed > 1440){ // jak wiecej od 24
                stopTimeDisplayed = 1440;
                Log.d("LOLnum", "lol");
                numOfTimesDisplayed = (stopTimeDisplayed - startTimeDisplayed) / gap +1;
                // podać liczbę godzin, które się wyświetlają (normalnie jest zawsze 17)
            }
        }

    }


    private void addRectangleClass(int startTime, int stopTime, int dayOfWeek) { // jeszcze jakiś text w środku i kolor rectangla i textu
        // zakładam, że zawsze bedzie 17 czasów pokazanych
        // zakładam, że podają co min 30 min (potem sie zmieni)
        // dstac y pierwszej lini i ostatniej a potem dzielic biorąc czas pomiędzy wszystkimi a czas trwania lekcji
        int numOfTimesDisplayed = 16;
        int i = 0;  // 8:00
        int y1 = DAYS_SECTION_SIZE + (i * 2 + 1) * ((scheduleHeight / ((numOfTimesDisplayed + 1) * 2)));
        int j = numOfTimesDisplayed; // 16:00
        int y2 = DAYS_SECTION_SIZE + (j * 2 + 1) * ((scheduleHeight / ((numOfTimesDisplayed + 1) * 2)));

        int spaceBetweenStartAndEnd = y2 - y1; // ogólna wysokosc od 8 do 16
        // dodawac y1 bo od tego ma iść
        // TODO
    }

    private void setCanvas(SurfaceHolder holder) {
        canvas = holder.lockCanvas();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);

    }

    private void makeRectange(int left, int top, int right, int bottom, String hexColor) {
        Rect rect = new Rect(left, top, right, bottom);
        rectangleClasses.add(rect);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor(hexColor));
        canvas.drawRect(rect, paint);
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

    // funkcja do robienia rectangla z textem

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
