/*
 * Code to read values from Flex Sensor and print on a line
 * with each degree (for each finger) separated by a comma.
 */

#include <Wire.h>

//For accelerometer
long accelX, accelY, accelZ;
float gForceX, gForceY, gForceZ;

long gyroX, gyroY, gyroZ;
float rotX, rotY, rotZ;

//To reference each flex sensor analog pin
const int thumb = A0; //Blue
const int index = A1; //Pink
const int middle = A2; //Yellow has gap in middle
const int ring = A3; //Green 
const int pinky = A4; //No color

// Measure the voltage at 5V and the actual resistance of your
// 22k resistor, and enter them below:
const float VCC = 4.98; // Measured voltage of Ardunio 5V line
const float R_DIV = 21900.0; // Measured resistance of 22k resistor

const float tS = 17080.0; // when straight
const float tB = 40500.0; //when bent
const float iS = 14900.0; // index when straight
const float iB = 31500.0; //index when bent
const float mS = 14820.0; //middle straight
const float mB = 28300.0; //middle bent
const float rS = 11650.0; //ring straight
const float rB = 29100.0; //ringt bent
const float pS = 13880.0; //pink straight
const float pB = 27200.0; //pinky bent

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(thumb, INPUT);
  pinMode(index, INPUT);
  pinMode(middle, INPUT);
  pinMode(ring, INPUT);
  pinMode(pinky, INPUT);
  Wire.begin();
  setupMPU();
}

void loop() {
  // put your main code here, to run repeatedly:
  //for accelerometer
  recordAccelRegisters();
  recordGyroRegisters();
  
  int tADC = analogRead(thumb);
  int iADC = analogRead(index);
  int mADC = analogRead(middle);
  int rADC = analogRead(ring);
  int pADC = analogRead(pinky);
  
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

  // Use the calculated resistance to estimate the sensor's
  // bend angle:
  //float tAngle = map(tR, STRAIGHT_RESISTANCE, BEND_RESISTANCE,
    //               0, 90.0);
  float tAngle = map(tR, tS, tB, 0, 90.0);
  float iAngle = map(iR, iS, iB,
                   0, 90.0);
  float mAngle = map(mR, mS, mB, 0, 90.0);
  float rAngle = map(rR, rS, rB, 0, 90.0);
 
  float pAngle = map(pR, pS, pB, 0, 90.0);


  //Prints out the degrees for each finger thumb-index-middle-ring-pinky
  Serial.println(String(tAngle) + "," + String(iAngle) + "," + String(mAngle) + "," + String(rAngle) + "," + String(pAngle));
  Serial.print("Gyro (deg)");
  Serial.print(" X=");
  Serial.print(rotX);
  Serial.print(" Y=");
  Serial.print(rotY);
  Serial.print(" Z=");
  Serial.print(rotZ);
  Serial.print(" Accel (g)");
  Serial.print(" X=");
  Serial.print(gForceX);
  Serial.print(" Y=");
  Serial.print(gForceY);
  Serial.print(" Z=");
  Serial.println(gForceZ);
 //Wait 1 second before printing again
  delay(1000);
}

void setupMPU(){
  Wire.beginTransmission(0b1101000); //This is the I2C address of the MPU (b1101000/b1101001 for AC0 low/high datasheet sec. 9.2)
  Wire.write(0x6B); //Accessing the register 6B - Power Management (Sec. 4.28)
  Wire.write(0b00000000); //Setting SLEEP register to 0. (Required; see Note on p. 9)
  Wire.endTransmission();  
  Wire.beginTransmission(0b1101000); //I2C address of the MPU
  Wire.write(0x1B); //Accessing the register 1B - Gyroscope Configuration (Sec. 4.4) 
  Wire.write(0x00000000); //Setting the gyro to full scale +/- 250deg./s 
  Wire.endTransmission(); 
  Wire.beginTransmission(0b1101000); //I2C address of the MPU
  Wire.write(0x1C); //Accessing the register 1C - Acccelerometer Configuration (Sec. 4.5) 
  Wire.write(0b00000000); //Setting the accel to +/- 2g
  Wire.endTransmission(); 
}

void recordAccelRegisters() {
  Wire.beginTransmission(0b1101000); //I2C address of the MPU
  Wire.write(0x3B); //Starting register for Accel Readings
  Wire.endTransmission();
  Wire.requestFrom(0b1101000,6); //Request Accel Registers (3B - 40)
  while(Wire.available() < 6);
  accelX = Wire.read()<<8|Wire.read(); //Store first two bytes into accelX
  accelY = Wire.read()<<8|Wire.read(); //Store middle two bytes into accelY
  accelZ = Wire.read()<<8|Wire.read(); //Store last two bytes into accelZ
  processAccelData();
}

void processAccelData(){
  gForceX = accelX / 16384.0;
  gForceY = accelY / 16384.0; 
  gForceZ = accelZ / 16384.0;
}

void recordGyroRegisters() {
  Wire.beginTransmission(0b1101000); //I2C address of the MPU
  Wire.write(0x43); //Starting register for Gyro Readings
  Wire.endTransmission();
  Wire.requestFrom(0b1101000,6); //Request Gyro Registers (43 - 48)
  while(Wire.available() < 6);
  gyroX = Wire.read()<<8|Wire.read(); //Store first two bytes into accelX
  gyroY = Wire.read()<<8|Wire.read(); //Store middle two bytes into accelY
  gyroZ = Wire.read()<<8|Wire.read(); //Store last two bytes into accelZ
  processGyroData();
}

void processGyroData() {
  rotX = gyroX / 131.0;
  rotY = gyroY / 131.0; 
  rotZ = gyroZ / 131.0;
}
