package com.blackbox.roadways.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.blackbox.roadways.R;

import static android.content.Context.MODE_PRIVATE;

public class StringUtils {

    private StringUtils() {
    }

    public static String arrayToString(String[] a, String separator) {
        if (a == null || separator == null) {
            return null;
        }
        StringBuffer result = new StringBuffer();
        if (a.length > 0) {
            result.append(a[0]);
            for (int i = 1; i < a.length; i++) {
                result.append(separator);
                result.append(a[i]);
            }
        }
        return result.toString();
    }

    public static String format_coordinate(Double coordinate) {
        String ChangedText = Location.convert(coordinate, 1);
        if (ChangedText.contains(":")) {
            ChangedText = ChangedText.replaceFirst(":", "\u00B0"); // for degree

            if (ChangedText.contains(":")) {
                ChangedText = ChangedText.replaceFirst(":", "\'"); // for minute
                ChangedText = ChangedText + "\"";
            } else {
                ChangedText = ChangedText + "\'";
            }
        }
        return ChangedText;
    }

    public static String format_print_coordinate(Double coordinate) {
        String ChangedText = Location.convert(coordinate, 1);
        if (ChangedText.contains(":")) {
            ChangedText = ChangedText.replaceFirst(":", "°"); // for degree

            if (ChangedText.contains(":")) {
                ChangedText = ChangedText.replaceFirst(":", "\'"); // for minute
                ChangedText = ChangedText + "\"";
            } else {
                ChangedText = ChangedText + "\'";
            }
        }
        return ChangedText;
    }


    public static String uptoTwoDecimal(String val) {
        String str = null;
        str = String.format("%.2f", Double.parseDouble(val));
        return str;
    }



    public static void setDevicesIDSharePreference(Context context, String devices_id) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("BREB_Pref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("devices_id", devices_id); // Storing string
        editor.commit();

    }

    public static String getDevicesIDSharePreference(Context context) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("BREB_Pref", 0); // 0 - for private mode
        return pref.getString("devices_id", ""); // getting String

    }




    public static void setCurrentPoleNumber(Activity activity, String pole_number){
        SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("BREB_Pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(POLE_NUMBER, pole_number);
        editor.apply();
    }

    public static String getCurrentPoleNumber(Activity activity){
        SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("BREB_Pref", MODE_PRIVATE);
        String pole_number = pref.getString(POLE_NUMBER, "");
        return pole_number;
    }


    public static void setUserName(Activity activity, String userName){
        SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("BREB_Pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(USERNAME, userName);
        editor.apply();
    }

    public static String getUserName(Activity activity){
        SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("BREB_Pref", MODE_PRIVATE);
        String pole_number = pref.getString(USERNAME, "");
        return pole_number;
    }

    public static void setUserPhoneNumber(Activity activity, String pole_number){
        SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("BREB_Pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(USER_PHONE, pole_number);
        editor.apply();
    }

    public static String getUserPhoneNumber(Activity activity){
        SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("BREB_Pref", MODE_PRIVATE);
        String pole_number = pref.getString(USER_PHONE, "");
        return pole_number;
    }

    public static void setLastSyncTime(Activity activity, String syncTime){
        SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("BREB_Pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(LAST_SYNC, syncTime);
        editor.apply();
    }

    public static String getLastSyncTime(Activity activity){
        SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("BREB_Pref", MODE_PRIVATE);
        String pole_number = pref.getString(LAST_SYNC, "");
        return pole_number;
    }


    public static String POLE_LIST="pole_list";
    public static String POLE_NUMBER="pole_number";
    public static String KV_SUBSTATION_33="33 KV Substation";
    public static String KV_SUBSTATION_33_NEW="132/33 KV Substation";
    public static String NEW_SURVEY="New Survey";
    public static String EDIT_CURRENT_SURVEY="Edit Survey";
    public static String INCOMPLETE_SURVEY="incomplete Survey";
    public static String USERNAME="user_name";
    public static String USER_PHONE="user_phone";
    public static String LAST_SYNC="last_sync";

    public static void openAlertDialogForDate(final Button tvDate, Activity activity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
        final DatePicker picker = (DatePicker) alertLayout.findViewById(R.id.dp);
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("Select Date");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //tvDate.setTextColor(Color.BLACK);
            }
        });

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
//                picker.clearFocus();
                tvDate.setText(getCurrentDate(picker));
//                tvDate.setTextColor(Color.BLACK);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public static String getCurrentDate(DatePicker picker) {
        //String selectedDate = picker.getDayOfMonth() + "-" + ((picker.getMonth() + 1)) + "-" + picker.getYear();
        StringBuilder builder = new StringBuilder();
        builder.append(picker.getYear()+ "-");
        builder.append(String.format("%02d", (picker.getMonth() + 1)) + "-");
        builder.append(String.format("%02d", picker.getDayOfMonth()));
        return builder.toString();
    }

    public static void alert(final Activity activity){
        new AlertDialog.Builder(activity)
                .setTitle("Device not registered")
                .setMessage("Your device not registered. Please contact with Admin with your Phone IMEI No: "+OrsacSharePref.getImeiNumber(activity))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        activity.finish();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }


}
