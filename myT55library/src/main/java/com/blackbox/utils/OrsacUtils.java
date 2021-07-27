package com.blackbox.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.blackbox.myt55library.R;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class OrsacUtils {
    public static ProgressDialog progressDoalog;

    @SuppressLint("MissingPermission")
    public static String getIMEI(Activity activity) {
        TelephonyManager telephonyManager = (TelephonyManager) activity
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public static String getUNIQUEID(Activity activity){
         return Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboardFrom(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void toast(Activity activity, String textValue) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) activity.findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(textValue);

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }


    public static String getCurrentDate(DatePicker picker) {
        //String selectedDate = picker.getDayOfMonth() + "-" + ((picker.getMonth() + 1)) + "-" + picker.getYear();
        StringBuilder builder = new StringBuilder();
        builder.append(picker.getDayOfMonth() + "-");
        builder.append((picker.getMonth() + 1) + "-");
        builder.append(picker.getYear());
        return builder.toString();
    }

    public static void alert(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Device not registered")
                .setMessage("Your device not registered. Please contact with Admin with your Phone IMEI No: " + OrsacSharePref.getImeiNumber(activity))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        activity.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    /*public static void alertNoArtisan(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("No Artisan Found?")
                .setMessage("No Artisan lived in this place.Please Take your photo with Anganwadi Center and any one photo of Anganwadi Center," +
                        " Primary School," +
                        " Club Ghar," +
                        " Local resident etc")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                       // ((SelectNoArtisanCheckBox)activity).onChecked(true);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //binding.chkNoArtisanFound.setChecked(false);
                        dialog.cancel();
                        //((SelectNoArtisanCheckBox)activity).onChecked(false);
                    }
                }).show();
    }*/

//    public static void checkImei(Activity activity) {
//        if (InternetConnection.checkConnection(activity)) {
//            getCheckImeiNo(activity);
//        } else {
//            OrsacUtils.toast(activity, OrsacConstants.NETWORK_ERROR);
//        }
//    }

//    public static void getCheckImeiNo(final Activity activity) {
//        GetDataService service = RetrofitClientInstance.getRetrofitInstanceForMasterData().create(GetDataService.class);
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("imei_no", OrsacSharePref.getImeiNumber(activity));
//
//        RequestBody reqjson_data = RequestBody.create(MediaType.parse("multipart/form-data"), jsonObject.toString());
//
//        Call<User> call = service.getUserData(reqjson_data);
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call,
//                                   final Response<User> response) {
//                if (response != null && response.body() != null) {
//                    User user = (User) response.body();
//                    if (user != null
//                            && user.getStatus().equalsIgnoreCase("true")
//                            && !user.getSurveyorName().equalsIgnoreCase("")
//                            && !user.getSurveyorMobile().equalsIgnoreCase("")) {
//                        OrsacApplication.getDaoSession().getUserDao().deleteAll();
//                        OrsacApplication.getDaoSession().getUserDao().insert(user);
//                        apiCallForMasterDataSplashScreen(activity, MainActivity.class);
//                    } else {
//                        alert(activity);
//                        //OrsacUtils.toast(activity, OrsacConstants.IMEI_NO_NOT_REGISTER);
//                    }
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                OrsacUtils.toast(activity, t.getMessage() + "");
//            }
//        });
//    }



    public static boolean setIMEINumber(Activity activity) {
        if (!OrsacUtils.getIMEI(activity).equals("")) {
            OrsacSharePref.saveImeiNumber(OrsacUtils.getIMEI(activity), activity);
            return true;
        } else {
            toast(activity, "IMEI Number Found!");
            return false;
        }
    }

//    public static void insetDataToDb(Activity activity, Class<MainActivity> mainActivityClass){
//        if(InternetConnection.checkConnection(activity)){
//            apiCall(activity,mainActivityClass);
//        }else {
//            toast(activity,OrsacConstants.NETWORK_ERROR);
//        }
//
//    }

   /* public static void alertBack(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Confirm!")
                .setMessage("Are you sure you want to exit?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        activity.finish();
                        activity.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }*/

    /*public static void alertSubmit(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Confirm!")
                .setMessage("Are you want to save the artisan data?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        activity.finish();
                        activity.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }*/

    public static void checkGPSStatus(final Activity activity) {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if ( locationManager == null ) {
            locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex){}
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex){}
        if ( !gps_enabled && !network_enabled ){
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setMessage("GPS not enabled");
            dialog.setCancelable(false);
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //this will navigate user to the device location settings screen
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivity(intent);
                }
            });
            AlertDialog alert = dialog.create();
            alert.show();
        }
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getTimeStamp(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return String.valueOf(timestamp.getTime());
    }

    public static String IMAGE_TYPE_ID_PROOF="road_image";
    public static int IMAGE_TYPE_ID_PROOF_CODE=103;

    public static String TRIP_STATUS_START="0";
    public static String TRIP_STATUS_END="1";


    public static String TRIP_STATUS_START_INTERMEDIATE="0";
    public static String TRIP_STATUS_PAUSE_INTERMEDIATE="1";
    public static String TRIP_STATUS_PAUSE_END="2";

    public static String NO_STATUS="-1";

    public static String getLocalDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        return dateFormat.format(cal.getTime());
    }

    public static String getLocalTime(){
        DateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
        Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        return dateFormat1.format(cal1.getTime());
    }

    public static String getStringValue(String strValue){
        return strValue == null ? "" : strValue ;
    }

    public static String getStringValue(String strValue , String opValue){
        return strValue == null ? opValue : strValue ;
    }

    public static void hideSoftKeyboard(EditText input, Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    public static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    public static boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }
}
