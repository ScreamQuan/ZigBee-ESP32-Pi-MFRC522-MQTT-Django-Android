package com.example.navigationview;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();*/
//
//            }
//        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView upho =header.findViewById(R.id.imageView);
        TextView adname = header.findViewById(R.id.adname);
        TextView adid = header.findViewById(R.id.adid);


        DatabaseHelper dbOpenHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from loginrecord order by id desc",null);
        cursor.moveToFirst();
        final String idd=cursor.getString(cursor.getColumnIndex("loginid"));
        adid.setText(idd);
        byte[] b = null;
        Bitmap image = null;

        Cursor cursor1=db.rawQuery("select * from adminuser where adminid=? ",new String[]{idd});
        if(cursor1.moveToFirst())
            b = cursor1.getBlob(cursor1.getColumnIndex("picture"));
            String c = cursor1.getString(cursor1.getColumnIndex("name"));
        if (b != null && b.length > 0) {
            image = BitmapFactory.decodeByteArray(b, 0, b.length);
            upho.setImageBitmap(image);
        }
        else
            upho.setImageDrawable(getResources().getDrawable(R.drawable.tab_mine_normal));
        if(c !=null &&c.length()>0){
           adname.setText(c);
        }
        else
            adname.setText("administrator");

            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            cursor.close();
            cursor1.close();
            db.close();
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_info)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController=Navigation.findNavController(this,R.id.nav_host_fragment);
        int id=item.getItemId();
//        if(id==R.id.action_settings)
//        {
//            //navController.navigate(R.id.nav_recyclerview);
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
