package qbabor4.pl.schoolschedule;

/**
 * Created by Jakub on 13-Dec-17.
 */

public enum SqlDataEnum {
    ID("Identyfikator"),
    START_TIME("Czas rozpoczęcia"),
    END_TIME("Czas zakończenia"),
    DAY_OF_WEEK("Dzień Tygodnia"),
    SUBJECT("Zajęcia"),
    CLASSROOM("Sala"),
    TEACHER("Nauczyciel"),
    DESCRIPTION("Opis"),
    COLOR("Kolor");
    //    FREQUENCY;

    String descriptionPL;

    SqlDataEnum(String descriptionPL){
        this.descriptionPL = descriptionPL;
    }

    public String getDescriptionPL(){
        return descriptionPL;
    }


}
