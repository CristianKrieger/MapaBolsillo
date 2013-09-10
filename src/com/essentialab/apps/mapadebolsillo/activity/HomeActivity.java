package com.essentialab.apps.mapadebolsillo.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.essentialab.apps.mapadebolsillo.R;
import com.essentialab.apps.mapadebolsillo.adapter.UniversalListAdapter;
import com.essentialab.apps.mapadebolsillo.db.MapDBAdapter;
import com.essentialab.apps.mapadebolsillo.entities.DrawerItem;
import com.essentialab.apps.mapadebolsillo.entities.HeadedList;
import com.essentialab.apps.mapadebolsillo.interfaces.ListHeaderInflationAction;
import com.essentialab.apps.mapadebolsillo.interfaces.ListItemInflationAction;
import com.essentialab.apps.mapadebolsillo.parser.ParsingUtils;
import com.essentialab.apps.mapadebolsillo.parser.entities.Agency;
import com.essentialab.apps.mapadebolsillo.parser.entities.Route;
import com.essentialab.apps.mapadebolsillo.parser.entities.Stop;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeActivity extends ActionBarActivity implements 
	GooglePlayServicesClient.ConnectionCallbacks, 
	GooglePlayServicesClient.OnConnectionFailedListener,
	LocationListener{
	
	private boolean isMetroAvailable = false;
	private boolean isMetroBusAvailable = false;
	private boolean isRTPAvailable = false;
	private boolean isSTEAvailable = false;
	private boolean isSUBAvailable = false;
	
//	private boolean isMetroSelected = false;
//	private boolean isMetroBusSelected = false;
//	private boolean isRTPSelected = false;
//	private boolean isSTESelected = false;
//	private boolean isSUBSelected = false;
	
	private boolean isMetroTableComplete = false;
	private boolean isMetroBusTableComplete = false;
	private boolean isRTPTableComplete = false;
	private boolean isSTETableComplete = false;
	private boolean isSUBTableComplete = false;
	
	private static final String DB_METADATA_FILE = "PREFS_DB_FILE";
	
	private static final String FILE_VAR_VERSION = "version";
	private static final String FILE_VAR_COMPLETE_METRO = "com_metro";
	private static final String FILE_VAR_COMPLETE_METROBUS = "com_metrobus";
	private static final String FILE_VAR_COMPLETE_STE = "com_ste";
	private static final String FILE_VAR_COMPLETE_RTP = "com_rtp";
	private static final String FILE_VAR_COMPLETE_SUB = "com_sub";
	
	private int selectedDrawerItem = 0;
	
	private static final int DRAWER_ITEM_METRO = 1;
	private static final int DRAWER_ITEM_METROBUS = 2;
	private static final int DRAWER_ITEM_RTP = 3;
	private static final int DRAWER_ITEM_STE = 4;
	private static final int DRAWER_ITEM_SUB = 5;
	
	private static final String AGENCY_ID_METRO = "METRO";
	private static final String AGENCY_ID_SUB = "SUB";
	private static final String AGENCY_ID_STE = "STE";
	private static final String AGENCY_ID_RTP = "RTP";
	private static final String AGENCY_ID_METROBUS = "MB";
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	private View pd;
	
	private SupportMapFragment mapFragment;
	private GoogleMap map;
	private LocationClient mLocationClient;
	private DataGetterAsyncTask myTask;
	
	// Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	
	private LocationRequest locationRequest;
	
	/*
	 * Define a request code to send to Google Play services
	 * This code is returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;	
	
	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {

	    // Global field to contain the error dialog
	    private Dialog mDialog;

	    // Default constructor. Sets the dialog field to null
	    public ErrorDialogFragment() {
	        super();
	        mDialog = null;
	    }

	    // Set the dialog to display
	    public void setDialog(Dialog dialog) {
	        mDialog = dialog;
	    }

	    // Return a Dialog to the DialogFragment.
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        return mDialog;
	    }
	}
	
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
	    setContentView(R.layout.activity_home);
	    pd = findViewById(R.id.act_home_progressdialog);
	    
	    initializeMap();
	    startNavigationDrawer();
	}
	
	private void initializeMap(){
		mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.act_home_map));
	    map = mapFragment.getMap();
	    
	    map.setMyLocationEnabled(true);
        
	    locationRequest = LocationRequest.create();
	    // Use high accuracy
        locationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        locationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
	    
	    mLocationClient = new LocationClient(this, this, this);
	    
	    myTask = new DataGetterAsyncTask();
	    myTask.execute();
	    resetLocationButton();
	}
	
	// Move myLocation and Zoom buttons
	public void resetLocationButton(){
		// Get a reference to the zoom buttons and the position button.
	    ViewGroup v1 = (ViewGroup)mapFragment.getView();
	    ViewGroup v2 = (ViewGroup)v1.getChildAt(0);
	    ViewGroup v3 = (ViewGroup)v2.getChildAt(0);
	    ViewGroup v4 = (ViewGroup)v3.getChildAt(1);

	    // Position button
	    View position =  (View)v4.getChildAt(0);
	    int positionWidth = position.getLayoutParams().width;
	    int positionHeight = position.getLayoutParams().height;
	    
		// Move Layout Position button.
	    RelativeLayout.LayoutParams positionParams = new RelativeLayout.LayoutParams(positionWidth,positionHeight);
	    int margin = positionWidth/5;
	    positionParams.setMargins(0, 0, 0, margin);
	    positionParams.addRule(RelativeLayout.ALIGN_RIGHT, RelativeLayout.TRUE);
	    positionParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
	    position.setLayoutParams(positionParams);
	    
	    // Zoom buttons
	    View zoom = (View)v4.getChildAt(2);
	    int zoomWidth = zoom.getLayoutParams().width;
	    int zoomHeight = zoom.getLayoutParams().height;

	    // Move Layout Zoom buttons.
	    RelativeLayout.LayoutParams zoomParams = new RelativeLayout.LayoutParams(zoomWidth, zoomHeight);
	    zoomParams.setMargins(0, 0, 0, margin);
	    zoomParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
	    zoomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
	    zoom.setLayoutParams(zoomParams);
	}
	
	private void startNavigationDrawer(){
		String[] mDrawerTitles = getResources().getStringArray(R.array.act_home_drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.act_home_drawerlayout);
        mDrawerList = (ListView) findViewById(R.id.act_home_drawer);
		
		mDrawerToggle = new ActionBarDrawerToggle(
        		this,
                mDrawerLayout,         
                R.drawable.ic_drawer,
                R.string.act_home_drawer_open,
                R.string.act_home_drawer_close){

            public void onDrawerClosed(View view) {
                getSupportActionBar().show();
            }

            public void onDrawerOpened(View drawerView) {
            }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));
        
        int[] mDrawables_dis = {
        	R.drawable.icn_metro_disabled,
        	R.drawable.icn_metrobus_disabled,
        	R.drawable.icn_rtp_disabled,
        	R.drawable.icn_trenligero_disabled,
        	R.drawable.icn_suburbano_disabled
        };
        
        int[] mDrawables_en = {
            	R.drawable.icn_metro_enabled,
            	R.drawable.icn_metrobus_enabled,
            	R.drawable.icn_rtp_enabled,
            	R.drawable.icn_trenligero_enabled,
            	R.drawable.icn_suburbano_enabled
            };
        
        ArrayList<Object> data = new ArrayList<Object>();
        for(int i=0; i<1; i++){
        	HeadedList<String, DrawerItem> x = new HeadedList<String, DrawerItem>();
        	x.setHeader("SISTEMA DE TRANSPORTE\nCiudad de México");
        	ArrayList<DrawerItem> y = new ArrayList<DrawerItem>();
        	for(int j=0; j<mDrawerTitles.length; j++)
        		y.add(new DrawerItem(mDrawables_en[j], mDrawables_dis[j], mDrawerTitles[j]));
        	x.setContents(y);
        	data.add(x);
        }
		mDrawerList.setAdapter(new UniversalListAdapter(getLayoutInflater(),
				R.layout.row_drawer,
				data, new ListItemInflationAction(),
				R.layout.header_drawer,
				new ListHeaderInflationAction()));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(item.getItemId()==android.R.id.home)
    		getSupportActionBar().hide();
        if (mDrawerToggle.onOptionsItemSelected(item))
          return true;
        return super.onOptionsItemSelected(item);
    }

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        selectItem(position, view);
	    }
	}

	private void selectItem(int position, View v) {
		boolean validSelection=false;
	    switch(position){
	    case DRAWER_ITEM_METRO:
	    	if(isMetroAvailable){
	    		validSelection=true;
	    	}	    		
	    	break;
	    case DRAWER_ITEM_METROBUS:
	    	if(isMetroBusAvailable){
	    		validSelection=true;
	    	}
		    break;
	    case DRAWER_ITEM_RTP:
	    	if(isRTPAvailable){
	    		validSelection=true;
	    	}
	    	break;
	    case DRAWER_ITEM_STE:
	    	if(isSTEAvailable){
	    		validSelection=true;
	    	}
	    	break;
	    case DRAWER_ITEM_SUB:
	    	if(isSUBAvailable){
	    		validSelection=true;
	    	}
	    	break;
	    }
	    if(validSelection){
	    	selectDrawerItem(position, v);
	    	mDrawerList.setItemChecked(position, true);
		    mDrawerLayout.closeDrawer(mDrawerList);
	    }else
		    Toast.makeText(getApplicationContext(),
					R.string.act_home_toast_agencydown,
					Toast.LENGTH_SHORT).show();
	}
	
	private void selectDrawerItem(int position, View v){
//		Toast.makeText(getApplicationContext(),
//				"UNPRESSED: "+Integer.toString(((DrawerItem)v.getTag()).iconId_unpressed)+"\n"+
//				"PRESSED: "+Integer.toString(((DrawerItem)v.getTag()).iconId_pressed),
//				Toast.LENGTH_SHORT).show();
		if(selectedDrawerItem==position){
			v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
			ImageView iv = (ImageView)v.findViewById(R.id.row_drawer_img);
			(iv).setImageDrawable(getResources().getDrawable(((DrawerItem)v.getTag()).iconId_unpressed));
			selectedDrawerItem=0;
			//REMOVE OVERLAY
			map.clear();
		}else{
			if(selectedDrawerItem!=0){
				View old = mDrawerList.getChildAt(selectedDrawerItem);
				old.setBackgroundColor(getResources().getColor(android.R.color.transparent));
				((ImageView)old.findViewById(R.id.row_drawer_img)).setImageDrawable(
						getResources().getDrawable(((DrawerItem)old.getTag()).iconId_unpressed));
				//REMOVE OVERLAY
				map.clear();
			}
			
			selectedDrawerItem=position;
			v.setBackgroundResource(R.drawable.drawer_background_selected);
			((ImageView)v.findViewById(R.id.row_drawer_img)).setImageDrawable(
					getResources().getDrawable(((DrawerItem)v.getTag()).iconId_pressed));
		}
	}
	
	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
	    super.onStart();
	    // Connect the client.
	    if(isGooglePlayServicesAvailable()){
	        mLocationClient.connect();
	    }
	}
	
	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
	    // Disconnecting the client invalidates it.
	    mLocationClient.disconnect();
//	    myTask.cancel(true);
	    super.onStop();
	}
	
	/*
	 * Handle results returned to the FragmentActivity
	 * by Google Play services
	 */
	@Override
	protected void onActivityResult(
	                int requestCode, int resultCode, Intent data) {
	    // Decide what to do based on the original request code
	    switch (requestCode) {

        case CONNECTION_FAILURE_RESOLUTION_REQUEST:
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
            switch (resultCode) {
                case Activity.RESULT_OK:
                    mLocationClient.connect();
                break;
            }
	    }
	}
	
	private boolean isGooglePlayServicesAvailable() {
	    // Check that Google Play services is available
	    int resultCode =  GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    // If Google Play services is available
	    if (ConnectionResult.SUCCESS == resultCode) {
	        // In debug mode, log the status
	        Log.d("Location Updates", "Google Play services is available.");
	        return true;
	    } else {
	        // Get the error dialog from Google Play services
	        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog( resultCode,
	        		this,  CONNECTION_FAILURE_RESOLUTION_REQUEST);

	        // If Google Play services can provide an error dialog
	        if (errorDialog != null) {
	            // Create a new DialogFragment for the error dialog
	            ErrorDialogFragment errorFragment = new ErrorDialogFragment();
	            errorFragment.setDialog(errorDialog);
	            errorFragment.show(getSupportFragmentManager(), "Location Updates");
	        }

	        return false;
	    }
	}

	/*
	 * Called by Location Services if the attempt to
	 * Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		/*
	     * Google Play services can resolve some errors it detects.
	     * If the error has a resolution, try sending an Intent to
	     * start a Google Play services activity that can resolve
	     * error.
	     */
	    if (result.hasResolution()) {
	        try {
	            // Start an Activity that tries to resolve the error
	            result.startResolutionForResult(
	                    this,
	                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
	            /*
	            * Thrown if Google Play services canceled the original
	            * PendingIntent
	            */
	        } catch (IntentSender.SendIntentException e) {
	            // Log the error
	            e.printStackTrace();
	        }
	    } else {
	       Toast.makeText(getApplicationContext(), "Error! Location services are not available", Toast.LENGTH_LONG).show();
	    }
	}
		
	@Override
	public void onConnected(Bundle connectionHint) {
		// Display the connection status
		mLocationClient.requestLocationUpdates(
				locationRequest,
				this); 
	    Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
	    Location location = mLocationClient.getLastLocation();
	    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
	    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
	    //map.animateCamera(cameraUpdate);
	    map.moveCamera(cameraUpdate);
	}

	@Override
	public void onDisconnected() {
	// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();	
	}

	@Override
	public void onLocationChanged(Location location) {
		// Report to the UI that the location was updated
		
        /*String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        map.clear();
		map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
				location.getLongitude())).title("Marker"));*/
	}
	
	private class DataGetterAsyncTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			isMetroAvailable = false;
			isMetroBusAvailable = false;
			isRTPAvailable = false;
			isSTEAvailable = false;
			isSUBAvailable = false;
			
			if(isCancelled()) return null;
			
			SharedPreferences metadata = getSharedPreferences(DB_METADATA_FILE, Context.MODE_PRIVATE);
			int dataVersion = metadata.getInt(FILE_VAR_VERSION, -1);
			Log.d("DEBUG", "SAVED DB VERSION: "+Integer.toString(dataVersion));
			//getVersionFromServer()
			boolean newVersion=false;
			int versionFromServer=0;
			if(dataVersion==versionFromServer){
				isMetroTableComplete = metadata.getBoolean(FILE_VAR_COMPLETE_METRO, false);
				isMetroBusTableComplete = metadata.getBoolean(FILE_VAR_COMPLETE_METROBUS, false);
				isSTETableComplete = metadata.getBoolean(FILE_VAR_COMPLETE_STE, false);
				isRTPTableComplete = metadata.getBoolean(FILE_VAR_COMPLETE_RTP, false);
				isSUBTableComplete = metadata.getBoolean(FILE_VAR_COMPLETE_SUB, false);
			}else{
			    dataVersion = versionFromServer;
			    newVersion=true;
			}
			Log.d("DEBUG", "SERVER DB VERSION: "+Integer.toString(versionFromServer));
			
			if(isMetroTableComplete && isMetroBusTableComplete && isSTETableComplete &&
					isSUBTableComplete && isRTPTableComplete)
				return null;
			
			MapDBAdapter db = new MapDBAdapter(getApplicationContext());
			db.open();
			
			if(newVersion){
				db.clearDB();
				Log.d("DEBUG", "LOCAL DB ERASED");
			}
			
			Agency[] agencies = (Agency[]) ParsingUtils.parseJSONObjectfromWeb(
					ParsingUtils.DATA_TYPE_AGENCIES, null);
			
			int currentAgency=0;
			for(int i=0;i<agencies.length;i++){
				Log.d("DEBUG", "AGENCY "+Integer.toString(i+1)+" OF "+Integer.toString(agencies.length));
				if(isCancelled()) break;
				db.insertAgency(agencies[i]);
				String agencyId=agencies[i].agency_id;
				currentAgency=0;
				boolean isComplete=false;
				
				if(agencyId.equals(AGENCY_ID_METRO)){
					isMetroAvailable=true;
					currentAgency = DRAWER_ITEM_METRO;
				}else if(agencyId.equals(AGENCY_ID_METROBUS)){
					isMetroBusAvailable=true;
					currentAgency = DRAWER_ITEM_METROBUS;
				} else if(agencyId.equals(AGENCY_ID_RTP)){
					isRTPAvailable=true;
					currentAgency = DRAWER_ITEM_RTP;
				}else if(agencyId.equals(AGENCY_ID_STE)){
					isSTEAvailable=true;
					currentAgency = DRAWER_ITEM_STE;
				}else if(agencyId.equals(AGENCY_ID_SUB)){
					isSUBAvailable=true;
					currentAgency = DRAWER_ITEM_SUB;
				}else{
					Log.d("DEBUG", "UNKNOWN AGENCY ON JSON");
					continue;
				}
				
				switch(currentAgency){
				case DRAWER_ITEM_METRO:
					if(isMetroTableComplete){
						Log.d("DEBUG", "SKIPPING METRO AGENCY");
						isComplete=true;
						break;
					}
				case DRAWER_ITEM_METROBUS:
					if(isMetroBusTableComplete){
						Log.d("DEBUG", "SKIPPING METROBUS AGENCY");
						isComplete=true;
						break;
					}
				case DRAWER_ITEM_RTP:
					if(isRTPTableComplete){
						Log.d("DEBUG", "SKIPPING RTP AGENCY");
						isComplete=true;
						break;
					}
				case DRAWER_ITEM_SUB:
					if(isSUBTableComplete){
						Log.d("DEBUG", "SKIPPING SUB AGENCY");
						isComplete=true;
						break;
					}
				case DRAWER_ITEM_STE:
					if(isSTETableComplete){
						Log.d("DEBUG", "SKIPPING STE AGENCY");
						isComplete=true;
						break;
					}
				}
				
				if(!isComplete){
					Route[] routes = (Route[]) ParsingUtils.parseJSONObjectfromWeb(
							ParsingUtils.DATA_TYPE_STOPS_PER_AGENCY, agencyId);
					
					for(int j=0;j<routes.length;j++){
						Log.d("DEBUG", "ROUTE "+Integer.toString(j+1)+" OF "+Integer.toString(routes.length));
						if(isCancelled()) break;
						
						if(db.routeExists(routes[j].route_id)){
							Log.d("DEBUG", "SKIPPING ROUTE");
							continue;
						}
						
						db.insertRoute(routes[j]);
						for(int k=0;k<routes[j].stops.length;k++){
							if(isCancelled()) break;
							Log.d("DEBUG", "STOP "+Integer.toString(k+1)+" OF "+Integer.toString(routes[j].stops.length));
							if(db.stopExists(routes[j].stops[k].stop_id)){
								Log.d("DEBUG", "SKIPPING STOP");
								continue;
							}
							db.insertStop(routes[j].stops[k]);
						}
					}
					
					switch(currentAgency){
					case DRAWER_ITEM_METRO:
						Log.d("DEBUG", "METRO TABLE COMPLETE");
						isMetroTableComplete=true;
						break;
					case DRAWER_ITEM_METROBUS:
						Log.d("DEBUG", "METROBUS TABLE COMPLETE");
						isMetroBusTableComplete=true;
						break;
					case DRAWER_ITEM_RTP:
						Log.d("DEBUG", "RTP TABLE COMPLETE");
						isRTPTableComplete=true;
						break;
					case DRAWER_ITEM_SUB:
						Log.d("DEBUG", "SUB TABLE COMPLETE");
						isSUBTableComplete=true;
						break;
					case DRAWER_ITEM_STE:
						Log.d("DEBUG", "STE TABLE COMPLETE");
						isSTETableComplete=true;
						break;
					}
				}
				
				if(isCancelled()) break;
			}		
			
			db.close();
			
			SharedPreferences.Editor editor = metadata.edit();
		    editor.putInt(FILE_VAR_VERSION, versionFromServer);
		    editor.putBoolean(FILE_VAR_COMPLETE_METRO, isMetroTableComplete);
		    editor.putBoolean(FILE_VAR_COMPLETE_METROBUS, isMetroBusTableComplete);
		    editor.putBoolean(FILE_VAR_COMPLETE_STE, isSTETableComplete);
		    editor.putBoolean(FILE_VAR_COMPLETE_SUB, isSUBTableComplete);
		    editor.putBoolean(FILE_VAR_COMPLETE_RTP, isRTPTableComplete);
		    
		    editor.commit();
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			pd.setVisibility(View.GONE);
			getSupportActionBar().setHomeButtonEnabled(true);
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
			
			getStopstoDraw();
		}

		@Override
		protected void onCancelled(Void result) {
			//TODO: Clear DB contents
			Log.e("HOME-ASYNCTASK", "Download was interrupted");
		}
	}
	
	// Get Stops from database to draw
	public void getStopstoDraw(){
		MapDBAdapter db = new MapDBAdapter(getApplicationContext());
		db.open();
		ArrayList<Stop> stops = new ArrayList<Stop>();
		stops = db.getStopsByAgency(AGENCY_ID_METRO);
		db.close();
		for(int i = 0; i < stops.size(); i++){
			Stop stop = new Stop();
			stop = stops.get(i);
			drawingStops(stop.stop_name, stop.stop_lat, stop.stop_lon);
		}
	}

	public void drawingStops(String _stopName, String _stopLan, String _stopLon) {
		LatLng position = new LatLng(Double.parseDouble(_stopLan), Double.parseDouble(_stopLon));
		map.addMarker(new MarkerOptions().position(position).title(_stopName));
		
		Marker algo = map.addMarker(new MarkerOptions().position(position).title(_stopName));
		
		Log.i("Marker algo Id", algo.getId());
	}
}