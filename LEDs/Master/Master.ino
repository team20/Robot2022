#include <Wire.h>
 
// I2C Master code for Arduino Nano 
  
int ledState = 0;
  
void setup() {
    Wire.begin();//start i2c
    Serial.begin(9600);//start serial input
}
  
void loop() {//send codes directly from serial
  if(Serial.available()){//if input exists
    Wire.beginTransmission(18);
    while (Serial.available()) {//send all current inputs at once (put in one line for reset+new color)
        ledState=Serial.parseInt();
        // Send the question: H=humidity, T=temperature
        Wire.write(ledState);
    }
    Wire.endTransmission();
    delay(100);
  }
}
