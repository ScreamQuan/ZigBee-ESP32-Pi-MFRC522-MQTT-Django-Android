package com.example.navigationview;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {
    TextView user,pwd;
    Button loginbutton,registerbutton;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        DatabaseHelper dbOpenHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        db.close();
        user=findViewById(R.id.AdminId);
        pwd=findViewById(R.id.AdminPwd);
        loginbutton=findViewById(R.id.Log);
        registerbutton=findViewById(R.id.Reg);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag=0;
                DatabaseHelper dbOpenHelper = new DatabaseHelper(LoginActivity.this);
                SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
                String inputuser = user.getText().toString();
                String inputpwd = pwd.getText().toString();
                Log.i("wx",inputpwd);
                Cursor cursor = db.rawQuery("select * from adminuser where adminid=?",
                        new String[] { inputuser });
                String pa=null;
                while (cursor.moveToNext()){
                    flag=1;
                    pa=cursor.getString(cursor.getColumnIndex("adminpwd"));
                    Log.i("wx",pa);
                }
                if(flag==0){
                    Toast.makeText(LoginActivity.this,"账号不存在，请注册!",Toast.LENGTH_SHORT).show();
//                    Intent intent=new Intent(Login.this,MainActivity.class);
//                    startActivity(intent);
                }

                if(flag==1){
                    if(inputpwd.equals(pa)){
                        ContentValues cv = new ContentValues();
                        cv.put("loginid",inputuser);
                        long id=db.insert("loginrecord", null, cv);
                        if(id>0) {
                            Log.i("info","已记录登录账号");
                        }
                        Toast.makeText(LoginActivity.this,"登录成功！",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(LoginActivity.this,"密码错误,请重新输入！",Toast.LENGTH_SHORT).show();
                        pwd.setText("");
                    }
                }
                cursor.close();
                db.close();

            }
        });
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText editText=new EditText(LoginActivity.this);
                editText.setHint("请输入权限密码：");
                editText.setTextSize(20);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                new AlertDialog.Builder(LoginActivity.this).setTitle("请输入权限密码").setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int key=123;
                                int key2=Integer.parseInt(editText.getText().toString());
                                DatabaseHelper dbOpenHelper = new DatabaseHelper(LoginActivity.this);
                                SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
                                if(key==key2){
                                    String inputuser=user.getText().toString();
                                    String inputpwd=pwd.getText().toString();
                                    String sql="select * from adminuser where adminid=?";
                                    Cursor cursor=db.rawQuery(sql,new String[]{inputuser});
                                    while (cursor.moveToNext()){
                                        Toast.makeText(LoginActivity.this,"账号已存在",Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    //String sql2="insert into adminuser values('"  +inputuser+"','"+inputpwd+"')";
                                    ContentValues cv = new ContentValues();
                                    cv.put("adminid", inputuser);
                                    cv.put("adminpwd", inputpwd);
                                    //cv.put("picture", null);
                                    long id=db.insert("adminuser", null, cv);
                                    if(id>0) {
                                        Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                    }
                                    //db.execSQL(sql2);
                                    cursor.close();
                                    db.close();
                                }else{
                                    Toast.makeText(LoginActivity.this,"权限密码错误，注册失败",Toast.LENGTH_LONG).show();
                                }
                            }

                        })
                        .setNegativeButton("取消",null).show();



            }
        });

    }

}
