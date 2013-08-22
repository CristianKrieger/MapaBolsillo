package com.essentialab.apps.mapadebolsillo.parser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.essentialab.apps.mapadebolsillo.parser.entities.Agency;
import com.essentialab.apps.mapadebolsillo.parser.entities.Route;
import com.essentialab.apps.mapadebolsillo.parser.entities.Stop;

public class ParsingUtils {
	
	public final static String URL_JSON_AGENCIES = "http://107.22.236.217/transporte-df/index.php/agencies";
	public final static String URL_JSON_STOPS_PER_AGENCY_PREFIX = "http://107.22.236.217/transporte-df/index.php/stopsagency/";
	
	public final static int DATA_TYPE_AGENCIES = 0;
	public final static int DATA_TYPE_STOPS_PER_AGENCY = 1;
	public final static int DATA_TYPE_REPORT = 2;
	
	public static String downloadFile(String url){
		//"http://107.22.236.217/transporte-df/index.php/stopsagency/METRO"
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			String data = EntityUtils.toString(httpEntity, "UTF-8");
			Log.d("Data to String", data);
			return data;
		} catch (Exception e) {
			Log.e("ERROR", "Data could not be downloaded from: "+url);
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * Method for populating Objects from JSON data.
	 * Params: [String]  data, downloaded JSON.
	 * 		   [Integer] dataType, the object type to return
	 * 						(See constant definition above).
	 * Returns: [Object] POYO based on the dataType received.
	 */
	public static Object parseObjectfromJSON(String data, int dataType){
		try {
			JSONObject jObject = new JSONObject(data);
			JSONArray jArray;
			switch(dataType){
			case DATA_TYPE_AGENCIES:
				jArray = jObject.getJSONArray("agencies");
				Agency[] agencies = new Agency[jArray.length()];
				for (int i = 0; i < jArray.length(); i++){
				        JSONObject obj = jArray.getJSONObject(i);
				        agencies[i]=new Agency(obj.getString("agency_id"),
				        		obj.getString("agency_name"),
				        		obj.getString("agency_url"),
				        		obj.getString("agency_timezone"),
				        		obj.getString("agency_lang"),
				        		obj.getString("agency_phone"));
				}
				return agencies;
			case DATA_TYPE_STOPS_PER_AGENCY:
				jArray = jObject.getJSONArray("routes");
				
				Route[] routes = new Route[jArray.length()];
				for (int i = 0; i < jArray.length(); i++){
				        JSONObject obj = jArray.getJSONObject(i);
				        
				        JSONArray jArray_inner = obj.getJSONArray("stops");
				        
				        Stop[] stops=new Stop[jArray_inner.length()];
				        
				        for(int j=0;j<jArray_inner.length(); j++){
				        	JSONObject obj_in = jArray_inner.getJSONObject(j);
				        	
				        	stops[j]=new Stop(obj_in.getString("stop_id"),
				        			obj_in.getString("stop_code"),
				        			obj_in.getString("stop_name"),
				        			obj_in.getString("stop_desc"),
				        			obj_in.getString("stop_lat"),
				        			obj_in.getString("stop_lon"),
				        			obj_in.getString("zone_id"),
				        			obj_in.getString("stop_url"),
				        			obj_in.getString("location_type"),
				        			obj_in.getString("parent_station"),
				        			obj_in.getString("wheelchair_boarding"),
				        			obj_in.getString("stop_direction"),
				        			obj_in.getString("route_id"),
				        			obj_in.getString("to_stop_id"));
				        }
				        
				        routes[i] = new Route(obj.getString("agency_id"),
				        		obj.getString("route_short_name"),
				        		obj.getString("route_long_name"),
				        		obj.getString("route_desc"),
				        		obj.getString("route_type"),
				        		obj.getString("route_url"),
				        		obj.getString("route_color"),
				        		obj.getString("route_text_color"),
				        		obj.getString("route_bikes_allowed"),
				        		obj.getString("route_id"),
				        		stops);
				}
				return routes;
			case DATA_TYPE_REPORT:
				break;
			}
		} catch (JSONException e) {
			Log.e("ERROR", "Could not parse: "+data);
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	/*
	 * Getter method for SETRAVI's JSON.
	 * Params: [Integer] dataType, object type to download.
	 * 		   [String]  agencyId, (only required for STOPS_PER_AGENCY)
	 * Returns: [Object] POYO based on the dataType received.
	 */
	public static Object parseJSONObjectfromWeb(int dataType, String agencyId){
		String url=null;
		switch(dataType){
		case DATA_TYPE_AGENCIES:
			url=URL_JSON_AGENCIES;
			break;
		case DATA_TYPE_STOPS_PER_AGENCY:
			url=URL_JSON_STOPS_PER_AGENCY_PREFIX+agencyId;
			break;
		case DATA_TYPE_REPORT:
			break;
		}
		return parseObjectfromJSON(downloadFile(url), dataType);
	}
}
