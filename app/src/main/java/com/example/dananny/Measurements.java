package com.example.dananny;

import android.util.Log;

import static android.support.constraint.Constraints.TAG;

public class Measurements {

    private TimeManager date;
    private String CURRMWT;
    private String CURRBATT;
    private String VBATT;
    private String VAVG;
    private String WAVG;
    private String DCLOAD;
    private float DCPOWER;

    Measurements(){
        date = new TimeManager();
        CURRMWT = "";
        CURRBATT = "";
        VBATT = "";
        VAVG = "";
        WAVG = "";
        DCLOAD = "";
        DCPOWER = 0;
    }

    Measurements(String d, String mwt, String iBattery, String vBattery, String vAvg, String wAvg, String dcload, float pow){

        Log.d(TAG, "MWT: " + mwt);
        Log.d(TAG, "Curr Batt: " + iBattery);
        Log.d(TAG, "Volt Batt: " + vBattery);
        Log.d(TAG, "Volt Avg: " + vAvg);
        Log.d(TAG, "Watt: " + wAvg);
        Log.d(TAG, "DC Load: " + dcload);
        Log.d(TAG, "Power: " + pow);

        date = new TimeManager(d);
        CURRMWT = mwt;
        CURRBATT = iBattery;
        VBATT = vBattery;
        VAVG = vAvg;
        WAVG = wAvg;
        DCLOAD = dcload;
        DCPOWER = pow;
    }
    public void setDate(String d){
        Log.d(TAG, "date: " + d);
        date = new TimeManager(d);
    }
    public void setCURRMWT(String mwt){
        Log.d(TAG, "MWT: " + mwt);
        CURRMWT =mwt;
    }
    public void setCURRBATT(String iBattery){
        Log.d(TAG, "Curr Batt: " + iBattery);
        CURRBATT = iBattery;
    }
    public void setVBATT(String vBattery){
        Log.d(TAG, "Volt Batt: " + vBattery);
        VBATT = vBattery;
    }
    public void setVAVG(String vAvg){
        Log.d(TAG, "Volt Avg: " + vAvg);
        VAVG =vAvg;
    }
    public void setWAVG(String wAvg){
        Log.d(TAG, "Watt: " + wAvg);
        WAVG =wAvg;
    }
    public void setDCLOAD(String dcload){
        Log.d(TAG, "DC Load: " + dcload);
        DCLOAD =dcload;
    }
    public void setDCPOWER(float p){
        Log.d(TAG, "Power: " + p);
        DCPOWER = p;
    }

    public TimeManager getDate(){
        return date;
    }
    public float getCURRMWT(){
        return Float.parseFloat(CURRMWT);
    }
    public float getCURRBATT(){
        return Float.parseFloat(CURRBATT);
    }
    public float getVBATT(){
        return Float.parseFloat(VBATT);
    }
    public float getVAVG(){
        return Float.parseFloat(VAVG);
    }
    public float getWAVG(){
        return Float.parseFloat(WAVG);
    }
    public float getDCLOAD(){
        return Float.parseFloat(DCLOAD);
    }
    public float getDCPOWER(){
        return DCPOWER;
    }
}
