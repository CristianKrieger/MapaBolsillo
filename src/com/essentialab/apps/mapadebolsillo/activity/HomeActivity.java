package com.essentialab.apps.mapadebolsillo.activity;

import java.util.ArrayList;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.essentialab.apps.mapadebolsillo.R;
import com.essentialab.apps.mapadebolsillo.adapter.UniversalListAdapter;
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
	
	private boolean isMetroSelected = false;
	private boolean isMetroBusSelected = false;
	private boolean isRTPSelected = false;
	private boolean isSTESelected = false;
	private boolean isSUBSelected = false;
	
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
	private String mTitle;
	private ActionBarDrawerToggle mDrawerToggle;
	private String[] mDrawerTitles;
	
	private ProgressBar pb;
	
	private SupportMapFragment mapFragment;
	private GoogleMap map;
	private LocationClient mLocationClient;
	private AgencyGetterAsyncTask myTask;
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_home);
	    pb=(ProgressBar) findViewById(R.id.act_home_pb);
	    
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
	    
	    myTask = new AgencyGetterAsyncTask();
	    myTask.execute();
	}
	
	private void startNavigationDrawer(){
		mDrawerTitles = getResources().getStringArray(R.array.act_home_drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.act_home_drawerlayout);
        mDrawerList = (ListView) findViewById(R.id.act_home_drawer);
        mTitle=getResources().getString(R.string.app_name);
		
		mDrawerToggle = new ActionBarDrawerToggle(
        		this,
                mDrawerLayout,         
                R.drawable.ic_drawer,
                R.string.act_home_drawer_open,
                R.string.act_home_drawer_close){

            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mTitle);
            }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
        int[] mDrawables = {
        	R.drawable.ic_launcher,
        	R.drawable.ic_launcher,
        	R.drawable.ic_launcher,
        	R.drawable.ic_launcher,
        	R.drawable.ic_launcher
        };
        
        ArrayList<Object> data = new ArrayList<Object>();
        for(int i=0; i<1; i++){
        	HeadedList<String, DrawerItem> x = new HeadedList<String, DrawerItem>();
        	x.setHeader("Servicios de Transporte");
        	ArrayList<DrawerItem> y = new ArrayList<DrawerItem>();
        	for(int j=0; j<mDrawerTitles.length; j++)
        		y.add(new DrawerItem(mDrawables[j], mDrawerTitles[j]));
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
	    mDrawerList.setItemChecked(position, true);
	    mDrawerLayout.closeDrawer(mDrawerList);
	    
	    switch(position){
	    case DRAWER_ITEM_METRO:
	    	if(isMetroAvailable){
	    		if(isMetroSelected){
	    			v.setBackgroundColor(getResources().getColor(R.color.row_drawer_bgnd_unselected));
	    			isMetroSelected=false;
	    			//REMOVE OVERLAY
	    			map.clear();
	    		}else{
	    			v.setBackgroundColor(getResources().getColor(R.color.row_drawer_bgnd_selected));
	    			isMetroSelected=true;
	    			new StopsGetterAsyncTask().execute(AGENCY_ID_METRO);
	    		}	    			
	    		return;
	    	}	    		
	    	break;
	    case DRAWER_ITEM_METROBUS:
	    	if(isMetroBusAvailable){
	    		if(isMetroBusSelected){
	    			v.setBackgroundColor(getResources().getColor(R.color.row_drawer_bgnd_unselected));
	    			isMetroBusSelected=false;
	    			//REMOVE OVERLAY
	    		}else{
	    			v.setBackgroundColor(getResources().getColor(R.color.row_drawer_bgnd_selected));
	    			isMetroBusSelected=true;
	    			new StopsGetterAsyncTask().execute(AGENCY_ID_METROBUS);
	    		}
	    		return;
	    	}
		    break;
	    case DRAWER_ITEM_RTP:
	    	if(isRTPAvailable){
	    		if(isRTPSelected){
	    			v.setBackgroundColor(getResources().getColor(R.color.row_drawer_bgnd_unselected));
	    			isRTPSelected=false;
	    			//REMOVE OVERLAY
	    		}else{
	    			v.setBackgroundColor(getResources().getColor(R.color.row_drawer_bgnd_selected));
	    			isRTPSelected=true;
	    			new StopsGetterAsyncTask().execute(AGENCY_ID_RTP);
	    		}
	    		return;
	    	}
	    	break;
	    case DRAWER_ITEM_STE:
	    	if(isSTEAvailable){
	    		if(isSTESelected){
	    			v.setBackgroundColor(getResources().getColor(R.color.row_drawer_bgnd_unselected));
	    			isSTESelected=false;
	    			//REMOVE OVERLAY
	    		}else{
	    			v.setBackgroundColor(getResources().getColor(R.color.row_drawer_bgnd_selected));
	    			isSTESelected=true;
	    			new StopsGetterAsyncTask().execute(AGENCY_ID_STE);
	    		}
	    		return;
	    	}
	    	break;
	    case DRAWER_ITEM_SUB:
	    	if(isSUBAvailable){
	    		if(isSUBSelected){
	    			v.setBackgroundColor(getResources().getColor(R.color.row_drawer_bgnd_unselected));
	    			isSUBSelected=false;
	    			//REMOVE OVERLAY
	    		}else{
	    			v.setBackgroundColor(getResources().getColor(R.color.row_drawer_bgnd_selected));
	    			isSUBSelected=true;
	    			new StopsGetterAsyncTask().execute(AGENCY_ID_SUB);
	    		}
	    		return;
	    	}
	    	break;
	    }
	    Toast.makeText(getApplicationContext(),
				R.string.act_home_toast_agencydown,
				Toast.LENGTH_SHORT).show();
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
	
	private class AgencyGetterAsyncTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			isMetroAvailable = false;
			isMetroBusAvailable = false;
			isRTPAvailable = false;
			isSTEAvailable = false;
			isSUBAvailable = false;
			
			Agency[] agencies = (Agency[]) ParsingUtils.parseJSONObjectfromWeb(
					ParsingUtils.DATA_TYPE_AGENCIES, null);
			
			for(int i=0;i<agencies.length;i++){
				if(agencies[i].agency_id.equals(AGENCY_ID_METRO)){
					isMetroAvailable=true;
					continue;
				}
				if(agencies[i].agency_id.equals(AGENCY_ID_METROBUS)){
					isMetroBusAvailable=true;
					continue;
				}
				if(agencies[i].agency_id.equals(AGENCY_ID_RTP)){
					isRTPAvailable=true;
					continue;
				}
				if(agencies[i].agency_id.equals(AGENCY_ID_STE)){
					isSTEAvailable=true;
					continue;
				}
				if(agencies[i].agency_id.equals(AGENCY_ID_SUB)){
					isSUBAvailable=true;
					continue;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			pb.setVisibility(View.GONE);
			getSupportActionBar().setHomeButtonEnabled(true);
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		}
	}
	
	private class StopsGetterAsyncTask extends AsyncTask<String, Void, Void>{
		Route[] routes;
		
		@Override
		protected Void doInBackground(String... params) {
			pb.setVisibility(View.VISIBLE);
			routes = (Route[]) ParsingUtils.parseJSONObjectfromWeb(
					ParsingUtils.DATA_TYPE_STOPS_PER_AGENCY, params[0]);
			
			//TODO: Preparar datos para inflar rutas por colores.
			//// Tal vez utilizar una lista o algo más ordenado que un arreglo
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			//TODO: Once the data has been managed. Add code for adding Overlay
			for(int a=0;a<routes.length;a++){
				Log.d("ROUTE", routes[a].route_long_name);
				Log.d("ROUTE", "------------------------");
				Log.d("ROUTE", "------------------------");
				for(int i=0;i<routes[a].stops.length;i++){
					Stop x = routes[a].stops[i];
					Log.d("STOP", "STOP: \n"
							+x.stop_name+"\n"
							+x.stop_lat+"\n"
							+x.stop_lon+"\n"+"\n");
					drawingStops(x.stop_name, x.stop_lat, x.stop_lon);
					Log.d("STOP", "------------------------");
				}
				Log.d("STOP", "------------------------");
			}
			pb.setVisibility(View.GONE);
		}
	}

	public void drawingStops(String _stopName, String _stopLan, String _stopLon) {
		
		LatLng position = new LatLng(Double.parseDouble(_stopLan), Double.parseDouble(_stopLon));
		map.addMarker(new MarkerOptions().position(position).title(_stopName));
		
		Marker algo = map.addMarker(new MarkerOptions().position(position).title(_stopName));
		
		Log.i("Marker algo Id", algo.getId());
	}
}