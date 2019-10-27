#include <Wire.h>
#include <SPI.h>
#include<SoftwareSerial.h>

SoftwareSerial XBee(2, 3);

const int xbeePin = 9;
int sensorIn = 0;         //paremeter to be passed to function that will read Analog Values
const int anemometer = A0;//anemomenter Sensor
const int MWT = A12;      //Micro Wind Turbine Current Sensor
const int Batt = A10;     //Battery Current Sensor
const int DCLoad =A9;    //DC Load Current Sensor




int mVperAmp = 100; //ACS712 Current sensor predertermined value-> use 185 for 5A Module,use 100 for 20A Module and 66 for 30A Module
float Voltage = 0;
double VRMS = 0;
double AmpsRMS = 0;
float VoltageRef = 0;
float voltageBAT = 0.0;
float battVoltage = 0.0;
float MWTCurrent = 0;
float BattCurrent = 0;
float DCLoadCurrent =0;


//Reference Values in order to Reset the Current Sensors to zero
float VRefMWT = 2.518;
float VRefBatt = 2.496;
float VRefDC = 2.562;


float getVPP(int sensorIn) {
  float result;
  float readValue = 0;           //value read from the sensor

  uint32_t start_time = millis();
  int i = 0;
  //sample for 1 Sec

  for (i = 0; i <= 100; i++) {
    readValue += ((float)analogRead(sensorIn)) / 100;
  }
  // Subtract min from max
  result = ((readValue) * 5.0) / 1024.0;

  return result;
}

void setup(void)
{
  Serial.begin(9600);
  while (!Serial) {
    // will pause Zero, Leonardo, etc until serial console opens
    delay(1);
  }
  pinMode(xbeePin, OUTPUT);
  pinMode(MWT, INPUT);
  pinMode(Batt, INPUT);
  pinMode(DCLoad,INPUT);

  uint32_t currentFrequency;

//calibration values
  Voltage = getVPP(MWT);
  VRefMWT = Voltage;

  Voltage = getVPP(Batt);
  VRefBatt = Voltage;

  Voltage = getVPP(DCLoad);
  VRefDC = Voltage;

}



void loop(void) {
  //wake xbee
  digitalWrite(xbeePin, LOW);
  String record;
  //Get measurements
  record = buildRecord();
  //Write to serial
  Serial.println(record);
  //Wait until serial write finished
  delay(100);
  sendXbee();
}

void sendXbee() {
  if (Serial.available())
  {
    XBee.write(Serial.read());
  }
  if (XBee.available())
  {
    Serial.write(XBee.read());
  }
}

void takeMeasurements() {
  String str;
  Serial.println(str);
  Serial.flush();
  delay(1000);
}

void Measurements() {
  const double Measurements = 5.0;
  double voltages[(int)Measurements];
  double total = 0;

  for (int i = 0; i < Measurements; i++) {
    delay(500);
    voltages[i] = getVoltage();
  }
  String txt;
  for (int i = 0; i < Measurements; i++) {
    total += voltages[i];
    txt += String(voltages[i]) + ",";
  }

  txt += String(total / Measurements) + "," + calculateWind(total / Measurements);
}
double getVoltage() {
  const float voltageConversionConstant = .004882814;

  int sensorValue = analogRead(anemometer);
  float sensorVoltage = sensorValue * voltageConversionConstant;

  return sensorVoltage;
}

String calculateWind(double Vavg) {
  //Anemometer Technical Variables
  const float voltageMin = .4;    // Mininum output voltage from anemometer in mV.
  const float voltageMax = 2.0;   // Maximum output voltage from anemometer in mV.
  const float windSpeedMin = 0;   // Wind speed in meters/sec corresponding to minimum voltage
  const float windSpeedMax = 32;  // Wind speed in meters/sec corresponding to maximum voltage

  float windSpeed = 0; // Wind speed in meters per second (m/s)

  if (Vavg <= voltageMin) {
    windSpeed = 0;
  }
  else {
    windSpeed = (Vavg - voltageMin) * windSpeedMax / (voltageMax - voltageMin);
  }

  return String(windSpeed);
}

String buildRecord() {
  String str;
  double total = 0;
  double avg = 0;
  const int numMeasurements = 10;

  //Get Micro Wind Turbine current measurement
  Voltage = getVPP(MWT);
  MWTCurrent = getMeasurements(VRefMWT, Voltage);
  str += String(MWTCurrent) + ",";

  //Get Battery current measurement
  Voltage = getVPP(Batt);
  BattCurrent = getMeasurements(VRefBatt, Voltage);
  str += String(BattCurrent) + ",";

//Get Battery Voltage measurement
  battVoltage = VoltageBAT();
  str += String(battVoltage) + ",";
  
  //Get 10 measurements a 1/3 of a second apart, add to total and build string
  for (int i = 0; i < numMeasurements; i++) {
    double measurement = getVoltage();
    total += measurement;
    //str += String(measurement);
    //str += ",";
    delay(300);
  }
  //Calculate average
  avg = total / numMeasurements;
  //Add average to string
  str += String(avg);
  str += ',';
  //Add wind speed of average
  str += String(calculateWind(avg));
 
  Voltage = getVPP(DCLoad);
  DCLoadCurrent = getMeasurements(VRefDC, Voltage);
  str += String(DCLoadCurrent);

  return str;
}
//GET THE CALIBRATION VALUES
float getMeasurements(float VoltageRef, float Voltage) {
 //Serial.println(Voltage,3); //calibration values
  VRMS = (Voltage - VoltageRef) * 1.07;
  AmpsRMS = (VRMS * 1000) / mVperAmp;
  return AmpsRMS / 2;
}

//VOLTAGE SENSOR CODE
float VoltageBAT() {
  int analogInput = A1;
  float vout = 0.0;
  float R1 = 30000.0;
  float R2 = 7500.0;
  int value = 0;
  // read the value at analog input
  value = analogRead(analogInput);
  vout = (value * 5.0) / 1023.0; // see text
  float voltageBAT = vout / (R2 / (R1 + R2));
  return voltageBAT;
}
