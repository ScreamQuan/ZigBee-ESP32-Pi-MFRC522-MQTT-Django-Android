#ifndef __SMARTHOME_H_
#define __SMARTHOME_H_
#define ENDPOINT_ID_SMARTHOME 0X01
#define PROFILE_ID_SMARTHOME 0X501
#define DEVICE_ID_COORDINATOR 0X01
#define DEVICE_ID_LEDDEVICE 0X02
#define DEVIDE_VERSION_ID 0X0

#define LEDJOINNET_CMD_ID 0X1
#define TOGGLE_LED_CMD_ID  0X2
#define HEART_BEAT_CMD_ID  0X3
#define TEMPERATURE_BEAT_CMD_ID  0X4
#define LIGHT_BEAT_CMD_ID 0X5


#define  HEART_BEAT_MAX_COUNT 6


#define TIMER_TIMEOUT_EVT 0X01
#define TEMPERATURE_TIMEOUT_EVT 0X04
#define LIGHT_TIMEOUT_EVT 0X03

#define LED_1     P1_0
#define LED_2     P1_1
#define LED_3     P1_4
#define buzzer    P0_0
#define Do        P1_5

extern void Led_Init(void);//初始化led
extern void Light_Init(void);//初始化光敏模块
extern void Buzzer_Init(void);//初始化蜂鸣器模块
extern unsigned char GetLight();
#endif