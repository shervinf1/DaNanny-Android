package com.example.dananny;

public class Measurements {

    private String date;
    private String volts;
    private String current;
    private String power;

    Measurements(){
        date="";
        volts="";
        current="";
        power="";
    }

    Measurements(String d, String v, String c, String p){
        date = d;
        volts = v;
        current = c;
        power = p;
    }

    public void setDate(String d){
        date=d;
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
    public String getDate(){
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
