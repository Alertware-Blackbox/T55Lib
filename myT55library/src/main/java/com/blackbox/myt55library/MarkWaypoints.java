package com.blackbox.myt55library;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.speech.tts.TextToSpeech;

import android.text.TextUtils.SimpleStringSplitter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blackbox.connection.Conn;
import com.blackbox.model.LocationDataModel;
import com.blackbox.t55.BluetoothGPSService;
import com.blackbox.utils.BleWrapperUiCallbacks;
import com.blackbox.utils.COORD;
import com.blackbox.utils.CoalGrid_Datum;
import com.blackbox.utils.Deg2UTM;
import com.blackbox.utils.ProjectionDetails;
import com.blackbox.utils.StringUtils;
import com.blackbox.utils.SxUtils;
import com.blackbox.utils.TrackApplication;
import com.blackbox.utils.XY;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.blackbox.utils.SxUtils.mBluetoothLeService;

public class MarkWaypoints extends Activity implements  Listener, LocationListener, BleWrapperUiCallbacks {

    Activity trackApplication;
    LocationManager mylocman;
    SharedPreferences myPrefs;
    private SharedPreferences.Editor sharedPreferencesEditor;
    String projectId;
    int inFix, SatellitesInFix;
    String proj_name;
    String wpDate, wpTime;
    private GpsStatus mGpsStatus = null;
    DecimalFormat df;
    String APP_DIRECTORY_NAME = "";
    String TEMP_DIRECTORY_NAME = "";
    private ProgressDialog pd;
    double lat_val, long_val, lat_val_show, long_val_show, accuracy = 1000;
    private int optionsselectedflag = 0;
    private int locationformat = 0;
    TextToSpeech textToSpeech;
    double minacc = 0.0;
    int i = 0;
    public boolean isGPSEnabled = false;
    boolean bluetoothStatus = false;
    LocationDataModel locationDataModel = null;
    Activity mActivity=null;
    private BluetoothGPSService bluetooth;
    private BluetoothAdapter mBtAdapter;
    Context mContext;
    public MarkWaypoints activity;
    Activity mCon;
    String _mac;

    public MarkWaypoints(Activity mCon, LocationDataModel locationDataModel,String _mac) {
        this.mCon=mCon;
        this._mac=_mac;
        this.locationDataModel=locationDataModel;
        SxUtils.savePairedMac(mCon,_mac);
        bluetooth = BluetoothGPSService.getInstance(mCon, MarkWaypoints.this,_mac);
       myPrefs = mCon.getSharedPreferences("preferences", MODE_PRIVATE);

       bluetooth.initialize(locationDataModel);



       //*************************************************
     //   trackApplication = mCon;

       bluetoothStatus = myPrefs.getBoolean("isOn", false);
       // myPrefs = mCon.getSharedPreferences("preferences", MODE_PRIVATE);
      //  bluetoothStatus =  myPrefs.getBoolean("isOn",false);
      //  System.out.println("Inside of bluetoothStatus...!!"+bluetoothStatus);
        if (bluetoothStatus) {
            System.out.println("Inside of bluetoothStatus...!!");
            LocalBroadcastManager.getInstance(mCon).registerReceiver(
                    mMessageReceiver, new IntentFilter("GPSLocationUpdates"));

        }
        else {
            mylocman = (LocationManager) mCon.getSystemService(mCon.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(mCon, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mCon, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                ActivityCompat.requestPermissions(mCon, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
            isGPSEnabled = mylocman.isProviderEnabled(LocationManager.GPS_PROVIDER);
            mylocman.requestLocationUpdates("gps", 5000, 5f, this);
            mylocman.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0f, this);
            mylocman.addGpsStatusListener(this);
            LocationListener myloclist = new MylocListener();
            mylocman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, myloclist);
        }


        //check minimum accuracy
        switch (TrackApplication.min_accuracy_format) {
            case 0:
                minacc = 5;
                break;
            case 1:
                minacc = 10;
                break;
            case 2:
                minacc = 15;
                break;
            case 3:
                minacc = 20;
                break;
            case 4:
                minacc = 0.0;
                break;
            default:
                minacc = 0.0;
        }
        init();
        APP_DIRECTORY_NAME = TrackApplication.TARGET_DB_PATH;
        TEMP_DIRECTORY_NAME = Environment.getExternalStorageDirectory()
                .toString() + "/Temp/";

        File file = new File(TEMP_DIRECTORY_NAME);
        //*************************************************
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.markwaypoints_new);
    }


    public void connect() {
        bluetooth = BluetoothGPSService.getInstance(mCon, (BleWrapperUiCallbacks) MarkWaypoints.this,locationDataModel);
        new Conn(mCon,_mac,bluetooth,locationDataModel);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mCon.startService(new Intent(mCon, BluetoothGPSService.class));
        } else {
            mCon.startService(new Intent(mCon, BluetoothGPSService.class));
        }
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(SxUtils.getPairedMac(mCon));
        }
    }
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothGPSService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(SxUtils.getPairedMac(mCon));
            mBluetoothLeService = mBluetoothLeService;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private static IntentFilter makeIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        return filter;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                return;
            }
            mylocman.requestLocationUpdates("gps", 5000, 5f, this);
            mylocman.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0f, this);
            mylocman.addGpsStatusListener(this);
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (bluetoothStatus) {
                if (mMessageReceiver != null) {
                    LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
                    mMessageReceiver = null;
                }
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    return;
                }
                mylocman.removeUpdates(this);
                mylocman.removeGpsStatusListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {

        if (bluetoothStatus) {
//            if (mMessageReceiver == null) {
            LocalBroadcastManager.getInstance(MarkWaypoints.this).registerReceiver(
                    mMessageReceiver, new IntentFilter("GPSLocationUpdates"));

        } else {
            mylocman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                return;
            }
            mylocman.requestLocationUpdates("gps", 5000, 5f, this);
            mylocman.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0f, this);
            mylocman.addGpsStatusListener(this);

        }


        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            if (bluetoothStatus) {
               /* if (mMessageReceiver != null) {
                    LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
                    mMessageReceiver = null;
                }*/
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    return;
                }
                mylocman.removeUpdates(this);
                mylocman.removeGpsStatusListener(this);
            }
        } catch (Exception e) {
        }
        super.onPause();
    }

    public void init() {
        getNMEA();
        loadPreferences();
    }

    private void getNMEA() {
        // TODO Auto-generated method stub

        if (bluetoothStatus) {

        } else {
            LocationManager LM = (LocationManager) mCon.getSystemService(mCon.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(mCon, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mCon, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                return;
            }
            ((LocationManager) mCon.getSystemService(mCon.LOCATION_SERVICE)).requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10f, new LocationListener() {
                @Override
                public void onLocationChanged(Location loc) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
            });
            LM.addNmeaListener(new GpsStatus.NmeaListener() {
                public void onNmeaReceived(long timestamp, String nmea) {
                    parseNmeaSentence(nmea);

                    String[] nmeaParts = nmea.split(",");
                    if (nmeaParts[0].equalsIgnoreCase("$GPGSA")) {

                        if (nmeaParts.length > 15 && !nmeaParts[15].equals("")) {
                        }
                    }
                }
            });
        }
    }

    public String parseNmeaSentence(String gpsSentence) throws SecurityException {
        String nmeaSentence = null;
        Pattern xx = Pattern.compile("\\$([^*$]*)(?:\\*([0-9A-F][0-9A-F]))?\r\n");
        Matcher m = xx.matcher(gpsSentence);
        if (m.matches()) {
            nmeaSentence = m.group(0);
            String sentence = m.group(1);
            String checkSum = m.group(2);
            SimpleStringSplitter splitter = new SimpleStringSplitter(',');
            splitter.setString(sentence);
            String command = splitter.next();
            if (command.equals("GPGSA")) {
				/*  $GPGSA,A,3,04,05,,09,12,,,24,,,,,2.5,1.3,2.1*39
					Where:
					     GSA      Satellite status
					     A        Auto selection of 2D or 3D fix (M = manual)
					     3        3D fix - values include: 1 = no fix
					                                       2 = 2D fix
					                                       3 = 3D fix
					     04,05... PRNs of satellites used for fix (space for 12)
					     2.5      PDOP (Position dilution of precision)
					     1.3      Horizontal dilution of precision (HDOP)
					     2.1      Vertical dilution of precision (VDOP)
				 *39      the checksum data, always begins with *
				 */
                // mode : A Auto selection of 2D or 3D fix / M = manual
                String mode = splitter.next();
                // fix type  : 1 - no fix / 2 - 2D / 3 - 3D
                String fixType = splitter.next();
                // discard PRNs of satellites used for fix (space for 12)
                for (int i = 0; ((i < 12) && (!"1".equals(fixType))); i++) {
                    splitter.next();
                }
                // Position dilution of precision (float)
                String pdop = splitter.next();
                // Horizontal dilution of precision (float)
                String hdop = splitter.next();
                // Vertical dilution of precision (float)
                String vdop = splitter.next();
            }
        }
        return nmeaSentence;
    }
    public void loadPreferences() {
        projectId = myPrefs.getString("projid", "");
        proj_name = myPrefs.getString("projectname", "");
    }

    @Override
    public void uiDeviceFound(BluetoothDevice device, int rssi, byte[] record) {

    }

    @Override
    public void uiDeviceConnected(BluetoothGatt gatt, BluetoothDevice device) {

    }

    @Override
    public void uiDeviceDisconnected(BluetoothGatt gatt, BluetoothDevice device) {

    }

    @Override
    public void uiAvailableServices(BluetoothGatt gatt, BluetoothDevice device, List<BluetoothGattService> services) {

    }

    @Override
    public void uiCharacteristicForService(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, List<BluetoothGattCharacteristic> chars) {

    }

    @Override
    public void uiCharacteristicsDetails(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void uiNewValueForCharacteristic(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic ch, String strValue, int intValue, byte[] rawValue, String timestamp) {

    }

    @Override
    public void uiGotNotification(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void uiSuccessfulWrite(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic ch, String description) {

    }

    @Override
    public void uiFailedWrite(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic ch, String description) {

    }

    @Override
    public void uiNewRssiAvailable(BluetoothGatt gatt, BluetoothDevice device, int rssi) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public class MylocListener implements LocationListener {

        public void onLocationChanged(Location loc) {
            int Satellites = 0;
            SatellitesInFix = 0;
            if (ActivityCompat.checkSelfPermission(mCon, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            df = new DecimalFormat("##.#######");
            DecimalFormat df2 = new DecimalFormat("##.##");
            lat_val = loc.getLatitude();
            long_val = loc.getLongitude();
            accuracy = loc.getAccuracy();
            //if options button favourite datum is not selected
            if (optionsselectedflag == 0) {
                if (TrackApplication.Datum_Format == 100) {
                    lat_val_show = lat_val;
                    long_val_show = long_val;
                } else if (TrackApplication.Datum_Format == 104) {
                    XY result = CoalGrid_Datum.translate(lat_val, long_val);
                    lat_val_show = result.x;
                    long_val_show = result.y;
                } else {
                    XY result = COORD.translate(100, TrackApplication.Datum_Format, lat_val, long_val);
                    lat_val_show = result.x;
                    long_val_show = result.y;
                }
            }
            //in case favourite datum is selected from options button
            else {
                if (locationformat == 100) {
                    lat_val_show = lat_val;
                    long_val_show = long_val;
                } else if (locationformat == 104) {
                    XY result = CoalGrid_Datum.translate(lat_val, long_val);
                    lat_val_show = result.x;
                    long_val_show = result.y;
                } else {
                    XY result = COORD.translate(100, locationformat, lat_val, long_val);
                    lat_val_show = result.x;
                    long_val_show = result.y;
                }
            }
            if (TrackApplication.Datum_Format == 104 || locationformat == 104) {
                df = new DecimalFormat("######.####");

            } else {
                if (TrackApplication.Coordinate_VIEW_Format > 2) {

                    Deg2UTM deg2utm = new Deg2UTM(lat_val, long_val);
                } else {

                }
            }



            Date date = new Date(loc.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String Date = sdf.format(date);
            wpTime = date.getTime() + "";
            String[] timestamp = Date.split(" ");
            wpDate = timestamp[0];
            wpTime = timestamp[1];



            if (ActivityCompat.checkSelfPermission(mCon, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            for (GpsSatellite sat : mylocman.getGpsStatus(null).getSatellites()) {
                if (sat.usedInFix()) {
                    SatellitesInFix++;
                }
                Satellites++;
            }

            if (SatellitesInFix > 0) {
               // satCount.setText(SatellitesInFix + "");
            } else {
               // satCount.setText(0 + "");
            }
        }

        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            if (LocationManager.GPS_PROVIDER.equals(provider)) {
                setGpsStatus();
            }
        }

        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            if (LocationManager.GPS_PROVIDER.equals(provider)) {
                setGpsStatus();
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
            if (!LocationManager.GPS_PROVIDER.equals(provider)) {
                return;
            }
            setGpsStatus();
        }
    }


    public void saveWaypoints() {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {

                pd = new ProgressDialog(MarkWaypoints.this);
                pd.setTitle("Saving");
                pd.setMessage("Please wait while Saving..");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                String tag_id = "1";
                try {

                } catch (Exception e) {
                    Log.e("save exception==", e.getMessage());
                }


                try {
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (pd != null) {
                    pd.dismiss();
//				onRestartManual();
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String text = "Waypoint marked successfully!!";
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(getBaseContext(), "Waypoint marked successfully!!",
                            Toast.LENGTH_SHORT)

                            .show();
                }
            }


        };
        task.execute((Void[]) null);

    }

    private void openaccuracy() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MarkWaypoints.this);
        alertDialogBuilder.setTitle("Minimum accuracy not satisfied!!");
        alertDialogBuilder.setMessage("The accuracy of the waypoint is greater than " + String.valueOf(minacc) + "m.! Do you want to continue?");
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // go to a new activity of the app
                saveWaypoints();
            }

        });


        // set negative button: No message

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel the alert box and put a Toast to the user
                dialog.cancel();
                finish();
                Intent positveActivity = new Intent(getApplicationContext(), MarkWaypoints.class);
                startActivity(positveActivity);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

    public void setGpsStatus() {
        if (mylocman.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mGpsStatus = mylocman.getGpsStatus(mGpsStatus);
            Iterable<GpsSatellite> sats = mGpsStatus.getSatellites();
            int inSky = 0;
            inFix = 0;
            for (GpsSatellite s : sats) {
                inSky += 1;
                if (s.usedInFix()) {
                    inFix += 1;
                }
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

    }

    //@Override
    public void onProviderDisabled(String provider) {

    }

    //@Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGpsStatusChanged(int arg0) {
        //setGpsStatus();
    }


    public void onRestart() {
        super.onRestart();

    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            try {
                String message = intent.getStringExtra("Status");
                if (message.equalsIgnoreCase("true")) {
                    Bundle b = intent.getBundleExtra("Location");

                    Location lastKnownLoc = (Location) b.getParcelable("Location");
                    if (lastKnownLoc != null) {

                        updatelocation (lastKnownLoc);


                    }
                }

            } catch (Exception e) {
                Log.v("error", e.toString());
            }
        }
    };
    private String convertlat(double latitude) {
        StringBuilder builder = new StringBuilder();

        if (latitude < 0) {
            builder.append("S ");
        } else {
            builder.append("N ");
        }

        String latitudeDegrees = Location.convert(Math.abs(latitude), Location.FORMAT_SECONDS);
        String[] latitudeSplit = latitudeDegrees.split(":");
        builder.append(latitudeSplit[0]);
        builder.append("°");
        builder.append(latitudeSplit[1]);
        builder.append("'");
        builder.append(latitudeSplit[2]);
        builder.append("\"");

        builder.append(" ");
        return builder.toString();
    }

    public static String format(Number n) {
        NumberFormat format = DecimalFormat.getInstance();
        format.setRoundingMode(RoundingMode.FLOOR);
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(6);
        return format.format(n);
    }
    public void updatelocation(Location loc)
    {
      //  int timetofix = mylocman.getGpsStatus(null).getTimeToFirstFix();
        df = new DecimalFormat("##.#######");
        DecimalFormat df2 = new DecimalFormat("##.##");
        lat_val = loc.getLatitude();
        long_val = loc.getLongitude();
        accuracy = loc.getAccuracy();
        //if options button favourite datum is not selected
        if (optionsselectedflag == 0) {
            if (TrackApplication.Datum_Format == 100) {
                lat_val_show = lat_val;
                long_val_show = long_val;
            } else if (TrackApplication.Datum_Format == 104) {
                XY result = CoalGrid_Datum.translate(lat_val, long_val);
                lat_val_show = result.x;
                long_val_show = result.y;
            } else {
                XY result = COORD.translate(100, TrackApplication.Datum_Format, lat_val, long_val);
                lat_val_show = result.x;
                long_val_show = result.y;
            }
        }
        //in case favourite datum is selected from options button
        else {
            if (locationformat == 100) {
                lat_val_show = lat_val;
                long_val_show = long_val;
            } else if (locationformat == 104) {
                XY result = CoalGrid_Datum.translate(lat_val, long_val);
                lat_val_show = result.x;
                long_val_show = result.y;
            } else {
                XY result = COORD.translate(100, locationformat, lat_val, long_val);
                lat_val_show = result.x;
                long_val_show = result.y;
            }
        }
        if (TrackApplication.Datum_Format == 104 || locationformat == 104) {
            df = new DecimalFormat("######.####");
        } else {
            if (TrackApplication.Coordinate_VIEW_Format > 2) {
                Deg2UTM deg2utm = new Deg2UTM(lat_val, long_val);
            } else {

            }
        }

        Date date = new Date(loc.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String Date = sdf.format(date);
        wpTime = date.getTime() + "";
        String[] timestamp = Date.split(" ");
        wpDate = timestamp[0];
        wpTime = timestamp[1];
    }
}