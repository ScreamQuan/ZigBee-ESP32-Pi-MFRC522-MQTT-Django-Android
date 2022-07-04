#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import RPi.GPIO as GPIO
import MFRC522
import time
import smbus
import sys
import json
import datetime
import RPi.GPIO as GPIO
import paho.mqtt.client as mqtt
HOST = "192.168.82.123"
PORT = 1883
class My1602(object):
    BUS = smbus.SMBus(1)
    LCD_ADDR = 0x3f
    BLEN = 1

    # '''
    # 开关灯
    def turn_light(self, key):
        self.BLEN = key
        if key == 1:
            self.BUS.write_byte(self.LCD_ADDR, 0x08)
        else:
            self.BUS.write_byte(self.LCD_ADDR, 0x00)
    # '''

    def write_word(self, addr, data):
        temp = data
        if self.BLEN == 1:
            temp |= 0x08
        else:
            temp &= 0xF7
        self.BUS.write_byte(addr, temp)

	# 写命令
    def send_command(self, comm):
        # 发送7-4位数据
        buf = comm & 0xF0
        buf |= 0x04  # RS = 0, RW = 0, EN = 1
        self.write_word(self.LCD_ADDR, buf)
        time.sleep(0.002)
        buf &= 0xFB
        self.write_word(self.LCD_ADDR, buf)

        # 发送3-0位数据
        buf = (comm & 0x0F) << 4
        buf |= 0x04  # RS = 0, RW = 0, EN = 1
        self.write_word(self.LCD_ADDR, buf)
        time.sleep(0.002)
        buf &= 0xFB
        self.write_word(self.LCD_ADDR, buf)

	# 写数据
    def send_data(self, data):
        # 发送7-4位数据
        buf = data & 0xF0
        buf |= 0x05  # RS = 1, RW = 0, EN = 1
        self.write_word(self.LCD_ADDR, buf)
        time.sleep(0.002)
        buf &= 0xFB
        self.write_word(self.LCD_ADDR, buf)

        # 发送3-0位数据
        buf = (data & 0x0F) << 4
        buf |= 0x05  # RS = 1, RW = 0, EN = 1
        self.write_word(self.LCD_ADDR, buf)
        time.sleep(0.002)
        buf &= 0xFB
        self.write_word(self.LCD_ADDR, buf)
	
	# 初始化
    def __init__(self):
        try:
            self.send_command(0x33)
            time.sleep(0.005)
            self.send_command(0x32)
            time.sleep(0.005)
            self.send_command(0x28)
            time.sleep(0.005)
            self.send_command(0x0C)
            time.sleep(0.005)
            self.send_command(0x01)
            self.BUS.write_byte(self.LCD_ADDR, 0x08)
        except:
            return None
        else:
            return None

	# 清屏
    def clear_lcd(self):
        self.send_command(0x01)  # 清屏

	# 显示字符
    def print_lcd(self, x, y, str):
        if x < 0:
            x = 0
        if x > 15:
            x = 15
        if y < 0:
            y = 0
        if y > 1:
            y = 1

        addr = 0x80 + 0x40 * y + x
        self.send_command(addr)

        for chr in str:
            self.send_data(ord(chr))

def publish_message(topic, msg):
    
    client = mqtt.Client('t')
    client.connect(HOST, PORT, 60)  # 用做连接
    client.username_pw_set('pi', 'pi')  # 用户名账号密码
    client.publish(topic, msg, 1)

if __name__ == '__main__':
    my1602 = My1602()
    MIFAREReader = MFRC522.MFRC522()
    GPIO.setwarnings(False)
    try:
        while True:
            # Scan for cards    
            (status,TagType) = MIFAREReader.MFRC522_Request(MIFAREReader.PICC_REQIDL)
            current_time = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time()))
            my1602.print_lcd(0, 1, current_time)
            my1602.print_lcd(0, 0,'check entrance')

            # If a card is found
            if status == MIFAREReader.MI_OK:
                print("Card detected")
    
                # Get the UID of the card
                (status,uid) = MIFAREReader.MFRC522_Anticoll()
                # Print UID
                print ("Card read UID: %s,%s,%s,%s" % (uid[0], uid[1], uid[2], uid[3]))
    
                # This is the default key for authentication
                key = [0xFF,0xFF,0xFF,0xFF,0xFF,0xFF]
        
                # Select the scanned tag
                MIFAREReader.MFRC522_SelectTag(uid)

                # Authenticate
                status = MIFAREReader.MFRC522_Auth(MIFAREReader.PICC_AUTHENT1A, 8, key, uid)
      
                
                # Variable for the data to write
                data = []
                v=1  
                
                
                a=MIFAREReader.MFRC522_Read(8)
                
                for y in range(0,a[0]):
                    v=v+1
    
                data.append(int(format(v, '#04x'),16))
                
                #Fill the data with 0x00
                for x in range(1,16):
                    data.append(0x00)
                
                # Write the data
                MIFAREReader.MFRC522_Write(8, data)
                print("Clock "+str(v)+" times successfully!")

                message="{\"uid\":\""+str(uid[0])+","+str(uid[1])+","+str(uid[2])+","+str(uid[3])+"\",\"time\":"+"\""+current_time+"\",\"times\":\""+str(v)+"\"}"
                print(message)
                publish_message("rfid",message)
                show1='Finish! time:'+str(v)
                my1602.print_lcd(0, 0,show1 )
                time.sleep(2)
                my1602.clear_lcd()
                # Stop
                MIFAREReader.MFRC522_StopCrypto1()
    except KeyboardInterrupt:
        GPIO.cleanup()
