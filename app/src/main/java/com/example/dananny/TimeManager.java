package com.example.dananny;

import java.util.ArrayList;

public class TimeManager {

    private int Year;
    private int Month;
    private int Day;

    private int Hour;
    private int Minutes;
    private int Seconds;

    TimeManager(){
        Year = 2000;
        Month = 1;
        Day = 1;

        Hour = 12;
        Minutes = 0;
        Seconds = 0;
    }

    TimeManager(int yyyy, int mm, int dd, int hh, int min, int sec){
        Year = yyyy;
        Month = mm;
        Day = dd;

        Hour = hh;
        Minutes = min;
        Seconds = sec;
    }

    TimeManager(String LongTimeFormat){
        //Date received as: Year-Month-Day Hour:Minutes:Seconds
        //YYYY-MM-DD HH:MM:SS
        //0123-56-89 12:45:78

        if(LongTimeFormat.length()==19){
            Year = Integer.parseInt(LongTimeFormat.substring(0,4));
            Month = Integer.parseInt(LongTimeFormat.substring(5,7));
            Day = Integer.parseInt(LongTimeFormat.substring(8,10));
            Hour = Integer.parseInt(LongTimeFormat.substring(11,13));
            Minutes = Integer.parseInt(LongTimeFormat.substring(14,16));
            Seconds = Integer.parseInt(LongTimeFormat.substring(17,19));
        }
        else {
            Year = 2000;
            Month = 1;
            Day = 1;

            Hour = 12;
            Minutes = 0;
            Seconds = 0;
        }
    }

    public void setYear(int yyyy){
        Year = yyyy;
    }
    public void setMonth(int mm){
        Month = mm;
    }
    public void setDay(int dd){
        Day = dd;
    }
    public void setHour(int hh){
        Hour=hh;
    }
    public void setMinutes(int mm){
        Minutes = mm;
    }
    public void setSeconds(int ss){
        Seconds = ss;
    }

    public int getYear(){
        return Year;
    }
    public int getMonth(){
        return Month;
    }
    public int getDay(){
        return Day;
    }
    public int getHour(){
        return Hour;
    }
    public int getMinutes(){
        return Minutes;
    }
    public int getSeconds(){
        return Seconds;
    }

    public String getYearString(){
        return String.valueOf(Year);
    }
    public String getMonthString(){
        return (Month<10)? "0" + String.valueOf(Month):String.valueOf(Month);
    }
    public String getDayString(){
        return (Day<10)? "0" + String.valueOf(Day):String.valueOf(Day);
    }
    public String getHourString(){
        if(Hour==0)
            return "00";
        else if (Hour<10)
            return "0" + String.valueOf(Hour);
        else
            return String.valueOf(Hour);
    }
    public String getMinutesString(){
        if(Minutes==0)
            return "00";
        else if (Minutes<10)
            return "0" + String.valueOf(Minutes);
        else
            return String.valueOf(Minutes);
    }
    public String getSecondsString(){
        if(Seconds==0)
            return "00";
        else if (Seconds<10)
            return "0" + String.valueOf(Seconds);
        else
            return String.valueOf(Seconds);
    }

    public String getDate(){
        return getYearString() + "-" +
                getMonthString() + "-" +
                getDayString();
    }

    public String getTime(){
        return getHourString() + ":" +
                getMinutesString() + ":" +
                getSecondsString();
    }

    public String getFullTimestamp(){
        return getDate() + " " + getTime();
    }

    public static ArrayList<DCMicrogridMeasurements> OrderValuesByTime(ArrayList<DCMicrogridMeasurements> inputValues){
        ArrayList<DCMicrogridMeasurements> outputValues = new ArrayList<>();

        TimeManager soonest = new TimeManager();
        DCMicrogridMeasurements measure2 = new DCMicrogridMeasurements();

        for(int i=0; i<inputValues.size();i++){
            DCMicrogridMeasurements measure1 = inputValues.get(i);
            for(int j=0; j<inputValues.size();j++){
                measure2 = inputValues.get(j);
                soonest = GetSoonestTime(measure1.getDate(),measure2.getDate());
            }
            if(soonest.getFullTimestamp()==measure1.getDate().getFullTimestamp()){
                outputValues.add(measure1);
                inputValues.remove(measure1);
            }
            else {
                outputValues.add(measure2);
                inputValues.remove(measure2);
            }
            i=-1;
        }

        return outputValues;
    }

    public static TimeManager GetSoonestTime(TimeManager time1, TimeManager time2){

        if(time1.getYear()<time2.getYear())
            return time1;
        else if(time1.getMonth()<time2.getMonth())
            return time1;
        else if(time1.getDay()<time2.getDay())
            return time1;
        else if(time1.getHour()<time2.getHour())
            return time1;
        else if(time1.getMinutes()<time2.getMinutes())
            return time1;
        else if(time1.getSeconds()<time2.getSeconds())
            return time1;
        else
            return time2;

    }
}