package com.mti.ad220project3;

import com.google.android.gms.internal.db;

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
	private static final String DATABASE_NAME = "locationinformation";
	private static final int DATABASE_VERSION = 1;
	private SQLiteDatabase database;  // database object
	private DatabaseOpenHelper databaseOpenHelper;  //  database helper
	private Context ctx;

	
	
	
	//  public constructor for DatabaseConnector
	public DatabaseConnector(Context context) {
		// create a new DatabaseOpenHelper
		this.ctx = context;
		databaseOpenHelper = new DatabaseOpenHelper(context); 
	
	}  //  end DatabaseConnector constructor
	

	
	
	private class DatabaseOpenHelper extends SQLiteOpenHelper {
	
		//  public constructor
		public DatabaseOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

			//  Gets here
			//Toast.makeText(ctx, "Helper Constructor", Toast.LENGTH_LONG).show();			
		}

		// creates table when the database is created
		@Override
		public void onCreate(SQLiteDatabase db) {
			// query to create a new table
			// Let's comment this out to see what happens
			//  This seems to do nothing!

			//  But never gets here
			Toast.makeText(ctx, "Here's where we try to create the table", Toast.LENGTH_LONG).show();			
			
			String createQuery = "CREATE TABLE testlocations" +
			"(_locid integer primary key autoincrement," +
			"latitude double, longitude double, seconds long, " +
			"altitude integer, image string);";
			
			try {
				db.execSQL(createQuery);  //execute the query
			} catch(SQLException e) {
				e.printStackTrace();
				Toast.makeText(ctx, "Errored connecting to database", Toast.LENGTH_LONG).show();
			}
				
		}  // end method onCreate

		
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS testlocations");
			
			onCreate(db);
			
		}  // end method onUpgrade
		
	}  //  end class DatabaseOpenHelper

	//  open the database connection
	public DatabaseConnector open() throws SQLException {
		//  create or open a database for reading/writing
		database = databaseOpenHelper.getWritableDatabase();
		return this;
	}  // end method open
	
	//  close the database connection
	public void close() {
		if(database != null)
			database.close();  // close the database connection
	}  // end method close

	

	

	//  inserts a new contact in the database
	public long insertData(double latitude, double longitude, long seconds, double altitude, String image) {
		
		ContentValues newPointInfo = new ContentValues();
		newPointInfo.put("latitude", latitude);
		newPointInfo.put("longitude", longitude);
		newPointInfo.put("seconds", seconds);
		newPointInfo.put("altitude", altitude);
		newPointInfo.put("image", image);
		
		return database.insertOrThrow("testlocations", null, newPointInfo);
	}  // end method insertContact	

	
	public Cursor returnData() {
		return database.query("testlocations", new String[] {"_locid", "latitude", "longitude", "seconds", "altitude", "image"}, null, null, null, null, null);
	}
	
	public void clearData() {
		//database.execSQL("delete * from testlocations");
		database.delete("testlocations", null, null);
	
	}
	
	public void createTable() {

		
		String createQuery = "CREATE TABLE testlocations2" +
		"(_locid integer primary key autoincrement," +
		"latitude double, longitude double, seconds long, " +
		"altitude integer, image string);";
		
		try {
			database.execSQL(createQuery);  //execute the query
		} catch(SQLException e) {
			e.printStackTrace();
			Toast.makeText(ctx, "Errored connecting to database", Toast.LENGTH_LONG).show();
		}
		
	}
	

	public void dropTable() {

		
		String createQuery = "DROP TABLE testlocations2";
		
		try {
			database.execSQL(createQuery);  //execute the query
		} catch(SQLException e) {
			e.printStackTrace();
			Toast.makeText(ctx, "Errored connecting to database", Toast.LENGTH_LONG).show();
		}
	}
	
	
	public Cursor listAllTables() {
		Cursor c;
		
		c = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
		//c = database.rawQuery("SELECT name FROM locationinformation WHERE type='table'", null);	
		
		return c;
	}
	
	
	
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
	
	
	
	
	
}  // end class DatabaseConnector
