package com.essentialab.apps.mapadebolsillo.db;

import com.essentialab.apps.mapadebolsillo.parser.entities.Agency;
import com.essentialab.apps.mapadebolsillo.parser.entities.Route;
import com.essentialab.apps.mapadebolsillo.parser.entities.Stop;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MapDBAdapter {
	// DB name
	public static final String DATABASE_NAME = "setravi";
	// DB version
	public static final int DATABASE_VERSION = 1;
	
	private SQLiteDatabase db;
	private final Context context;
	
	private MapDBHelper dbHelper;
	
	private boolean _isConnected = false;
	
	// Construct
	public MapDBAdapter(Context _context){
		context = _context;
		dbHelper = new MapDBHelper(context);
	}
	
	// Open connection to DB
	public void open() throws SQLException{
		db = dbHelper.getWritableDatabase();
		_isConnected = true;
	}
	
	// Close connection to DB
	public void close(){
		db.close();
		_isConnected = false;
	}
	
	// Status connection to DB
	public boolean isConnected(){
		return _isConnected;
	}
	
	// Empty Tables
	// Empty agencies table
	public void emptyAgenciesTable(){
		db.delete("agencies", null, null);
	}
	
	// Empty routes table
	public void emptyRoutesTable(){
		db.delete("routes", null, null);
	}
	
	// Empty stops table
	public void emptyStopsTable(){
		db.delete("stops", null, null);
	}
	
	// Insert agency
	// Private method
	private void _insertAgency(ContentValues cv){
		db.insert("agencies", null, cv);
	}
	// Public method
	public void insertAgency(Agency agency){
		ContentValues cv = new ContentValues();
		cv.put("agency_id", agency.agency_id);
		cv.put("agency_name", agency.agency_name);
		cv.put("agency_url", agency.agency_url);
		cv.put("agency_timezone", agency.agency_timezone);
		cv.put("agency_lang", agency.agency_lang);
		cv.put("agency_phone", agency.agency_phone);
		
		_insertAgency(cv);
	}
	
	// Insert route
	// Private method
	private void _insertRoute(ContentValues cv){
		db.insert("routes", null, cv);
	}
	// Public method
	public void insertRoute(Route route){
		ContentValues cv = new ContentValues();
		
		cv.put("agency_id", route.agency_id);
		cv.put("route_short_name", route.route_short_name);
		cv.put("route_long_name", route.route_long_name);
		cv.put("route_desc", route.route_desc);
		cv.put("route_type", route.route_type);
		cv.put("route_url", route.route_url);
		cv.put("route_color", route.route_color);
		cv.put("route_text_color", route.route_text_color);
		cv.put("route_bikes_allowed", route.route_bikes_allowed);
		cv.put("route_id", route.route_id);
		
		_insertRoute(cv);
	}
	
	// Insert Stop
	// Private method
	private void _insertStop(ContentValues cv){
		db.insert("stops", null, cv);
	}
	// Public method
	public void insertStop(Stop stop){
		ContentValues cv = new ContentValues();
		
		cv.put("stop_id", stop.stop_id);
		cv.put("stop_code", stop.stop_code);
		cv.put("stop_name", stop.stop_name);
		cv.put("stop_desc", stop.stop_desc);
		cv.put("stop_lat", stop.stop_lat);
		cv.put("stop_lon", stop.stop_lon);
		cv.put("zone_id", stop.zone_id);
		cv.put("stop_url", stop.stop_url);
		cv.put("location_type", stop.location_type);
		cv.put("parent_station", stop.parent_station);
		cv.put("wheelchair_boarding", stop.wheelchair_boarding);
		cv.put("stop_direction", stop.stop_direction);
		cv.put("route_id", stop.route_id);
		cv.put("to_stop_id", stop.to_stop_id);
		
		_insertStop(cv);
	}

	private static class MapDBHelper extends SQLiteOpenHelper{
		public MapDBHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		// Create DB
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("Creating Database", "OK");
			// Agencies Table
			String agencyTable = "create table agencies"+
								"(_id integer primary key autoincrement,"+
								"agency_id text,"+
								"agency_name text,"+
								"agency_url text,"+
								"agency_timezone text,"+
								"agency_lang text,"+
								"agency_phone text);";
			db.execSQL(agencyTable);
			
			// Routes Table
			String routeTable = "create table routes"+
								"(_id integer primary key autoincrement,"+
								"agency_id text,"+
								"route_short_name text,"+
								"route_long_name text,"+
								"route_desc text,"+
								"route_type text"+
								"route_url text,"+
								"route_color text,"+
								"route_text_color text,"+
								"route_bikes_allowed text,"+
								"route_id text);";
			db.execSQL(routeTable);
			
			// Stops Table
			String stopTable = "create table stops"+
								"(_id integer primary key autoincrement,"+
								"stop_id text,"+
								"stop_code text,"+
								"stop_name text,"+
								"stop_desc text,"+
								"stop_lat text,"+
								"stop_lon text,"+
								"zone_id text,"+
								"stop_url text,"+
								"location_type text,"+
								"parent_station text,"+
								"wheelchair_boarding text,"+
								"stop_direction text,"+
								"route_id text,"+
								"to_stop_id text);";
			db.execSQL(stopTable);
		}

		// Update DB if is necessary
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
	}
}
