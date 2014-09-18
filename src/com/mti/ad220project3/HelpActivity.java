package com.mti.ad220project3;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Build;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		
		Button btnOK3 = (Button) findViewById(R.id.btnOK3);
	    //Toast.makeText(getBaseContext(), "Seems to get here", Toast.LENGTH_SHORT).show();    	    

		
		btnOK3.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				
				startActivity(intent);
				
			}
			
			
		});				
		
		
		
		

	}






}
