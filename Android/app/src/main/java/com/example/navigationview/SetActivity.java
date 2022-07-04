package com.example.navigationview;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.navigationview.ui.mqtt.MqttManager;
import com.lichfaker.log.Logger;

import org.greenrobot.eventbus.EventBus;

public class SetActivity extends AppCompatActivity {
    private TextView textView;
    private SeekBar seekBar;
    private Button ring,ringoff;
    public static final String URL = "tcp://192.168.82.123:1883";
    private String userName = "android";
    private String password = "android";
    private String clientId = "1940707251wjx";
    public String set;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.set);

        textView = (TextView) findViewById(R.id.tv1);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        ring = (Button)findViewById(R.id.ring);
        ringoff = (Button)findViewById(R.id.ringoff);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
                boolean b = MqttManager.getInstance().creatConnect(URL, userName, password, clientId);
                Logger.d("isConnected: " + b);
                MqttManager.getInstance().subscribe("set", 0);

            }

        }).start();
        /* 设置SeekBar 监听setOnSeekBarChangeListener */
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /*拖动条停止拖动时调用 */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("SeekBarActivity", "拖动停止");
                AlertDialog.Builder builder=new AlertDialog.Builder(SetActivity.this);
                builder.setMessage("确定要更改温度阈值吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String message="{\"set\":\""+set+"\"}";
                        MqttManager.getInstance().publish("set", 0, message.getBytes());

                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dialog.dismiss();
                    }
                });
                builder.create().show();
            }
            /*拖动条开始拖动时调用*/
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("SeekBarActivity", "开始拖动");
            }
            /* 拖动条进度改变时调用*/
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                textView.setText("设置温度阈值为：" + progress + "°C");

                String message=String.valueOf(progress);
                set=message;
                //String message="{\"set\":\""+progress+"\"}";

            }
        });
        ring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String on ="{\"ring\":\"on\"}";
                MqttManager.getInstance().publish("ring", 0, on.getBytes());
                MqttManager.getInstance().publish("ring", 0, on.getBytes());
            }
        });
        ringoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String off = "{\"ring\":\"off\"}";
                MqttManager.getInstance().publish("ring", 0, off.getBytes());
            }
        });


    }


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
