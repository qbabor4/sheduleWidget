package qbabor4.pl.alarmmanagertry;

import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Jakub on 09-Dec-17.
 */

public class CanvasTouchListener implements View.OnTouchListener {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Toast.makeText(TimetableCanvas.getInstance(), "add free days", Toast.LENGTH_SHORT).show();
        return true;
    }
}
