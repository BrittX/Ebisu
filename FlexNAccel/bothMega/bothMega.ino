/*
 * Code to incorporate both the accelerometer data and the flex sensor
 * data to print to a singular text file. 
 * Output: thumb, index, middle, ring, pinky, yaw, pitch, roll
 * 
 * Author: Brittany Barnes
 * Code adapted from:
 *  Flex Sensor: https://learn.sparkfun.com/tutorials/flex-sensor-hookup-guide
 *  Accelerometer: https://diyhacking.com/arduino-mpu-6050-imu-sensor-tutorial/
 */
 #include <Wire.h>
 // I2Cdev and MPU6050 must be installed as libraries, or else the .cpp/.h files
// for both classes must be in the include path of your project
#include "I2Cdev.h"

#include "MPU6050_6Axis_MotionApps20.h"
//#include "MPU6050.h" // not necessary if using MotionApps include file

// Arduino Wire library is required if I2Cdev I2CDEV_ARDUINO_WIRE implementation
// is used in I2Cdev.h
#if I2CDEV_IMPLEMENTATION == I2CDEV_ARDUINO_WIRE
    #include "Wire.h"
#endif

#define OUTPUT_READABLE_YAWPITCHROLL
#define LED_PIN 13 // (Arduino is 13, Teensy is 11, Teensy++ is 6)
bool blinkState = false;

// class default I2C address is 0x68
// specific I2C addresses may be passed as a parameter here
// AD0 low = 0x68 (default for SparkFun breakout and InvenSense evaluation board)
// AD0 high = 0x69
MPU6050 mpu;
//MPU6050 mpu(0x69); // <-- use for AD0 high
 
 //Variables for Flex Sensors
//To reference each flex sensor analog pin
const int thumb = A0; //Blue
const int index = A1; //Pink
const int middle = A2; //Yellow has gap in middle
const int ring = A3; //Green 
const int pinky = A4; //No color

//Voltage for Arduino 5V and the resistors 
const float VCC = 4.98; // Measured voltage of Ardunio 5V line
const float R_DIV = 21900.0; // Measured resistance of 22k resistor

// Ohms for each flex sensor (bent/straight)
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

// Variables for accelerometer
// MPU control/status vars
bool dmpReady = false;  // set true if DMP init was successful
uint8_t mpuIntStatus;   // holds actual interrupt status byte from MPU
uint8_t devStatus;      // return status after each device operation (0 = success, !0 = error)
uint16_t packetSize;    // expected DMP packet size (default is 42 bytes)
uint16_t fifoCount;     // count of all bytes currently in FIFO
uint8_t fifoBuffer[64]; // FIFO storage buffer

// orientation/motion vars
Quaternion q;           // [w, x, y, z]         quaternion container
VectorInt16 aa;         // [x, y, z]            accel sensor measurements
VectorInt16 aaReal;     // [x, y, z]            gravity-free accel sensor measurements
VectorInt16 aaWorld;    // [x, y, z]            world-frame accel sensor measurements
VectorFloat gravity;    // [x, y, z]            gravity vector
float euler[3];         // [psi, theta, phi]    Euler angle container
float ypr[3];           // [yaw, pitch, roll]   yaw/pitch/roll container and gravity vector

volatile bool mpuInterrupt = false;     // indicates whether MPU interrupt pin has gone high
void dmpDataReady() {
    mpuInterrupt = true;
}

void setup() {
  // join I2C bus (I2Cdev library doesn't do this automatically)
    #if I2CDEV_IMPLEMENTATION == I2CDEV_ARDUINO_WIRE
        Wire.begin();
        TWBR = 24; // 400kHz I2C clock (200kHz if CPU is 8MHz)
    #elif I2CDEV_IMPLEMENTATION == I2CDEV_BUILTIN_FASTWIRE
        Fastwire::setup(400, true);
    #endif
  Serial.begin(9600);
  //Set up for the FS
  pinMode(thumb, INPUT);
  pinMode(index, INPUT);
  pinMode(middle, INPUT);
  pinMode(ring, INPUT);
  pinMode(pinky, INPUT);

  //Accelerometer
  while (!Serial); // wait for Leonardo enumeration, others continue immediately
  // initialize device
  mpu.initialize();

  // load and configure the DMP
  devStatus = mpu.dmpInitialize();
  // supply your own gyro offsets here, scaled for min sensitivity
    mpu.setXGyroOffset(220);
    mpu.setYGyroOffset(76);
    mpu.setZGyroOffset(-85);
    mpu.setZAccelOffset(1788); // 1688 factory default for my test chip

    // make sure it worked (returns 0 if so)
    if (devStatus == 0) {
        // turn on the DMP, now that it's ready
        mpu.setDMPEnabled(true);

        // enable Arduino interrupt detection
        attachInterrupt(0, dmpDataReady, RISING);
        mpuIntStatus = mpu.getIntStatus();

        // set our DMP Ready flag so the main loop() function knows it's okay to use it
        dmpReady = true;

        // get expected DMP packet size for later comparison
        packetSize = mpu.dmpGetFIFOPacketSize();
    } else {
        // ERROR!
        // 1 = initial memory load failed
        // 2 = DMP configuration updates failed
        // (if it's going to break, usually the code will be 1)
        Serial.print(F("DMP Initialization failed (code "));
        Serial.print(devStatus);
        Serial.println(F(")"));

    // configure LED for output
    pinMode(LED_PIN, OUTPUT);
    }
}

void loop() {
  //Accelerometer
  // if programming failed, don't try to do anything
    if (!dmpReady) return;

    // wait for MPU interrupt or extra packet(s) available
    while (!mpuInterrupt && fifoCount < packetSize) {
        // other program behavior stuff here
        // .
        // .
        // .
        // if you are really paranoid you can frequently test in between other
        // stuff to see if mpuInterrupt is true, and if so, "break;" from the
        // while() loop to immediately process the MPU data
        // .
        // .
        // .
    }

    // reset interrupt flag and get INT_STATUS byte
    mpuInterrupt = false;
    mpuIntStatus = mpu.getIntStatus();

    // get current FIFO count
    fifoCount = mpu.getFIFOCount();

    // check for overflow (this should never happen unless our code is too inefficient)
    if ((mpuIntStatus & 0x10) || fifoCount == 1024) {
        // reset so we can continue cleanly
        mpu.resetFIFO();
        //Serial.println(F("FIFO overflow!"));

    // otherwise, check for DMP data ready interrupt (this should happen frequently)
    } else if (mpuIntStatus & 0x02) {
        // wait for correct available data length, should be a VERY short wait
        while (fifoCount < packetSize) fifoCount = mpu.getFIFOCount();

        // read a packet from FIFO
        mpu.getFIFOBytes(fifoBuffer, packetSize);
        
        // track FIFO count here in case there is > 1 packet available
        // (this lets us immediately read more without waiting for an interrupt)
        fifoCount -= packetSize;

        #ifdef OUTPUT_READABLE_YAWPITCHROLL
            // display Euler angles in degrees
            mpu.dmpGetQuaternion(&q, fifoBuffer);
            mpu.dmpGetGravity(&gravity, &q);
            mpu.dmpGetYawPitchRoll(ypr, &q, &gravity);
            
            //Reading in the data for each flex sensor from the analog port
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
            float tAngle = map(tR, tS, tB, 0, 90.0);
            float iAngle = map(iR, iS, iB,
                             0, 90.0);
            float mAngle = map(mR, mS, mB, 0, 90.0);
            float rAngle = map(rR, rS, rB, 0, 90.0);
           
            float pAngle = map(pR, pS, pB, 0, 90.0);
          
            //Prints out the degrees for each finger thumb-index-middle-ring-pinky and MPU data
            Serial.println(String(tAngle) + "," + String(iAngle) + "," + String(mAngle) + "," + String(rAngle) + "," + String(pAngle) + "," + ypr[0] * 180/M_PI + "," + ypr[1] * 180/M_PI + "," + ypr[2] * 180/M_PI);
          
            delay(50); //wait half a second before each new line of data
        #endif

         // blink LED to indicate activity
        blinkState = !blinkState;
        digitalWrite(LED_PIN, blinkState);
    }
  
}
