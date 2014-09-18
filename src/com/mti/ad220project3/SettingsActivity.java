package com.mti.ad220project3;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;



public class SettingsActivity extends Activity {



	
	private String array_spinner[];
	private String zoomLevel;
	private Boolean trafficOnOff;
	
	private SharedPreferences sharedPreferences;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);		

		this.zoomLevel = sharedPreferences.getString("ZoomLevel", "15");  
        this.trafficOnOff = sharedPreferences.getBoolean("TrafficOnOff", false); 		

        array_spinner=new String[21];
        for(int i = 0; i<21; i++) {
            array_spinner[i]= Integer.toString(i+1);
        }
   
				
		final Spinner spnZoom = (Spinner) findViewById(R.id.spnZoom);		
		
	    ArrayAdapter adapter = new ArrayAdapter(this,
	    	        android.R.layout.simple_spinner_item, array_spinner);		
		
	    spnZoom.setAdapter(adapter);		
		spnZoom.setSelection(14);
		
	    spnZoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { 
	
	        	zoomLevel = spnZoom.getSelectedItem().toString();
		        	
	        } 

	        public void onNothingSelected(AdapterView<?> adapterView) {
	            return;
	        } 
	    }); 
		

		Button btnOK = (Button) findViewById(R.id.btnOK);
	    //Toast.makeText(getBaseContext(), "Seems to get here", Toast.LENGTH_SHORT).show();    	    

		
		btnOK.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {

			    //Toast.makeText(getBaseContext(), "Zoom Level=" + zoomLevel, Toast.LENGTH_SHORT).show();    	    
				
				
				
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				
				startActivity(intent);
				
			}
			
			
		});				
		
		
		final Button btnSetZoom = (Button) findViewById(R.id.btnSetZoom);
		final Button btnZoomDefault = (Button) findViewById(R.id.btnZoomDefault);
		final RadioButton radTrafficOn = (RadioButton) findViewById(R.id.radTrafficOn);
		final RadioButton radTrafficOff = (RadioButton) findViewById(R.id.radTrafficOff);		
		final Editor editor = sharedPreferences.edit();	
		
		btnSetZoom.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				editor.putString("ZoomLevel", zoomLevel);					
				if(radTrafficOn.isChecked()) {
					editor.putBoolean("TrafficOnOff", true);					
				} else {
					editor.putBoolean("TrafficOnOff", false);					
				}
				
				editor.commit();					
				
				btnSetZoom.setVisibility(View.INVISIBLE);
				btnZoomDefault.setVisibility(View.INVISIBLE);
			}
			
		});		
	
		
		btnZoomDefault.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				editor.putString("ZoomLevel", "15");					
				editor.putBoolean("TrafficOnOff", false);	
				editor.commit();					
				
				btnSetZoom.setVisibility(View.INVISIBLE);
				btnZoomDefault.setVisibility(View.INVISIBLE);
			}
			
		});				
		
		
		
		
		
		
		
	}

	
	
	
	
	
	
	
}
