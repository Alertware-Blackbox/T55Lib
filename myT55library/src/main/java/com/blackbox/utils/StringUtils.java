package com.blackbox.utils;

import android.location.Location;
import android.os.Build;

import java.io.File;

public class StringUtils {

	  private StringUtils() {}
	  public static String arrayToString(String[] a, String separator) {
	    if (a == null || separator == null) {
	        return null;
	    }
	    StringBuffer result = new StringBuffer();
	    if (a.length > 0) {
	        result.append(a[0]);
	        for (int i=1; i < a.length; i++) {
	            result.append(separator);
	            result.append(a[i]);
	        }
	    }
	    return result.toString();
	  }
	  
	  public static String format_coordinate(Double coordinate){
			String ChangedText = Location.convert(coordinate,TrackApplication.Coordinate_VIEW_Format);
			if(ChangedText.contains(":"))
			{
				ChangedText = ChangedText.replaceFirst(":", "\u00B0"); // for degree
				
				if(ChangedText.contains(":"))
				{
					ChangedText = ChangedText.replaceFirst(":", "\'"); // for minute
					ChangedText = ChangedText + "\"";
				}
				else
				{
					ChangedText = ChangedText +  "\'";
				}
			}
			return ChangedText;
		}
	  public static String format_print_coordinate(Double coordinate){
			String ChangedText = Location.convert(coordinate,TrackApplication.Coordinate_Printing_Format);
			if(ChangedText.contains(":"))
			{
				ChangedText = ChangedText.replaceFirst(":", "°"); // for degree
				
				if(ChangedText.contains(":"))
				{
					ChangedText = ChangedText.replaceFirst(":", "\'"); // for minute
					ChangedText = ChangedText + "\"";
				}
				else
				{
					ChangedText = ChangedText +  "\'";
				}
			}
			return ChangedText;
		}

	  
	  public static String uptoTwoDecimal(String val){
		  String str = null;
		  str = String.format("%.2f", Double.parseDouble(val));
		  return str;
	  }
	  
	  void calculateSpace(){
			try
			{
				String directory  = null;
				if (Build.MODEL.equals("WP 61"))
				
				 directory = GlobalConstant.WP61_EXTERNAL_PATH;
				
				else
					directory = GlobalConstant.EXTERNAL_PATH;
				File path = new File(directory);
				long val = path.getFreeSpace() ;
				long val2 = path.getTotalSpace();
				long free_space_mb = val/(1000 * 1000); 
				long total_space_mb = val2/(1000 * 1000); 
				
				float free_space_gb = free_space_mb/1000 ; 
				float total_space_gb = total_space_mb/1000; 
				

			}
			catch(Exception j)
			{
				j.printStackTrace();
			} 
		}
	  
	
}
