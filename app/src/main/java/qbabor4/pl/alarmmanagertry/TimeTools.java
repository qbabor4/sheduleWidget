package qbabor4.pl.alarmmanagertry;

import java.util.Calendar;

/**
 * Created by Jakub on 17-Dec-17.
 */

public class TimeTools {

    public static final String[] DAYS_OF_WEEK_PL = {"Pon", "Wt", "Śr", "Czw", "Pt", "Sob", "Ndz"};
    public static final String[] DAYS_OF_WEEK_PL_FULL = {"Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"};

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

    private static int getMinutes(int hours, int minutes) {
        return minutes + hours * 60;
    }

    public static int getCurrentTimeInMinutes() {
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int minutes = rightNow.get(Calendar.MINUTE);
        return getMinutes(hour, minutes);
    }

    /**
     * Get current day starting from 0 as Monday and ending with 6 as sunday
     *
     * @return
     */
    public static int getDayInWeek() {
        Calendar rightNow = Calendar.getInstance();
        int dayInWeek = rightNow.get(Calendar.DAY_OF_WEEK);
        int dayOfWeekFromMonday = dayInWeek - 1;
        if (dayOfWeekFromMonday == 0) { // if sunday case
            dayOfWeekFromMonday = 7;
        }
        return dayOfWeekFromMonday - 1; // 0 as monday, not sunday
    }
}
