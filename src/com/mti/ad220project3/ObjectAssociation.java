package com.mti.ad220project3;

import java.util.ArrayList;

import android.util.Log;

public class ObjectAssociation {

	private String name;
	private ArrayList<String> associations;
	private boolean utteranceFound;
	
	public ObjectAssociation(String name) {
		this.name = name;
		
		associations = new ArrayList<String>();
	}
	
	public String getName() {
		return this.name;
	}

	public boolean checkForAssociations(String utterance) {
		utteranceFound = false;
		
		String test1 = "test";
		String test2 = "test2";
		
		for (int i = 0; i < associations.size(); i++) {
			// for some reason this seems to always return true
			if(associations.get(i).equals(utterance)){
		        utteranceFound = true;
		        Log.i("testresults", utterance + " " + associations.get(i) );
		        
			}
			
			

		}
		
		return utteranceFound;
	}


	
	public void addUtterance(String utterance) {
		associations.add(utterance);
	}

	public ArrayList<String> getAssociations() {
		
		return associations;
	}

	public void clearAll() {
		//  Not sure if I will need this....
		associations.clear();
	}
}
