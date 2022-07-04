package com.example.navigationview;

import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.navigationview.ui.mqtt.MqttManager;
import com.lichfaker.log.Logger;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class SensorActivity extends AppCompatActivity {

    // public static final String URL = "tcp://183.230.40.39:6002";
    public static final String URL = "tcp://192.168.82.123:1883";
    private String userName = "android";
    private String password = "android";
    private String clientId = "1940707251wjx";

   //String cmdon = "1";
    //String cmdoff = "0";
    TextView txtWenDu;
    TextView txtShiDu;
    TextView txtGuangQiang;
    Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor);
        EventBus.getDefault().register(this);
        txtWenDu=(TextView) findViewById(R.id.vofTemp);
        txtShiDu= (TextView) findViewById(R.id.vofHumi);
        txtGuangQiang = (TextView) findViewById(R.id.vofIllu);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                boolean b = MqttManager.getInstance().creatConnect(URL, userName, password, clientId);
                Logger.d("isConnected: " + b);
                MqttManager.getInstance().subscribe("sensor", 0);

            }

        }).start();


        //MqttManager.getInstance().publish("feng", 0, cmdoff.getBytes());
    }

    /**
     * 订阅接收到的消息
     * 这里的Event类型可以根据需要自定义, 这里只做基础的演示
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
        public void showdata ( String event){

        Log.i("wjx", String.valueOf(event));
        String[] msg=event.split(",");
        //Log.i("www", String.valueOf(msg));
        if(msg[0].equals("sensor"))
        {
            String msg1=event.substring(7);
            Log.i("wjx", String.valueOf(msg1));
            JSONObject jsonObject = JSON.parseObject(msg1);
            String tem = jsonObject.getString("temp");
            String hum = jsonObject.getString("humi");
            String ill = jsonObject.getString("illu");
            txtGuangQiang.setText(ill);
            txtWenDu.setText(tem);
            txtShiDu.setText(hum);
        }

    };

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}

