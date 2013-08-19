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

import com.essentialab.apps.mapadebolsillo.parser.entities.Agencies;

public class ParsingUtils {
	
	public final static String URL_JSON_AGENCIES = "http://107.22.236.217/transporte-df/index.php/agencies";
	
	public final static int DATA_TYPE_AGENCIES = 0;
	public final static int DATA_TYPE_ROUTES = 1;
	public final static int DATA_TYPE_STOPS = 2;
	public final static int DATA_TYPE_AGENCY = 3;
	public final static int DATA_TYPE_ROUTE = 4;
	public final static int DATA_TYPE_STOP = 5;
	public final static int DATA_TYPE_REPORT = 6;
	
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
				Agencies[] agencies = new Agencies[jArray.length()];
				for (int i = 0; i < jArray.length(); i++){
				        JSONObject obj = jArray.getJSONObject(i);
				        agencies[i]=new Agencies(obj.getString("agency_id"),
				        		obj.getString("agency_name"),
				        		obj.getString("agency_url"),
				        		obj.getString("agency_timezone"),
				        		obj.getString("agency_lang"),
				        		obj.getString("agency_phone"));
				}
				return agencies;
			case DATA_TYPE_ROUTES:
				break;
			case DATA_TYPE_STOPS:
				break;
			case DATA_TYPE_AGENCY:
				break;
			case DATA_TYPE_ROUTE:
				break;
			case DATA_TYPE_STOP:
				break;
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
	
	public static Object parseJSONObjectfromWeb(int dataType){
		String url=null;
		switch(dataType){
		case DATA_TYPE_AGENCIES:
			url=URL_JSON_AGENCIES;
		case DATA_TYPE_ROUTES:
			break;
		case DATA_TYPE_STOPS:
			break;
		case DATA_TYPE_AGENCY:
			break;
		case DATA_TYPE_ROUTE:
			break;
		case DATA_TYPE_STOP:
			break;
		case DATA_TYPE_REPORT:
			break;
		}
		return parseObjectfromJSON(downloadFile(url), dataType);
	}
}
