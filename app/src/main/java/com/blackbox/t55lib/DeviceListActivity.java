/*
package com.blackbox.t55lib;

*/
/**
 * Created by Krishnendu Dasgupta on 26-04-2018.
 *//*


import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.blackbox.t55.BluetoothGPSService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Set;

public class DeviceListActivity extends AppCompatActivity {

    // textview for connection status
    TextView textConnectionStatus;
    ListView pairedListView;

    //An EXTRA to take the device MAC to the next activity
    public static String EXTRA_DEVICE_ADDRESS;
    private TextView tv_toolbar;
    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_device_list);
        pref = this.getSharedPreferences("preferences", 0);
        boolean loginStatus=pref.getBoolean("isOn",false);

        textConnectionStatus = (TextView) findViewById(R.id.connecting);
        textConnectionStatus.setTextSize(40);
       */
/* FloatingActionButton btn_refresh=(FloatingActionButton)findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DeviceListActivity.this, DeviceListActivity.class);
                startActivity(i);
                finish();
            }
        });*//*

        // Initialize array adapter for paired devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.row_device);

        // Find and set up the ListView for paired devices
        pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setOnItemClickListener(mDeviceClickListener);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        //onCreate();

    }

    @Override
    public void onResume()
    {
        super.onResume();
        //It is best to check BT status at onResume in case something has changed while app was paused etc
        checkBTState();
        mPairedDevicesArrayAdapter.clear();// clears the array so items aren't duplicated when resuming from onPause
        textConnectionStatus.setText(" "); //makes the textview blank

        Intent intent = getIntent();
        String name = intent.getStringExtra("address");
        textConnectionStatus.setText(name);
        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        // Get a set of currently paired devices and append to pairedDevices list
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if(pairedDevices==null){
            Toast.makeText(this, "No device to connect", Toast.LENGTH_LONG).show();

        }else{

        }
        // Add previously paired devices to the array
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);//make title viewable
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
              //  textConnectionStatus.setText(device.getName());
            }
        } else {
            mPairedDevicesArrayAdapter.add("no devices paired");
            startService(new Intent(DeviceListActivity.this, BluetoothGPSService.class));
          //  textConnectionStatus.setText( "Disconnected ");
        }


    }

    //method to check if the device has Bluetooth and if it is on.
    //Prompts the user to turn it on if it is off
    private void checkBTState()
    {
        // Check device has Bluetooth and that it is turned on
        mBtAdapter= BluetoothAdapter.getDefaultAdapter(); // CHECK THIS OUT THAT IT WORKS!!!
        if(mBtAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (!mBtAdapter.isEnabled()) {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }
    // Set up on-click listener for the listview
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
        {
               textConnectionStatus.setText("Connecting...");
            //    Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("address",address);
            editor.putBoolean("isOn",true);
            editor.commit();
            if(address.equals(info)){

                Toast.makeText(DeviceListActivity.this, "Please paired with device", Toast.LENGTH_LONG).show();
            }else {

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DeviceListActivity.this);
                alertBuilder.setTitle("Confirm!!");
                alertBuilder.setIcon(android.R.drawable.ic_btn_speak_now);
                alertBuilder.setMessage("Ready to Connect?");
                alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Make an intent to start next activity while taking an extra which is the MAC address.
                        startService(new Intent(DeviceListActivity.this, BluetoothGPSService.class));
                        Intent i = new Intent(DeviceListActivity.this, MainActivity.class);
                        i.putExtra("address", address);
                        textConnectionStatus.setText("Connected");
                        startActivity(i);
                        finish();
                    }
                });
                alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        textConnectionStatus.setText("");
                    }
                });
                alertBuilder.setCancelable(false);
                AlertDialog dialog = alertBuilder.create();
              //  dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                dialog.show();

            }

        }
    };

    @Override
    public void onBackPressed(){
        openDialog();
    }
    public void openDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DeviceListActivity.this);
        alertBuilder.setTitle("Confirm!!");
        alertBuilder.setIcon(android.R.drawable.ic_lock_power_off);
        alertBuilder.setMessage("Are you sure you want to exit?");
        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertBuilder.setCancelable(false);
        AlertDialog dialog = alertBuilder.create();
       // dialog.getWindow().getAttributes().windowAnimations = R.style
        dialog.show();
    }

}*/
