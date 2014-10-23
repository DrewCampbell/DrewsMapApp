package com.mti.ad220project3;

//https://developers.google.com/maps/documentation/android/v1/hello-mapview

// for a key
// https://developers.google.com/maps/documentation/javascript/tutorial



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;




public class MainActivity extends ActionBarActivity implements LocationListener {

	
	//  Created by Andrew Campbell
	//  Submitting project on 8/8/2014
	//  Additions to project complete 10/25/14
	//  Project for mti add220 android application development
	//  Will try to submit this in play store under AD220Project3AndrewCa
	
	
	
	  static final LatLng SACRAMENTO = new LatLng(38.581572, -121.494400);	
	  static final LatLng MTI = new LatLng(38.661101, -121.341945);	
	  LatLng currentLocation;
	  LatLng testLocation;
	  
	  private GoogleMap map;
  	  private LocationManager locationManager;
  	  private SharedPreferences sharedPreferences;	  
	  Geocoder geoCoder;
  	  
	  private String zoomLevel;
	  private int intZoomLevel;
	  private boolean trafficOnOff;
	  
	  double pLong;
	  double pLat;
	  double pAlt;

	  private long startTime;
	  LatLng oldLatLng;
	  
	  Boolean keepTracking;
	  Boolean positionTracking;

	  
	  protected PowerManager.WakeLock mWakeLock;
  	  
	  // Changed this for testing purposes
	  //protected static final int REQUEST_OK = 1;
	  protected static final int REQUEST_OK = 1234;

	  //  The following is used to detect audio input
	  private static final int RECORDER_SAMPLERATE = 8000;
	  private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	  private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	  //  This will hold a String value of the utterance
	  private String utterance;
	  //  This will hold the image associated with the utterance
	  private String associatedImage;
	  //  Will have the current selected file
	  private String associatedFile;
	  //  ArrayList of ObjectAssociation objects
	  private ArrayList<ObjectAssociation> associations;
	  
	  //  Set if you are training the speech recognition
	  private boolean trainingMode=false;
	  
	  //  This will store locations
	  ArrayList<LocationItem> locations = new ArrayList<LocationItem>();
	  
	  //  This will be used to change the visibility of menu items
	  MenuItem menuPositionTrackStart;
	  MenuItem menuPositionTrackStop;
	  MenuItem menuMarkLocation;	  
	  MenuItem menuPositionTrackOpen;
	  MenuItem menuPositionTrackSave;
	  MenuItem menuOpenTemplate;
	  MenuItem menuNewTemplate;

	  MenuItem menuTimeTrackStart;
	  MenuItem menuTimeTrackStop;	  
	  MenuItem menuTimeTrackOpen;
	  MenuItem menuTimeTrackSave;
	  
	  DatabaseConnector databaseConnect;  //  will be used to connect to our database
	  Cursor cursor;  //  Cursor to hold returns from database


	  
	  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        

        double thisLatitude;
        double thisLongitude;
        LatLng thisLocation = new LatLng(38.661101, -121.341945);
  	    
        associations = new ArrayList<ObjectAssociation>();
  	    
        keepTracking = false;
        
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, this);

		geoCoder = new Geocoder(getBaseContext());	  
        
        
        
        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapview)).getMap();

        // Will add these in the settings
        this.trafficOnOff = sharedPreferences.getBoolean("TrafficOnOff", false); 
        if(this.trafficOnOff==true) {
        	Log.e("traffic", "traffic is true");
            map.setTrafficEnabled(true);
            
        } else {
        	Log.e("traffic", "traffic is false");
            map.setTrafficEnabled(false);
            
        }
        	       
        
		this.zoomLevel = sharedPreferences.getString("ZoomLevel", "15");        
		this.intZoomLevel = Integer.valueOf(this.zoomLevel);
       	
		


		thisLatitude = Double.longBitsToDouble(sharedPreferences.getLong("latitude", Double.doubleToLongBits(38.661101)));
		thisLongitude = Double.longBitsToDouble(sharedPreferences.getLong("longitude", Double.doubleToLongBits(-121.341945)));
		thisLocation = new LatLng(thisLatitude, thisLongitude);
		
        //Marker sacramento = map.addMarker(new MarkerOptions().position(SACRAMENTO)
        //        .title("Sacramento"));
        //Marker mti = map.addMarker(new MarkerOptions().position(MTI)
        //        .title("MTI")    	
        //		.position(MTI)
        //		.title("MTI")
        //		.snippet("This is my school")
        //		.icon(BitmapDescriptorFactory
        //		.fromResource(R.drawable.smiley1)));
        
        
        


            // Move the camera instantly to MTI with a zoom of 3.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(thisLocation, 3));

            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomTo(this.intZoomLevel), 2000, null);
            //map.animateCamera(CameraUpdateFactory.zoomTo(5), 2000, null);
            
       
            
   
    	    
    	    
    }

	@Override  
	protected void onDestroy() {
		// We will close database here
		super.onDestroy();
		
	}                                                          
	  

    public void moveToLatLong(GoogleMap map, double lat, double lng) {
    	
    	LatLng point = new LatLng(lat, lng);
    	

	    //Toast.makeText(getBaseContext(), "lat=" + lat, Toast.LENGTH_SHORT).show();  
	    //Toast.makeText(getBaseContext(), "long=" + lng, Toast.LENGTH_SHORT).show();      	
    	
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 18));
        
	    //  Will comment this out to test
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, this.intZoomLevel));    	
    }

    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
	
		menuPositionTrackStart = menu.findItem(R.id.action_position_track_start); 
		menuPositionTrackStop = menu.findItem(R.id.action_position_track_stop); 
		menuMarkLocation = menu.findItem(R.id.action_mark_location); 		
		menuPositionTrackOpen = menu.findItem(R.id.action_position_track_open); 
		menuPositionTrackSave = menu.findItem(R.id.action_position_track_save); 		
		menuOpenTemplate = menu.findItem(R.id.action_position_track_open_template);
		menuNewTemplate = menu.findItem(R.id.action_position_track_new_template);
		
		menuTimeTrackStart = menu.findItem(R.id.action_time_track_start); 
		menuTimeTrackStop = menu.findItem(R.id.action_time_track_stop); 
		menuTimeTrackOpen = menu.findItem(R.id.action_time_track_open); 
		menuTimeTrackSave = menu.findItem(R.id.action_time_track_save); 		

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		Editor editor = sharedPreferences.edit();
		
		switch (item.getItemId()) {

		case R.id.action_settings:
			
			editor.putLong("latitude", Double.doubleToRawLongBits(pLat));
			editor.putLong("longitude", Double.doubleToRawLongBits(pLong)); 
			editor.commit();
			
			Intent settings = new Intent(this, SettingsActivity.class);
			startActivity(settings);
			break;
		case R.id.action_about:	  			
			
			editor.putLong("latitude", Double.doubleToRawLongBits(pLat));
			editor.putLong("longitude", Double.doubleToRawLongBits(pLong)); 
			editor.commit();
			
			Intent about = new Intent(this, AboutActivity.class);
			startActivity(about);
			break;	
		case R.id.action_help:
			
			editor.putLong("latitude", Double.doubleToRawLongBits(pLat));
			editor.putLong("longitude", Double.doubleToRawLongBits(pLong)); 
			editor.commit();
			
			Intent help = new Intent(this, HelpActivity.class);
			startActivity(help);
			break;				
		case R.id.action_move:

			break;
		case R.id.action_latlong:

			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.custom);
			//final Button btnGoto = (Button) findViewById(R.id.btnGoTo);
			//btnGoto.setOnClickListener(new View.OnClickListener() {
				//public void onClick(View v) {
					//double latitude, longitude;
					
					
					//EditText txtLatitude = (EditText) findViewById(R.id.txtLatitude);
					//EditText txtLongitude = (EditText) findViewById(R.id.txtLongitude);	
					
					//latitude = Double.valueOf(txtLatitude.getText().toString()).doubleValue();
					//longitude = Double.valueOf(txtLongitude.getText().toString()).doubleValue();
					
					
					//moveToLatLong(map, latitude, longitude);						
					
					//}
			//});

			
			//  Code copied from the web
			//final Button btnGoto = (Button) findViewById(R.id.btnGoTo);
	        //btnGoto.setOnClickListener(new View.OnClickListener() {
	        //    public void onClick(View v) {
	        //        // Perform action on click
	        //    }
	        //});

			
			
			final Button btnGoto = (Button) dialog.findViewById(R.id.btnGoTo);
			
								
			
			btnGoto.setOnClickListener(new View.OnClickListener() {
				
				
                @Override
                public void onClick(View v) {
  
                	
            		Editor editor = sharedPreferences.edit();	                	
                	
                	double latitude, longitude;
                	
                	EditText txtLatitude = (EditText) dialog.findViewById(R.id.txtLatitude);
					EditText txtLongitude = (EditText) dialog.findViewById(R.id.txtLongitude);	
					try {
					latitude = Double.valueOf(txtLatitude.getText().toString()).doubleValue();
					longitude = Double.valueOf(txtLongitude.getText().toString()).doubleValue();

                	
        			moveToLatLong(map, latitude, longitude);	
        			dialog.dismiss();

        			currentLocation = new LatLng(latitude, longitude);	
        					
        	        Marker currentMarker = map.addMarker(new MarkerOptions().position(currentLocation)
        	                .title("Current Location")    	
        	        		.snippet("(" + latitude + ",  " + longitude + ")")
        	        		.icon(BitmapDescriptorFactory
        	        		.fromResource(R.drawable.smiley1)));
					
    				editor.putLong("latitude", Double.doubleToRawLongBits(latitude));
    				editor.putLong("longitude", Double.doubleToRawLongBits(longitude));    			
    				
    				editor.commit();				

					
					}
					catch(Exception e) {

			    	    Toast.makeText(getBaseContext(), "invalid location Lat=" + txtLatitude + ", Long=" + txtLongitude, Toast.LENGTH_SHORT).show();    	    

						
					}
                }
            });
			
			
			
			
			
			final Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			
			btnCancel.setOnClickListener(new View.OnClickListener() {
			
                @Override
                public void onClick(View v) {			

		    	    //Toast.makeText(getBaseContext(), "Cancel Clicked", Toast.LENGTH_SHORT).show();    	    

        			dialog.dismiss();			
			
                }
            });
						
			
			dialog.show();
			
			break;
		case R.id.action_address:
			final Dialog dialog2 = new Dialog(this);
			dialog2.setContentView(R.layout.custom2);			
			
			final Button btnGoto2 = (Button) dialog2.findViewById(R.id.btnGoto2);
			
			btnGoto2.setOnClickListener(new View.OnClickListener() {
				

			
				
                @Override
                public void onClick(View v) {
  
                	
            		Editor editor = sharedPreferences.edit();	                	
                	
                	double latitude, longitude;
                	
                	EditText address1 = (EditText) dialog2.findViewById(R.id.txtAddress1);
					EditText address2 = (EditText) dialog2.findViewById(R.id.txtAddress2);	
					
					String firstAddress = address1.getText().toString();
					String secondAddress = address2.getText().toString();					
					String fullAddress = firstAddress + " " + secondAddress;

		    	    //Toast.makeText(getBaseContext(), fullAddress, Toast.LENGTH_SHORT).show();
					
					
					try {
					

						List<Address> geocodeResults = geoCoder.getFromLocationName(fullAddress, 1);
						double lat = 0f;
						double lon = 0f;		
								
						for(Address loc: geocodeResults) {
							lat = loc.getLatitude();
							lon = loc.getLongitude();						
						}

						if(lat!=0 && lon!=0) {

				    	    Toast.makeText(getBaseContext(), "Moving to lat: " + lat + ", long:" + lon, Toast.LENGTH_SHORT).show();    	    							
							
							
							moveToLatLong(map, lat, lon);	
		        			dialog2.dismiss();							

						
		    				editor.putLong("latitude", Double.doubleToRawLongBits(lat));
		    				editor.putLong("longitude", Double.doubleToRawLongBits(lon));  
		    				editor.commit();
		    				
						} else {
				    	    Toast.makeText(getBaseContext(), "invalid location: " + fullAddress, Toast.LENGTH_SHORT).show();    	    							
						}
						

	        			dialog2.dismiss();

	        			
	        		currentLocation = new LatLng(lat, lon);		
	        			
        	        Marker currentMarker = map.addMarker(new MarkerOptions().position(currentLocation)
        	                .title("Current Location")    	
        	        		.snippet("(" + lat + ",  " + lon + ")")
        	        		.icon(BitmapDescriptorFactory
        	        		.fromResource(R.drawable.smiley1)));
									
					
					
					}
					catch(Exception e) {

			    	    Toast.makeText(getBaseContext(), "invalid location: " + fullAddress, Toast.LENGTH_SHORT).show();    	    

						
					}
                }
            });

			
			
			final Button btnCancel2 = (Button) dialog2.findViewById(R.id.btnCancel2);
			
			btnCancel2.setOnClickListener(new View.OnClickListener() {
			
                @Override
                public void onClick(View v) {			

		    	    //Toast.makeText(getBaseContext(), "Cancel Clicked", Toast.LENGTH_SHORT).show();    	    

        			dialog2.dismiss();			
			
                }
            });			
			
			dialog2.show();			
			
			
			/*
			Geocoder geoCoder = new Geocoder(this);

			//  Trying this code here on thursday
			try {


				
				List<Address> geocodeResults = geoCoder.getFromLocationName("Yellowstone", 1);
				double lat = 0f;
				double lon = 0f;		
						
				for(Address loc: geocodeResults) {
					lat = loc.getLatitude();
					lon = loc.getLongitude();
				}

				moveToLatLong(map, lat, lon);				
				
				
			} catch (Exception e) {

	    	    Toast.makeText(getBaseContext(), "Exception", Toast.LENGTH_SHORT).show();    	    

			}
			
			
			*/
			
			
			
			break;

		case R.id.action_thislocation:

			
			double lat, lng;
            
            
            if(pLat!=0 && pLong!=0) {
            	moveToLatLong(map, pLat, pLong);			

            
				editor.putLong("latitude", Double.doubleToRawLongBits(pLat));
				editor.putLong("longitude", Double.doubleToRawLongBits(pLong));    
            	
				editor.commit();
            
            }
            else {
	    	    Toast.makeText(getBaseContext(), "Cannot locate GPS", Toast.LENGTH_SHORT).show();            	
            }
            
			break;

		//  New code to for position tracking
		//  Start the tracking
		case R.id.action_position_track_start:
    	    Toast.makeText(getBaseContext(), "Start voice tracking", Toast.LENGTH_SHORT).show(); 			

    	    
    	    menuPositionTrackStart.setVisible(false);
    	    menuPositionTrackStop.setVisible(true);    	    
    	    menuMarkLocation.setVisible(true);
    	    menuPositionTrackOpen.setVisible(false);
    	    menuPositionTrackSave.setVisible(false);    	    
    	    menuOpenTemplate.setVisible(false);
    	    menuNewTemplate.setVisible(false);
    	    break;

    	    
		// Stop the tracking	
		case R.id.action_position_track_stop:
    	    Toast.makeText(getBaseContext(), "Stop voice tracking", Toast.LENGTH_SHORT).show(); 
    	    
    	    menuPositionTrackStart.setVisible(true);
    	    menuPositionTrackStop.setVisible(false);   
    	    menuMarkLocation.setVisible(false);    	    
    	    menuPositionTrackOpen.setVisible(true);
    	    menuPositionTrackSave.setVisible(true);
    	    menuOpenTemplate.setVisible(true);
    	    menuNewTemplate.setVisible(true);
    	    break;
			
		//  mark the location
		case R.id.action_mark_location:
			
			positionTracking = true;

			
			
		    // Get the minimum buffer size required for the successful creation of an AudioRecord object. 
		    int bufferSizeInBytes = AudioRecord.getMinBufferSize( RECORDER_SAMPLERATE,
		                                                          RECORDER_CHANNELS,
		                                                          RECORDER_AUDIO_ENCODING
		                                                         ); 
			
			
			
			//while(positionTracking == true) {
				Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				//  This is how the original version had it. 
				//i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
				//  Another tutorial had this, which also worked maybe even better
				i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				// Not sure what this was doing exactly.  Not in my other demo version so I took it out
				//i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,10);				
				try {
					startActivityForResult(i, REQUEST_OK);
				} catch(Exception e) {
					Toast.makeText(this, "Error initializing speech to text engine", Toast.LENGTH_LONG).show();
				}
			
				ArrayList<String> info;
				info = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			
			//}
						
			
			
			break;
	
		case R.id.action_position_track_open:
			
		    final Dialog dialogOpen = new Dialog(this);
		    dialogOpen.setContentView(R.layout.customopen);
			
		    final ListView listViewFilesOpen = (ListView) dialogOpen.findViewById(R.id.listviewfiles);			
			final Button btnOpen = (Button) dialogOpen.findViewById(R.id.btnOpen);
		    final EditText txtOpenFileName = (EditText) dialogOpen.findViewById(R.id.txtFileName);	
			
			
		    //  Set the spinner to list the tables
		    ArrayAdapter adapterOpen = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		    adapterOpen.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		    
		    listViewFilesOpen.setAdapter(adapterOpen);
			
		    
		    //  Here we will show all the tables in the database			    
		    databaseConnect = new DatabaseConnector(getBaseContext());
		    databaseConnect.open();
		    cursor = databaseConnect.listAllTables();
    	    if (cursor.moveToFirst()) {
    	        while ( !cursor.isAfterLast() ) {
    	            adapterOpen.add(cursor.getString(0));
    	            cursor.moveToNext();
    	        }
    	    }
		        	   
    	    
		    databaseConnect.close();

		    
		    listViewFilesOpen.setClickable(true);

		    listViewFilesOpen.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				    
					String fileName = listViewFilesOpen.getItemAtPosition(position).toString();
					//Toast.makeText(getBaseContext(), fileName, Toast.LENGTH_LONG).show();
					
					txtOpenFileName.setText(fileName);
					
				}

		    });
			
			
			
			
			
			btnOpen.setOnClickListener(new View.OnClickListener() {
			
                @Override
                public void onClick(View v) {
                	
		    	  
		    	    //Toast.makeText(getBaseContext(), "Open Clicked", Toast.LENGTH_SHORT).show();                  	
		   
		    	    String tableName = txtOpenFileName.getText().toString();
		    	    
		    	    
		    	    databaseConnect = new DatabaseConnector(getBaseContext());
		    	    
		    	    databaseConnect.open();
		    			    	   
	    	    
		    	    map.clear();
		    	    final String root;
		    	    
		    	    root = Environment.getExternalStorageDirectory().getAbsolutePath();
		    	    
		    	    cursor = databaseConnect.returnData(tableName);
		    	     
		    	    
		    	    if (cursor.moveToFirst()) {
		    	        while ( !cursor.isAfterLast() ) {
		    	            //Toast.makeText(getBaseContext(), "LocID:"+ cursor.getString(0) +"Latitude:"+ cursor.getString(1) + ", Longitude:" + cursor.getString(2) + ", Seconds:" + cursor.getString(3) + "Alititude:" + cursor.getString(4) + "Image:" + cursor.getString(5), Toast.LENGTH_LONG).show();

		    	            //  Let's add icons onto map here

		    	        	LatLng newLatLng = new LatLng(cursor.getDouble(1), cursor.getDouble(2));

		    	        	/*
		    	        	if(cursor.getString(5).equals("grapes")) {
			    	            map.addMarker(new MarkerOptions()
	    	                    .title("Location")    	
	    	            		.position(newLatLng)
	    	            		.snippet("Time=" + cursor.getLong(3) + "Altitude=" + cursor.getDouble(4))
	    	            		.icon(BitmapDescriptorFactory
	    	            		.fromResource(R.drawable.grapes)
	    	            		));   		    	        		
		    	        	}
		    	            
		    	        	if(cursor.getString(5).equals("blackberries"))
		    	            map.addMarker(new MarkerOptions()
		    	                    .title("Location")    	
		    	            		.position(newLatLng)
		    	            		.snippet("Time=" + cursor.getLong(3) + "Altitude=" + cursor.getDouble(4))
		    	            		.icon(BitmapDescriptorFactory
		    	            		.fromResource(R.drawable.blackberry)
		    	            		));    
		    	   			*/
		    	        	String fileName;
	    	        		fileName = cursor.getString(5);
		    	        	try {

		    	        		map.addMarker(new MarkerOptions()
		    	        		.title("Location")    	
		    	        		.position(newLatLng)
		    	        		.snippet("Time=" + cursor.getLong(3) + "Altitude=" + cursor.getDouble(4))
		    	        		.icon(BitmapDescriptorFactory
		    	        		//.fromResource(R.drawable.blackberry)
		    	        		.fromPath(root + "/Pictures/icons/" + fileName)    	            		
		    	        				));
			    	            //Toast.makeText(getBaseContext(), "File name is : " + root + "/Pictures/icons/" + fileName, Toast.LENGTH_LONG).show();

		    	        	} catch(Exception e) {
			    	            Toast.makeText(getBaseContext(), "Error in file : " + root + "/Pictures/icons/" + fileName, Toast.LENGTH_LONG).show();
		    	        		
		    	        	}
		    	        	
		    	        	
		    	            
		    	            cursor.moveToNext();
		    	        }
		    	    }
		    	    
		    	    //Toast.makeText(getBaseContext(), "gets here", Toast.LENGTH_SHORT).show();  			    	    

		    	    databaseConnect.close();

        			dialogOpen.dismiss();
                      
                
                
                }
           

                
            });			
			
			
			
			final Button btnCancelOpen = (Button) dialogOpen.findViewById(R.id.btnCancel);			
			
			
			btnCancelOpen.setOnClickListener(new View.OnClickListener() {
			
                @Override
                public void onClick(View v) {			

		    	    Toast.makeText(getBaseContext(), "Cancel Clicked", Toast.LENGTH_SHORT).show();    	    

        			dialogOpen.dismiss();			
			
                }
            });
		    
		    dialogOpen.show();
			
			break;
			
		case R.id.action_position_track_save:
			
		    final Dialog dialogSave = new Dialog(this);
		    dialogSave.setContentView(R.layout.customsave);

		    final ListView listViewFilesSave = (ListView) dialogSave.findViewById(R.id.listviewfiles);
		    final EditText txtSaveFileName = (EditText) dialogSave.findViewById(R.id.txtFileName);
		    

		    //  Set the spinner to list the tables
		    ArrayAdapter adapterSave = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		    adapterSave.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		    
		    //spinnerFilesSave2.setAdapter(adapterSave);
		    listViewFilesSave.setAdapter(adapterSave);
		    
		    //  Here we will show all the tables in the database			    

		    databaseConnect = new DatabaseConnector(getBaseContext());
		    databaseConnect.open();
		    cursor = databaseConnect.listAllTables();
    	    if (cursor.moveToFirst()) {
    	        while ( !cursor.isAfterLast() ) {
    	            adapterSave.add(cursor.getString(0));
    	            cursor.moveToNext();
    	        }
    	    }
		      
		    databaseConnect.close();		    

		    listViewFilesSave.setClickable(true);

		    listViewFilesSave.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				    
					String fileName = listViewFilesSave.getItemAtPosition(position).toString();
					//Toast.makeText(getBaseContext(), fileName, Toast.LENGTH_LONG).show();
					
					txtSaveFileName.setText(fileName);
					
				}

		    });    
		    


		    
		    
		    Calendar c1 = Calendar.getInstance();      
		    String dateStamp = String.valueOf(c1.get(Calendar.YEAR)) + c1.get(Calendar.MONTH) + c1.get(Calendar.DAY_OF_MONTH) + c1.get(Calendar.HOUR_OF_DAY) + c1.get(Calendar.MINUTE);

		    
		    
		    txtSaveFileName.setText("postracking" + dateStamp);

		    
		    
		    
			final Button btnSave = (Button) dialogSave.findViewById(R.id.btnSave);
			
			btnSave.setOnClickListener(new View.OnClickListener() {
				
				
                @Override
                public void onClick(View v) {
                	
		    	    //Toast.makeText(getBaseContext(), "Save Clicked", Toast.LENGTH_SHORT).show();                  	

		    	    
		    	    //  Adding code here!!!!!!!!!!!!!!!!!
		    	    
		    	    
	               	final String databaseName;
                	
			    	    //Toast.makeText(getBaseContext(), "Save Clicked", Toast.LENGTH_SHORT).show();                  	 	
			    	    //Toast.makeText(getBaseContext(), "Clicked = " + spinnerFilesSave2.getSelectedItem(), Toast.LENGTH_SHORT).show();

			    	    
			    	    String tableName = txtSaveFileName.getText().toString();
			    	    //String tableName = "WhyIsThisNotWorking";
			    	    
			    	    databaseConnect = new DatabaseConnector(getBaseContext());

			    	    databaseConnect.open();
			    	
			    	    databaseConnect.dropTable(tableName);
			    	    databaseConnect.createTable(tableName);
			    	    		    	   
			    	    cursor = databaseConnect.listAllTables();
			    	    	    	    
			    	    databaseConnect.clearData(tableName);

			    	    double latitude;
			    	    double longitude;
			    	    long seconds;
			    	    double altitude;
			    	    String image;
			    	    String timeStamp;

			    	    //  size is now 0????  This was working before
			    	    //Toast.makeText(getBaseContext(), "Size is " + locations.size(), Toast.LENGTH_LONG).show();
			    	    
			    	    
			    	    for (LocationItem location : locations) {
			    	        latitude = location.getLatitude();
			    	        longitude = location.getLongitude();
			    	    	seconds = location.getSeconds();
			    	    	altitude = location.getAltitude();
			    	    	image = location.getImage();
			    	        timeStamp = location.getTimeStamp();
			    	    	
		    	            //Toast.makeText(getBaseContext(), tableName + " " +latitude + " " +  longitude + " " + seconds + " " + altitude + " " + image + " " + timeStamp, Toast.LENGTH_LONG).show();
			    	        Log.i("Testing", "errored here???" +  tableName + " " +latitude + " " +  longitude + " " + seconds + " " + altitude + " " + image + " " + timeStamp);
			    	    	databaseConnect.insertData(tableName, latitude, longitude, seconds,altitude, image, timeStamp);
			    	    }
	    	            Toast.makeText(getBaseContext(), "HERE?????????", Toast.LENGTH_LONG).show();
			    	   			    	    

			    	    cursor = databaseConnect.returnData(tableName);

			    	    if (cursor.moveToFirst()) {
			    	        while ( !cursor.isAfterLast() ) {
			    	            //Toast.makeText(getBaseContext(), "LocID:"+ cursor.getString(0) +"Latitude:"+ cursor.getString(1) + ", Longitude:" + cursor.getString(2) + ", Seconds:" + cursor.getString(3) + "Alititude:" + cursor.getString(4) + "Image:" + cursor.getString(5), Toast.LENGTH_LONG).show();
			    	            cursor.moveToNext();
			    	        }
			    	    }
			    	    
			    	    //Toast.makeText(getBaseContext(), "gets here", Toast.LENGTH_SHORT).show();  			    	    

			    	    databaseConnect.close();		    	    
			    	    
	        			dialogSave.dismiss();	
		    	     
		    	     	    
                }
           
            });	
		    
		    
		    final Button btnCancelSave = (Button) dialogSave.findViewById(R.id.btnCancel);
			
			
			btnCancelSave.setOnClickListener(new View.OnClickListener() {
			
                @Override
                public void onClick(View v) {			

		    	    Toast.makeText(getBaseContext(), "Cancel Clicked", Toast.LENGTH_SHORT).show();    	    

        			dialogSave.dismiss();			
			
                }
            });


			
			
			final Button btnDelete = (Button) dialogSave.findViewById(R.id.btnDelete);
			
			btnDelete.setOnClickListener(new View.OnClickListener() {
			
                @Override
                public void onClick(View v) {			

		    	    Toast.makeText(getBaseContext(), "Delete Clicked", Toast.LENGTH_SHORT).show();    	    

		    	    String tableName = txtSaveFileName.getText().toString();

		    	    databaseConnect = new DatabaseConnector(getBaseContext());
		    	    databaseConnect.open();
		    	    databaseConnect.dropTable(tableName);
		    	    databaseConnect.close();
		    	     
	
		    	    
		    	    listViewFilesSave.refreshDrawableState();

        			dialogSave.dismiss();
                
                }
            });
			
			
			
		    dialogSave.show();
			
			
			break;
			
		case R.id.action_position_track_open_template:



			
			final Dialog templateOpen = new Dialog(this);			    
		    templateOpen.setContentView(R.layout.open_template);			

		    
		    // First populate the listview with the file names

		    //  Cutting and pasting code - start

		    
		    final TextView textViewFile = (TextView) templateOpen.findViewById(R.id.textViewFile);
		    
		    final ListView listViewFiles = (ListView) templateOpen.findViewById(R.id.listviewfiles);
		    
		    //  Set the ListView to list the files
		    ArrayAdapter adapterFiles = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item);
		    adapterFiles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		    
	    		    
		    		             
    	  		    
    	    //  Here we will show all the files in the templates folder			    
		  
       	    final File[] listFilesOpen;

    	    final String rootOpen;
    	    
    	    rootOpen = Environment.getExternalStorageDirectory().getAbsolutePath();
    	    

    	    BitmapFactory.Options optionsOpen = new BitmapFactory.Options();
    	    optionsOpen.inPreferredConfig = Bitmap.Config.ARGB_8888;
    	    
    	    final File fileDirectory = new File(rootOpen + "/Android/data/templates");

    	    
    	   
 	         	     
            if (fileDirectory.isDirectory())
            {
            	
        	    
        	    listFilesOpen = fileDirectory.listFiles();
        	    
        	    
                for (int filePointer = 0; filePointer < listFilesOpen.length; filePointer++)
                {            	    

            	    adapterFiles.add(listFilesOpen[filePointer].getName());
                	   
                }  // end for
            	

            } 
            
            else {
        	    Toast.makeText(getBaseContext(), "Not currently a directory.  Making directory.", Toast.LENGTH_SHORT).show();

        	    fileDirectory.mkdirs();
            
            }
    	    
		    listViewFiles.setAdapter(adapterFiles);	
		    

		    //  set up listview to list files  files should be clickable
		    listViewFiles.setClickable(true);
		    listViewFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		        @Override
		        public void onItemClick(AdapterView<?> arg0, View view1, int position, long arg3) {

		        	//  onclick, get the associated file
		        	
		        	File pictureDirectory = new File(rootOpen + "/Android/data/templates");
	        	    File[] listFiles2 = pictureDirectory.listFiles();		        	
		        	
	        	    // return associated file
	        	    associatedFile = listFiles2[position].getName();
	        	    	
		            // reset all unselected items as white.  This is the only way I figured out how to do this.
		            for(int i =0; i<arg0.getChildCount(); i++) {
		            	arg0.getChildAt(i).setBackgroundColor(Color.WHITE);
		            }	  
		            
		            arg0.getItemAtPosition(position);
								    		                		
                	listViewFiles.setItemChecked(position, true);
                    view1.setBackgroundColor(Color.BLUE);
	        	    
                    textViewFile.setText(associatedFile);
		            //Toast.makeText(getApplicationContext(),associatedFile,Toast.LENGTH_SHORT).show();                    

		        }
		    });
		    		    
		    
		    // This will be for cancel button
		    final Button btnCancelTemplate = (Button) templateOpen.findViewById(R.id.btnCancelTemplate);
			
			btnCancelTemplate.setOnClickListener(new View.OnClickListener() {
			
                @Override
                public void onClick(View v) {			

		    	    //Toast.makeText(getBaseContext(), "Cancel Clicked", Toast.LENGTH_SHORT).show();    	    

        			templateOpen.dismiss();			
			
                }
            });			    
		    
		    // This will be for open button
		    final Button btnOpenTemplate = (Button) templateOpen.findViewById(R.id.btnOpenTemplate);
			
			btnOpenTemplate.setOnClickListener(new View.OnClickListener() {
			
                @Override
                public void onClick(View v) {			

		    	    //Toast.makeText(getBaseContext(), "Open Clicked", Toast.LENGTH_SHORT).show();    	    

                	//  Not sure if I will use these
                	double northMostLat=-90;
                	double southMostLat=90;
                	double eastMostLong=-180;
                	double westMostLong=180;
                	double midLat;
                	double midLong;
                	
                	
		    	    
		        	File myDir = getFilesDir();
		        	

		    	    //Toast.makeText(getBaseContext(), "File: " + fileDirectory + "/" +  textViewFile.getText().toString(), Toast.LENGTH_LONG).show();  		    	    
		    	    String inputFile = fileDirectory + "/" +  textViewFile.getText().toString();
		    	    
		    	    String inputString;  		    	    

		    	    
		    	    File file = new File(inputFile);
		    	    FileInputStream fin = null;
		    	    String fileContents = "";
	       	    		 
	    	    	byte fileContent[] = new byte[(int)file.length()];
		    	    
		    	    try {
			 
		                // create FileInputStream object
		    	    	fin = new FileInputStream(file);
		   
		    	    		             
		    	    	// Reads up to certain bytes of data from this input stream into an array of bytes.
		    	    	fin.read(fileContent);
		    	    	//create string from byte array
		    	    	fileContents = new String(fileContent);
		    	    	//System.out.println("File content: " + s);
			    	    
			    	    Toast.makeText(getBaseContext(), fileContents, Toast.LENGTH_LONG).show();  			    	    
			    	    //Toast.makeText(getBaseContext(), "here", Toast.LENGTH_SHORT).show();  
			    	    menuPositionTrackStart.setVisible(true);
			    	    menuPositionTrackOpen.setVisible(true);
	
					} catch(java.io.FileNotFoundException e) {
						
						Toast.makeText(getBaseContext(), "Invalid file name.", Toast.LENGTH_LONG).show();											
									
					}
					
					catch (Throwable t) {
						
						Toast.makeText(getBaseContext(), "Exception" + t.toString(), Toast.LENGTH_LONG).show();
						
					}
					
		    	    associations.clear();
		    	    String objectName;
		    	    String utterance;
		    	    
		    	    
		    	    //  parsing through contents of file
		    	    String[] objectString = fileContents.split("<name=");
			
		    	    for(int i = 1; i<objectString.length; i++) {
			    	    //Toast.makeText(getBaseContext(), "string = " + objectString[i], Toast.LENGTH_LONG).show();		    	    	
						
			    	  
			    	    objectName = objectString[i].substring(0, objectString[i].indexOf(">"));  	   
						ObjectAssociation myAssociation = new ObjectAssociation(objectName);			    	    
			    	    
			    	    //Toast.makeText(getBaseContext(), "object = " + objectName, Toast.LENGTH_LONG).show();				    	   
			    	    
			    	    String[] utterString = objectString[i].split("<utter=");
			    	    for(int j = 1; j<utterString.length; j++) {				    	    	
			    	    	utterance = utterString[j].substring(0,utterString[j].indexOf("></utter>"));
							myAssociation.addUtterance(utterance);			    	    	
			    	    	
			    	    	//Toast.makeText(getBaseContext(), "utter = " + utterance, Toast.LENGTH_LONG).show();				    	    
			    	    }
						associations.add(myAssociation);
		    	    }
		    	    		    	    
        			templateOpen.dismiss();			
			
                }
            });	
			

		    // This will be for cancel button
		    final Button btnDeleteTemplate = (Button) templateOpen.findViewById(R.id.btnDeleteTemplate);
			
			btnDeleteTemplate.setOnClickListener(new View.OnClickListener() {
			
                @Override
                public void onClick(View v) {			

		    	    //Toast.makeText(getBaseContext(), "Delete Clicked", Toast.LENGTH_SHORT).show();    	    


		    	    try {
		    	        // delete the original file
		    	        //new File(fileDirectory + "/" + "template20149151851").delete();  
		    	        new File(fileDirectory + "/" + textViewFile.getText().toString()).delete();  		    	        
		    	        
			    	    //Toast.makeText(getBaseContext(), "file is " + fileDirectory + "/" + "template20149151851", Toast.LENGTH_SHORT).show(); 
		    	    } catch (Exception e) {
		    	        Log.e("tag", e.getMessage());
		    	    }
		    	    
		    	    templateOpen.dismiss();			
			
                }
            });			    
		    
		    templateOpen.show();
		    
			break;

		case R.id.action_position_track_new_template:
		    final Dialog dialogNewTemplate = new Dialog(this);
		    dialogNewTemplate.setContentView(R.layout.custom_trainer);
					    
		    final ListView listViewImages = (ListView) dialogNewTemplate.findViewById(R.id.listviewimages);
		    final ImageView imageViewPicture = (ImageView) dialogNewTemplate.findViewById(R.id.imageViewPicture);

		    trainingMode = true;
		    
		    //  Set the ListView to list the files
		    ArrayAdapter adapterImages = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		    adapterImages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		    
	    		    
		    		            
    	    //Toast.makeText(getBaseContext(), "New Template Clicked", Toast.LENGTH_SHORT).show(); 
    	  		    
    	    //  Here we will show all the files in a certain folder			    
		  
       	    final File[] listFiles;

    	    final String root;
    	    
    	    root = Environment.getExternalStorageDirectory().getAbsolutePath();
    	    

    	    BitmapFactory.Options options = new BitmapFactory.Options();
    	    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    	    
    	    File pictureDirectory = new File(root + "/Pictures/icons");

 	         	     
            if (pictureDirectory.isDirectory())
            {
 
        	    //Toast.makeText(getBaseContext(), "It's a directory", Toast.LENGTH_SHORT).show();             	

        	    listFiles = pictureDirectory.listFiles();

        	    associatedImage = listFiles[0].getAbsolutePath();
                for (int picturePointer = 0; picturePointer < listFiles.length; picturePointer++)
                {            	    

            	    adapterImages.add(listFiles[picturePointer].getName());
                	   
            	    //Toast.makeText(getBaseContext(), listFiles[picturePointer].toString(), Toast.LENGTH_SHORT).show(); 
                }  // end for
            	
                if(listFiles.length!=0) {
                	Bitmap bitmap = BitmapFactory.decodeFile(listFiles[0].getAbsolutePath(), options);		    
                	imageViewPicture.setImageBitmap(bitmap);   
                }
            } else {
        	    Toast.makeText(getBaseContext(), "Not currently a directory.  Making directory.", Toast.LENGTH_SHORT).show();

        	    pictureDirectory.mkdirs();
            
            }
    	    
		    listViewImages.setAdapter(adapterImages);	
		    

		    //  set up listview to list files
		    listViewImages.setClickable(true);
		    listViewImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		        @Override
		        public void onItemClick(AdapterView<?> arg0, View view1, int position, long arg3) {

		        	
		        	//  on click we want to change what this associated Image is going to be
		        	
		    	    BitmapFactory.Options options = new BitmapFactory.Options();
		    	    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		        	
		        	File pictureDirectory = new File(root + "/Pictures/icons");
	        	    File[] listFiles2 = pictureDirectory.listFiles();		        	
		        	
	        	    //  This would get the entire path
	        	    associatedImage = listFiles2[position].getAbsolutePath();
	        	    //  Let's try to return just the file name.  This seems to be null.
	        	    associatedImage = listFiles2[position].getName();
	        	    		
	        	    
	        	    Bitmap bitmap = BitmapFactory.decodeFile(listFiles2[position].getAbsolutePath(), options);
                	imageViewPicture.setImageBitmap(bitmap); 


                	
		            // reset all unselected items as white.  This is the only way I figured out how to do this.
		            for(int i =0; i<arg0.getChildCount(); i++) {
		            	arg0.getChildAt(i).setBackgroundColor(Color.WHITE);
		            }	  
		            
		            arg0.getItemAtPosition(position);
								    		                		
                	listViewImages.setItemChecked(position, true);
                    view1.setBackgroundColor(Color.BLUE);
                	
		            //Toast.makeText(getApplicationContext(),"Clicked!",Toast.LENGTH_SHORT).show();
		        }
		    });
		    
		    //  trainer button associates image to voice input utterances
		    final Button btnTrainer = (Button) dialogNewTemplate.findViewById(R.id.btnTrainer);		    
		    
			btnTrainer.setOnClickListener(new View.OnClickListener() {
				
                @Override
                public void onClick(View v) {			

		    	    //Toast.makeText(getBaseContext(), "Trainer Clicked", Toast.LENGTH_SHORT).show();    	     
		    	    
		    	    
		    	    //  Here is where we want to train the voice recognition

					Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);	
					try {
						startActivityForResult(i, REQUEST_OK);
					} catch(Exception e) {
						//Toast.makeText(this, "Error initializing speech to text engine", Toast.LENGTH_LONG).show();
					}
				
					ArrayList<String> info;
					info = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
		    	    		    	    
		    	    
                }
            });				    
		    
		    
			// button to save utterances to a file  
			final Button btnSaveTemplate = (Button) dialogNewTemplate.findViewById(R.id.btnSaveTemplate);
		    
			btnSaveTemplate.setOnClickListener(new View.OnClickListener() {
				
                @Override
                public void onClick(View v) {			

		    	    Toast.makeText(getBaseContext(), "Confirm FileName to Save to", Toast.LENGTH_SHORT).show();    
		    	    
// ******************************************************************************************************************
		    	    //  This is the inner dialog box.  Open this new dialog box
		    	    //  This is the confirmation template activity
	
		    	    final Dialog confirmSaveTemplate = new Dialog(MainActivity.this);
				      
				    confirmSaveTemplate.setContentView(R.layout.save_template);			

				    
				    // First populate the listview with the file names

				    //  Cutting and pasting code - start
				    
				    final ListView listViewFiles = (ListView) confirmSaveTemplate.findViewById(R.id.listviewfiles);
				    
				    //  Set the ListView to list the files
				    ArrayAdapter adapterFiles = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item);
				    adapterFiles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		    
			    		    
				    		             
		    	  		    
		    	    //  Here we will show all the files in the templates folder			    
				  
		       	    final File[] listFiles;

		    	    final String root;
		    	    
		    	    root = Environment.getExternalStorageDirectory().getAbsolutePath();
		    	    

		    	    BitmapFactory.Options options = new BitmapFactory.Options();
		    	    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

				    //  End of cut and pasted code
				    final EditText textViewFiles = (EditText) confirmSaveTemplate.findViewById(R.id.txtFileName);
				    
				    Calendar c1 = Calendar.getInstance();      
				    String dateStamp = String.valueOf(c1.get(Calendar.YEAR)) + c1.get(Calendar.MONTH) + c1.get(Calendar.DAY_OF_MONTH) + c1.get(Calendar.HOUR_OF_DAY) + c1.get(Calendar.MINUTE);
				    //  Set the default name of the template file				    
				    textViewFiles.setText("template" + dateStamp);
		    	    
		    	    
		    	    
		    	    final File fileDirectory = new File(root + "/Android/data/templates");

		    	    
		    	   
		 	         	     
		            if (fileDirectory.isDirectory())
		            {
		            	
		        	    //Toast.makeText(getBaseContext(), "It's a directory", Toast.LENGTH_SHORT).show();             	
		        	    
		        	    listFiles = fileDirectory.listFiles();
		        	    
		        	    
		                for (int filePointer = 0; filePointer < listFiles.length; filePointer++)
		                {            	    

		            	    adapterFiles.add(listFiles[filePointer].getName());
		                	   
		            	    //Toast.makeText(getBaseContext(), listFiles[picturePointer].toString(), Toast.LENGTH_SHORT).show(); 
		                }  // end for
		            	

		            } 
		            
		            else {
		        	    Toast.makeText(getBaseContext(), "Not currently a directory.  Making directory.", Toast.LENGTH_SHORT).show();

		        	    fileDirectory.mkdirs();
		            
		            }
		    	    
				    listViewFiles.setAdapter(adapterFiles);	
				    

				    //  set up listview to list files  files should be clickable
				    listViewFiles.setClickable(true);
				    listViewFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				        @Override
				        public void onItemClick(AdapterView<?> arg0, View view1, int position, long arg3) {

				        	//  onclick, get the associated file
				        	
				        	File pictureDirectory = new File(root + "/Android/data/templates");
			        	    File[] listFiles2 = pictureDirectory.listFiles();		        	
				        	
			        	    //  This would get the entire path
			        	    associatedFile = listFiles2[position].getAbsolutePath();
			        	    //  Let's try to return just the file name.  This seems to be null.
			        	    associatedFile = listFiles2[position].getName();

				            // reset all unselected items as white.  This is the only way I figured out how to do this.
				            for(int i =0; i<arg0.getChildCount(); i++) {
				            	arg0.getChildAt(i).setBackgroundColor(Color.WHITE);
				            }	  
				            
				            arg0.getItemAtPosition(position);
										    		                		
		                	listViewFiles.setItemChecked(position, true);
		                    view1.setBackgroundColor(Color.BLUE);

						    textViewFiles.setText(associatedFile);
				            //Toast.makeText(getApplicationContext(),"Clicked!",Toast.LENGTH_SHORT).show();
				        }
				    });
				    				    
				    
				   
				    // This will be for cancel button
				    final Button btnCancelTemplate = (Button) confirmSaveTemplate.findViewById(R.id.btnCancelTemplate);
					
					btnCancelTemplate.setOnClickListener(new View.OnClickListener() {
					
		                @Override
		                public void onClick(View v) {			

				    	    //Toast.makeText(getBaseContext(), "Cancel Clicked", Toast.LENGTH_SHORT).show();    	    

		        			confirmSaveTemplate.dismiss();			
					
		                }
		            });	

				    final Button btnSaveTemplate = (Button) confirmSaveTemplate.findViewById(R.id.btnSaveTemplate);
					
					btnSaveTemplate.setOnClickListener(new View.OnClickListener() {
					
		                @Override
		                public void onClick(View v) {			

				    	    Toast.makeText(getBaseContext(), "Save Clicked", Toast.LENGTH_SHORT).show();    	    

				    	    trainingMode = false;
				    	    
				    	    //  Here is where we will actually save the template file
				    	    
				            String filename = textViewFiles.getText().toString();
				            String outputString="";
				            

				            //  create the string associations to save to a text file
				    		for(ObjectAssociation association : associations){
				    			outputString=outputString + "<name="+ association.getName() + ">\n";
				    			
				    			ArrayList<String> utterances = association.getAssociations();
					    		for(String utterance : utterances){
					    			outputString=outputString + "	<utter=" + utterance + "></utter>\n";					    			
					    			
					    		}
				    			outputString=outputString + "</name>\n";				    		
				    			
				    		}				            
				            
				            
			    		    Toast.makeText(getBaseContext(), outputString, Toast.LENGTH_LONG).show();				            
				            
				        	File myDir = getFilesDir();
				        	
				    	    Toast.makeText(getBaseContext(), "MyDir = " + fileDirectory, Toast.LENGTH_SHORT).show();   
				    	    
				            try {

				            	File secondFile = new File(fileDirectory + "/", filename);
				                if (secondFile.getParentFile().mkdirs()) {
				                    secondFile.createNewFile();
				                }
				                
				                FileOutputStream fos = new FileOutputStream(secondFile);

				                fos.write(outputString.getBytes());
				                fos.flush();
				                fos.close();
				    		    Toast.makeText(getBaseContext(), "File created", Toast.LENGTH_SHORT).show();
					    	    menuPositionTrackStart.setVisible(true);
					    	    menuPositionTrackOpen.setVisible(true);
				    		    
				            } catch (Exception e) {
				                e.printStackTrace();
				                
				    		    Toast.makeText(getBaseContext(), "File not created", Toast.LENGTH_SHORT).show();            
				            }
				    	    
				    	    
				    	    
		        			confirmSaveTemplate.dismiss();			
					
		                }
		            });	
				    
				    // This will be for delete button
				    final Button btnDeleteTemplate = (Button) confirmSaveTemplate.findViewById(R.id.btnDeleteTemplate);
					
					btnDeleteTemplate.setOnClickListener(new View.OnClickListener() {
					
						// Change this to whatever you want to name it
						String inputFile = textViewFiles.getText().toString();
						
		                @Override
		                public void onClick(View v) {			

				    	    //Toast.makeText(getBaseContext(), "Delete Clicked", Toast.LENGTH_SHORT).show();    	    

				    	    try {
				    	        // delete the original file
				    	        //new File(fileDirectory + "/" + inputFile).delete();  

				    	        new File(fileDirectory + "/" + textViewFiles.getText().toString()).delete(); 				    	        
					    	    //Toast.makeText(getBaseContext(), "Attempting to delete" + fileDirectory + "/" + inputFile, Toast.LENGTH_SHORT).show();
				    	    } catch (Exception e) {
				    	        Log.e("tag", e.getMessage());
				    	    }
				    	    
		        			confirmSaveTemplate.dismiss();			
					
		                }
		            });	
		    		
					
					
					confirmSaveTemplate.show();
		    	    
		    	    //  Might not need the following code....
				    
				    //  Check file system
		    	    //File pictureDirectory = new File(root + "/Pictures/icons");
		    	    //fileDirectory = new File(root + "/Android/data/templates");
		    	    if (fileDirectory.isDirectory()) {
		            	
		        	    //Toast.makeText(getBaseContext(), "It's a directory", Toast.LENGTH_SHORT).show();             	

		           	    File[] listFiles2;
		        	    listFiles2 = fileDirectory.listFiles();
		        		            	
		                for (int picturePointer = 0; picturePointer < listFiles2.length; picturePointer++)
		                {            	    
		                	 //This was for testing I guess.  Can probably delete this  
		            	    //Toast.makeText(getBaseContext(), listFiles2[picturePointer].toString(), Toast.LENGTH_SHORT).show(); 
		                }  // end for
		                
		            }
		    	    else {
		        	    Toast.makeText(getBaseContext(), "Not currently a directory.  Making directory.", Toast.LENGTH_SHORT).show();

		        	    fileDirectory.mkdirs();
		    	    }
		    	    
		    	    
		    	    //Toast.makeText(getBaseContext(), "Number Of Objects " + associations.size() , Toast.LENGTH_SHORT).show();    	
		    	    
		    		for(ObjectAssociation association : associations){
		    			//  Can probably delete this.  Looks like this was for testing
			    	    //Toast.makeText(getBaseContext(), association.getName(), Toast.LENGTH_SHORT).show();  
			    	    
		    		}
		    	    


		    		
		    		
		    	    
		    	    dialogNewTemplate.dismiss();			
			
                }
            });				
			
//***********************************  I think the previous is for the inner dialog box ******************************
			
		    final Button btnCancelPositionTrackOpen = (Button) dialogNewTemplate.findViewById(R.id.btnCancelTemplate);
			
			btnCancelPositionTrackOpen.setOnClickListener(new View.OnClickListener() {
			
                @Override
                public void onClick(View v) {			

		    	    Toast.makeText(getBaseContext(), "Cancel Clicked", Toast.LENGTH_SHORT).show();    	    

		    	    trainingMode = false;

		    	    dialogNewTemplate.dismiss();			
			
                }
            });			
		    
		    
		    
		    
		    dialogNewTemplate.show();
		    
			
			break;	
			
			
		case R.id.action_time_track_start:	

    	    Toast.makeText(getBaseContext(), "Start Tracking!", Toast.LENGTH_SHORT).show();    	    
    		
    	    menuTimeTrackStart.setVisible(false);
    	    menuTimeTrackStop.setVisible(true);
    	    menuTimeTrackSave.setVisible(false);
    	    menuTimeTrackOpen.setVisible(false);    	    

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  	    

    	    
        	startTime = System.currentTimeMillis();	
    	    

    	    // Need handler for callbacks to the UI thread
    	    final Handler mHandler = new Handler();

    	    keepTracking=true;
    	    // Create runnable for posting
    	    final Runnable mUpdateResults = new Runnable() {
    	        public void run() {
            		    	        	
    	        	updateResultsInUi();
    	        }
    	    };

    	    // Fire off a thread to do some work that we shouldn't do directly in the UI thread
            Thread t = new Thread() {
                public void run() {
                	while(keepTracking==true) {
                		try{
                			sleep(10000);
                			mHandler.post(mUpdateResults);
                		
                		} catch(Exception e) {
                			
                		}
                	}	
                }
            };
            t.start();   	    
    	    
			break;
		case R.id.action_time_track_stop:	

    	    Toast.makeText(getBaseContext(), "Stop Tracking!", Toast.LENGTH_SHORT).show();    	    

    	    menuTimeTrackStart.setVisible(true);
    	    menuTimeTrackStop.setVisible(false);
    	    menuTimeTrackSave.setVisible(true);
    	    menuTimeTrackOpen.setVisible(true); 
    	    
            //this.mWakeLock.release();
            //super.onDestroy();    	    
    	    
    	    
    	    keepTracking=false;
			break;			
		case R.id.action_time_track_open:	
						
			    final Dialog dialogOpen2 = new Dialog(this);			    
			    dialogOpen2.setContentView(R.layout.customopen);
			    
			    final ListView listViewFilesOpen2 = (ListView) dialogOpen2.findViewById(R.id.listviewfiles);
			    final EditText txtOpenFileName2 = (EditText) dialogOpen2.findViewById(R.id.txtFileName);	
			    
			    
			    //  Set the spinner to list the tables
			    ArrayAdapter adapterOpen2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
			    adapterOpen2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		    
			    listViewFilesOpen2.setAdapter(adapterOpen2);
				
			    
			    //  Here we will show all the tables in the database			    
			    databaseConnect = new DatabaseConnector(getBaseContext());
			    databaseConnect.open();
			    cursor = databaseConnect.listAllTables();
	    	    if (cursor.moveToFirst()) {
	    	        while ( !cursor.isAfterLast() ) {
	    	            adapterOpen2.add(cursor.getString(0));
	    	            cursor.moveToNext();
	    	        }
	    	    }
			        	   
	    	    
			    databaseConnect.close();

			    
			    listViewFilesOpen2.setClickable(true);

			    listViewFilesOpen2.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {

					    
						String fileName = listViewFilesOpen2.getItemAtPosition(position).toString();
						//Toast.makeText(getBaseContext(), fileName, Toast.LENGTH_LONG).show();
						
						txtOpenFileName2.setText(fileName);
						
					}

			    });  
			    
			    
	    	    
				final Button btnOpen2 = (Button) dialogOpen2.findViewById(R.id.btnOpen);	    	    
	    	    
				btnOpen2.setOnClickListener(new View.OnClickListener() {
					
					
	                @Override
	                public void onClick(View v) {
	                	
	                	double northMostLat=-90;
	                	double southMostLat=90;
	                	double eastMostLong=-180;
	                	double westMostLong=180;
	                	double midLat;
	                	double midLong;
	                	
	                	
			    	    //Toast.makeText(getBaseContext(), "Open Clicked", Toast.LENGTH_SHORT).show();                  	
			    	    String tableName = txtOpenFileName2.getText().toString();
			    	    
			    	    
			    	    databaseConnect = new DatabaseConnector(getBaseContext());
			    	    
			    	    databaseConnect.open();
			    			    	   
			    	    cursor = databaseConnect.listAllTables();
			    	    
		    	    
			    	    map.clear();
			    	    
			    	    cursor = databaseConnect.returnData(tableName);

			    	    if (cursor.moveToFirst()) {
			    	        while ( !cursor.isAfterLast() ) {
			    	            //Toast.makeText(getBaseContext(), "LocID:"+ cursor.getString(0) +"Latitude:"+ cursor.getString(1) + ", Longitude:" + cursor.getString(2) + ", Seconds:" + cursor.getString(3) + "Alititude:" + cursor.getString(4) + "Image:" + cursor.getString(5), Toast.LENGTH_LONG).show();

			    	        	// get east, west, northern and southern.  Used to zoom to correct location
			    	        	if(cursor.getDouble(1)>northMostLat) {
			    	        		northMostLat =cursor.getDouble(1); 
			    	        	}
			    	        	if(cursor.getDouble(1)<southMostLat) {
			    	        		southMostLat =cursor.getDouble(1); 
			    	        	}
			    	        	if(cursor.getDouble(2)>eastMostLong) {
			    	        		eastMostLong =cursor.getDouble(2); 
			    	        	}
			    	        	if(cursor.getDouble(2)<westMostLong) {
			    	        		westMostLong =cursor.getDouble(2); 
			    	        	}			    	        	
			    	        	
			    	        	//  Let's add icons onto map here
			    	        	LatLng newLatLng = new LatLng(cursor.getDouble(1), cursor.getDouble(2));
			    	        	
			    	            
			    	            map.addMarker(new MarkerOptions()
			    	                    .title("Location")    	
			    	            		.position(newLatLng)
			    	            		.snippet("Time=" + cursor.getLong(3) + "Altitude=" + cursor.getDouble(4))
			    	            		.icon(BitmapDescriptorFactory
			    	            		.fromResource(R.drawable.smiley1)
			    	            		));    
			    	   
			    	            if(oldLatLng!=null) {
			    	            	Polyline line = map.addPolyline(new PolylineOptions()
			    	            	.add(oldLatLng, newLatLng)
			    	            	.width(5)
			    	            	.color(Color.RED));
			    	            }
			    	            oldLatLng = newLatLng;
			    	            
			    	            
			    	            cursor.moveToNext();
			    	        }
			    	    

			    	       
			    	    
		            	//moveToLatLong(map, (eastMostLat+westMostLat)/2, (northMostLong + southMostLong)/2);

			    	    midLat = (northMostLat+southMostLat)/2;
						midLong = (eastMostLong + westMostLong)/2;
			    	    moveToLatLong(map, midLat,midLong);			    	    
			    	    
		            	//Toast.makeText(getBaseContext(), "long:" + eastMostLong  , Toast.LENGTH_SHORT).show();  		            	
			    	    }
			    	    
			    	    //Toast.makeText(getBaseContext(), "gets here", Toast.LENGTH_SHORT).show();  			    	    

			    	    databaseConnect.close();

	        			dialogOpen2.dismiss();
	        			
	                }
	           

	                
	            });		
			    
			    			    
			    
				final Button btnCancelOpen2 = (Button) dialogOpen2.findViewById(R.id.btnCancel);
				
				btnCancelOpen2.setOnClickListener(new View.OnClickListener() {
				
	                @Override
	                public void onClick(View v) {			

			    	    Toast.makeText(getBaseContext(), "Cancel Clicked", Toast.LENGTH_SHORT).show();    	    

	        			dialogOpen2.dismiss();			
				
	                }
	            });
			    
			    dialogOpen2.show();
			    
			break;
		case R.id.action_time_track_save:	
			//  This will save the user's track
			
		    final Dialog dialogSave2 = new Dialog(this);
		    dialogSave2.setContentView(R.layout.customsave);


		    
		    final ListView listViewFilesSave2 = (ListView) dialogSave2.findViewById(R.id.listviewfiles);
		    final EditText txtSaveFileName2 = (EditText) dialogSave2.findViewById(R.id.txtFileName);

		    //  Set the spinner to list the tables
		    ArrayAdapter adapterSave2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		    adapterSave2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		    
		    //spinnerFilesSave2.setAdapter(adapterSave);
		    listViewFilesSave2.setAdapter(adapterSave2);
		    
		    //  Here we will show all the tables in the database			    

		    databaseConnect = new DatabaseConnector(getBaseContext());
		    databaseConnect.open();
		    cursor = databaseConnect.listAllTables();
    	    if (cursor.moveToFirst()) {
    	        while ( !cursor.isAfterLast() ) {
    	            adapterSave2.add(cursor.getString(0));
    	            cursor.moveToNext();
    	        }
    	    }
		      
		    databaseConnect.close();		    

		    listViewFilesSave2.setClickable(true);

		    listViewFilesSave2.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				    
					String fileName = listViewFilesSave2.getItemAtPosition(position).toString();
					Toast.makeText(getBaseContext(), fileName, Toast.LENGTH_LONG).show();
					
					txtSaveFileName2.setText(fileName);
					
				}

		    });    
		    


		    
		    
		    Calendar c2 = Calendar.getInstance();      
		    String dateStamp2 = String.valueOf(c2.get(Calendar.YEAR)) + c2.get(Calendar.MONTH) + c2.get(Calendar.DAY_OF_MONTH) + c2.get(Calendar.HOUR_OF_DAY) + c2.get(Calendar.MINUTE);

		    
		    
		    txtSaveFileName2.setText("timetracking" + dateStamp2);
		    
		    
			final Button btnSave2 = (Button) dialogSave2.findViewById(R.id.btnSave);
			
			btnSave2.setOnClickListener(new View.OnClickListener() {
				
				
                @Override
                public void onClick(View v) {
                	
                	final String databaseName;
                	
		    	    Toast.makeText(getBaseContext(), "Save Clicked", Toast.LENGTH_SHORT).show();                  	 	
		    	    //Toast.makeText(getBaseContext(), "Clicked = " + spinnerFilesSave2.getSelectedItem(), Toast.LENGTH_SHORT).show();

		    	    
		    	    String tableName = txtSaveFileName2.getText().toString();
		    	    //String tableName = "WhyIsThisNotWorking";
		    	    
		    	    databaseConnect = new DatabaseConnector(getBaseContext());

		    	    databaseConnect.open();
		    	
		    	    databaseConnect.dropTable(tableName);
		    	    databaseConnect.createTable(tableName);
		    	    		    	   
		    	    cursor = databaseConnect.listAllTables();
		    	    
		    	    //if (cursor.moveToFirst()) {
		    	    //    while ( !cursor.isAfterLast() ) {
		    	    //        Toast.makeText(getBaseContext(), "Table Name=> "+cursor.getString(0), Toast.LENGTH_LONG).show();
		    	    //        cursor.moveToNext();
		    	    //    }
		    	    //}
		    	    
		    	    //databaseConnect.setTableName("ThisIsATest");		    	    
		    	    databaseConnect.clearData(tableName);

		    	    double latitude;
		    	    double longitude;
		    	    long seconds;
		    	    double altitude;
		    	    String image;
		    	    String timeStamp;
		    	    
		    	    for (LocationItem location : locations) {
		    	        latitude = location.getLatitude();
		    	        longitude = location.getLongitude();
		    	    	seconds = location.getSeconds();
		    	    	altitude = location.getAltitude();
		    	    	image = location.getImage();
		    	    	timeStamp = location.getTimeStamp();
		    	    	
		    	    	databaseConnect.insertData(tableName, latitude, longitude, seconds,altitude, image, timeStamp);
		    	    }
		    	    
		    	    
		    	    
		    	    //databaseConnect.insertData(38.661269, -121.342058, 30, 12, "blackberry");
		    	    //databaseConnect.insertData(38.677959, -121.176058, 150, 102, "grape");			    	    
		    	    
		    	    //  Right now it errors out when I run this
		    	    cursor = databaseConnect.returnData(tableName);

		    	    if (cursor.moveToFirst()) {
		    	        while ( !cursor.isAfterLast() ) {
		    	            Toast.makeText(getBaseContext(), "LocID:"+ cursor.getString(0) +"Latitude:"+ cursor.getString(1) + ", Longitude:" + cursor.getString(2) + ", Seconds:" + cursor.getString(3) + "Alititude:" + cursor.getString(4) + "Image:" + cursor.getString(5), Toast.LENGTH_LONG).show();
		    	            cursor.moveToNext();
		    	        }
		    	    }
		    	    
		    	    //Toast.makeText(getBaseContext(), "gets here", Toast.LENGTH_SHORT).show();  			    	    

		    	    databaseConnect.close();		    	    
		    	    
        			dialogSave2.dismiss();	
		    	    
                }
           

                
            });			    
		    
		    
		    
			final Button btnCancelSave2 = (Button) dialogSave2.findViewById(R.id.btnCancel);
			
			btnCancelSave2.setOnClickListener(new View.OnClickListener() {
			
                @Override
                public void onClick(View v) {			

		    	    Toast.makeText(getBaseContext(), "Cancel Clicked", Toast.LENGTH_SHORT).show();    	    

        			dialogSave2.dismiss();			
			
                }
            });


			final Button btnDelete2 = (Button) dialogSave2.findViewById(R.id.btnDelete);
			
			btnDelete2.setOnClickListener(new View.OnClickListener() {
			
                @Override
                public void onClick(View v) {			

		    	    Toast.makeText(getBaseContext(), "Delete Clicked", Toast.LENGTH_SHORT).show();    	    

		    	    String tableName = txtSaveFileName2.getText().toString();

		    	    databaseConnect = new DatabaseConnector(getBaseContext());
		    	    databaseConnect.open();
		    	    databaseConnect.dropTable(tableName);
		    	    databaseConnect.close();
			
		    	        
		    	    //  Don't think this next line does what I want it to do.  So will just dismiss the dialog.
		    	    listViewFilesSave2.refreshDrawableState();

        			dialogSave2.dismiss();
                }
            });
			
			
			
			
		    dialogSave2.show();			
		    break;
		 
		    

		}

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
    private void updateResultsInUi() {

        // Back in the UI thread -- update our UI e  lements based on the data in mResults
	    //Toast.makeText(getBaseContext(), "Test!", Toast.LENGTH_SHORT).show();    	    

	    
    	Date currentDate = new Date(System.currentTimeMillis());

    	Calendar calendar = Calendar.getInstance();
    	long currentTime = System.currentTimeMillis();	    
  
	    String timeStamp = String.valueOf(calendar.get(Calendar.YEAR)) + calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH) + calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);

     	
    	LatLng newLatLng = new LatLng(pLat, pLong);
    	
    
        map.addMarker(new MarkerOptions()
                .title("Current Location")    	
        		.position(newLatLng)
        		.snippet("Time=" + (currentTime-startTime)/1000 + "Altitude=" + pAlt)
        		.icon(BitmapDescriptorFactory
        		.fromResource(R.drawable.smiley1)
        		));    	

        //  Will save location information into a location object
        locations.add(new LocationItem(pLat, pLong, (currentTime-startTime)/1000, pAlt, "smiley1", timeStamp));        
        
        		// both of these worked
        		//.fromResource(R.drawable.blackberry)
        		//.fromResource(R.drawable.grapes)
        
        if(oldLatLng!=null) {
        	Polyline line = map.addPolyline(new PolylineOptions()
        	.add(oldLatLng, newLatLng)
        	.width(5)
        	.color(Color.RED));
        }
        oldLatLng = newLatLng;
    }
	
	
	
	
	
	@Override
	public void onLocationChanged(Location location) {

		
		if(location==null) 
			return;
		
		pLong = location.getLongitude();
		pLat = location.getLatitude();	
		pAlt = location.getAltitude();
		

		//moveToLatLong(map, pLat, pLong);
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        
	        boolean objectFound=false;
	        String correctObject = "";
	        
	        if (requestCode==REQUEST_OK  && resultCode==RESULT_OK) {
	        		ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	        		utterance = thingsYouSaid.get(0);
	        		
	    			//Toast.makeText(this, thingsYouSaid.get(0), Toast.LENGTH_LONG).show();	
	    			//Toast.makeText(this, "Length = " + thingsYouSaid.size(), Toast.LENGTH_LONG).show();
	    			Toast.makeText(this, associatedImage + " - " + utterance, Toast.LENGTH_LONG).show();	
	     
		    	    //   Do different things if you are training or tracking
	    			if(trainingMode) {
	    				addAssociation(associatedImage, utterance);
		    			//Toast.makeText(this, "Training mode", Toast.LENGTH_LONG).show();		    		
	    			} else {
		    			//Toast.makeText(this, "Not training mode", Toast.LENGTH_LONG).show();
	    				
	    				
		    			//  First go through each object association in arraylist
			    		for(ObjectAssociation association : associations){
			    			if(association.checkForAssociations(thingsYouSaid.get(0))) {
				    			Toast.makeText(this, "This is what you meant:" + association.getName(), Toast.LENGTH_LONG).show();
				    			objectFound = true;
				    			correctObject = association.getName();
			    			}			    			
			    			
			    		}	    				
			            locations.add(new LocationItem(pLat, pLong, 0, pAlt, correctObject, ""));        

	    			}
	    				
	    
	    			
	    			if(objectFound) {
		    			String root;
	    	    	    root = Environment.getExternalStorageDirectory().getAbsolutePath();

	    	    	    
		    			Toast.makeText(this, root + "/Pictures/icons/" + correctObject , Toast.LENGTH_LONG).show();	
		    			LatLng newLatLng = new LatLng(pLat, pLong);		    				
	    				Marker mti = map.addMarker(new MarkerOptions().position(newLatLng)
	    						.title("MTI")    	
	    						.position(newLatLng)
	    						.title("MTI")
	    						.snippet("test")
	    						.icon(BitmapDescriptorFactory
	    								//  from file does not appear to be working yet
	    								//  This is the right file path name....
	    								.fromPath(root + "/Pictures/icons/" + correctObject)));
	    								//.fromFile(root + "/Pictures/icons/" + correctObject)));	    				
	    								//.fromResource(R.drawable.grapes)));
	    								
	    	            		//Toast.makeText(getBaseContext(), "File is : " + root + "/Pictures/icons/" + correctObject, Toast.LENGTH_LONG).show();

	    				
	    				
	    			} else {
		    			Toast.makeText(this, "No object found for this utterance", Toast.LENGTH_LONG).show();	    				
	    			}
	    			
	    			    			
	    			
	    			if(thingsYouSaid.get(0).equals("done") ) {
	    				positionTracking = false;
	    			}	    	    	
	    	    	
	    			/*
	    			if(thingsYouSaid.get(0).equals("grape")||thingsYouSaid.get(0).equals("grapes")||thingsYouSaid.get(0).equals("great")||thingsYouSaid.get(0).equals("grace")||thingsYouSaid.get(0).equals("drapes") ) {
	    				Marker mti = map.addMarker(new MarkerOptions().position(newLatLng)
	    						.title("MTI")    	
	    						.position(newLatLng)
	    						.title("MTI")
	    						.snippet("test")
	    						.icon(BitmapDescriptorFactory
	    								.fromResource(R.drawable.grapes)));
	    				
		    	        locations.add(new LocationItem(pLat, pLong, 0, pAlt, "grapes", "This is just for test purposes right now"));
	    			}
	    			if(thingsYouSaid.get(0).equals("blackberry")||thingsYouSaid.get(0).equals("blackberries") ) {
	    				Marker mti = map.addMarker(new MarkerOptions().position(newLatLng)
	    						.title("MTI")    	
	    						.position(newLatLng)
	    						.title("MTI")
	    						.snippet("test")
	    						.icon(BitmapDescriptorFactory
	    								.fromResource(R.drawable.blackberry)));

		    	        locations.add(new LocationItem(pLat, pLong, 0, pAlt, "blackberries", "This is just for test purposes right now"));	    				
	    			}
	    			*/	    			
	        }
	    }
	
	
	public void addAssociation(String associatedImage, String utterance) {
		
		boolean associatedImageFound = false;
		boolean utteranceFound = false;

		
		for(ObjectAssociation association : associations){
			
			if (association.getName().equals(associatedImage)) {
				associatedImageFound = true;
			    //Toast.makeText(getBaseContext(), "Image was found", Toast.LENGTH_SHORT).show();	
				
				if(association.checkForAssociations(utterance)==true) {
				    //Toast.makeText(getBaseContext(), "Utterance was found", Toast.LENGTH_SHORT).show();	
					utteranceFound = true;
				} else {
				    //Toast.makeText(getBaseContext(), "Utterance wasn't found for image", Toast.LENGTH_SHORT).show();					
					
				    //  no utterance found.  Add to utterances
					association.addUtterance(utterance);					
				}
				
			}
			//  Testing this - seems to work right now.  Will thoroughly test this before deleting this
			//if (utteranceFound==false) {
			//    Toast.makeText(getBaseContext(), "Utterance wasn't found for image", Toast.LENGTH_SHORT).show();					
				
			    //  no utterance found.  Add to utterances
			//	association.addUtterance(utterance);
			//}
			
		}
		
		
		//  this image has not been associated with anything yet
		if(associatedImageFound==false) {

		    Toast.makeText(getBaseContext(), "Image wasn't found", Toast.LENGTH_SHORT).show();		
			
			ObjectAssociation myAssociation = new ObjectAssociation(associatedImage);
			myAssociation.addUtterance(utterance);
			
			associations.add(myAssociation);
			
		}

	    //Toast.makeText(getBaseContext(), "Size is " + associations.size(), Toast.LENGTH_SHORT).show();		
	
	}
	
	
	
	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}   
    
    
    
    
}

