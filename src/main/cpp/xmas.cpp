#include "application.h"

#define ledPin D7

int handleTweet(String message);

void setup()
{
   Particle.function("tweet", handleTweet);
   pinMode(ledPin, OUTPUT);
}

void loop()
{
   delay(1000);
   digitalWrite(ledPin, LOW);
   delay(1000);
}

int handleTweet(String message)
{
   digitalWrite(ledPin, HIGH);
   Serial.print("Received tweet: ");
   Serial.print(message);
   Serial.println();

   return 0;
}

