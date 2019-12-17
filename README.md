# IoT
>
![IMG_20191214_084842912_HDR](https://user-images.githubusercontent.com/48699769/70953103-12e53700-2047-11ea-8760-93a47ca49332.jpg)
![IMG_20191214_084851975_HDR](https://user-images.githubusercontent.com/48699769/70953104-12e53700-2047-11ea-9804-5059d981f934.jpg)
![IMG_20191214_084905579_HDR](https://user-images.githubusercontent.com/48699769/70953105-12e53700-2047-11ea-8243-b9360132e5f4.jpg)
![IMG_20191214_084931311_HDR](https://user-images.githubusercontent.com/48699769/70953106-12e53700-2047-11ea-8dc0-7e12d5c53b24.jpg)
![IMG_20191214_090801147](https://user-images.githubusercontent.com/48699769/70953107-137dcd80-2047-11ea-8e06-970847b4ee7f.jpg)
>
Esse projeto é um protótipo de um Estacionamento urbano, integrado com componentes de Internet das Coisas (IoT), acrescido da possibilidade de realizar uma reserva, 'checkin' e o 'checkout' de uma vaga, através de um aplicativo android.
>>>
Como requisito de segurança, o app envia uma notificação em tempo real para o smartphone do usuário do aplicativo/motorista, caso o veículo seja deslocado da vaga ocupada.
>>>
### Para a construção desse projeto, foi necessário três ambientes e seus respectivos artefatos:
  > #### **1° - Protótipo de um sensor a serem instalados nas vagas do estacionamento (Componentes: ).**
> - Componentes: ESP 8266 Node MCU 12E, protoboard, conversor de nível lógico, 3 leds (verde, amarelo e vermelho), 3 resistores, sersor ultrassônico HC-SR04, fios machos para a integração dos componentes, cabo de alimentação USB e uma fonte de alimentação com entrada USB.
   >>
  > #### **2° - Servidor para receber as informações provenientes do sensore de IoT, instalado na vaga.**
> - Plano gratuito do servidor MQTT da plataforma Cloud MQTT.
   >>
  > #### **3° - Aplicativo android que permite a interação do usuário com as vagas do estacionamento disponíveis na cidade/local.**
> - IDE Android Studio, linguagem Java e biblioteca de integração com o Servidor MQTT (Eclipse Poho).
