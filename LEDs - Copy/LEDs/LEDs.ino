#include <Adafruit_NeoPixel.h>
#include <Wire.h>
#ifdef __AVR__
 #include <avr/power.h> // Required for 16 MHz Adafruit Trinket
#endif

// Which pin on the Arduino is connected to the NeoPixels?
// On a Trinket or Gemma we suggest changing this to 1:
#define LED_PIN    6

// How many NeoPixels are attached to the Arduino?
#define LED_COUNT 60

// Declare our NeoPixel strip object:
Adafruit_NeoPixel strip(LED_COUNT, LED_PIN, NEO_GRB + NEO_KHZ800);
// Argument 1 = Number of pixels in NeoPixel strip
// Argument 2 = Arduino pin number (most are valid)
// Argument 3 = Pixel type flags, add together as needed:
//   NEO_KHZ800  800 KHz bitstream (most NeoPixel products w/WS2812 LEDs)
//   NEO_KHZ400  400 KHz (classic 'v1' (not v2) FLORA pixels, WS2811 drivers)
//   NEO_GRB     Pixels are wired for GRB bitstream (most NeoPixel products)
//   NEO_RGB     Pixels are wired for RGB bitstream (v1 FLORA pixels, not v2)
//   NEO_RGBW    Pixels are wired for RGBW bitstream (NeoPixel RGBW products)


// setup() function -- runs once at startup --------------------------------

int colorIndex = 0;
int pattern=-1;
long startTime=-1;const long endTime=30000;
int DHpin = 8;
byte dat[5];
  
char C;
void setup() {
  // These lines are specifically to support the Adafruit Trinket 5V 16 MHz.
  // Any other board, you can remove this part (but no harm leaving it):
#if defined(__AVR_ATtiny85__) && (F_CPU == 16000000)
  clock_prescale_set(clock_div_1);
#endif
  // END of Trinket-specific code.

  strip.begin();           // INITIALIZE NeoPixel strip object (REQUIRED)
  strip.show();            // Turn OFF all pixels ASAP
  strip.setBrightness(50); // Set BRIGHTNESS to about 1/5 (max = 255)
  Wire.begin(18);
  Wire.onReceive(receiveEvent); // data slave recieved 
}

void loop() {
//  pattern=(int)((millis()-testStart)/testInc)-1;if(pattern>7){pattern=7;}
  switch(pattern){
    case 7:
      if(startTime<0){startTime=millis();}
      for(int i=0;i<LED_COUNT;i++){strip.setPixelColor(i,Timer(colorIndex, i, strip.Color(0,0,250)));}
      delay(50);
      break;
    case 6:
      if(startTime<0){startTime=millis();}
      for(int i=0;i<LED_COUNT;i++){strip.setPixelColor(i,Timer(colorIndex, i, strip.Color(0,250,0)));}
      delay(50);
      break;
    case 5:
      for(int i=0;i<LED_COUNT;i++){strip.setPixelColor(i,MovingRedGreenGradient(colorIndex, i));}
      delay(1);
      break;
    case 4:
      for(int i=0;i<LED_COUNT;i++){strip.setPixelColor(i,TheaterLights(colorIndex, i, strip.Color(0,0,250), strip.Color(0,0,0)));}
      delay(100);
      break;
    case 3:
      for(int i=0;i<LED_COUNT;i++){strip.setPixelColor(i,MovingGreenBlueGradient(colorIndex, i));}
      delay(1);
      break;
    case 2:
      for(int i=0;i<LED_COUNT;i++){strip.setPixelColor(i,TheaterLights(colorIndex, i, strip.Color(250,0,0), strip.Color(0,0,0)));}
      delay(100);
      break;
    case 1:
      for(int i=0;i<LED_COUNT;i++){strip.setPixelColor(i,TheaterLights(colorIndex, i, strip.Color(0,250,0), strip.Color(0,0,0)));}
      delay(100);
      break;
    case 0:
      for(int i=0;i<LED_COUNT;i++){strip.setPixelColor(i,TheaterLights(colorIndex, i, strip.Color(255,0,115), strip.Color(0,0,0)));}
      delay(100);
      break;
    default:
      for(int i=0;i<LED_COUNT;i++){strip.setPixelColor(i,MovingRainbow(colorIndex, i));}
      delay(50);
      break;
  }
  strip.show();
  colorIndex++;
}
void receiveEvent(int howMany) {
    // remember the question: H=humidity, T=temperature
    while (0 < Wire.available()) {
        byte x = Wire.read();
        if(x%10==0 && x!=0){pattern = x/10-1;}
    }
}
uint32_t Timer(int c, int i, uint32_t color){
  if(c%2==0 && 2>(i+c)%int(LED_COUNT*(1-(millis()-startTime)/endTime)) && i*endTime>LED_COUNT*(millis()-startTime)){return(color);}
  return(strip.Color(0,0,0));
}

uint32_t MovingRedGreenGradient(int c, int i){
  return(strip.Color(255*((i+c)%LED_COUNT)/LED_COUNT,0,255*(LED_COUNT-(i+c)%LED_COUNT)/LED_COUNT%255));
}
uint32_t MovingGreenBlueGradient(int c, int i){
  return(strip.Color(0,255*((i+c)%LED_COUNT)/LED_COUNT,255*(LED_COUNT-(i+c)%LED_COUNT)/LED_COUNT%255));
}
uint32_t TheaterLights(int c, int i, uint32_t color1, uint32_t color2){
  if(i%2==c%2){return(color1);}return(color2);
}
uint32_t HSV2RGB(uint32_t h, double s, double v){
  h%=360;
  double c = v*s;
  double x= c*(1-abs((h/60)%2-1));
  double m = v-c;
  uint8_t r=(uint8_t)(((abs(180-h)>120)*c+(abs(180-h)>60)*(x-c)+m)*255);
  uint8_t g=(uint8_t)(((abs(120-h)<60)*c+(abs(120-h)<120)*(x-c)+m)*255);
  uint8_t b=(uint8_t)(((abs(240-h)<60)*c+(abs(240-h)<120)*(x-c)+m)*255);
  return(strip.Color(r,g,b));  
}
uint32_t RainbowColor(int c, int i){
  return(HSV2RGB((c*18)%360,1,1));
//return(strip.Color(0,255,0));
}
uint32_t SeizureRainbowColor(int c, int i){
  if(c%2==0)return(RainbowColor(c/2, i));
  return(strip.Color(0,0,0));
}
uint32_t MovingRainbow(int c, int i){
  return(RainbowColor(c+i,i));
}
