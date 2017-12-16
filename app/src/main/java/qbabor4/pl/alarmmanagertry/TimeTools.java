package qbabor4.pl.alarmmanagertry;

/**
 * Created by Jakub on 17-Dec-17.
 */

public class TimeTools {

    public static int getTimeInMinutesFromTimePicker(String time) {
        int colonIndex = time.indexOf(':');
        int hour = Integer.parseInt(time.substring(0, colonIndex));
        int minutes = Integer.parseInt(time.substring(colonIndex+1));
        return hour*60 + minutes;
    }

    public static String getTimePickerFormatTime(String time){
        int timeInt = Integer.parseInt(time);
        int hour = timeInt/60;
        String minutes = String.valueOf(timeInt%60);
        if (minutes.length() == 1){
            minutes = "0" + minutes;
        }
        return hour + ":" + minutes;
    }
}
