package com.essentialab.apps.mapadebolsillo.db;

import java.util.ArrayList;

import com.essentialab.apps.mapadebolsillo.parser.entities.Agency;
import com.essentialab.apps.mapadebolsillo.parser.entities.Route;
import com.essentialab.apps.mapadebolsillo.parser.entities.Stop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
	//	cv.put("routWe", route.route_url);
		cv.put("routeColor", route.route_color);
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
	
	/**
	 * Get agency by agency_id
	 * @param String agency_id
	 * @return Agency object by agency_id from database
	 *
	 */
	public Agency getAgencyById(String agency_id){
		Agency agency = new Agency();
			
		Cursor result = db.query("agencies", null, "agency_id ='"+agency_id+"'",null, null, null, null);
		if(result.moveToFirst()){
			agency.agency_id = result.getString(0);
			agency.agency_name = result.getString(1);
			agency.agency_url = result.getString(2);
			agency.agency_timezone = result.getString(3);
			agency.agency_lang = result.getString(4);
			agency.agency_phone = result.getString(5); 
		}
		return agency;
	}
	
	/**
	 * Get all Agencies from database
	 * @return All Agencies
	 *
	 */
	public ArrayList<Agency> getAllAgencies(){
		ArrayList<Agency> agencies = new ArrayList<Agency>();
		
		Cursor result = db.query("agencies", null, null, null, null, null, null);
		
		if(result.moveToFirst()){
			do{
				Agency agency = new Agency();
				agency.agency_id = result.getString(1);
				agency.agency_name = result.getString(2);
				agency.agency_url = result.getString(3);
				agency.agency_timezone = result.getString(4);
				agency.agency_lang = result.getString(5);
				agency.agency_phone = result.getString(6); 
				
				agencies.add(agency);
			}while(result.moveToNext());
		}
		
		return agencies;
	}
	
	/**
	 * Get Route by route_id
	 * @param String route_id
	 * @return Route Object by route_id
	 *
	 */
	public Route getRouteById(String route_id){
		Route route = new Route();
		
		Cursor result = db.query("routes", null, "route_id ='"+route_id+"'", null, null, null, null);
		
		if(result.moveToFirst()){
			route.agency_id = result.getString(1);
			route.route_short_name = result.getString(2);
			route.route_long_name = result.getString(3);
			route.route_desc = result.getString(4);
			route.route_type = result.getString(5);
			//route.route_url = result.getString(6);
			route.route_color = result.getString(7);
			route.route_text_color = result.getString(8);
			route.route_bikes_allowed = result.getString(9);
			route.route_id = result.getString(10);
		}
		
		return route;
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
					//			"routWe text, null"+
								"routeColor text,"+
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
			
			db.execSQL("DROP TABLE IF EXIST routes");
			db.execSQL("DROP TABLE IF EXIST agencies");
			db.execSQL("DROP TABLE IF EXIST stops");
		}
	}
}
