# 为了能在外部脚本中调用Django ORM模型，必须配置脚本环境变量，将脚本注册到Django的环境变量中
import os, sys
from re import A
import django
# 第一个参数固定，第二个参数是工程名称.settings
os.environ.setdefault('DJANGO_SETTING_MODULE', 'web.settings')
django.setup()

# 引入mqtt包
import paho.mqtt.client as mqtt
# 使用独立线程运行
from threading import Thread

import time
import json

from dataInfo.models import dataInfo,rfidInfo
# 建立mqtt连接
def on_connect(client, userdata, flag, rc):
    #print("Connect with the result code " + str(rc))
    client.subscribe('sensor', qos=0)
    client.subscribe('rfid', qos=0)
    #client.subscribe('')
# 接收、处理mqtt消息
#{ "temp": "12","humi":"22","illu":"34" }
def on_message(client, userdata, msg):
    #print(msg)
    out = str(msg.payload.decode('utf-8'))
    #print(out)
    #out = json.loads(out)
    #print(out)
    # 收到消息后执行任务
    if msg.topic == 'sensor' and is_json(out):
        out = json.loads(out)
        print("temperature:"+out["temp"]+",humidity:"+out["humi"]+",illumination:"+out["illu"])
        dataInfo.objects.create(temperature=out["temp"],humidity=out["humi"],illumination=out["illu"])
        #publish_message('1','123')
    if msg.topic == 'rfid':
        out = json.loads(out)
        print("uid:"+out["uid"]+",time:"+out["time"]+",times:"+out["times"])
        rfidInfo.objects.create(uid=out["uid"],time=out["time"],times=out["times"])
        
        

# mqtt客户端启动函数
def mqttfunction():
    global client
    # 使用loop_start 可以避免阻塞Django进程，使用loop_forever()可能会阻塞系统进程
    # client.loop_start()
    # client.loop_forever() 有掉线重连功能
    client.loop_forever(retry_first_connection=True)

client = mqtt.Client(client_id="test", clean_session=False)
def publish_message(topic, msg):
    """
    发送mqtt消息
    :param topic: 主题
    :param msg: 消息内容
    :return: None
    """
    #client = mqtt.Client('t') 
    #client.connect(HOST, PORT, 60)  # 用做连接
    #client.username_pw_set('admin', 'password')  # 用户名账号密码
    client.publish(topic, msg, 1)
# 启动函数
def mqtt_run():
    client.on_connect = on_connect
    client.on_message = on_message
    # 绑定 MQTT 服务器地址
    broker = '127.0.0.1'
    # MQTT服务器的端口号
    client.connect(broker, 1883, 62)
    client.username_pw_set('django', 'django')
    client.reconnect_delay_set(min_delay=1, max_delay=2000)
    # 启动
    mqttthread = Thread(target=mqttfunction)
    mqttthread.start()

def is_json(myjson):
    try:
        json_object = json.loads(myjson)
    except ValueError:
        return False
    return True

# 启动 MQTT
# mqtt_run()
if __name__ == "__main__":
    mqtt_run()

