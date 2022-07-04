
from machine import Pin
from time import sleep
import network
import time
#import recv
import machine#该模块中提供了reset方法重启板子
import _thread#该模块中提供了start_new_thread创建线程
from machine import Pin,Timer,PWM
import ujson
from machine import UART
import umqtt.simple as mqtt
gp_buzzer = 27  #蜂鸣器管脚定义
buzzer = Pin(gp_buzzer, Pin.OUT) #设置蜂鸣器GPIO口为输入模式
wlan=None #在python中，None表示一个空对象，类似与c语言中的NULL
client=None
ssid='tink'
password='1357924666'
server='192.168.82.123'
clientid='esp32'
state = 0
u = UART(2, baudrate=115200, bits=8, parity=0, rx=22, tx=23, timeout=10)
led=Pin(2, Pin.OUT, value=0)
set = 40

def recvMessage(topic, msg):
  global state
  global led
  global set
  print((topic, msg))
  if topic == b"ring":
    msg = ujson.loads(msg)
    if msg["ring"] == "on":
      led.value(1)
      buzzer.value(1)  #设置为高电平
      #sleep(1)         #延
      #u.write(b'05')
      state = 1
      #print("1") 
      #pwm =PWM(Pin(13,Pin.OUT),duty=1000,fre=10)
    elif msg["ring"] == "off":
      led.value(0)
      buzzer.value(0)  #设置为低电平
      #sleep(1)       #延时
      state = 0
      print("")
      #pwm =PWM(Pin(13),duty=0)
  elif topic== b"set":
      msg1 = ujson.loads(msg)
      set=msg1["set"]

def ConnectWifi():
  global wlan#在函数中，经global修饰的外部变量才允许被修改
  wlan=network.WLAN(network.STA_IF)
  wlan.active(True)
  wlan.connect(ssid,password)
  #利用while循环来等待连上wifi
  while wlan.isconnected() == False:
    time.sleep(2)
  print('Connected to wifi successfully!')

def ConnectMqtt():
  global client
  client=mqtt.MQTTClient(clientid,server,0,'test','test123')
  client.connect()
  print('Connected to mqtt successfully!')

def receiveMessage():
  #try...except...左右时捕获异常
  print("receiveMessage running")
  try:
    while True:
	  #阻塞等待订阅消息
      client.wait_msg()


  except:
    #当出现异常时延迟2秒的目的是不要让板子频繁启动
    time.sleep(2)
	#复位
    machine.reset()
def threadPublish():
  print("threadPublish running")
  #只有通过global声明的外部变量才允许在函数体中修改
  #global count
  global set
  global state
  try:
    while True:
      if(u.any()): 
        publishMessage= u.readline() 
        print(publishMessage)
        client.publish('sensor',publishMessage)
        jsobj = ujson.loads(publishMessage)
        temp= jsobj["temp"]      
        if int(temp)>int(set):
          led.value(1)
          buzzer.value(1)  #设置为高电平
          sleep(1)         #延
        elif state==0:
          led.value(0)
          buzzer.value(0)  #设置为高电平
          sleep(1) 
  except:#当捕获到异常时重启板子
    time.sleep(2)#出现异常时通过延时让板子不要频繁启动
    machine.reset()

def main():  
  ConnectWifi()  
  try:
    ConnectMqtt()
    client.set_callback(recvMessage)
    client.subscribe('ring')
    client.subscribe('set')
  except:
    print("mqtt connect except")
    time.sleep(2)
    machine.reset()
  #开启一个新的线程用来负责接收订阅消息，这样主线程可以专心做其他的工作
  #receiveMessage()
  _thread.start_new_thread(receiveMessage,())
  print("new thread:receiveMessage")
  _thread.start_new_thread(threadPublish,())
  print("new thread:threadPublish")
  
main()













>>> b'{"humi":"53","temp":"27","illu":"00"}\r\n'


