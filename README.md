# Monitoring-System-of-Computer-Lab

&ensp;&ensp;&ensp;&ensp;该项目针对大型机房的物理运行环境状况、设备运行情况、人员活动情况以及消防问题等危机无法得到及时发现和处理，很难有效地提前预见、防范和避免的问题，实现对机房环境以及设备维修记录的监控。

# Environment Configuration

* Visual Studio Code：1.63.2
  * Python：3.7.0
    * paho-mqtt：1.6.1
    * Django：3.2.13
    * djangorestframework：3.13.1
    * mysqlclient：2.1.0
    
* Android Studio：3.6.1
  * Android SDK：Android SDK Platform 28 （Android 9.0，Pie）
  * Gradle dependencies（Not All）：
    * fastjson：1.2.5
    * org.xutils:xutils：3.3.40
    * org.eclipse.paho.android.service：1.1.1
    * org.eclipse.paho.client.mqttv3：1.1.1
    * org.eclipse.paho.client.mqttv：3-1.0.2
    * eventbus：3.0.0
    
* IAR：8.10.1 (IAR Assembler for 8051)

* uPycraft：v1.1

* Python on Pi：3.7.0
  * Important module：
    * python-spidev 
    * MFRC522

# System Block diagram

&ensp;&ensp;&ensp;&ensp;该系统总体设计主要通过感知层、网络层和应用层三部分实现，其中硬件设计主要在感知层，软件设计在三层体系中均有体现，主要是在应用层中进行设计。  

&ensp;&ensp;&ensp;&ensp;感知层中数据的获取：一方面负责收集机房的传感器数据以及控制报警灯闪烁的ZigBee终端节点同协调器组建ZigBee主从网络，通过外接天线进行数据交互，并最终由协调器通过串口发送给ESP32；另一方面树莓派连接MFRC522感应模块，通过RFID通信读取维修员卡号，记录打卡次数写入卡中。最后都是通过MQTT协议传输采集的数据以及发送的消息。  

&ensp;&ensp;&ensp;&ensp;网络层中，由ESP32和树莓派将获取到的数据通过MQTT消息传输协议在主题发布消息到由借助EMQX搭建的MQTT服务器，同时在由Django搭建的 web服务器通过MQTT客户端订阅主题获取MQTT服务器上的数据并保存到MySQL数据库中。  

&ensp;&ensp;&ensp;&ensp;在应用层，安卓移动设备上可以通过APP开启MQTT传输订阅主题，能实时获取到底层传感器数据，同时可以在APP上通过MQTT传输发布设定的温度阈值或者拉响蜂鸣器警报消息。除此之外，还可以通过http向web服务器发送GET和POST请求，获取到存储于MySQL数据库中的机房环境历史数据以及机房管理员的维护检查机房设备的历史记录。  
<div align=center>
<img src="https://user-images.githubusercontent.com/83326493/177072051-09bd3da2-2bb9-42bd-b121-b0c8b71c6269.png">
</div>


# How To Use

1.确保ZigBee、ESP32、树莓派正常运行程序，能够接受到传感器数据并将数据通过主题订阅，以消息的形式发送到MQTT服务器；

2.运行DJango Web服务，为确保安卓应用能访问到服务器，使用以下指令：  

    `python manage.py runserver 0.0.0.0:端口号`
 
3.启动安卓APP，观察现象。

# Reference contribution

SPI-PY：https://github.com/lthiery/SPI-Py.git

MFRC522：https://github.com/kangaroo711/MFRC522-python.git
