package com.blackbox.myt55library;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.blackbox.model.LocationDataModel;
import com.blackbox.t55.BluetoothGPSService;

import java.util.Timer;
import java.util.TimerTask;

public class NewActivity extends AppCompatActivity {
    LocationDataModel locationDataModel = null;
    Activity mActivity=null;
    private BluetoothGPSService bluetooth;

    String _mac;
    Activity mCon;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public NewActivity(Activity mActivity, LocationDataModel locationDataModel) {
        this.locationDataModel = locationDataModel;
        this.mActivity = mActivity;
    }
    public double get_longitude()
    {
        return locationDataModel.getLon();
    }
}
