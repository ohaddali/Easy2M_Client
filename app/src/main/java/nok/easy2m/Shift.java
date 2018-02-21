package nok.easy2m;

import android.support.annotation.NonNull;

import java.sql.Time;
import java.util.Date;

/**
 * Created by naordalal on 21/02/2018.
 */

public class Shift implements Comparable<Shift>
{
    private String workerName;
    private String role;
    private Time startTime;
    private Time endTime;
    private int dayInTheWeek;

    public Shift(String workerName , String role , Time start , Time end , int day)
    {
        this.workerName = workerName;
        this.role = role;
        this.startTime = start;
        this.endTime = end;
        this.dayInTheWeek = day;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String name) {
        this.workerName = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public int getDayInTheWeek() {
        return dayInTheWeek;
    }

    public void setDayInTheWeek(int dayInTheWeek) {
        this.dayInTheWeek = dayInTheWeek;
    }


    @Override
    public int compareTo(@NonNull Shift shift)
    {
        if(new Integer(dayInTheWeek).compareTo(new Integer(shift.getDayInTheWeek())) != 0)
            return new Integer(dayInTheWeek).compareTo(new Integer(shift.getDayInTheWeek()));

        if(role.compareTo(shift.getRole()) != 0)
            return role.compareTo(shift.getRole());

        return startTime.compareTo(shift.getStartTime());

    }
}
