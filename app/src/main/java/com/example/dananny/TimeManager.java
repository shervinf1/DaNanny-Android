package com.example.dananny;

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
}