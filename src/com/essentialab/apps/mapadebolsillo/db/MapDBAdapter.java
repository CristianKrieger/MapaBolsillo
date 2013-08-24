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
	/**
	 * Initialize the database
	 * @param _context
	 */
	public MapDBAdapter(Context _context){
		context = _context;
		dbHelper = new MapDBHelper(context);
	}
	
	// Open connection to DB
	/**
	 * Open the connection with database
	 * @throws SQLException
	 */
	public void open() throws SQLException{
		db = dbHelper.getWritableDatabase();
		_isConnected = true;
	}
	
	// Close connection to DB
	/**
	 * Close the connection with database
	 */
	public void close(){
		db.close();
		_isConnected = false;
	}
	
	// Status connection to DB
	/**
	 * Check the status connection with database
	 * @return boolean 
	 */
	public boolean isConnected(){
		return _isConnected;
	}
	
	// Empty Tables
	// Empty agencies table
	/**
	 * Delete content from agencies table
	 */
	public void emptyAgenciesTable(){
		db.delete("agencies", null, null);
	}
	
	// Empty routes table
	/**
	 * Delete content from routes table
	 */
	public void emptyRoutesTable(){
		db.delete("routes", null, null);
	}
	
	// Empty stops table
	/**
	 * Delete content from stops table
	 */
	public void emptyStopsTable(){
		db.delete("stops", null, null);
	}
	
	// Delete DB data
	/**
	 * Remove all Tables
	 */
	public void clearDB(){
		emptyAgenciesTable();
		emptyRoutesTable();
		emptyStopsTable();
	}
	
	// Delete routes by agency
	/**
	 * Delete route by route_id from routes table
	 * @param route_id
	 */
	public void deleteRoutesByAgency(String agency_id){
		ArrayList<Route> routes = getRoutesByAgencyId(agency_id);
		for(int i=0;i<routes.size();i++){
			db.delete("routes", "route_id ='"+routes.get(i).route_id+"'", null);
			db.delete("stops", "route_id ='"+routes.get(i).route_id+"'", null);
		}
	}
	
	// Delete Agency, routes and stops from agency_id and route_id given
	/**
	 * Delete Agency and data related to agenci_id and route_id given
	 * @param agency_id
	 * @param route_id
	 */
	public void deleteAgency(String agency_id){
		deleteRoutesByAgency(agency_id);
		db.delete("agencies", "agency_id ='"+agency_id+"'", null);
	}
	
	
	// Delete route and stops from route id given
	/**
	 * Delete route and stops from route_id given
	 * @param route_id
	 */
	public void deleteRouteById(String route_id){
		db.delete("routes", "route_id ='"+route_id+"'", null);
		db.delete("stops", "route_id ='"+route_id+"'", null);
	}
	
	// Insert agency
	// Private method
	/**
	 * Insert data on agencies table
	 * @param cv
	 */
	private void _insertAgency(ContentValues cv){
		db.insert("agencies", null, cv);
	}
	// Public method
	/**
	 * Get data to insert on agencies table
	 * @param agency
	 */
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
	/**
	 * Insert data on routes table
	 * @param cv
	 */
	private void _insertRoute(ContentValues cv){
		db.insert("routes", null, cv);
	}
	// Public method
	/**
	 * Get data to insert on routes table
	 * @param route
	 */
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
	/**
	 * Insert data on stops table
	 * @param cv
	 */
	private void _insertStop(ContentValues cv){
		db.insert("stops", null, cv);
	}
	// Public method
	/**
	 * Get data to insert on stops table
	 * @param Stop
	 */
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
			agency.agency_id = result.getString(1);
			agency.agency_name = result.getString(2);
			agency.agency_url = result.getString(3);
			agency.agency_timezone = result.getString(4);
			agency.agency_lang = result.getString(5);
			agency.agency_phone = result.getString(6); 
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
	 * @param String 
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
			route.route_url = result.getString(6);
			route.route_color = result.getString(7);
			route.route_text_color = result.getString(8);
			route.route_bikes_allowed = result.getString(9);
			route.route_id = result.getString(10);
		}
		
		return route;
	}
	
	// Get routes by agency_id
	
	public ArrayList<Route> getRoutesByAgencyId(String agency_id){
		ArrayList<Route> routes = new ArrayList<Route>();
		
		Cursor result = db.query("routes", null, "agency_id ='"+agency_id+"'", null, null, null, null);
		if(result.moveToFirst()){
			do{
				Route route = new Route();
				route.agency_id = result.getString(1);
				route.route_short_name = result.getString(2);
				route.route_long_name = result.getString(3);
				route.route_desc = result.getString(4);
				route.route_type = result.getString(5);
				route.route_url = result.getString(6);
				route.route_color = result.getString(7);
				route.route_text_color = result.getString(8);
				route.route_bikes_allowed = result.getString(9);
				route.route_id = result.getString(10);
				
				routes.add(route);
			}while(result.moveToNext());
		}
		
		return routes;
	}
	// Get Stops by route
	public ArrayList<Stop> getStopsByRoute(String route_id){
		ArrayList<Stop> stops = new ArrayList<Stop>();
		Cursor result = db.query("stops", null, "route_id ='"+route_id+"'", null, null, null, null);
		if(result.moveToFirst()){
			do{
				Stop stop = new Stop();
				stop.stop_id = result.getString(1);
				stop.stop_code = result.getString(2);
				stop.stop_name = result.getString(3);
				stop.stop_desc = result.getString(4);
				stop.stop_lat = result.getString(5);
				stop.stop_lon = result.getString(6);
				stop.zone_id = result.getString(7);
				stop.stop_url = result.getString(8);
				stop.location_type = result.getString(9);
				stop.parent_station = result.getString(10);
				stop.wheelchair_boarding = result.getString(11);
				stop.stop_direction = result.getString(12);
				stop.route_id = result.getString(13);
				stop.to_stop_id = result.getString(14);
				
				stops.add(stop);
			}while(result.moveToNext());
		}
		
		return stops;
	}
	
	// Get stops by ageny_id
	public ArrayList<Stop> getStopsByAgency(String agency_id){
		ArrayList<Stop> stops = new ArrayList<Stop>();
		ArrayList<Route> routes = new ArrayList<Route>();
		routes = getRoutesByAgencyId(agency_id);
		ArrayList<Stop> stopes = new ArrayList<Stop>();
		for(int i = 0; i < routes.size(); i++){
		stopes = getStopsByRoute(routes.get(i).route_id);
			for(int j = 0; j < stopes.size(); j++){
				stops.add(stopes.get(j));
			}
		}
		return stops;
	}
	
	// Find route by route_id
	/**
	 * Find route by route_id
	 * @param route_id
	 * @return boolean
	 */
	public boolean routeExists(String route_id){
		Route route = new Route();
		Cursor result = db.query("routes", null, "route_id ='"+route_id+"'", null, null, null, null);
		if(result.moveToFirst()){
			do{
				String flag = route.route_id = result.getString(10);
				Log.i("Flag", flag);
				if(flag.equals(route_id))
					return true;
			}while(result.moveToNext());
		}
		return false;
	}

	private static class MapDBHelper extends SQLiteOpenHelper{
		public MapDBHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		// Create DB
		@Override
		public void onCreate(SQLiteDatabase db) {
			// Agencies Table
			final String agencyTable = "create table agencies"+
								"(_id integer primary key autoincrement,"+
								"agency_id text null,"+
								"agency_name text null,"+
								"agency_url text null,"+
								"agency_timezone text null,"+
								"agency_lang text null,"+
								"agency_phone text null);";
			db.execSQL(agencyTable);
			
			// Routes Table
			final String routeTable = "create table routes"+
								"(_id integer primary key autoincrement,"+
								"agency_id text null,"+
								"route_short_name text null,"+
								"route_long_name text null,"+
								"route_desc text null,"+
								"route_type text null,"+
								"route_url text null,"+
								"routeColor text null,"+
								"route_text_color text null,"+
								"route_bikes_allowed text null,"+
								"route_id text null);";
			db.execSQL(routeTable);
			
			// Stops Table
			final String stopTable = "create table stops"+
								"(_id integer primary key autoincrement,"+
								"stop_id text null,"+
								"stop_code text null,"+
								"stop_name text null,"+
								"stop_desc text null,"+
								"stop_lat text null,"+
								"stop_lon text null,"+
								"zone_id text null,"+
								"stop_url text null,"+
								"location_type text null,"+
								"parent_station text null,"+
								"wheelchair_boarding text null,"+
								"stop_direction text null,"+
								"route_id text null,"+
								"to_stop_id text null);";
			db.execSQL(stopTable);
		}

		// Update DB if is necessary
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			db.execSQL("DROP TABLE IF EXIST routes");
//			db.execSQL("DROP TABLE IF EXIST agencies");
//			db.execSQL("DROP TABLE IF EXIST stops");
//			onCreate(db);
		}
	}
}
