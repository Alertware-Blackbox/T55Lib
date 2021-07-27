package com.blackbox.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.widget.Toast;

public class SampleMocker {
    String providerName;
    Context ctx;

    /**
     * Class constructor
     *
     * @param name provider
     * @param ctx  context
     * @return Void
     */
    public SampleMocker(String name, Context ctx) {
        this.providerName = name;
        this.ctx = ctx;

        LocationManager lm = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);

        /*boolean isMock = false;
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            isMock = lm.isFromMockProvider();
        } else { // Old Android versions (<6)
            isMock = !Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
        }*/


       /* try {
            Log.d(TAG ,"Removing Test providers");
            lm.removeTestProvider(LocationManager.GPS_PROVIDER);
        } catch (IllegalArgumentException error) {
            Log.d(TAG,"Got exception in removing test  provider");
        }*/



        try
        {
            if (lm.getProvider("providerName") == null) {
                lm.addTestProvider(providerName,false, false, false, false, false,
                        true, true, 0, 1);
            }
                lm.setTestProviderEnabled(providerName, true);
                //lm.requestLocationUpdates("providerName",0,0,1,0,2);

        } catch(SecurityException e) {

            Toast.makeText(ctx, "Please enable the mock location in developer option !!", Toast.LENGTH_LONG).show();
            //throw new SecurityException("Not allowed to perform MOCK_LOCATION");
        }
    }

    /**
     * Pushes the location in the system (mock). This is where the magic gets done.
     *
     * @param lat latitude
     * @param lon longitude
     * @return Void
     */
    public void pushLocation(double lat, double lon,float accuracy,double altitude) {
        LocationManager lm = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);

        Location mockLocation = new Location(providerName);
        mockLocation.setLatitude(lat);
        mockLocation.setLongitude(lon);
        mockLocation.setAltitude(0);
        mockLocation.setTime(System.currentTimeMillis());
       // mockLocation.setAccuracy(accuracy);
        mockLocation.setSpeed(0);
        mockLocation.setBearing(0);
        //mockLocation.setAccuracy(accuracy);
        mockLocation.setAccuracy(accuracy);
        mockLocation.setAltitude(altitude);
        mockLocation.setElapsedRealtimeNanos(System.nanoTime());
        lm.setTestProviderLocation(LocationManager.GPS_PROVIDER, mockLocation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
         //   mockLocation.setBearingAccuracyDegrees(0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           // mockLocation.setVerticalAccuracyMeters(accuracy);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          //  mockLocation.setSpeedAccuracyMetersPerSecond(0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
          //  mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        lm.setTestProviderLocation(providerName, mockLocation);
    }

    /**
     * Removes the provider
     *
     * @return Void
     */
    public void shutdown() {
        LocationManager lm = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);
        lm.removeTestProvider(providerName);

        try {
            lm.removeTestProvider(providerName);
        } catch (IllegalArgumentException e) {
            // handle the error e.getMessage()
        }
    }
}
