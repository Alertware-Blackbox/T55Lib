package com.blackbox.t55;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blackbox.model.LocationDataModel;
import com.blackbox.myt55library.MarkWaypoints;
import com.blackbox.utils.BleWrapperUiCallbacks;
import com.blackbox.utils.TrackApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.media.session.PlaybackState.STATE_CONNECTING;

/**
 * Created by Saurav Kr Lall on 16-07-2021.
 */

public class BluetoothGPSService extends Service implements Parcelable {
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private static final String LOG_TAG = "T55";
    private static String macID=null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public double longitude;
    public double latitude;
    private final static String TAG = "BroadcastService";
    // MAC-address of Bluetooth module
    public String newAddress = null;
    SharedPreferences pref;
    private Timer timer;
    private TimerTask timerTask;
    InputStream tmpIn = null;
    boolean enabled;
    boolean ready = true;
    TextUtils.SimpleStringSplitter splitter;
    public static final String GPS_MOCK_PROVIDER = "gps";
    public static final String NETWORK_MOCK_PROVIDER = "network";
    String fixTime = "";
    private float precision = 10f;
    boolean read_pubx = false;
    boolean loginStatus;
    CountDownTimer cdt = null;
    private Location fix = null;
    Intent bi = new Intent(COUNTDOWN_BR);
    public static final String COUNTDOWN_BR = "t55.countdown_br";
    LocationDataModel locationDataModel;
    int Clicks = 0;
    private static BluetoothGPSService mInstance = null;
    static LocationDataModel _locationDataModel = null;
    private BleWrapperUiCallbacks mUiCallback = null;
    private Activity mParent = null;
    private static final BleWrapperUiCallbacks NULL_CALLBACK = new BleWrapperUiCallbacks.Null();

    private long UTC_TIMEZONE=1470960000;
    private String OUTPUT_DATE_FORMATE="hh:mm a";
    private String OUTPUT_DATE_FORMATE1="dd/MM/yyyy";
    String wpDate, wpTime;
    Date date1;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("onBind=2", "onBind----");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pref = getApplicationContext().getSharedPreferences("preferences", 0);
        String service_status = pref.getString("address", "");
        newAddress = macID;
        loginStatus = pref.getBoolean("isOn", false);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        //if (loginStatus) {
            BluetoothDevice device = btAdapter.getRemoteDevice(newAddress);

            //Attempt to create a bluetooth socket for comms
            try {
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            // Establish the connection.
            try {
                if (btSocket != null) {
                    btSocket.connect();
                    enabled = true;
                } else {
                    enabled = false;
                }

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    enabled = false;
                    btSocket.close();//If IO exception occurs attempt to close socket
                } catch (IOException e2) {
                    enabled = false;
                    e2.printStackTrace();
                }
            }
            if (enabled) {
                startTimer();
            }
      //  }
       /* else {

            Log.e("else ===loginStatus", loginStatus + "");

        }*/
        return START_STICKY;
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 2000, 2000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            public void run() {
                try {
                    if (pref.getBoolean("iserviceStarted", true)) {
                        //Service Should Start
                        tmpIn = btSocket.getInputStream();

                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(tmpIn, "US-ASCII"));
                            String s;
                            long now = SystemClock.uptimeMillis();
                            long lastRead = now;
                            while ((enabled) && (now < lastRead + 5000)) {
                                if (reader.ready()) {
                                    s = reader.readLine();
                                    Log.v(LOG_TAG, "data: " + System.currentTimeMillis() + " " + s);
                                    parseNmeaSentence1(s + "\r\n");
                                    //if(read_pubx){
//                                            Location loc=parsePUBX(s+"\r\n");
                                    Location loc = parseNmeaSentence1(s + "\r\n");
                                    sendNmeaStringToActivity(s, getApplicationContext());
                                    if (loc == null) {
                                        sendMessageToActivity(loc, "false", getApplicationContext());
                                    } else {
                                        sendMessageToActivity(loc, "true", getApplicationContext());
                                    }
                                    // }
                                    ready = true;
                                    lastRead = SystemClock.uptimeMillis();
                                } else {
                                    Log.d(LOG_TAG, "data: not ready " + System.currentTimeMillis());
                                    SystemClock.sleep(500);
                                }
                                now = SystemClock.uptimeMillis();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e(LOG_TAG, "error while getting data", e);
                        } finally {
                            // cleanly closing everything...
                        }
                    }
                } catch (Exception e) {
                    //Some exception
                    e.printStackTrace();
                }
            }
        };
    }

    /* initialize BLE and get BT Manager & Adapter */
    public boolean initialize(LocationDataModel locationDataModel) {
        _locationDataModel = _locationDataModel;
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mParent.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return false;
            }
        }

        if (btAdapter == null) btAdapter = mBluetoothManager.getAdapter();
        if (btAdapter == null) {
            return false;
        }
        return true;
    }

    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        btAdapter = mBluetoothManager.getAdapter();
        if (btAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    public class LocalBinder extends Binder {
        public BluetoothGPSService getService() {
            return BluetoothGPSService.this;
        }
    }

    private static void sendMessageToActivity(Location l, String msg, Context context) {
        Intent intent = new Intent("GPSLocationUpdates");
        // You can also include some extra data.
        intent.putExtra("Status", msg);
        Bundle b = new Bundle();
        b.putParcelable("Location", l);
        intent.putExtra("Location", b);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    private static void sendNmeaStringToActivity(String msg, Context context) {
        Intent intent = new Intent("GPSLocationUpdates");
        // You can also include some extra data.
        intent.putExtra("nmeaString", msg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public Location parsePUBX(String pubx) {
        String nmeaSentence = null;
//		Log.v(LOG_TAG, "data: "+System.currentTimeMillis()+" "+gpsSentence);
        Pattern xx = Pattern.compile("\\$([^*$]*)\\*([0-9A-F][0-9A-F])?\r\n");
        Matcher m = xx.matcher(pubx);
        if (m.matches()) {
            nmeaSentence = m.group(0);
            String sentence = m.group(1);
            String checkSum = m.group(2);
            Log.v(LOG_TAG, "data: " + System.currentTimeMillis() + " " + sentence + " cheksum; " + checkSum + " control: " + String.format("%X", computeChecksum(sentence)));
            splitter = new TextUtils.SimpleStringSplitter(',');
            splitter.setString(sentence);
            String command = splitter.next();
            if (command.equals("PUBX")) {
                try {
                    String msgId = splitter.next();
                    String strTime = splitter.next();
                    String lat = splitter.next();  //ddmm.mmmmm
                    String ns = splitter.next();
                    String lon = splitter.next();  //ddmm.mmmmm
                    String ew = splitter.next();
                    String altRef = splitter.next();
                    String sat = splitter.next();
                    if (sat == "NF")
                        sat = "No Fix";
                    if (sat == "DR")
                        sat = "Dead reckoning only solution";
                    if (sat == "G2")
                        sat = "Stand alone 2D solution";
                    if (sat == "G3")
                        sat = "Stand alone 3D solution";
                    if (sat == "D2")
                        sat = "Differential 2D solution";
                    if (sat == "D3")
                        sat = "Differential 3D solution";
                    if (sat == "RK")
                        sat = "Combined GPS + dead reckoning solution";
                    if (sat == "TT")
                        sat = "Time only solution";

                    String hAcc = splitter.next(); //Horizontal accuracy estimate in meter
                    String vAcc = splitter.next();    //Vertical accuracy estimat in meter
                    String SOG = splitter.next();     //Speed over ground(Km/Hr)
                    _locationDataModel.setSpeed(SOG);
                    String COG = splitter.next();     //Course over ground in degree
                    String vVel = splitter.next(); //Vertical velocity (positive downwards)
                    String diffAge = splitter.next(); //Age of differential corrections (blank when DGPS is not used)
                    String hdop = splitter.next();
                    String vdop = splitter.next();
                    String tdop = splitter.next();    //TDOP, Time Dilution of Precision
                    String satelit = splitter.next();  //Number of satellites used in the navigation solution
                    String reserved = splitter.next();    //Reserved, always set to 0
                    String dr = splitter.next();  //DR used
                    Location fix = new Location(GPS_MOCK_PROVIDER);

                    if (!strTime.equals(fixTime)) {
                        fix = new Location(GPS_MOCK_PROVIDER);
                        fixTime = strTime;
                        long fixTimestamp = parseNmeaTime(strTime);
                        fix.setTime(fixTimestamp);
                    }
                    if (lat != null && !lat.equals("")) {
                        fix.setLatitude(parseNmeaLatitude(lat, ns));
                    }
                    if (lon != null && !lon.equals("")) {
                        fix.setLongitude(parseNmeaLongitude(lon, ew));
                    }
                    if (hAcc != null && !hAcc.equals("")) {
                        fix.setAccuracy(Float.parseFloat(hAcc));
                    }
                    if (altRef != null && !altRef.equals("")) {
                        fix.setAltitude(Double.parseDouble(altRef));
                    }
                    if (SOG != null && !SOG.equals("")) {
                        fix.setSpeed(Float.parseFloat(SOG));
                    }
//                if (SOG != null && !SOG.equals("")){
                    long fixTimestamp = parseNmeaTime(strTime);
                    fix.setTime(fixTimestamp);
//                }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        fix.setElapsedRealtimeNanos(fixTimestamp);
                    }
                    return fix;
                } catch (Exception e) {
                    Log.v("error", e.toString());
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //Location[gps 23.580348,87.175040 hAcc=694663 et=?!? vAcc=??? sAcc=??? bAcc=???]
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Location parseNmeaSentence1(String gpsSentence) {
        String nmeaSentence = null;
//		Log.v(LOG_TAG, "data: "+System.currentTimeMillis()+" "+gpsSentence);
        //    writeStringAsFile1(gpsSentence,getApplicationContext());
        Pattern xx = Pattern.compile("\\$([^*$]*)\\*([0-9A-F][0-9A-F])?\r\n");
        Matcher m = xx.matcher(gpsSentence);

        if (m.matches()) {
            nmeaSentence = m.group(0);
            String sentence = m.group(1);
            String checkSum = m.group(2);
            Log.v(LOG_TAG, "data: " + System.currentTimeMillis() + " " + sentence + " cheksum; " + checkSum + " control: " + String.format("%X", computeChecksum(sentence)));
            splitter = new TextUtils.SimpleStringSplitter(',');
            splitter.setString(sentence);
            String command = splitter.next();


            if (command.equals("PUBX")) {
                String msgId = splitter.next();
                String strTime = splitter.next();
                String lat = splitter.next();  //ddmm.mmmmm
                String ns = splitter.next();
                String lon = splitter.next();  //ddmm.mmmmm

                String ew = splitter.next();
                String altRef = splitter.next();
                _locationDataModel.setOrtho_height(altRef);
                String sat = splitter.next();
                if (sat == "NF")
                    sat = "No Fix";
                if (sat == "DR")
                    sat = "Dead reckoning only solution";
                if (sat == "G2")
                    sat = "Stand alone 2D solution";
                if (sat == "G3")
                    sat = "Stand alone 3D solution";
                if (sat == "D2")
                    sat = "Differential 2D solution";
                if (sat == "D3")
                    sat = "Differential 3D solution";
                if (sat == "RK")
                    sat = "Combined GPS + dead reckoning solution";
                if (sat == "TT")
                    sat = "Time only solution";

                String hAcc = splitter.next(); //Horizontal accuracy estimate in meter
                _locationDataModel.setAccuracy_M(hAcc);
                String vAcc = splitter.next();    //Vertical accuracy estimat in meter
                _locationDataModel.setAccuracy_V(vAcc);
                String SOG = splitter.next();     //Speed over ground(Km/Hr)
                String COG = splitter.next();     //Course over ground in degree
                String vVel = splitter.next(); //Vertical velocity (positive downwards)
                String diffAge = splitter.next(); //Age of differential corrections (blank when DGPS is not used)
                String hdop = splitter.next();
                _locationDataModel.setHDOP(hdop);
                String vdop = splitter.next();
                _locationDataModel.setVDOP(vdop);
                String tdop = splitter.next();    //TDOP, Time Dilution of Precision
                _locationDataModel.setPDOP(tdop);
                String satelit = splitter.next();  //Number of satellites used in the navigation solution
              //  _locationDataModel.setUsed_satellite(satelit);
                String reserved = splitter.next();    //Reserved, always set to 0
                String dr = splitter.next();  //DR used
                Location fix = new Location(GPS_MOCK_PROVIDER);

                if (!strTime.equals(fixTime)) {
                    fix = new Location(GPS_MOCK_PROVIDER);
                    fixTime = strTime;
                    long fixTimestamp = parseNmeaTime(strTime);
                    fix.setTime(fixTimestamp);
                }
                if (lat != null && !lat.equals("")) {
                    fix.setLatitude(parseNmeaLatitude(lat, ns));
                }
                if (lon != null && !lon.equals("")) {
                    fix.setLongitude(parseNmeaLongitude(lon, ew));
                }
                if (hAcc != null && !hAcc.equals("")) {
                    fix.setAccuracy(Float.parseFloat(hAcc));
                }
                if (altRef != null && !altRef.equals("")) {
                    fix.setAltitude(Double.parseDouble(altRef));
                }
                if (SOG != null && !SOG.equals("")) {
                    fix.setSpeed(Float.parseFloat(SOG));
                    _locationDataModel.setSpeed(String.valueOf(Float.parseFloat(SOG)));
                }
//                if (SOG != null && !SOG.equals("")){
                long fixTimestamp = parseNmeaTime(strTime);
                fix.setTime(fixTimestamp);
//                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    fix.setElapsedRealtimeNanos(fixTimestamp);
                }
                return fix;
            } else if (command.equals("GNGGA")) {
				/* $GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*47

					Where:
					     GGA          Global Positioning System Fix Data
					     123519       Fix taken at 12:35:19 UTC
					     4807.038,N   Latitude 48 deg 07.038' N
					     01131.000,E  Longitude 11 deg 31.000' E
					     1            Fix quality: 0 = invalid
					                               1 = GPS fix (SPS)
					                               2 = DGPS fix
					                               3 = PPS fix
											       4 = Real Time Kinematic
											       5 = Float RTK
					                               6 = estimated (dead reckoning) (2.3 feature)
											       7 = Manual input mode
											       8 = Simulation mode
					     08           Number of satellites being tracked
					     0.9          Horizontal dilution of position
					     545.4,M      Altitude, Meters, above mean sea level
					     46.9,M       Height of geoid (mean sea level) above WGS84
					                      ellipsoid
					     (empty field) time in seconds since last DGPS update
					     (empty field) DGPS station ID number
					     *47          the checksum data, always begins with *
				 */
                // UTC time of fix HHmmss.S
                String time = splitter.next();
                // latitude ddmm.M
                String lat = splitter.next();
                // direction (N/S)
                String latDir = splitter.next();
                // longitude dddmm.M
                String lon = splitter.next();
                // direction (E/W)
                String lonDir = splitter.next();
				/* fix quality:
				  	0= invalid
					1 = GPS fix (SPS)
					2 = DGPS fix
					3 = PPS fix
					4 = Real Time Kinematic
					5 = Float RTK
					6 = estimated (dead reckoning) (2.3 feature)
					7 = Manual input mode
					8 = Simulation mode
				 */
                String quality = splitter.next();
                // Number of satellites being tracked
                String nbSat = splitter.next();
                _locationDataModel.setUsed_satellite(nbSat);
                // Horizontal dilution of position (float)
                String hdop = splitter.next();
                // Altitude, Meters, above mean sea level
                String alt = splitter.next();
                _locationDataModel.setAltitude_MSL(alt);


                // Height of geoid (mean sea level) above WGS84 ellipsoid
                String geoAlt = splitter.next();
                // time in seconds since last DGPS update
                // DGPS station ID number
                if (quality != null && !quality.equals("") && !quality.equals("0")) {
                    Location fix = new Location(GPS_MOCK_PROVIDER);
                    if (!time.equals(fixTime)) {
                        fixTime = time;
                        long fixTimestamp = parseNmeaTime(time);
                        fix.setTime(fixTimestamp);
                        Log.v(LOG_TAG, "Fix: " + fix);
                    }
                    if (lat != null && !lat.equals("")) {
                        fix.setLatitude(parseNmeaLatitude(lat, latDir));
                    }
                    if (lon != null && !lon.equals("")) {
                        fix.setLongitude(parseNmeaLongitude(lon, lonDir));
                    }
                    if (hdop != null && !hdop.equals("")) {
                        fix.setAccuracy(Float.parseFloat(hdop) * precision);
                    }
                    if (alt != null && !alt.equals("")) {
                        fix.setAltitude(Double.parseDouble(alt));
                      //  _locationDataModel.setAltitude_MSL(alt);
                    }
                    long fixTimestamp = parseNmeaTime(time);
                    fix.setTime(fixTimestamp);
                    if (nbSat != null && !nbSat.equals("")) {
                        Bundle extras = new Bundle();
                        extras.putInt("satellites", Integer.parseInt(nbSat));
                        fix.setExtras(extras);
                    }
                    fix.setElapsedRealtimeNanos(fixTimestamp);
//					LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                    locationManager.setTestProviderLocation(GPS_MOCK_PROVIDER, fix);
                    return fix;
                } else if (quality.equals("0")) {
                    return null;
                }
            } else if (command.equals("GNRMC")) {
				/* $GPRMC,123519,A,4807.038,N,01131.000,E,022.4,084.4,230394,003.1,W*6A
				   Where:
				     RMC          Recommended Minimum sentence C
				     123519       Fix taken at 12:35:19 UTC
				     A            Status A=active or V=Void.
				     4807.038,N   Latitude 48 deg 07.038' N
				     01131.000,E  Longitude 11 deg 31.000' E
				     022.4        Speed over the ground in knots
				     084.4        Track angle in degrees True
				     230394       Date - 23rd of March 1994
				     003.1,W      Magnetic Variation
				     *6A          The checksum data, always begins with *
				*/
                // UTC time of fix HHmmss.S
                String time = splitter.next();

                String aa=time.replace(".00","").trim();
                String frst_two=aa.substring(0,2)+ "-" + aa.substring(2,4) + "-" + aa.substring(4,6);

                _locationDataModel.setTime(frst_two);


                // String time_spl[] = time.split(".00");
                //  String time_ori=time_spl[0];
                //
                System.out.println("First two ltr--->> "+aa+" --->> "+frst_two);



                // fix status (A/V)
                String status = splitter.next();
                // latitude ddmm.M
                String lat = splitter.next();
                // direction (N/S)
                String latDir = splitter.next();
                // longitude dddmm.M
                String lon = splitter.next();
                // direction (E/W)
                String lonDir = splitter.next();
                // Speed over the ground in knots
                String speed = splitter.next();
                // Track angle in degrees True
                String bearing = splitter.next();
                // UTC date of fix DDMMYY
                String date = splitter.next();
                long l=Long.parseLong(date);
               // NMEAGPRMCDate(l);
                String frst_two_date=date.substring(0,2)+ "-" + date.substring(2,4) + "-" + date.substring(4,6);
                _locationDataModel.setRMCDate(frst_two_date);
               // NMEAGPRMCDate(l);

                // Magnetic Variation ddd.D
                String magn = splitter.next();
                // Magnetic variation direction (E/W)
                String magnDir = splitter.next();
                // for NMEA 0183 version 3.00 active the Mode indicator field is added
                // Mode indicator, (A=autonomous, D=differential, E=Estimated, N=not valid, S=Simulator )
                if (status != null && !status.equals("") && status.equals("A")) {
                    read_pubx = true;
                    Location fix = null;
                    if (!time.equals(fixTime)) {
                        fix = new Location(GPS_MOCK_PROVIDER);
                        fixTime = time;
                        long fixTimestamp = parseNmeaTime(time);
                        fix.setTime(fixTimestamp);
//						Log.v(LOG_TAG, "Fix: "+fix);
                    }
                    if (lat != null && !lat.equals("")) {
                        fix.setLatitude(parseNmeaLatitude(lat, latDir));
                    }
                    if (lon != null && !lon.equals("")) {
                        fix.setLongitude(parseNmeaLongitude(lon, lonDir));
                    }
                    if (speed != null && !speed.equals("")) {
                        fix.setSpeed(parseNmeaSpeed(speed, "N"));
                    }
                    if (bearing != null && !bearing.equals("")) {
                        fix.setBearing(Float.parseFloat(bearing));
                    }
                    long fixTimestamp = parseNmeaTime(time);
                    fix.setElapsedRealtimeNanos(fixTimestamp);
//					LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                    locationManager.setTestProviderLocation(GPS_MOCK_PROVIDER, fix);
                    return fix;
                } else if (status.equals("V")) {
//                    read_pubx=false;
                    return null;
                }
            } else if (command.equals("GNGSA")) {
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
                _locationDataModel.setMode(mode);
                // fix type  : 1 - no fix / 2 - 2D / 3 - 3D
                String fixType = splitter.next();
                // discard PRNs of satellites used for fix (space for 12)
                for (int i = 0; ((i < 12) && (!"1".equals(fixType))); i++) {
                    splitter.next();
                }
                // Position dilution of precision (float)
                String pdop = splitter.next();
               // _locationDataModel.setPDOP(pdop);

                // Horizontal dilution of precision (float)
                String hdop = splitter.next();
              //  _locationDataModel.setAltitude_MSL(String.valueOf(Float.parseFloat(hdop) * precision));
                // Vertical dilution of precision (float)
                String vdop = splitter.next();

            } else if (command.equals("GPVTG")) {
				/*  $GPVTG,054.7,T,034.4,M,005.5,N,010.2,K*48
					where:
					        VTG          Track made good and ground speed
					        054.7,T      True track made good (degrees)
					        034.4,M      Magnetic track made good
					        005.5,N      Ground speed, knots
					        010.2,K      Ground speed, Kilometers per hour
					        *48          Checksum
				 */
                // Track angle in degrees True
                String bearing = splitter.next();
                // T
                splitter.next();
                // Magnetic track made good
                String magn = splitter.next();
                // M
                splitter.next();
                // Speed over the ground in knots
                String speedKnots = splitter.next();
                // N
                splitter.next();
                // Speed over the ground in Kilometers per hour
                String speedKm = splitter.next();
                // K
                splitter.next();
                // for NMEA 0183 version 3.00 active the Mode indicator field is added
                // Mode indicator, (A=autonomous, D=differential, E=Estimated, N=not valid, S=Simulator )
            } else if (command.equals("GPGLL")) {
				/*  $GPGLL,4916.45,N,12311.12,W,225444,A,*1D

					Where:
					     GLL          Geographic position, Latitude and Longitude
					     4916.46,N    Latitude 49 deg. 16.45 min. North
					     12311.12,W   Longitude 123 deg. 11.12 min. West
					     225444       Fix taken at 22:54:44 UTC
					     A            Data Active or V (void)
					     *iD          checksum data
				 */
                // latitude ddmm.M
                String lat = splitter.next();
                // direction (N/S)
                String latDir = splitter.next();
                // longitude dddmm.M
                String lon = splitter.next();
                // direction (E/W)
                String lonDir = splitter.next();
                // UTC time of fix HHmmss.S
                String time = splitter.next();
                // fix status (A/V)
                String status = splitter.next();
                // for NMEA 0183 version 3.00 active the Mode indicator field is added
                // Mode indicator, (A=autonomous, D=differential, E=Estimated, N=not valid, S=Simulator )
            } else if (command.equals("GPGSV")) {

                // latitude ddmm.M
                String lat = splitter.next();
                // direction (N/S)
                String latDir = splitter.next();
                // sat view
                String sat_view = splitter.next();
                _locationDataModel.setViewed_satellite(sat_view);
                // direction (E/W)
                String lonDir = splitter.next();
                // UTC time of fix HHmmss.S
                String time = splitter.next();
                // fix status (A/V)
                String status = splitter.next();
                // for NMEA 0183 version 3.00 active the Mode indicator field is added
                // Mode indicator, (A=autonomous, D=differential, E=Estimated, N=not valid, S=Simulator )

            }
        } else {
//			Log.e("check match=","Mis match case");
        }
        return null;

    }
    public static String NMEAGPRMCDate(long d)
    {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        Date date = new Date(d*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd  yyyy "); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        _locationDataModel.setRMCDate(String.valueOf(sdf.format(date)));
        return sdf.format(date);

       /* String date3 = DateFormat.format(" MMM dd  yyyy ", cal).toString(); // the format of your date
        // sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        _locationDataModel.setRMCDate(date3);*/
    }
    public double parseNmeaLatitude(String lat, String orientation) {
        double latitude = 0.0;
        if (lat != null && orientation != null && !lat.equals("") && !orientation.equals("")) {
            double temp1 = Double.parseDouble(lat);
            double temp2 = Math.floor(temp1 / 100);
            double temp3 = (temp1 / 100 - temp2) / 0.6;
            if (orientation.equals("S")) {
                latitude = -(temp2 + temp3);
            } else if (orientation.equals("N")) {
                latitude = (temp2 + temp3);
            }
//            Log.e("latitude=",latitude+"");
        }
        _locationDataModel.setLat(latitude);
        return latitude;
    }

    public double parseNmeaLongitude(String lon, String orientation) {
        double longitude = 0.0;
        if (lon != null && orientation != null && !lon.equals("") && !orientation.equals("")) {
            double temp1 = Double.parseDouble(lon);
            double temp2 = Math.floor(temp1 / 100);
            double temp3 = (temp1 / 100 - temp2) / 0.6;
            if (orientation.equals("W")) {
                longitude = -(temp2 + temp3);
            } else if (orientation.equals("E")) {
                longitude = (temp2 + temp3);

            }

//            Log.e("longitude=",longitude+"");
        }
        //Set Data by Parsing...
        _locationDataModel.setLon(longitude);
        System.out.println("AAA--->> " + longitude);
        //Set Data by Parsing...
        // TrackApplication.nav_lat_val = String.valueOf(longitude);
        return longitude;
    }

    public float parseNmeaSpeed(String speed, String metric) {
        float meterSpeed = 0.0f;
        if (speed != null && metric != null && !speed.equals("") && !metric.equals("")) {
            float temp1 = Float.parseFloat(speed) / 3.6f;
            if (metric.equals("K")) {
                meterSpeed = temp1;
            } else if (metric.equals("N")) {
                meterSpeed = temp1 * 1.852f;
            }
        }
        _locationDataModel.setSpeed(String.valueOf(meterSpeed));
        return meterSpeed;
    }

    public long parseNmeaTime(String time) {
        long timestamp = 0;
        SimpleDateFormat fmt = new SimpleDateFormat("HHmmss.SSS");
        fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            if (time != null && time != null) {
                long now = System.currentTimeMillis();
                long today = now - (now % 86400000L);
                long temp1;
                // sometime we don't have millisecond in the time string, so we have to reformat it
                temp1 = fmt.parse(String.format((Locale) null, "%010.3f", Double.parseDouble(time))).getTime();
                long temp2 = today + temp1;
                // if we're around midnight we could have a problem...
                if (temp2 - now > 43200000L) {
                    timestamp = temp2 - 86400000L;
                } else if (now - temp2 > 43200000L) {
                    timestamp = temp2 + 86400000L;
                } else {
                    timestamp = temp2;

                }
            }
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error while parsing NMEA time", e);
        }


        getDateFromUTCTimestamp(timestamp,OUTPUT_DATE_FORMATE);
     //   _locationDataModel.setTime(String.valueOf(now));
        return timestamp;
    }

    public byte computeChecksum(String s) {
        byte checksum = 0;
        for (char c : s.toCharArray()) {
            checksum ^= (byte) c;
        }
        return checksum;
    }

    public static final Creator<BluetoothGPSService> CREATOR = new Creator<BluetoothGPSService>() {
        public BluetoothGPSService createFromParcel(Parcel in) {
            return new BluetoothGPSService(in);
        }

        public BluetoothGPSService[] newArray(int size) {
            return new BluetoothGPSService[size];
        }
    };

    public BluetoothGPSService() {
    }

    private BluetoothGPSService(Parcel in) {
        readFromParcel(in);
    }


    public void writeToParcel(Parcel out, int flags) {
        out.writeInt((int) latitude);
        out.writeInt((int) longitude);

    }

    public void readFromParcel(Parcel in) {
        latitude = in.readInt();
        longitude = in.readInt();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeStringAsFile(final String fileContents, Context context) {
        try {
            FileOutputStream fos = context.openFileOutput("sxgeo_advanced_data.txt",
                    getApplicationContext().MODE_APPEND);
            //FileWriter out = new FileWriter(new File(context.getFilesDir(), "navic_data.txt"));
            fos.write(fileContents.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    public String getDateFromUTCTimestamp(long mTimestamp, String mDateFormate) {
        String date = null;
        try {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.setTimeInMillis(mTimestamp * 1000L);
            date = DateFormat.format(mDateFormate, cal.getTimeInMillis()).toString();

            SimpleDateFormat formatter = new SimpleDateFormat(mDateFormate);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(date);

            SimpleDateFormat dateFormatter = new SimpleDateFormat(mDateFormate);
            dateFormatter.setTimeZone(TimeZone.getDefault());
            date = dateFormatter.format(value);
            _locationDataModel.setTime(date);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }


    public void writeStringAsFile1(final String fileContents, Context context) {
        String DATA_PATH =
                Environment.getExternalStorageDirectory().toString() + "/SxgeoAdvance/sxgeodata.txt";
        File file = new File(DATA_PATH);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            writer.append(fileContents);
            writer.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onCreate() {


       /* Timer T=new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(), "Countdown " + Clicks, Toast.LENGTH_SHORT).show();
                       // myTextView.setText("count="+Clicks);
                        Clicks++;
                    }
                });
            }
        }, 1000, 1000);*/


        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);
    }

    //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Device found
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {

                //Device is now connected
                //  Toast.makeText(getApplicationContext(), contadorClicks, Toast.LENGTH_LONG).show();
                Toast.makeText(context, "You are now connected", Toast.LENGTH_LONG).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Done searching
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect

                Toast.makeText(context, "not connected", Toast.LENGTH_SHORT).show();

            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected
                Toast.makeText(context, "not connected", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public boolean connect(final String address) {
        if (btAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (newAddress != null && address.equalsIgnoreCase(newAddress)) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");

        }

        final BluetoothDevice device = btAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        // mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        newAddress = address;
        // mConnectionState = STATE_CONNECTING;
        return true;
    }

    /* creates BleWrapper object, set its parent activity and callback object */
    public BluetoothGPSService(Activity parent, BleWrapperUiCallbacks callback) {
        this.setDependencies(parent, callback);
    }

    public static BluetoothGPSService getInstance(Activity parent, BleWrapperUiCallbacks callback, LocationDataModel locationDataModel) {
        if (mInstance == null) {
            mInstance = new BluetoothGPSService(parent, callback);
        } else {
            mInstance.setDependencies(parent, callback);
        }
        _locationDataModel = locationDataModel;
        return mInstance;
    }

    public static BluetoothGPSService getInstance(Activity parent, BleWrapperUiCallbacks callback,String _macID) {
        if (mInstance == null) {
            //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            macID=_macID;
            mInstance = new BluetoothGPSService(parent, callback);
        } else {
            mInstance.setDependencies(parent, callback);
        }
        return mInstance;
    }

    public void setDependencies(Activity parent, BleWrapperUiCallbacks callback) {

        this.mParent = parent;
        mUiCallback = callback;
        if (mUiCallback == null) mUiCallback = NULL_CALLBACK;
    }
}
