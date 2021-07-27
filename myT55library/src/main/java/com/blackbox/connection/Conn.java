package com.blackbox.connection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;

import com.blackbox.model.LocationDataModel;
import com.blackbox.t55.BluetoothGPSService;
import com.blackbox.utils.SxUtils;



public class Conn {
    Activity activity;
    String data;
    private BluetoothGPSService bluetooth=null;
    private LocationDataModel locationDataModel;
    public Conn(Activity activity, String data, BluetoothGPSService bluetooth, LocationDataModel locationDataModel) {
        this.activity = activity;
        this.bluetooth = bluetooth;
        this.data = data;
        this.locationDataModel = locationDataModel;
        authConnetion(data);
    }
    public void authConnetion(final String data) {
        @SuppressLint("StaticFieldLeak")
        class ConnectionAsync extends AsyncTask<String, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(data.length()==0||data==null){
                    SxUtils.hasCon=false;
                }
                else
                {
                   // if(data!=null){
                        SxUtils.macID=data;
                  //  }

                }
            }

            @Override
            protected Void doInBackground(String... params) {
                if(data.length()>0)
                {
                    bluetooth.initialize();
                    bluetooth.connect(SxUtils.macID);
                }

                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(data.length()>0)
                {
                    try {
                        System.out.println("AAAAAAAAAAAAAAAAAAAAA");
                        SxUtils.savePairedMac(activity,data);
                        SxUtils.macID=data;
                        SxUtils.hasCon=true;
                    } catch (Exception e) {
                        SxUtils.hasCon=false;
                    }
                }
                else
                {
                    SxUtils.hasCon=false;

                }
            }
        }
        ConnectionAsync la = new ConnectionAsync();
        la.execute();
    }
}
