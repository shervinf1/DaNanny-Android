package com.example.dananny;

public class Measurements {

    private TimeManager date;
    private String volts;
    private String current;
    private String power;

    Measurements(){
        date= new TimeManager();
        volts="";
        current="";
        power="";
    }

    Measurements(String d, String v, String c, String p){
        date = new TimeManager(d);
        volts = v;
        current = c;
        power = p;
    }

    public void setDate(String d){
        date = new TimeManager(d);
    }
    public void setVolts(String v){
        volts=v;
    }
    public void setCurrent(String c){
        current=c;
    }
    public void setPower(String p){
        power=p;
    }
    public TimeManager getDate(){
        return date;
    }
    public String getVolts(){
        return volts;
    }
    public String getCurrent(){
        return current;
    }
    public String getPower(){
        return power;
    }
}
