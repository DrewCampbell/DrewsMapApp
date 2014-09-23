package com.mti.ad220project3;

public class LocationItem {

	double latitude;
	double longitude;
	long seconds;
	double altitude;
	String image;
	
	
	public LocationItem(double latitude, double longitude, long seconds, double altitude, String image) {
	
		this.latitude = latitude;
		this.longitude = longitude;
		this.seconds = seconds;
		this.altitude = altitude;
		this.image = image;
	
	}
	
		
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public void setSeconds(long seconds) {
		this.seconds = seconds;
	}	
	
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}	
	
	public void setImage(String image) {
		this.image = image;
	}
	
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public long getSeconds() {
		return seconds;
	}	
	
	public double getAltitude() {
		return altitude;
	}
	
	public String getImage() {
		return image;
	}
	
}
