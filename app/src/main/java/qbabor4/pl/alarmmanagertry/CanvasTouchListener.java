package qbabor4.pl.alarmmanagertry;

import android.graphics.Rect;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import java.sql.Time;
import java.util.List;

/**
 * zmiana canvasa przy rotate?
 *
 * Created by Jakub on 09-Dec-17.
 */

public class CanvasTouchListener implements View.OnTouchListener {

    List<Rect> rectangles;

    public CanvasTouchListener(List<Rect> rectangles){
        this.rectangles = rectangles;
    }


    public void setRectangles(List<Rect> rectangles){
        this.rectangles = rectangles;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int touchX = (int)event.getX();
        int touchY = (int)event.getY();

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                System.out.println("Touching down!");
                Log.d("LOL", "touch down");
                Toast.makeText(TimetableCanvas.getInstance(), "down", Toast.LENGTH_SHORT).show();
                for(Rect rect : rectangles){
                    if(rect.contains(touchX,touchY)){
                        System.out.println("Touched Rectangle, start activity.");
                        Toast.makeText(TimetableCanvas.getInstance(), "rectangle", Toast.LENGTH_SHORT).show();
//                        Intent i = new Intent(<your activity info>);
//                        startActivity(i);
                        // przekazac w extra dane z klasy? (moze przekazac tylko id klasy?
                        // moze tu pobrac?
                        // przekawyac mapę rectangli z id?
                        // przekazac id
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Toast.makeText(TimetableCanvas.getInstance(), "up", Toast.LENGTH_SHORT).show();
                break;
            case MotionEvent.ACTION_MOVE:
                Toast.makeText(TimetableCanvas.getInstance(), "sliding", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}
