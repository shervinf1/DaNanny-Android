package com.example.dananny;

public class Device {
    private String name;
    private int gpio;
    private String room;
    private String status;
    private float threshold;
    private float consumption;

    Device(){
        name = "";
        gpio = 0;
        room = "";
        status = "";
        threshold = 0;
        consumption = 0;
    }

    Device(String n, int pin, String r, String s, float t, float c){
        name = n;
        gpio = pin;
        room = r;
        status = s;
        threshold = t;
        consumption = c;
    }

    public void setName(String n){
        name = n;
    }
    public void setGpio(int pin){
        gpio = pin;
    }
    public void setRoom(String r){ room = r;}
    public void setStatus(String s){
        status = s;
    }
    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }
    public void setConsumption(float consumption) {
        this.consumption = consumption;
    }

    public String getName(){
        return name;
    }
    public int getGpio(){
        return gpio;
    }
    public String getRoom(){ return room; }
    public String getStatus(){
        return status;
    }
    public float getConsumption() {
        return consumption;
    }
    public float getThreshold() {
        return threshold;
    }
}
