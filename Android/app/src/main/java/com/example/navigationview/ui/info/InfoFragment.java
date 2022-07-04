package com.example.navigationview.ui.info;

import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.IDNA;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.navigationview.DatabaseHelper;
import com.example.navigationview.LoginActivity;
import com.example.navigationview.MyApplication;
import com.example.navigationview.R;
import com.linchaolong.android.imagepicker.ImagePicker;

import org.w3c.dom.Text;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InfoFragment extends Fragment {

    private InfoViewModel mViewModel;
    Handler handler=new Handler();
    public static InfoFragment newInstance() {
        return new InfoFragment();
    }

    private List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    public List<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();
    public List<HashMap<String, Object>> list2 = new ArrayList<HashMap<String, Object>>();
    TextView usid,usuid,uspwd,name;
    Button save;
    ImageView photo;
    byte[] photobytes;
    String addurl = new MyApplication().selectbypageurl;
    String showurl = new MyApplication().selectbypageurl;

    //TextView usid,uspwd,name;
    private ImagePicker imagePicker = new ImagePicker();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //id.setText();
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(InfoViewModel.class);
        // TODO: Use the ViewModel
        usid=getActivity().findViewById(R.id.userid);
        usuid=getActivity().findViewById(R.id.UID);
        uspwd=getActivity().findViewById(R.id.PWD);
        name=getActivity().findViewById(R.id.NAME);
        photo = getActivity().findViewById(R.id.photo);
        save = getActivity().findViewById(R.id.SAVE);
        photo = getActivity().findViewById(R.id.photo);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraOrGallery();
            }
        });
        DatabaseHelper dbOpenHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from loginrecord order by id desc",null);
        cursor.moveToFirst();
        final String idd=cursor.getString(cursor.getColumnIndex("loginid"));
        Log.i("wjx",idd);
        Cursor cursor1=db.rawQuery("select * from adminuser where adminid=? ",new String[]{idd});
        cursor1.moveToFirst();
        //Log.i("wjx",cursor1.getString(cursor1.getColumnIndex("adminid")));
//        String iid = cursor1.getString(cursor1.getColumnIndex("adminid"));
//        String ipwd = cursor1.getString(cursor1.getColumnIndex("adminpwd"));
//        String iuid = cursor1.getString(cursor1.getColumnIndex("uid"));
//        String uname = cursor1.getString(cursor1.getColumnIndex("name"));
//        cursor.close();
//        db.close();
//        usid.setText(iid);
//        uspwd.setText(ipwd);
//        usuid.setText(iuid);
//        name.setText(uname);
//        //Log.i("wjx",picture);
        usid.setText(idd);
        byte[] b = null;
        Bitmap image = null;
        b = cursor1.getBlob(cursor1.getColumnIndex("picture"));
        if (b != null && b.length > 0) {
            image = BitmapFactory.decodeByteArray(b, 0, b.length);
            photo.setImageBitmap(image);
        }
        else {
            photo.setImageDrawable(getResources().getDrawable(R.drawable.tab_mine_normal));
        }
        cursor.close();
        cursor1.close();
        db.close();
        //photo.setImageAlpha(R.mipmap.ic_launcher);
        new Thread(new Runnable() {
            @Override
            public void run() {
                postDataInfo(idd);
            }
        }).start();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper dbOpenHelper = new DatabaseHelper(getActivity());
                SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
                String i = usid.getText().toString();
                String a = usuid.getText().toString();
                String b = uspwd.getText().toString();
                String c = name.getText().toString();
                String e = usid.getText().toString();
                if (a.trim().isEmpty()) {
                    //Log.i("wjx", String.valueOf(a));
                    Toast.makeText(getActivity(), "uid卡号是必填项，请填写完整！", Toast.LENGTH_SHORT).show();
                }
                else{
                    ContentValues cv = new ContentValues();
                    cv.put("uid", a);
                    cv.put("adminpwd", b);
                    cv.put("name", c);
                    cv.put("picture", photobytes);
                    long id = db.update("adminuser", cv, "adminid=?", new String[]{e});
                    if (id > 0) {
                        Toast.makeText(getActivity(), "更新个人信息成功！", Toast.LENGTH_LONG).show();
                    }
                    db.close();

                    RequestParams params = new RequestParams(addurl);
                    //post
                    params.setMultipart(true);
                    params.addBodyParameter("adminid", i);
                    Log.i("wjx", i);
                    params.addBodyParameter("adminpwd", b);
                    params.addBodyParameter("adminuid", a);
                    params.addBodyParameter("adminname", c);
    //                byte[] b2 = photobytes;
    //                if (b2 != null && b2.length > 0) {
    //                    Bitmap bp = BitmapFactory.decodeByteArray(b2, 0, b2.length);
    //                    params.addBodyParameter("adminphoto", bp.toString());
    //                    Log.i("wjx", bp.toString());
    //                }

                    final ProgressDialog dia = new ProgressDialog(getActivity());
                    dia.setMessage("上传中....");
                    dia.show();
                    x.http().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            //Toast.makeText(x.app(), result, Toast.LENGTH_LONG).show();
                            //加载成功回调，返回获取到的数据
                            //Log.i("cjf", "onSuccess: " + result);
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Toast.makeText(x.app(), ex.toString(), Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                            Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFinished() {
                            dia.dismiss();//加载完成
                        }
                    });
            }
            }
        });
    }

    public void postDataInfo(String a) {

        RequestParams params = new RequestParams(showurl);
        //get
        params.addQueryStringParameter("table",String.valueOf(3));
        params.addQueryStringParameter("adminid", a);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //Log.i("wjx", result);
                list = JSON.parseObject(result,
                        new TypeReference<List<HashMap<String, Object>>>() {
                        });
                //mData.clear();
                //Log.i("wjx", String.valueOf(list));
                //mData.addAll(list);
                String iid = (String)list.get(0).get("adminid");
                String ipwd = (String)list.get(0).get("adminpwd");
                String iuid = (String)list.get(0).get("adminuid");
                String uname = (String)list.get(0).get("adminname");
                //String pho = (String)list.get(0).get("adminphoto");
                //Log.i("wjx","uname:"+(String)list.get(0).get("adminphoto"));
                //usid.setText(iid);
                uspwd.setText(ipwd);
                usuid.setText(iuid);
                name.setText(uname);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    //picture
    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePicker.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
    private void startCameraOrGallery() {
        new AlertDialog.Builder(getActivity()).setTitle("设置图片")
                .setItems(new String[] { "从相册中选取图片", "拍照" }, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        // 回调
                        ImagePicker.Callback callback = new ImagePicker.Callback() {
                            @Override public void onPickImage(Uri imageUri) {
                            }

                            @Override public void onCropImage(Uri imageUri) {
                                //picture.setImageURI(imageUri);
                                Glide.with(getActivity()).load(new File(imageUri.getPath())).into(photo);
                                //fileurl=imageUri.getPath();
                                Glide.with(getActivity()).load(new File(imageUri.getPath())).asBitmap().into(new SimpleTarget<Bitmap>(100, 100) {
                                    @Override
                                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                                        //savedb
                                        photobytes = stream.toByteArray();
                                    }
                                });

                            }
                        };
                        if (which == 0) {
                            // 从相册中选取图片
                            imagePicker.startGallery(InfoFragment.this, callback);
                        } else {
                            // 拍照
                            imagePicker.startCamera(InfoFragment.this, callback);
                        }
                    }
                })
                .show()
                .getWindow()
                .setGravity(Gravity.CENTER);
    }


}

