#include <Ultrasonic.h>
#include <ESP8266WiFi.h>
#include <PubSubClient.h>

const char* ssid = "nome da rede";
const char* password =  "senha";

const char* mqttServer = "tailor.cloudmqtt.com";
const int mqttPort = 16607;
const char* mqttUser = "fyvugjqh";
const char* mqttPassword = "6NVSxJiSFcDY";

WiFiClient espClient;
PubSubClient client(espClient);
/*
-------------------------------------------------
NodeMCU / ESP8266  |  NodeMCU / ESP8266  |
D0 = 16            |  D6 = 12            |
D1 = 5             |  D7 = 13            |
D2 = 4             |  D8 = 15            |
D3 = 0             |  D9 = 3             |
D4 = 2             |  D10 = 1            |
D5 = 14            |                     |
-------------------------------------------------
*/

const int LedVerde = 5; //D1
const int LedAmarelo = 0; //D3
const int LedVermelho = 14; //D5
const int trigPin = 2;  //D4
const int echoPin = 4;  //D2

Ultrasonic ultrasonic(trigPin, echoPin);

void mqtt_callback(char* topic, byte* dados_tcp, unsigned int length);

void setup()
{  
  pinMode(LedVerde, OUTPUT);
  pinMode(LedAmarelo, OUTPUT);
  pinMode(LedVermelho, OUTPUT);
 
  Serial.begin(115200);
 
  WiFi.begin(ssid, password);
 
  while (WiFi.status() != WL_CONNECTED) 
  {   
     delay(100);
     Serial.println("Conectando ao WiFi..");
  }
  Serial.println("Conectado!"); 
  client.setServer(mqttServer, mqttPort);
  client.setCallback(callback);
 
  while (!client.connected())
  {
    Serial.println("Conectando ao servidor MQTT...");
    
    if (client.connect("Estacionamento Online", mqttUser, mqttPassword ))
    {
      Serial.println("Conectado ao servidor MQTT!");
    }
    else
    {
      Serial.print("Falha ao conectar ");
      Serial.print(client.state());
      delay(2000);
    }
  }
//  client.publish("Reiniciando", "Em funcionamento");
  client.subscribe("LED");
}

void callback(char* topic, byte* dados_tcp, unsigned int length)
{
  String mensagem;
 
  for(int i = 0; i < length; i++) 
  {
     char c = (char)dados_tcp[i];
     mensagem += c;
  }

  if (mensagem.equals("SENSOR") || !mensagem.equals("SENSOR"))
  {
    if (mensagem.equals("L1"))
    {
      digitalWrite(LedVerde, HIGH);   
    }
    else if (mensagem.equals("D1"))
    {
      digitalWrite(LedVerde, LOW);  
    }
    
    if (mensagem.equals("L2"))
    {
      digitalWrite(LedAmarelo, HIGH);   
    }
    else if (mensagem.equals("D2"))
    {
      digitalWrite(LedAmarelo, LOW);  
    }
    
    if (mensagem.equals("L3"))
    {
      digitalWrite(LedVermelho, HIGH);
    }
    else if (mensagem.equals("D3"))
    {
      digitalWrite(LedVermelho, LOW);
    }
  }
} 

void loop()
{ 
  client.loop();

  char sensor[4];
  dtostrf(ultrasonic.distanceRead(), 3, 0, sensor);
  if(ultrasonic.distanceRead() > 100)
  {
    client.publish("SENSOR", sensor);
    delay(500);
  }
}
