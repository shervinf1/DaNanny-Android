package com.example.dananny;

import java.util.Date;

public class Measurements {

    private float voltage;
    private float current;
    private float watts;
    private Date date;

    Measurements(){
        voltage = 0;
        current = 0;
        watts = 0;
        date = new Date();
    }

    Measurements(float v, float c, float w, Date d){
        voltage = v;
        current = c;
        watts = w;
        date = d;
    }

    public void setCurrent(float current) {
        this.current = current;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    public void setWatts(float watts) {
        this.watts = watts;
    }

    public Date getDate() {
        return date;
    }

    public float getCurrent() {
        return current;
    }

    public float getVoltage() {
        return voltage;
    }

    public float getWatts() {
        return watts;
    }
}

