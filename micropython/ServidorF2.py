''' Projeto em microPython controle do NEO PIXEL
    Sergio L M Marques
    
    Descrição: Servidor web que recebe comandos do tipo POST com dados
    no corpo da requisição no formato JSON.
    
    Implementação do servidor com thread e controle do neo-pixel (usando um
    codigo livre na internet);
    
    Detalhe importante, deve-se usar o pino de controle acima do 30 , no meu
    caso 33 pois os pinos baixos (0,1,2,3,4) sofrem interferencias das
    bibliotecas de socket (não sei porque) e ai não funciona o acionamento do
    neo pixel.


'''
#importações necessárias para que o projeto funcione
from micropython import const 
from esp32 import RMT
import machine, neopixel
import json
import time
import socket
import _thread
from machine import Pin
from time import ticks_ms, ticks_diff
import network

import gc
gc.collect()

# Copyright public licence and also I don't care.
# 2020 Josh "NeverCast" Lloyd.
# The peripheral clock is 80MHz or 12.5 nanoseconds per clock.
# The smallest precision of timing requried for neopixels is
# 0.35us, but I've decided to go with 0.05 microseconds or
# 50 nanoseconds. 50 nanoseconds = 12.5 * 4 clocks.
# By dividing the 80MHz clock by 4 we get a clock every 50 nanoseconds.

# Neopixel timing in RMT clock counts.
T_0H = const(35 // 5) # 0.35 microseconds / 50 nanoseconds
T_1H = const(70 // 5) # 0.70 microseconds / 50 nanoseconds
T_0L = const(80 // 5) # 0.80 microseconds / 50 nanoseconds
T_1L = const(60 // 5) # 0.60 microseconds / 50 nanoseconds

# Encoded timings for bits 0 and 1.
D_ZERO = (T_0H, T_0L)
D_ONE = (T_1H, T_1L)

# [D_ONE if ((channel >> bit) & 1) else D_ZERO for channel in channels for bit in range(num_bits - 1, -1, -1)]
# Reset signal is low for longer than 50 microseconds.
T_RST = const(510 // 5) # > 50 microseconds / 50 nanoseconds

# Channel width in bits 
CHANNEL_WIDTH = const(8)

class Pixels:
    def __init__(self, pin, pixel_count, rmt_channel=2, pixel_channels=3):
        self.rmt = RMT(rmt_channel, pin=pin, clock_div=4)
        # pixels stores the data sent out via RMT
        self.channels = pixel_channels
        single_pixel = (0,) * pixel_channels
        self.pixels = [D_ZERO * (pixel_channels * CHANNEL_WIDTH)] * pixel_count
        # colors is only used for __getitem__
        self.colors = [single_pixel] * pixel_count

    def write(self):
        # The bus should be idle low ( I think... )
        # So we finish low and start high.
        pulses = tuple()
        for pixel in self.pixels:
            pulses += pixel
        pulses = pulses[:-1] + (T_RST,) # The last low should be long.
        self.rmt.write_pulses(pulses, start=1)

    def __setitem__(self, pixel_index, colors):
        self_colors = self.colors
        self_pixels = self.pixels 
        if isinstance(pixel_index, int):
            # pixels[0] = (r, g, b)
            self_colors[pixel_index] = tuple(colors)
            self_pixels[pixel_index] = tuple(clocks for bit in (D_ONE if ((channel >> bit) & 1) else D_ZERO for channel in colors for bit in range(CHANNEL_WIDTH - 1, -1, -1)) for clocks in bit)
        elif isinstance(pixel_index, slice):
            start = 0 if pixel_index.start is None else pixel_index.start
            stop = len(self.pixels) if pixel_index.stop is None else pixel_index.stop
            step = 1 if pixel_index.step is None else pixel_index.step
            # Assume that if the first colors element is an int, then its not a sequence
            # Otherwise always assume its a sequence of colors
            if isinstance(colors[0], int):
                # pixels[:] = (r,g,b)
                for index in range(start, stop, step):
                    self_colors[index] = tuple(colors)
                    self_pixels[index] = tuple(clocks for bit in (D_ONE if ((channel >> bit) & 1) else D_ZERO for channel in colors for bit in range(CHANNEL_WIDTH - 1, -1, -1)) for clocks in bit)
            else:
                # pixels[:] = [(r,g,b), ...]
                # Assume its a sequence, make it a list so we know the length
                if not isinstance(colors, list):
                    colors = list(colors)
                color_length = len(colors)
                for index in range(start, stop, step):
                    color = colors[(index - start) % color_length]
                    self_colors[index] = tuple(color)
                    self_pixels[index] = tuple(clocks for bit in (D_ONE if ((channel >> bit) & 1) else D_ZERO for channel in color for bit in range(CHANNEL_WIDTH - 1, -1, -1)) for clocks in bit)
        else:
            raise TypeError('Unsupported pixel_index {} ({})'.format(pixel_index, type(pixel_index)))

    def __getitem__(self, pixel_index):
        # slice instances are passed through
        return self.colors[pixel_index]
#############################################################################################################
    
# efeito de piscar um led apos o outro na cor branca (bem forte)
# n indica quantas venzes irá fazer esse efeito
def cycle(n):
    global pix
    if n <= 0:
        n =1
    for i in range(2 * n):
        rainbow = [[0 , 0 , 0]]        
        pix[:] = rainbow
        pix.write()
        print((i % n))
        pix[0:(i % n)+1] = [[255 , 255 ,255]]        #(255, 255, 255)
        pix.write()
        time.sleep_ms(50)
    rainbow = [[0 , 0 , 0]]        
    pix[:] = rainbow
    pix.write()

# define o efeito de relogio
# apaga todos os leds e liga apenas a posição que foi passada por paramentro
def relogio(pos):
    global pix
    rainbow = [[0 , 0 , 0]]        
    pix[:] = rainbow
    pix.write()
    pix[0:(pos)+1] = [[0 , 128 ,0]]        #(255, 255, 255)
    pix.write()
        
# faz um efeito de arco-iris com os leds, fazendo eles movimentarem e
# trocarem as cores simulando um efeito de onda
# qtd é a quantidade de leds que serão manipulados 1 volta = 12 leds
def RAINBOW(qtd):
    global pix
    rainbow = [[126 , 1 , 0],[114 , 13 , 0],[102 , 25 , 0],[90 , 37 , 0],[78 , 49 , 0],[66 , 61 , 0],[54 , 73 , 0],[42 , 85 , 0],[30 , 97 , 0],[18 , 109 , 0],[6 , 121 , 0],[0 , 122 , 5],[0 , 110 , 17],[0 , 98 , 29],[0 , 86 , 41],[0 , 74 , 53],[0 , 62 , 65],[0 , 50 , 77],[0 , 38 , 89],[0 , 26 , 101],[0 , 14 , 113],[0 , 2 , 125],[9 , 0 , 118],[21 , 0 , 106],[33 , 0 , 94],[45 , 0 , 82],[57 , 0 , 70],[69 , 0 , 58],[81 , 0 , 46],[93 , 0 , 34],[105 , 0 , 22],[117 , 0 , 10]]
    i = qtd
    while i> 0:
        rainbow = rainbow[-1:] + rainbow[:-1]
        pix[:] = rainbow
        pix.write()
        i = i-1
    rainbow = [[0 , 0 , 0]]        
    pix[:] = rainbow
    pix.write()


def drawArc(qtd,red,green,blue):
    global pix
    global rainbow
    r1 = [red,green,blue]       
    rainbow[qtd] = r1
    r1 = [0,0,0]
    i = 11
    while i> qtd:
        rainbow[i] = r1
        i=i-1
    print(rainbow)
    i = 12
    while i> 0:
        pix[0:i] = rainbow
        i=i-1
    pix.write()

# callback acionada pela interrupção associada a uma tecla
# usado para teste
def callback(p):
    print('pin change',p)
    RAINBOW(40)
    
# define o procedimento do servidor HTTP;
def httpserver(group,mport):
    global recebeu
    global qtd
    global pix
    global red
    global blue
    global green
    global efeito
    # cria o socket para stream
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # faz o bind no localhost isto é no ip associado a conexão wifi e na porta 80
    s.bind(('',80))
    # permite at´5 conexões simultaneas
    s.listen(5)
    # entra no loop de espera de conexão
    while True:
        cl, addr = s.accept()
        print('client connected from %s', str(addr))
        while True:
            data = cl.recv(1024)    # recebe os dados da conexao
            # testa para ver se recebeu dados
            if data:
                msg1 = str(data) # convert para string os bytes recebidos
                # verifica se não recebeu uma fecha conexão
                if msg1.find("Connection: close") > 0: # se sim mostra um efeito e sai do loop
                    RAINBOW(24)
                    break
           
                jsonStr = ""
                iniciaJson = False
                # vefirica se mensagem contem o comando POST
                if msg1.find("POST") >= 0: # se sim verifica se tem o {} para indica dados em Json
                    ini = msg1.find('{')
                    fim = msg1.find('}')
                    if ini>0 and fim > 0: # se achou os {} copia os dados para serem decodificados
                        jsonStr = msg1[ini:fim+1] # +1 para pegar o ultimo caracter
                        
                        y = json.loads(jsonStr) # convert o jSon para um array de valores.
                        if y: # se conversão ok obtem os valores passados;
                            qtd = y["angulo"]
                            red = y["red"]
                            green = y["green"]
                            blue = y["blue"]
                            efeito = y["efeito"]
                            recebeu = True      # indica no loop principal que pode fazer o efeito
                            # indicado
                            
        cl.close()
        print("fechou a conexão")
        rainbow = [[0 , 0 , 0]]        
        pix[:] = rainbow
        pix.write()
     

#variaveis de controle do projeoto
efeito3 = False
efeito = 0;
red = 0
blue = 128
gree = 0
recebeu=False
qtd = 30
last1 = ticks_ms()
cnt = 0

ssid = 'casa'
password = 'veneza03'

# inicia o vetor de dados onde indica as posições do arco que serão mostradas.
rainbow = [[0 , 0 , 0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0]]
      
# define um pino para uma chave a ser utilizada para testes        
btn1 = machine.Pin(32, machine.Pin.IN, machine.Pin.PULL_UP)
btn1.irq(trigger= machine.Pin.IRQ_FALLING, handler=callback)


p = Pin(33)         # define o pino de controle do neo  ring
pix = Pixels(p, 12) # define a quantidade de leds do neo ring 

# configura a placa de  rede para conectar no wifi local
sta_if = network.WLAN(network.STA_IF)
sta_if.active(True)
sta_if.connect(ssid,password)
# espera se conectar para poder continuar
while sta_if.isconnected() == False:
  pass
# imprime a configuração atual 
print('network config:',sta_if.ifconfig() )
   
# starta o servidor socket 
_thread.start_new_thread(httpserver,("",80))

# entra em loop a espera de comandos a serem recebido pela thread do
# servidor. ao recebe um comando valido ele sinaliza via variável recebeu
# e na variável efeito indica o que deve ser feito.
while True:
    a = 1
    if recebeu == True:
        efeito3 = False
        if efeito == 0:
            drawArc(qtd, red, green,blue)
        if efeito == 1:
            cycle(12)          
        if efeito == 2:
            RAINBOW(48)
        if efeito == 3:
            efeito3 = True
        recebeu = False
        
    # esse efeito persite mesmo quando fechamos a conexão.
    if efeito3 == True: #anda o relogio
        if (ticks_diff(ticks_ms(), last1)> 1000):
            last1 = ticks_ms()
            cnt = cnt + 1
            if cnt == 12:
               cnt = 0
               
            relogio(cnt)   
        
       
        
