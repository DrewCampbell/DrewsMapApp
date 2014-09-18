package com.mti.ad220project3;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;






public class TrackingThread extends Thread implements LocationListener {

	
	
	
	private GoogleMap map2;
	private MainActivity mainAct;
	
	public TrackingThread(GoogleMap map, MainActivity mainAct) {

		
		this.map2 = map;
		
	}


    			
	
	public void run(){

	
		
		double lat = 38.661101;
		double lng = -121.494400;
			
    	LatLng point = new LatLng(lat, lng);
		
    	try {
    		sleep(1000); 
        
    	} catch(Exception e) {
        	
        }
        
        
        
    	//map2.addMarker(new MarkerOptions()
    	//        .position(new LatLng(38.661101, -121.494400))
    	//        .title("Hello world"));


	}
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	
}




