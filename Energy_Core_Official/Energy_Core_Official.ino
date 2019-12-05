#include <Wire.h>
#include <Adafruit_INA219.h>
#include "dht.h"
#define dht_apin A0

Adafruit_INA219 ina219_A;
Adafruit_INA219 ina219_B(0x41);
Adafruit_INA219 ina219_C(0x44);
dht DHT;

int voltageSensor = A1;

void setup(void)
{
  Serial.begin(115200);
  delay(500);
  pinMode(voltageSensor, INPUT);
  uint32_t currentFrequency;
  Serial.println("Initializing INA219 A");
  ina219_A.begin();
  Serial.println("Initializing INA219 B");
  ina219_B.begin();
  Serial.println("Initializing INA219 C");
  ina219_C.begin();
}

void loop(void)
{
  //INA219_A - Device 1 (Fan)
  Serial.println("Reading Sensor INA219 A");
  float shuntvoltage_A = ina219_A.getShuntVoltage_mV();
  float busvoltage_A = ina219_A.getBusVoltage_V();
  float current_mA_A = ina219_A.getCurrent_mA();
  float loadvoltage_A = ina219_A.getPower_mW();
  float power_mW_A = busvoltage_A + (shuntvoltage_A / 1000);

  //INA219_B - Device 2 (LED)
  Serial.println("Reading Sensor INA219 B");
  float shuntvoltage_B = ina219_B.getShuntVoltage_mV();
  float busvoltage_B = ina219_B.getBusVoltage_V();
  float current_mA_B = ina219_B.getCurrent_mA();
  float loadvoltage_B = ina219_B.getPower_mW();
  float power_mW_B = busvoltage_B + (shuntvoltage_B / 1000);

  //INA219_C - Device 3 (GENERATION)
  Serial.println("Reading Sensor INA219 C");
  float shuntvoltage_C = ina219_C.getShuntVoltage_mV();
  float busvoltage_C = ina219_C.getBusVoltage_V();
  float current_mA_C = ina219_C.getCurrent_mA();
  float loadvoltage_C = ina219_C.getPower_mW();
  float power_mW_C = busvoltage_C + (shuntvoltage_C / 1000);

  // Temperature Sensor
  Serial.println("Reading Temperature Sensor");
  DHT.read11(dht_apin);

  // Read voltage sensor
  Serial.println("Reading Battery Voltage Sensor");
  float batteryVoltage = getVoltage();

  // Write to Serial
  Serial.println(
    String(batteryVoltage) + ", " + String(DHT.temperature) + ", " +
    "c0ymhiknYSiY12pMECUf" + ", " + String(current_mA_A) + ", " + String(loadvoltage_A) + ", " + String(power_mW_A) + ", " +
    "B5OLqgfidaVf1vvmcsZC" + ", " + String(current_mA_B) + ", " + String(loadvoltage_B) + ", " + String(power_mW_B) + ", " +
    "UEnGPmDLN1vzO1Go93SK" + ", " + String(current_mA_C) + ", " + String(loadvoltage_C) + ", " + String(power_mW_C)
  );

  // Wait 7 seconds
  delay(7000);
}

float getVoltage() {

  float vout = 0.0;
  float vin = 0.0;
  float R1 = 30000.0;
  float R2 = 7500.0;
  int value = 0;

  // read the value at analog input
  value = analogRead(voltageSensor);
  vout = (value * 5.0) / 1024.0; // see text
  vin = vout / (R2 / (R1 + R2));

  return vin;
}

// ****Extra code****

//  shuntvoltage = ina219.getShuntVoltage_mV();
//  busvoltage = ina219.getBusVoltage_V();
//  current_mA = ina219.getCurrent_mA();
//  power_mW = ina219.getPower_mW();
//  loadvoltage = busvoltage + (shuntvoltage / 1000);

//  Serial.println("-------------------------------------------");
//  Serial.print("Bus Voltage:   "); Serial.print(busvoltage); Serial.println(" V");
//  Serial.print("Shunt Voltage: "); Serial.print(shuntvoltage); Serial.println(" mV");
//  Serial.print("Load Voltage:  "); Serial.print(loadvoltage); Serial.println(" V");
//  Serial.print("Current:       "); Serial.print(current_mA); Serial.println(" mA");
//  Serial.print("Power:         "); Serial.print(power_mW); Serial.println(" mW");
//  Serial.println("-------------------------------------------");
