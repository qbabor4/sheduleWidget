package qbabor4.pl.alarmmanagertry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.view.MotionEvent.ACTION_DOWN;

/**
 * clickable rectangle
 * dostac wymiery layoutu
 * zrobić siatkę z dniami
 *
 * Created by Jakub on 09-Dec-17.
 */

public class TimetableCanvas extends Activity {

    List<Rect> rectangleClasses;
    private static TimetableCanvas ins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_canvas);
        setInstance();
        SurfaceView surface = (SurfaceView) findViewById(R.id.surface);
        surface.setOnTouchListener(new CanvasTouchListener());
        surface.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Canvas canvas = holder.lockCanvas();
                Paint paint = new Paint();

                // dostac wymiary layouta
                rectangleClasses = new ArrayList<>();

//                int x = getWidth();
//                int y = getHeight();
                int radius;
                radius = 100;

                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.WHITE);
                canvas.drawPaint(paint);
                // Use Color.parseColor to define HTML colors
                paint.setColor(Color.parseColor("#CD5C5C"));
                // dodać napis na rectanglu (na środku

//                canvas.drawCircle(50, 50, radius, paint);

                Rect firstClass = new Rect(50, 50, 200, 300);
                rectangleClasses.add(firstClass);

                canvas.drawRect(firstClass, paint); // patrzec jaki duzy jest ekran i na tej podstawie robic rectangle


                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    private void setInstance(){
        ins = this;
    }

    public static TimetableCanvas getInstance(){
        return ins;
    }

//    @Override
    public boolean onTouch(View v, MotionEvent event) { // nie działa
        int touchX = (int)event.getX();
        int touchY = (int)event.getY();
        Toast.makeText(this, "add free days", Toast.LENGTH_SHORT).show();

        switch(event.getAction()){
            case MotionEvent.ACTION_BUTTON_PRESS:
                System.out.println("Touching down!");
                for(Rect rect : rectangleClasses){
                    if(rect.contains(touchX,touchY)){
                        System.out.println("Touched Rectangle, start activity.");
//                        Intent i = new Intent(<your activity info>);
//                        startActivity(i);
                        // przekazac w extra dane z klasy? (moze przekazac tylko id klasy?
                        // przekazac id
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("Touching up!");
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("Sliding your finger around on the screen.");
                break;
        }
        return true;
    }


//    public class MyView extends View {
//
//        public MyView(Context context) {
//            super(context);
//        }
//
//        @Override
//        protected void onDraw(Canvas canvas) {
////            super.onDraw(canvas);
//            int x = getWidth();
//            int y = getHeight();
//            int radius;
//            radius = 100;
//            Paint paint = new Paint();
//            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(Color.WHITE);
//            canvas.drawPaint(paint);
//            // Use Color.parseColor to define HTML colors
//            paint.setColor(Color.parseColor("#CD5C5C"));
//            canvas.drawCircle(x / 2, y / 2, radius, paint);
//        }
//    }

}
