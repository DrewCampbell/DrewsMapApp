package com.mti.ad220project3;







import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);


		
		Button btnOK2 = (Button) findViewById(R.id.btnOK2);
	    //Toast.makeText(getBaseContext(), "Seems to get here", Toast.LENGTH_SHORT).show();    	    

		
		btnOK2.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				
				startActivity(intent);
				
			}
			
			
		});					
			
			
			
		
		
	
	
	
	
	}





}
