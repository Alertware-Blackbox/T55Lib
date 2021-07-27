package com.blackbox.utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;

import com.blackbox.myt55library.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TrackApplication extends Application {

	public static String deviceID;
	public static final String APP_FOLDER_NAME = "SxgeoAdvance";
	public static final String MAP_FOLDER_NAME = "osmdroid";
	public static int Coordinate_VIEW_Format =0;
	public static int Coordinate_Printing_Format =0;
	public static int min_distance =0;
	public static int Datum_Format =0;
	public static int dist_view_format =0;
	public static int area_view_format=0;
	public static int speed_view_format=0;
	public static int helpflag=0;
	public static int min_accuracy_format=0;
	public static String nav_lat_val ="Na";
	public static String nav_lng_val ="";
	public static String nav_type ="";
	public static int type_sel=0;
	public static int mode=1;
	public static int onlinegps=0;
	public static int country_sel=0;
	public static int coor_sel=0;
	String db_loc_type =null, licenced = null, secret_number;
	public static final String PROJECT_IP_ADDRESS = "http://115.119.255.236/";
	public static String LOC_VIDEO_PATH ="/Videos/";
	public static String LOC_IMAGE_PATH = "/Images/";
	String app_folder_path =null,map_folder_path = null;
	private boolean secret_number_given = false;
	public static String TARGET_DB_PATH =null;
	private SharedPreferences preference =null;
	private String mapLicense_key = null;
	public static String LOC_SIG_PATH = "/Signatures/";
	private boolean error_external_path = false;

	private boolean error_internal_path = false;
	
	public TrackApplication() {
			
		deviceID="";
	}
	public class AddedValue{
		public String lat,lon,pdop,satcount,alti,timediff;
	}
	public class Track{
		public String track_id,track_name;
	}

	@Override
	public void onCreate() {
		super.onCreate();
//		TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//		String deviceId = mngr.getDeviceId();
//		deviceID = deviceId.substring(deviceId.length() - 7);
//		loadPreferences();
//		memoryPathSetting();
	}
	

	


	private void determineDatabasePath() 
	{
		String path=null,map_path = null;
		if (Build.MODEL.equals("WP 61"))
		{
			if(doesSDcardExist(GlobalConstant.WP61_EXTERNAL_PATH))
			{
				path = GlobalConstant.WP61_EXTERNAL_PATH + APP_FOLDER_NAME + "/";

				db_loc_type = GlobalConstant.EXTERNAL_MEMORY;
			}
			else
			{
				path = GlobalConstant.WP61_INTERNAL_PATH + APP_FOLDER_NAME + "/";
				db_loc_type = GlobalConstant.INTERNAL_MEMORY;
			}		
		}
		else
		{
			if(doesSDcardExist(GlobalConstant.WP60_EXTERNAL_PATH)){
				path = GlobalConstant.WP60_EXTERNAL_PATH + APP_FOLDER_NAME + "/";
				map_path = GlobalConstant.WP60_EXTERNAL_PATH+MAP_FOLDER_NAME+"/";
				db_loc_type = GlobalConstant.EXTERNAL_MEMORY;
			}
			else if(doesSDcardExist(GlobalConstant.EXTERNAL_PATH))
			{
				path = GlobalConstant.EXTERNAL_PATH + APP_FOLDER_NAME + "/";
				map_path = GlobalConstant.EXTERNAL_PATH+MAP_FOLDER_NAME+"/";
				db_loc_type = GlobalConstant.EXTERNAL_MEMORY;
			}
			else if(doesSDcardExist(GlobalConstant.WP60_INTERNAL_PATH)){
				path = GlobalConstant.WP60_INTERNAL_PATH + APP_FOLDER_NAME + "/";
				map_path = GlobalConstant.WP60_INTERNAL_PATH + MAP_FOLDER_NAME+"/";
				db_loc_type = GlobalConstant.INTERNAL_MEMORY;
			}
			else
			{
				path = GlobalConstant.INTERNAL_PATH + APP_FOLDER_NAME + "/";
				map_path = GlobalConstant.INTERNAL_PATH + MAP_FOLDER_NAME+"/";
				db_loc_type = GlobalConstant.INTERNAL_MEMORY;
			}
			
		}
		if(path !=null) savePath(path, db_loc_type,map_path);
	}
	
	void continue_With_Internal_Memory()
	{
		String folder_name = APP_FOLDER_NAME + "/";
		String use_path = null,use_map_path = null;
		if (Build.MODEL.equals("WP 61"))
		{
			use_path = GlobalConstant.WP61_INTERNAL_PATH;
		}
		else{
			use_path = GlobalConstant.INTERNAL_PATH;
		}
		
		File dir = new File(use_path, folder_name);
		if(!dir.exists())
			if(dir.mkdir())
			{
				app_folder_path = dir.getAbsolutePath() + "/";
				db_loc_type = GlobalConstant.INTERNAL_MEMORY;
				savePath(app_folder_path, db_loc_type,map_folder_path);
			}
			else
			{
				error_internal_path = true;
			}
			
	}
	private boolean doesSDcardExist(String sdcard_path)
	{
		String path = sdcard_path + APP_FOLDER_NAME + "/";
		File directory = new File(path);
		if (!directory.isDirectory())
		{
			return directory.mkdirs();
		}
	        
		return directory.canWrite();
	}
	
	private boolean isDirectoryExists(String path)
	{
		File dir = new File(path);
		return dir.exists();
	}
	
	public String getDeviceID() {
			return deviceID;
	}
	public String getformDeviceID() {
		if(deviceID.substring(0,1).equalsIgnoreCase("0")){
			return deviceID.substring(1,deviceID.length());
		}
		else {
			return deviceID;
		}
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	
	public String getSecretNumber()
	{
		
		return secret_number ;
	}
	public boolean ifSecretNumPresent()
	{
		return secret_number_given;
	}
	public String getAppStoragePath()
	{
		return app_folder_path;
	}
	public String getIMEI_number()
	{
		return deviceID;
	}
	public void setLicenceStatus(String status)
	{
		licenced = status;
	}
	public void setSecretNumSaved(String number)
	{
		secret_number_given = true ;
		secret_number = number;
	}
	public String getMemoryInUse()
	{
		return db_loc_type ;
	}
	public boolean ifPathError()
	{
		return error_external_path;
	}
	
	public void savePath(String path, String mem_tag, String mappath){
		
		File file = new File(path);
		if(!file.exists())
		{
			file.mkdir();
		}

		TARGET_DB_PATH = path;
		app_folder_path =path;
		map_folder_path=mappath;
		db_loc_type = mem_tag;
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(getString(R.string.pref_data_db_path), path);
		editor.putString(getString(R.string.pref_data_db_loc_type), mem_tag);
		editor.commit();		
	}
}
