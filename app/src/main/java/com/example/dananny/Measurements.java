package com.example.dananny;

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

        date = new TimeManager(d);
    }
    public void setCURRMWT(String mwt){
        CURRMWT =mwt;
    }
    public void setCURRBATT(String iBattery){
        CURRBATT = iBattery;
    }
    public void setVBATT(String vBattery){
        VBATT = vBattery;
    }
    public void setVAVG(String vAvg){
        VAVG =vAvg;
    }
    public void setWAVG(String wAvg){
        WAVG =wAvg;
    }
    public void setDCLOAD(String dcload){
        DCLOAD =dcload;
    }
    public void setDCPOWER(float p){
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
