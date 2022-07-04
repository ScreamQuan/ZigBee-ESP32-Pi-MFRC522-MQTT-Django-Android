#include "smartHome.h"
#include"OnBoard.h"
#include "sapi.h"
#include "hal_led.h"
#include "ds18b20.h"
#include "hal_adc.h"
#include "hal_sleep.h"




#define NUM_IN_CMD_LEDDEVICE 1
#define NUM_OUT_CMD_LEDDEVICE 3
const cId_t ledDeviceInputCommandList[NUM_IN_CMD_LEDDEVICE]=
                                {TOGGLE_LED_CMD_ID};
const cId_t ledDeviceOutputCommandList[NUM_OUT_CMD_LEDDEVICE]=
                                {LEDJOINNET_CMD_ID,HEART_BEAT_CMD_ID,TEMPERATURE_BEAT_CMD_ID};
const SimpleDescriptionFormat_t zb_SimpleDesc=
{
  ENDPOINT_ID_SMARTHOME,
  PROFILE_ID_SMARTHOME,
  DEVICE_ID_LEDDEVICE,
  DEVIDE_VERSION_ID,
  0,
  NUM_IN_CMD_LEDDEVICE,
  (cId_t*)ledDeviceInputCommandList,
  NUM_OUT_CMD_LEDDEVICE,
  (cId_t*)ledDeviceOutputCommandList  
};
/***********
执行时机：发送的数据包被接收方收到时被调用
handle:包的编号；
status:ZSUCCESS表示成功接收
************/

void Led_Init()
{
 P1SEL |= 0XEC;
 P1DIR |= 0X13;
}

void Light_Init()
{
  P1SEL &= ~0x20;                 //设置P1.5为普通IO口
  P1DIR &= ~0x20;                 //P1.5定义为输入口
}

void Buzzer_Init()
{
  P0SEL &= ~0x01;                 //设置P07为普通IO口
  P0DIR |= 0x01;                 //P07定义为输出口

}

void Delay_ms(uint8 k)
{
   MicroWait(k);//毫秒延时
}

uint8 GetLight()//光强检测
{
    uint8 light=0;//百分比的整数值
    float vol=0.0; //adc采样电压  
    uint16 adc= HalAdcRead(HAL_ADC_CHANNEL_7, HAL_ADC_RESOLUTION_14); //ADC 采样值 P06口
    //最大采样值8192(因为最高位是符号位)
    if(adc>=8192)
    {
        return 0;
    }
    adc=8192-adc;//反相一下，因为低湿度时AO口输出较高电平
                   //湿度时AO口输出较低电平   
    //转化为百分比
    vol=(float)((float)adc)/8192.0;
    //取百分比两位数字
    light=vol*100;
    //light=adc;
    return light;
}
void zb_SendDataConfirm( uint8 handle, uint8 status )
{
  
}

/***********
执行时机：接收到的数据包被调用
************/
void zb_ReceiveDataIndication( uint16 source, uint16 command, 
                              uint16 len, uint8 *pData  )
{
  if(command==TOGGLE_LED_CMD_ID)
  {
   
    uint8 flag[2];
          flag[0] = pData[0];
          flag[1] = pData[1];
    if(flag[0]==0x30)
    {
      if(flag[1]==0x31)
        LED_3 = 1;
      else
        LED_3 = 0;
     
    }
    else if(flag[0]==0x31)
    {
       if(flag[1]==0x31)
        LED_3 = 1;
      else
        LED_3 = 0;  
    }
  }
}


void zb_AllowBindConfirm( uint16 source )
{
}

void zb_HandleKeys( uint8 shift, uint8 keys )
{
  
}

void zb_BindConfirm( uint16 commandId, uint8 status )
{
}


//void zb_SendDataRequest ( uint16 destination, uint16 commandId, uint8 len,
//                          uint8 *pData, uint8 handle, uint8 ack, uint8 radius );
void zb_StartConfirm( uint8 status )
{
  
  if(status==ZSUCCESS)
  {
    Led_Init();//初始化led
    Ds18b20Initial();//初始化温度
    uint8 flag[1];//标志
    flag[0] = 0X30;//节点一标志
    //可把节点所包含的led灯的ID号发送过去
    zb_SendDataRequest(0X0,LEDJOINNET_CMD_ID,
                       1,flag,0,FALSE,AF_DEFAULT_RADIUS);
    osal_start_timerEx(sapi_TaskID,TIMER_TIMEOUT_EVT,2000);
    osal_start_timerEx(sapi_TaskID,TEMPERATURE_TIMEOUT_EVT,1000);
  }
}

void zb_HandleOsalEvent( uint16 event )
{
  if(event&TIMER_TIMEOUT_EVT)//心跳包事件
  {
    osal_start_timerEx(sapi_TaskID,TIMER_TIMEOUT_EVT,2000);
    zb_SendDataRequest(0X0,HEART_BEAT_CMD_ID,
                       0,NULL,0,FALSE,AF_DEFAULT_RADIUS); 
  }
  
  if(event&TEMPERATURE_TIMEOUT_EVT)//温度事件
  {
    uint8 T[6];    //温度+提示符     
    Temp_test();   //温度检测    
    T[0]=temp/10+48;
    T[1]=temp%10+48;
    T[2]='\r';
    T[3]=GetLight()/10+0X30;//光强检测
    T[4]=GetLight()%10+0X30;
    T[5]='\0';
    osal_start_timerEx(sapi_TaskID,TEMPERATURE_TIMEOUT_EVT,1000);
    zb_SendDataRequest(0X0,TEMPERATURE_BEAT_CMD_ID ,
                       6,T,0,FALSE,AF_DEFAULT_RADIUS); 
     if(GetLight()> 80)
    {
      buzzer = 1;
      Delay_ms(100);
      
    }
    else
    {
      buzzer = 0;
    }
  }
 
  }


void zb_FindDeviceConfirm( uint8 searchType, 
                          uint8 *searchKey, uint8 *result )
{
  
}