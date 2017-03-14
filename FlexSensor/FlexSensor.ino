/******************************************************************************
Flex_Sensor_Example.ino
Example sketch for SparkFun's flex sensors
  (https://www.sparkfun.com/products/10264)
Jim Lindblom @ SparkFun Electronics
April 28, 2016

Create a voltage divider circuit combining a flex sensor with a 47k resistor.
- The resistor should connect from A0 to GND.
- The flex sensor should connect from A0 to 3.3V
As the resistance of the flex sensor increases (meaning it's being bent), the
voltage at A0 should decrease.

Development environment specifics:
Arduino 1.6.7
******************************************************************************/
const int Thumb = A0; // Pin connected to voltage divider output
const int Index = A1; 
const int Middle = A2;
const int Ring = A3;
const int Pinky = A4; 

// Measure the voltage at 5V and the actual resistance of your
// 22k resistor, and enter them below:
const float VCC = 4.98; // Measured voltage of Ardunio 5V line
const float R_DIV = 21900.0; // Measured resistance of 22k resistor

// Upload the code, then try to adjust these values to more
// accurately calculate bend degree.
const float STRAIGHT_RESISTANCE = 15300.0; // resistance when straight
const float BEND_RESISTANCE = 27500.0; // resistance at 90 deg

void setup() 
{
  Serial.begin(9600);
  pinMode(Thumb, INPUT);
}

void loop() 
{
  // Read the ADC, and calculate voltage and resistance from it
  int tADC = analogRead(Thumb);
  int iADC = analogRead(Index);
  int mADC = analogRead(Middle);
  int rADC = analogRead(Ring);
  int pADC = analogRead(Pinky);
  
  //Calculate voltage
  float tV = tADC * VCC / 1023.0;
  float iV = iADC * VCC / 1023.0;
  float mV = mADC * VCC / 1023.0;
  float rV = rADC * VCC / 1023.0;
  float pV = pADC * VCC / 1023.0;

  //Calculate Resistance
  float tR = R_DIV * (VCC / tV - 1.0);
  float iR = R_DIV * (VCC / iV - 1.0);
  float mR = R_DIV * (VCC / mV - 1.0);
  float rR = R_DIV * (VCC / rV - 1.0);
  float pR = R_DIV * (VCC / pV - 1.0);
  
  Serial.println("Thumb Resistance: " + String(tR) + " ohms");
  Serial.println("Index Resistance: " + String(iR) + " ohms");
  Serial.println("Middle Resistance: " + String(mR) + " ohms");
  Serial.println("Ring Resistance: " + String(rR) + " ohms");
  Serial.println("Pinky Resistance: " + String(pR) + " ohms");
  
  // Use the calculated resistance to estimate the sensor's
  // bend angle:
  float tAngle = map(tR, STRAIGHT_RESISTANCE, BEND_RESISTANCE,
                   0, 90.0);
  float iAngle = map(iR, STRAIGHT_RESISTANCE, BEND_RESISTANCE,
                   0, 90.0);
  float mAngle = map(mR, STRAIGHT_RESISTANCE, BEND_RESISTANCE,
                   0, 90.0);
  float rAngle = map(rR, STRAIGHT_RESISTANCE, BEND_RESISTANCE,
                   0, 90.0);
  float pAngle = map(pR, STRAIGHT_RESISTANCE, BEND_RESISTANCE,
                   0, 90.0);
  Serial.println("Thumb Bend: " + String(tAngle) + " degrees");
  Serial.println("Index Bend: " + String(iAngle) + " degrees");
  Serial.println("Middle Bend: " + String(mAngle) + " degrees");
  Serial.println("Ring Bend: " + String(rAngle) + " degrees");
  Serial.println("Pinky Bend: " + String(pAngle) + " degrees");
  Serial.println();

  delay(1000);
}
