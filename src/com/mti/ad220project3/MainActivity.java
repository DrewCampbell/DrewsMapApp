package com.mti.ad220project3;

//https://developers.google.com/maps/documentation/android/v1/hello-mapview

// for a key
// https://developers.google.com/maps/documentation/javascript/tutorial



import java.io.File;
import java.io.FileWriter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
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
	  
	  
	  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        

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
       	

        //Marker sacramento = map.addMarker(new MarkerOptions().position(SACRAMENTO)
        //        .title("Sacramento"));
        Marker mti = map.addMarker(new MarkerOptions().position(MTI)
                .title("MTI")    	
        		.position(MTI)
        		.title("MTI")
        		.snippet("This is my school")
        		.icon(BitmapDescriptorFactory
        		.fromResource(R.drawable.smiley1)));
        
        
        


            // Move the camera instantly to MTI with a zoom of 3.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(MTI, 3));

            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomTo(this.intZoomLevel), 2000, null);
            //map.animateCamera(CameraUpdateFactory.zoomTo(5), 2000, null);
            
                       
            //  Go to New York City.
            //moveToLatLong(map, 40.712784, -74.005941);

            //thisLocator.getLatitude();
            //thisLocator.getLongitude();            
            
    	    //Toast.makeText(this, "this latitude = " + thisLocator.getLatitude(), Toast.LENGTH_SHORT).show();
    	    //Toast.makeText(this, "this latitude = " + thisLocator.getLongitude(), Toast.LENGTH_SHORT).show();    	    
    	    
    	    
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
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId()) {

		case R.id.action_settings:
			Intent settings = new Intent(this, SettingsActivity.class);
			startActivity(settings);
			break;
		case R.id.action_about:
			Intent about = new Intent(this, AboutActivity.class);
			startActivity(about);
			break;	
		case R.id.action_help:
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

		    	    Toast.makeText(getBaseContext(), "Cancel Clicked", Toast.LENGTH_SHORT).show();    	    

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

		    	    Toast.makeText(getBaseContext(), "Cancel Clicked", Toast.LENGTH_SHORT).show();    	    

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
            }
            else {
	    	    Toast.makeText(getBaseContext(), "Cannot locate GPS", Toast.LENGTH_SHORT).show();            	
            }
            
			break;

			
		//  New code to for position tracking
		case R.id.action_position_track_start:
			
			positionTracking = true;



			
			
		    // Get the minimum buffer size required for the successful creation of an AudioRecord object. 
		    int bufferSizeInBytes = AudioRecord.getMinBufferSize( RECORDER_SAMPLERATE,
		                                                          RECORDER_CHANNELS,
		                                                          RECORDER_AUDIO_ENCODING
		                                                         ); 
			
			
			
			//while(positionTracking == true) {
				Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
				i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,10);				
				try {
					startActivityForResult(i, REQUEST_OK);
				} catch(Exception e) {
					Toast.makeText(this, "Error initializing speech to text engine", Toast.LENGTH_LONG).show();
				}
			
				ArrayList<String> info;
				info = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			
			//}
			
			//if(info.get(0).contains("test")) {
			//	Toast.makeText(this, "word detected", Toast.LENGTH_LONG).show();				
			//}	else {
			//	
			//	Toast.makeText(this, "word not detected", Toast.LENGTH_LONG).show();				
			//}
			

			
		
			
			
			
			
			
			
			break;
			
		case R.id.action_track_start:	

    	    Toast.makeText(getBaseContext(), "Start Tracking!", Toast.LENGTH_SHORT).show();    	    


            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	    //final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            //this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
            //this.mWakeLock.acquire();    	    

    	   // PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	   // PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
    	   // wl.acquire();
    	    
        	startTime = System.currentTimeMillis();	
    	    
    
    	    //TrackingThread t = new TrackingThread(map, this);
    	    //t.start();


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
		case R.id.action_track_stop:	

    	    Toast.makeText(getBaseContext(), "Stop Tracking!", Toast.LENGTH_SHORT).show();    	    


            //this.mWakeLock.release();
            //super.onDestroy();    	    
    	    
    	    
    	    keepTracking=false;
			break;			
		case R.id.action_track_save:	
			//  This will save the user's track
			
			
			AlertDialog.Builder adSave = new AlertDialog.Builder(this);
			
			adSave.setTitle("Save");
			adSave.setMessage("Save File");
			adSave.setCancelable(false);


		    adSave.setPositiveButton("OK", new DialogInterface.OnClickListener()
		    {
		        @Override
		        public void onClick(DialogInterface dialog, int whichButton)
		        {
		         
		        }
		    });
			

			
			
			adSave.create();
						
			
			adSave.show();

			
			
			
			File file;
			
			
			
	    	File myDir = getFilesDir();			

	    	try {			
	    		File trackFile = new File(myDir + "/track/", "test.trk");
	    		trackFile.createNewFile();

	    		FileWriter fw = new FileWriter(trackFile);
	    		fw.write("Test");
	    		fw.close();
	    		
	    		
	    	} catch(Exception e) {
	    		
	    	}

		
		}

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
    private void updateResultsInUi() {

        // Back in the UI thread -- update our UI elements based on the data in mResults
	    //Toast.makeText(getBaseContext(), "Test!", Toast.LENGTH_SHORT).show();    	    

	    
    	Date currentDate = new Date(System.currentTimeMillis());

    	Calendar calendar = Calendar.getInstance();
    	long currentTime = System.currentTimeMillis();	    
  
     	
    	LatLng newLatLng = new LatLng(pLat, pLong);
    	
    
        map.addMarker(new MarkerOptions()
                .title("Current Location")    	
        		.position(newLatLng)
        		.snippet("Time=" + (currentTime-startTime)/1000)
        		.icon(BitmapDescriptorFactory
        		.fromResource(R.drawable.smiley1)
        		));    	
    			
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
	        if (requestCode==REQUEST_OK  && resultCode==RESULT_OK) {
	        		ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

	        		
	    			Toast.makeText(this, thingsYouSaid.get(0), Toast.LENGTH_LONG).show();	
	    			Toast.makeText(this, "Length = " + thingsYouSaid.size(), Toast.LENGTH_LONG).show();
	    			
	    			LatLng newLatLng = new LatLng(pLat, pLong);

	    			if(thingsYouSaid.get(0).equals("done") ) {
	    				positionTracking = false;
	    			}	    	    	
	    	    	
	    			if(thingsYouSaid.get(0).equals("grape")||thingsYouSaid.get(0).equals("grapes")||thingsYouSaid.get(0).equals("great")||thingsYouSaid.get(0).equals("grace")||thingsYouSaid.get(0).equals("drapes") ) {
	    				Marker mti = map.addMarker(new MarkerOptions().position(newLatLng)
	    						.title("MTI")    	
	    						.position(newLatLng)
	    						.title("MTI")
	    						.snippet("test")
	    						.icon(BitmapDescriptorFactory
	    								.fromResource(R.drawable.grapes)));
	    			}
	    			if(thingsYouSaid.get(0).equals("blackberry")||thingsYouSaid.get(0).equals("blackberries") ) {
	    				Marker mti = map.addMarker(new MarkerOptions().position(newLatLng)
	    						.title("MTI")    	
	    						.position(newLatLng)
	    						.title("MTI")
	    						.snippet("test")
	    						.icon(BitmapDescriptorFactory
	    								.fromResource(R.drawable.blackberry)));
	    			}
	    				    			
	        }
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

