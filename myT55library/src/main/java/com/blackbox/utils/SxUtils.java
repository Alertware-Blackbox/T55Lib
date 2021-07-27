package com.blackbox.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;

import com.blackbox.t55.BluetoothGPSService;

import java.io.File;

import static com.blackbox.utils.OrsacSharePref.orsacpreference;
import static com.blackbox.utils.OrsacSharePref.saveSharePref;

public class SxUtils
{
	
		public static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in Meters
		public static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000; // in Milliseconds
		
		public static final long POINT_RADIUS = 1000; // in Meters
		public static final long PROX_ALERT_EXPIRATION = -1; 
	
		public String Def_project_id = "default";
		public String Def_project_tag_id = "1";
		public String Def_project_name = "Default";
		public String Def_bsp_vec_bounding_coord = "88.430916 22.567497 88.437660 22.574521" ;
		public String Def_bsp_vec_feature_type = "Multipolygon" ;
		public static int Def_projection = 4326 ;
		public String Def_vector_layer_name = "Default_Polygon" ;
		public String Def_raster_layer_name = "Default_Raster" ;
		public String Def_vec_file_extension = "_2.shp" ;
		public String Def_ras_file_extension = "_1.JPG" ;
		public String Def_raster_size = "1" ;
		public String Def_bsp_vec_obj_count = "93" ;
		public String vector_file_type = "ESRI Shapefile" ;
		public String raster_file_type = "JPG File" ;
		
		
		public String Def_raster_bsp_id = "1" ;
		public String Def_vector_bsp_id = "2" ;
		public String Def_poly_line_col = "32,53,187" ;
		public String Def_poly_fill_col = "255,255,254" ;
		public String Def_poly_obj_col = "rgb(0,20,0)" ;
		public String Def_poly_obj_fill_col = "rgb(247,221,75)";
		public String Def_obj_width = "1";
		
		public String Def_form_name_poly =  "Point" ;
		public String Def_form_id_poly = "0001" ;
		public String Def_obj_type =  "Point" ;
		
		public int Def_form_data_count =  5 ;
		
		public String[] Def_form_attri_labels =  new String[]{"Object Name", "Object Number", "Object", "Date of Collection",
																"Type of Object",	"Photo", "Video", "Collector Signature"};
		public String[] Def_form_attri_type =  new String[]{"Text", "Numeric", "Float", "Date","Drop-down", "Image","Video", "Signature"};
		public String[] Def_form_attri_mandatory =  new String[]{"Y", "N", "N", "Y", "N", "N","N", "N"};
//		public Object[] Def_form_attri_max =  new Object[]{"", "", 10000, "", "", ""};
		public String[] Def_form_dropdown_option = new String[]{"Default 1","Default 2"};
	    public static final String PAIRED_DEVICE_MACADDRESS = "paired_device_mac_address";
	     private static SharedPreferences sharedpreferences;
	     public static String macID="NA";
	  @SuppressLint("NewApi")
		boolean isinFlightMode(Context context)
		  {
			   if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
	          return Settings.Global.getInt(context.getContentResolver(),
	             Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
	      } else {
	          return Settings.System.getInt(context.getContentResolver(),
	                 Settings.System.AIRPLANE_MODE_ON, 0) != 0;
	      }
		  }
	  
	  

		 public static boolean isNetworkAvailable(Context ctx) {
				ConnectivityManager connMgr = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
			 return connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected() ||
					 connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();

		 }
		 
		 public ContentValues makeAttriSet(String proj_id, int attri_index)
		 {

				 ContentValues values = new ContentValues() ;
				 values.put("proj_id", proj_id) ;
				 values.put("form_id", Def_form_id_poly) ;
				 values.put("attri_id", (attri_index+1)) ;
				 values.put("attri_name", Def_form_attri_labels[attri_index]) ;
				 values.put("attri_manda", Def_form_attri_mandatory[attri_index]) ;
				 
				 values.put("attri_dupli", "N") ;
				 values.put("attri_type", Def_form_attri_type[attri_index]) ;
				 
				 values.put("attri_length", 50) ;
				 values.put("attri_min", 5) ;
				 values.put("attri_max", 10000) ;
				 values.put("attri_default", "") ;	
				 

			return values;
			
			 
		 }
		 public static void DeleteRecursive(File fileOrDirectory) {
			    if (fileOrDirectory.isDirectory())
			        for (File child : fileOrDirectory.listFiles())
			            DeleteRecursive(child);

			    fileOrDirectory.delete();
			}
	public double getArea(double areareceived)
	{
		double area;
		if(TrackApplication.area_view_format==0)
		{
			area=areareceived/10000;
		}
		else
		{
			area=areareceived*0.00024711;
		}
		return area;
	}
	public double getDist(double distance)
	{
		double dist;
		if(TrackApplication.dist_view_format==0)
		{
			dist=distance;
		}
		else
		{
			dist=distance/1000;
		}
		return dist;
	}
	public String getSpeed(String speedreceived)
	{
		double speed;
		String speedformated="";
		if(TrackApplication.dist_view_format==0)
		{
			speedformated=speedreceived;
		}
		else
		{
			speed= Double.parseDouble(speedreceived);
			speed=(speed*18)/5;
			speedformated= String.valueOf(speed);
		}
		return speedformated;
	}

	public static BluetoothGPSService mBluetoothLeService=null;
	public static boolean hasCon=false;
	public static String getPairedMac(Activity activity){
		saveSharePref(activity);
		return sharedpreferences.getString(PAIRED_DEVICE_MACADDRESS, "00:00:00:00:00");
	}
	public static void saveSharePref(Activity activity){
		sharedpreferences = activity.getSharedPreferences(orsacpreference,
				Context.MODE_PRIVATE);
	}
	//Application Selection
	public static void savePairedMac(Activity activity, String mac_address){
		saveSharePref(activity);
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putString(PAIRED_DEVICE_MACADDRESS, mac_address);
		editor.commit();
	}
		 
		 
}
