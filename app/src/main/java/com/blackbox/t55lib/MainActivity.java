package com.blackbox.t55lib;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbox.model.LocationDataModel;
import com.blackbox.myt55library.MarkWaypoints;
import com.blackbox.myt55library.NewActivity;
import com.blackbox.t55.BluetoothGPSService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    LocationDataModel locationDataModel;
    MarkWaypoints newActivity;
    double longitude;
    TextView txt,tv_utc_time,tv_latitude,tv_longitude,tv_height,tv_altitude,tv_hdop,tv_satelite_used
            ,tv_accurancy_h,tv_accurancy_v,tv_vdop,tv_pdop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_utc_time = (TextView)findViewById(R.id.tv_utc_time);
        tv_latitude = (TextView)findViewById(R.id.tv_latitude);
        tv_longitude = (TextView)findViewById(R.id.tv_longitude);
        tv_height = (TextView)findViewById(R.id.tv_speed);
        tv_altitude = (TextView)findViewById(R.id.tv_altitude);
        tv_hdop = (TextView)findViewById(R.id.tv_hdop);
        tv_satelite_used = (TextView)findViewById(R.id.tv_satelite_used);
        tv_accurancy_h =  (TextView)findViewById(R.id.tv_accurancy_h);
        tv_accurancy_v = (TextView)findViewById(R.id.tv_accurancy_v);
        tv_vdop = (TextView)findViewById(R.id.tv_vdop);
        tv_pdop = (TextView)findViewById(R.id.tv_pdop);
        locationDataModel = new LocationDataModel(); //1 Implementaion for user...
        newActivity = new MarkWaypoints(this, locationDataModel, "34:81:F4:3E:3A:7B"); //2
        newActivity.connect();

        //  newActivity.checkBTState();
        //   newActivity.connect_service();
       /* if (Build.VERSION.SDK_INT < 23) {
            if (Constants.checkAndRequestPermissions(this)) {*/
                Timer timer = new Timer();
                TimerTask hourlyTask = new TimerTask() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        //  if (WristbandConstants.hasCon) {
                        runOnUiThread(() -> {
                            tv_utc_time.setText("UTC Time : " + locationDataModel.getTime());
                            tv_latitude.setText("Latitude : " + locationDataModel.getLat());
                            tv_longitude.setText("Longitude : " + locationDataModel.getLon());
                            tv_height.setText("Speed Over Ground : " + locationDataModel.getSpeed());
                            tv_altitude.setText("Altitude : " + locationDataModel.getAltitude_MSL());
                            tv_hdop.setText("HDOP : " + locationDataModel.getHDOP());
                            tv_satelite_used.setText("Satelite Used : " + locationDataModel.getUsed_satellite());
                            tv_accurancy_h.setText("Horizantal Accurancy : " + locationDataModel.getAccuracy_M());
                            tv_accurancy_v.setText("Vertical Accurancy : " + locationDataModel.getAccuracy_V());
                            tv_vdop.setText("VDOP : " + locationDataModel.getVDOP());
                            tv_pdop.setText("PDOP : " + locationDataModel.getPDOP());


                        });
                    }
                };
                timer.schedule(hourlyTask, 0L, 3000);
            }
        }
   /* }
}*/