#include <SoftwareSerial.h> 
SoftwareSerial mySerial(10, 11); //RX, TX 

int const PWMA = 6; 
int const PWMB = 5; 
int const dirA = 7; 
int const dirB = 4; 


void setup() { 
  mySerial.begin(9600);
  pinMode(PWMA, OUTPUT);
  pinMode(PWMB, OUTPUT);
  pinMode(dirA, OUTPUT);  
  pinMode(dirB, OUTPUT);

  digitalWrite(dirA, LOW);
  digitalWrite(dirB, LOW);
  analogWrite(PWMA, 0);
  analogWrite(PWMB, 0);

  Serial.begin(9600); 
}

void loop() {

  if (mySerial.available() > 0) {
    
    int incomingByte = mySerial.read();

    static int yukari=95;
    static int kuyruk=70;
    switch (incomingByte) {
    case 'F':
      while(kuyruk<255){
        kuyruk=kuyruk+15;
        moveForward(kuyruk, true);
        Serial.println("ileri");
        Serial.println(kuyruk);
        break;
      }
      break;
    case 'R':
      turn(255, true);
      Serial.println(" sağa dön");
      break;
    case 'L':
      turn(255, false);
      Serial.println("sola dön");
      break;
    case 'B':
      while(kuyruk>0){
        kuyruk=kuyruk-15;
        moveForward(kuyruk, false);
        Serial.println("geri");
        Serial.println(kuyruk);
        break;
      }
      break;
    case 'S':
      moveForward(0, true);
      up(0, true);
      Serial.println("stop");
      kuyruk=90;
      yukari=90;
      break;

    case 'G':
      while(yukari<255){
        yukari=yukari+15;
        up(yukari, true);
        Serial.println("ucuyor");
        Serial.println(yukari);
        break;
      }
      break;

    case 'K':
      while(yukari>0){
        yukari =yukari-15;
        up(yukari, false);
        Serial.println("iniyor");
        Serial.println(yukari);
        break;
      }
      break;

    default: 
      break;
    }
  }
}

void moveForward(int speedBot, boolean forward){
  if (forward){
    analogWrite(PWMA, speedBot);
    delay(15);
  }
  else{

    analogWrite(PWMA, speedBot);
    Serial.println(speedBot);
    delay(30);
  }
}

// for up  
void up(int speedBot, boolean forward){
  if (forward){
    analogWrite(PWMB, speedBot);
    delay(15);
  }
  else{

    analogWrite(PWMB, speedBot);
    Serial.println(speedBot);
    delay(30);
  }


}

void turn(int speedBot, boolean right){
  if (right){
    digitalWrite(dirA, HIGH);
    digitalWrite(dirB, LOW);
  }
  else{
    digitalWrite(dirA, LOW);
    digitalWrite(dirB, HIGH);
  }
  analogWrite(PWMA, speedBot);
  analogWrite(PWMB, speedBot);
}


