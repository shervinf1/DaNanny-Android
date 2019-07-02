package com.example.dananny;

public class Device {
    private String name;
    private int gpio;
    private String room;
    private String status;

    Device(){
        name = "";
        gpio = 0;
        room = "";
        status = "";
    }

    Device(String n, int pin, String r, String s){
        name = n;
        gpio = pin;
        room = r;
        status = s;
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
}
