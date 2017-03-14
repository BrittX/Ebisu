int const Flex1 = A0; //A0 pin

void setup() {
  Serial.begin(9600);

}

void loop() {
  int FlexV1 = analogRead(Flex1);
  //int flex0to100 = map(flexSensorReading, 512, 614, 0, 100);
  // put your main code here, to run repeatedly:
  Serial.println(FlexV1);

  delay(100);
}
