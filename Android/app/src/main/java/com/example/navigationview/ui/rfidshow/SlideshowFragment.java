package com.example.navigationview.ui.rfidshow;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.navigationview.DatabaseHelper;
import com.example.navigationview.MyApplication;
import com.example.navigationview.R;
import com.example.navigationview.SwipeRecyclerView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    private SwipeRecyclerView slideshowview;
    private MyRecycleViewAdapter adapter;
    //
    private List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    private List<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();
    private int pageSize = 10;
    String url = new MyApplication().selectbypageurl;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        slideshowview = (SwipeRecyclerView) root.findViewById(R.id.swipeRecyclerView2);
        slideshowview.getSwipeRefreshLayout()
                .setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        slideshowview.getRecyclerView().setLayoutManager(new GridLayoutManager(getActivity(), 1));
        //recyclerView.getRecyclerView().setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter=new MyRecycleViewAdapter();
        slideshowview.setAdapter(adapter);
        slideshowview.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fetchRData();
                    }
                }, 1000);

            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fetchMData();
                    }
                }, 1000);
            }
        });
        slideshowview.setRefreshing(true);
        return root;
    }
    public void fetchMData() {
        RequestParams params = new RequestParams(url);
        //get
        params.addQueryStringParameter("table", String.valueOf(2));
        params.addQueryStringParameter("offset", String.valueOf(mData.size()));
        params.addQueryStringParameter("pagesize", String.valueOf(pageSize+mData.size()));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("SlideshowViewFragment", result);
                list = JSON.parseObject(result,
                        new TypeReference<List<HashMap<String, Object>>>() {
                        });
                //mData.clear();
                mData.addAll(list);
                slideshowview.complete();
                adapter.notifyDataSetChanged();
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

    public void fetchRData() {
        RequestParams params = new RequestParams(url);
        //get
        params.addQueryStringParameter("table", String.valueOf(2));
        params.addQueryStringParameter("offset", String.valueOf(0));
        params.addQueryStringParameter("pagesize", String.valueOf(pageSize));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("SlideshowViewFragment", result);
                list = JSON.parseObject(result,
                        new TypeReference<List<HashMap<String, Object>>>() {
                        });

                mData.clear();
                mData.addAll(list);
                slideshowview.complete();
                adapter.notifyDataSetChanged();
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
    class  MyRecycleViewAdapter extends RecyclerView.Adapter<MyRecycleViewAdapter.ViewHolder>
    {
        public  class ViewHolder extends RecyclerView.ViewHolder {
            public TextView admin;
            public TextView visittime;
            public ImageView picture;
            public TextView times;



            public ViewHolder(View convertView) {
                super(convertView);
                admin = convertView.findViewById(R.id.admin);
                visittime = convertView.findViewById(R.id.visittime);
                picture = convertView.findViewById(R.id.picture);
                times=convertView.findViewById(R.id.times);

            }
        }
        @NonNull
        @Override
        public MyRecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(getActivity()).inflate(R.layout.item2,parent, false);

            return new MyRecycleViewAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyRecycleViewAdapter.ViewHolder holder, final int position) {
            //picture
            //Glide.with(getActivity()).load(new MyApplication().
            // imagebaseurl+mData.get(position).get("picture").toString()).
            // placeholder(R.mipmap.ic_launcher).into(holder.picture);
            DatabaseHelper dbOpenHelper = new DatabaseHelper(getActivity());
            SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

            byte[] b = null;
            Bitmap image = null;
            Cursor cursor1=db.rawQuery("select * from adminuser where uid=? ",new String[]{(String)mData.get(position).get("uid")});
            if(cursor1.moveToFirst())
                b = cursor1.getBlob(cursor1.getColumnIndex("picture"));
                if (b != null && b.length > 0) {
                    image = BitmapFactory.decodeByteArray(b, 0, b.length);
                    holder.picture.setImageBitmap(image);
                }
            else
                holder.picture.setImageDrawable(getResources().getDrawable(R.drawable.tab_mine_normal));
            holder.admin.setText((String)mData.get(position).get("uid"));
            holder.visittime.setText((String)mData.get(position).get("time"));
            holder.times.setText((String)mData.get(position).get("times"));
            cursor1.close();
            db.close();

        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }
    }
}
