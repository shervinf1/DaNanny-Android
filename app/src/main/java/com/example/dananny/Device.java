package com.example.dananny;

public class Device {
    private String name;
    private int gpio;
    private String status;

    Device(){
        name = "";
        gpio = 0;
        status = "";
    }

    Device(String n, int pin, String s){
        name = n;
        gpio = pin;
        status = s;
    }

    public void setName(String n){
        name = n;
    }
    public void setGpio(int pin){
        gpio = pin;
    }
    public void setStatus(String s){
        status = s;
    }
    public String getName(){
        return name;
    }
    public int getGpio(){
        return gpio;
    }
    public String getStatus(){
        return status;
    }
}
