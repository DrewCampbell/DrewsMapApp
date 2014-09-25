package com.mti.ad220project3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseConnector {
	
	//  database name
	//  private static final String DATABASE_NAME = "UserLocationInfo";
	private static final String DATABASE_NAME = "UserContacts";	
	private SQLiteDatabase database;  // database object
	private DatabaseOpenHelper databaseOpenHelper;  //  database helper
	
	//  public constructor for DatabaseConnector
	public DatabaseConnector(Context context) {
		// create a new DatabaseOpenHelper
		databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, 1); 
	
	}  //  end DatabaseConnector constructor
	
	//  open the database connection
	public void open() throws SQLException {
		//  create or open a database for reading/writing
		database = databaseOpenHelper.getWritableDatabase();
	}  // end method open
	
	//  close the database connection
	public void close() {
		if(database != null)
			database.close();  // close the database connection
	}  // end method close
	

	//  inserts a new contact in the database
	public void insertContact(String name, String email, String phone, String state, String city) {
		
		ContentValues newContact = new ContentValues();
		newContact.put("name", name);
		newContact.put("email", email);
		newContact.put("phone", phone);
		newContact.put("street", state);
		newContact.put("city", city);
		
		open();  //  open the database
		database.insert("contacts", null, newContact);
		close();  // close the database
	}  // end method insertContact
	
	
	// updates a contact in the database
	// Don't think I will need this function
	public void updateContact() {
		
	}
	
	//  get a cursor will all information in the table
	public Cursor getAllContacts() {
		return  database.query("contacts", new String[] {"_id", "name"}, null, null, null, null, "name");		
	}

	//  Not sure if I will need this
	public Cursor getOneContact(long id) {
		return database.query("contacts", null, "_id=" + id, null, null, null, null);
	}
	
	public void deleteContact(long id) {
		open();
		database.delete("contacts", "_id=" + id, null);
	}
	

	
	
	
	
	private class DatabaseOpenHelper extends SQLiteOpenHelper {
	
		//  public constructor
		public DatabaseOpenHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		// creates table when the database is created
		@Override
		public void onCreate(SQLiteDatabase db) {
			// query to create a new table
			String createQuery = "CREATE TABLE contacts" +
			"(_id integer primary key autoincrement," +
			"name TEXT, email TEXT, phone TEXT, " +
			"street TEXT, city TEXT);";
			
			db.execSQL(createQuery);  //execute the query
		}  // end method onCreate

		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}  // end method onUpgrade
		
	}  //  end class DatabaseOpenHelper
	
}  // end class DatabaseConnector
