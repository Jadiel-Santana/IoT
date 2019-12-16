# IoT
>
Esse projeto é um protótipo de um Estacionamento urbano, integrado com componentes de Internet das Coisas (IoT), acrescido da possibilidade de realizar uma reserva, 'checkin' e o 'checkout' de uma vaga, através de um aplicativo android.
>>>
Como requisito de segurança, o app envia uma notificação em tempo real para o smartphone do usuário do aplicativo/motorista, caso o veículo seja deslocado da vaga ocupada.
>>>
### Para a construção desse projeto, foi necessário três ambientes e seus respectivos artefatos:
  >> #### **1° - Protótipo de um sensor a serem instalados nas vagas do estacionamento (Componentes: ).**
   > - Componentes: ESP 8266 Node MCU 12E, protoboard, conversor de nível lógico, 3 leds (verde, amarelo e vermelho), 3 resistores, sersor ultrassônico HC-SR04, fios machos para a integração dos componentes, cabo de alimentação USB e uma fonte de alimentação com entrada USB.
   >>
  > #### **2° - Servidor para receber as informações provenientes do sensore de IoT, instalado na vaga.**
   > - Plano gratuito do servidor MQTT da plataforma Cloud MQTT.
   >>
  > #### **3° - Aplicativo android que permite a interação do usuário com as vagas do estacionamento disponíveis na cidade/local.**
   > - IDE Android Studio, linguagem Java e biblioteca de integração com o Servidor MQTT (Eclipse Poho).
