package com.mti.ad220project3;


import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

	
	private final int SPLASH_LENGTH = 3000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);


		/* New handler to start the Main-Activity
		 * and close this splash screen after some seconds.*/
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
				/* Create and intent that will start the Main-Activity. */
				Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
				SplashActivity.this.startActivity(mainIntent);
				SplashActivity.this.finish();
				
				
			}
			
		}, SPLASH_LENGTH);		
		
		
		
		
		
		
		
	}







}
