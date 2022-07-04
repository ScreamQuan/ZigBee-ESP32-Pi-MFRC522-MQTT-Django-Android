package com.example.navigationview;

        import android.app.Application;

        import org.xutils.x;

public class MyApplication extends Application {

    public String selectbypageurl="http://192.168.82.123:8080/API/v1.0/moniter/";
    //public String selectbypageurl2="http://172.31.130.210:8080/show2/";
    //public String addurl="http://172.31.130.210:8080/add/";

    //public String showurl="http://172.31.130.210:8080/show3/";
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        //x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }
}
