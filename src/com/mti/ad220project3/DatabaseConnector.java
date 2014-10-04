package com.mti.ad220project3;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseConnector {
	
	//  database name
	private static final String DATABASE_NAME = "locationinformation";
	private String tableName;
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
			Toast.makeText(ctx, "Helper Constructor", Toast.LENGTH_LONG).show();			
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
			"altitude integer, image string, timestamp string);";
			
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

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	

	//  inserts a new contact in the database
	public long insertData(String tableName, double latitude, double longitude, long seconds, double altitude, String image, String timeStamp) {
		
		createTable(tableName);
		
		ContentValues newPointInfo = new ContentValues();
		newPointInfo.put("latitude", latitude);
		newPointInfo.put("longitude", longitude);
		newPointInfo.put("seconds", seconds);
		newPointInfo.put("altitude", altitude);
		newPointInfo.put("image", image);
		
		return database.insertOrThrow(tableName, null, newPointInfo);
	}  // end method insertContact	

	
	public Cursor returnData(String tableName) {
		return database.query(tableName, new String[] {"_locid", "latitude", "longitude", "seconds", "altitude", "image", "timestamp"}, null, null, null, null, null);
	}
	
	public void clearData(String tableName) {
		//database.execSQL("delete * from testlocations");
		database.delete(tableName, null, null);
	
	}
	
	public void createTable(String tableToCreate) {

		
		String createQuery = "CREATE TABLE " + tableToCreate +
		"(_locid integer primary key autoincrement," +
		"latitude double, longitude double, seconds long, " +
		"altitude integer, image string, timestamp string);";
		
		try {
			database.execSQL(createQuery);  //execute the query
		} catch(SQLException e) {
			e.printStackTrace();
			Toast.makeText(ctx, "Errored connecting to database", Toast.LENGTH_LONG).show();
			Toast.makeText(ctx, "Error creating table", Toast.LENGTH_LONG).show();
		}
		
	}
	

	public void dropTable(String tableName) {

		
		String createQuery = "DROP TABLE " + tableName;
		
		try {
			database.execSQL(createQuery);  //execute the query
		} catch(SQLException e) {
			e.printStackTrace();
			Toast.makeText(ctx, "Errored connecting to database", Toast.LENGTH_LONG).show();
			Toast.makeText(ctx, "Error dropping table", Toast.LENGTH_LONG).show();
		}
	}

	
	public void copyTable() {




		Calendar c1 = Calendar.getInstance();      
		String myfrmt = String.valueOf(c1.get(Calendar.YEAR)) + c1.get(Calendar.MONTH) + c1.get(Calendar.DAY_OF_MONTH) + c1.get(Calendar.HOUR_OF_DAY) + c1.get(Calendar.MINUTE);


		
		Toast.makeText(ctx, myfrmt, Toast.LENGTH_LONG).show();
		
		//String createQuery = "CREATE TABLE timetracking2" + myfrmt + " AS SELECT * FROM testlocations";
		String createQuery = "CREATE TABLE timetracking20141004 AS SELECT * FROM testlocations";		
		
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
