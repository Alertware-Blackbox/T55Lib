package com.blackbox.t55;

import android.content.Context;
import android.util.AttributeSet;

public class GPSStateView extends IconView {

	public GPSStateView(Context context){
		this(context, null);
	}
	
	public GPSStateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDescription("GPS State");
	}
	
	public void stateOff(){
		
		//setImageResource(R.drawable.satred);
		setUnits("off");
	}
	
	public void stateOn(){
		//setImageResource(R.drawable.satyellow);
		setUnits("no lock");
	}
	
	public void stateLock(){
		//setImageResource(R.drawable.satgreen);
		setUnits("lock");
	}
}

