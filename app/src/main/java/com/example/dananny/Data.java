package com.example.dananny;

public class Data {
    private int index;
    private int volts;

    Data(){
        index = 0;
        volts = 0;
    }
    Data(int i, int v){
        index = i;
        volts = v;
    }

    void setIndex(int i){
        index = i;
    }
    void setVolts(int v){
        volts=v;
    }
    int getIndex(){
        return index;
    }
    int getVolts(){
        return volts;
    }
}
