package com.example.navigationview.ui.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.navigationview.SensorActivity;
import com.example.navigationview.MyApplication;
import com.example.navigationview.R;
import com.example.navigationview.SetActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GalleryFragment extends Fragment  {
    private List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    public List<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();
    Button b1,b2,b3;
    String url = new MyApplication().selectbypageurl;
    private GalleryViewModel galleryViewModel;
    Handler handler=new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        Button b1=root.findViewById(R.id.showdata);
        Button b2=root.findViewById(R.id.setthreshold);
        Button b3=root.findViewById(R.id.about);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), SensorActivity.class);
                startActivity(intent);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent2=new Intent(getActivity(), SetActivity.class);
                    startActivity(intent2);
                }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "该APP主要是通过对机房环境的远程实时监控以" +
                        "及设置报警阈值等功能来保证机房设备处于一个较佳的运行环境，同时确保设备的正" +
                        "常运作。", Toast.LENGTH_LONG).show();
            }
        });


        return root; }








    }
