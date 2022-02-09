#include <Wire.h>
 
// I2C Master code for Arduino Nano 
 
const int buttonPin = 8;   // the number of the pushbutton pin
const int ledPin = 13;    // the wellknown led
const int redLedPin = 2;  // our own led
  
int ledState = 0;
  
void setup() {
    Wire.begin();
    Serial.begin(9600);
}
  
void loop() {
    if (Serial.available()) {
        ledState=Serial.parseInt();
    }
    Wire.beginTransmission(18);
    // Send the question: H=humidity, T=temperature
    Wire.write(ledState);
    Wire.endTransmission();
    delay(100);
}
