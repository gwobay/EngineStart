package com.example.volunteerhandbook;

import com.example.volunteerhandbook.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/*
 * make sure user permission is set in manifest xml file
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 */
public class PositionLocator implements
				LocationListener,
				GooglePlayServicesClient.ConnectionCallbacks,
				GooglePlayServicesClient.OnConnectionFailedListener 
{

	public PositionLocator(Activity av, int minutesForEachSampling) {
		// TODO Auto-generated constructor stub
		mUser=av;
		samplingMultiple=minutesForEachSampling*12;
		userListener=null;
		lastLocation=null;
		if (minutesForEachSampling==0) samplingMultiple=12;
		init(samplingMultiple);
	}

	   // A request to connect to Location Services
    private LocationRequest mLocationRequest;
    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
    // Handle to SharedPreferences for this app
    SharedPreferences mPrefs;
    // Handle to a SharedPreferences editor
    SharedPreferences.Editor mEditor;
    /*
     * Note if updates have been turned on. Starts out as "false"; is set to "true" in the
     * method handleRequestSuccess of LocationUpdateReceiver.
     *
     */
    boolean mUpdatesRequested = false;

    Activity mUser;
	static Location newLocation;
	static Location lastLocation;
	static GpsStatus mGpsStatus;
	String locationString;
	int samplingMultiple;
	
	public Location getMyLocation()
	{
		return newLocation;
	}
	
    void setActivityClient(Activity wAty)
    {
    	mUser=wAty;
    }

    LocationListener userListener;
    GooglePlayServicesClient.ConnectionCallbacks userHandler;
    
    void init(int factor)    //we don't care real-time movement, so sampling every 10 mins should be OK
    {
    	mLocationClient = new LocationClient(mUser, this, this);
    	mLocationClient.connect();
    	mLocationRequest = LocationRequest.create();
    	mLocationRequest.setInterval(factor*LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
    	mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//within 100m
    	mLocationRequest.setFastestInterval(factor*LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
    	mUpdatesRequested = false;
    	//mPrefs = mUser.getSharedPreferences(LocationUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    	//mEditor = mPrefs.edit();
    	
    }
    
    public void setParameters(int minutesForEachSampling)
    {
    	samplingMultiple=minutesForEachSampling*12;
    	if (minutesForEachSampling==0) samplingMultiple=12;   	
    }
    /**
     * Verify that Google Play services is available before making a request.
     * 
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(mUser);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(LocationUtils.APPTAG, mUser.getString(R.string.play_services_available));

            // Continue
            return mLocationClient.isConnected();
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, mUser, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(((FragmentActivity )mUser).getSupportFragmentManager(), LocationUtils.APPTAG);
            }
            return false;
        }
    }

    /**
     * Invoked by the "Get Location" button.
     *
     * Calls getLastLocation() to get the current location
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    
    public void getLocation(View v) {

        // If Google Play Services is available
        if (servicesConnected()) {

            // Get the current location
            Location currentLocation = mLocationClient.getLastLocation();

            // Display the current location in the UI
           // mLatLng
            ((TextView)v).setText(LocationUtils.getLatLng(mUser, currentLocation));
        }
    }

    public Location getLocation(Activity who)
    {
    	mUser=who;
    	if (!servicesConnected()) return lastLocation;
    	return mLocationClient.getLastLocation();
    }
    
    public Location getLocation()
    {
    	if (!servicesConnected()) return lastLocation;
    	return mLocationClient.getLastLocation();
    }

    View showWindow;
    /**
     * Invoked by the "Start Updates" button
     * Sends a request to start location updates
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void startUpdates(View v, LocationListener l) {
        mUpdatesRequested = true;
        userListener=l;
        if (servicesConnected()) {
            startPeriodicUpdates();
            showWindow=v;
        }
    }

    /**
     * Invoked by the "Stop Updates" button
     * Sends a request to remove location updates
     * request them.
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void stopUpdates() {
        mUpdatesRequested = false;
        
        if (servicesConnected()) {
            stopPeriodicUpdates();
        }
    }

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
       // mConnectionStatus.setText(R.string.connected);
    	Log.d("LOCATOR", "Connected to the location service");
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
        if (mUpdatesRequested) {
            startPeriodicUpdates();
        }
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
       // mConnectionStatus.setText(R.string.disconnected);
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                		mUser,
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                e.printStackTrace();
            }
        } else {

            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    /**
     * Report location updates to the UI.
     *
     * @param location The updated location.
     */
    @Override
    public void onLocationChanged(Location location) {

        // Report to the UI that the location was updated
       // mConnectionStatus.setText(R.string.location_updated);

        // In the UI, set the latitude and longitude to the value received
        //mLatLng.setText(LocationUtils.getLatLng(this, location));
    	lastLocation=newLocation;
    	newLocation=location;
    	if (userListener != null)
    		userListener.onLocationChanged(location);
    }

    public  void onProviderDisabled (String provider)
    {
    	
    }
    public  void onProviderEnabled (String provider)
    {
    	
    }
    public  void onStatusChanged (String provider, int status, Bundle extras)
    {
    	
    }
    	/**
    
     * In response to a request to start updates, send a request
     * to Location Services
     */
    private void startPeriodicUpdates() {

        mLocationClient.requestLocationUpdates(mLocationRequest, this);//mUser);
       // if (showWindow != null)
        {
        	//((TextView)showWindow).setText(R.string.location_requested);
        }
    }

    /**
     * In response to a request to stop updates, send a request to
     * Location Services
     */
    private void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates(this);//mUser);
       // mConnectionState.setText(R.string.location_updates_stopped);
        mLocationClient.disconnect();
    }

	static private class Location_Address{
		public Location mLocation;
		public String mAddress;
		public Location_Address(Location l, String adr)
		{
			mLocation=l;
			mAddress=adr;
		}
		public Location_Address(Location l)
		{
			mLocation=l;
			mAddress=null;
		}
		public Location getLocation()
		{
			return mLocation;
		}
		public String getAddress()
		{
			return mAddress;
		}
	}

	public void getPosition(Activity user, AddressReadyListener l, String myAddress)
	{
		 Location_Address myLA=new Location_Address(null, myAddress);
		 userAddressReader=l;
        // Start the background task
        (new GetAddressTask(user)).execute(myLA);
	}


    // For Eclipse with ADT, suppress warnings about Geocoder.isPresent()
    public interface AddressReadyListener
    {
    	public void onAddressReady(String newAddress);
    }
    AddressReadyListener userAddressReader;
    @SuppressLint("NewApi")
    public void getMyAddress(View v, AddressReadyListener l) {

        // In Gingerbread and later, use Geocoder.isPresent() to see if a geocoder is available.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD || !Geocoder.isPresent()) {
            // No geocoder is present. Issue an error message
            Toast.makeText(mUser, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
            return;
        }
        userAddressReader = l;
        showWindow=v;
        if (servicesConnected() && mLocationClient.isConnected()) {

            // Get the current location
            Location currentLocation = mLocationClient.getLastLocation();

            // Turn the indefinite activity indicator on
           // mActivityIndicator.setVisibility(View.VISIBLE);
            Location_Address myLA=new Location_Address(currentLocation);
            // Start the background task
            (new GetAddressTask(mUser)).execute(myLA);
        }
    }


    /**
     * An AsyncTask that calls getFromLocation() in the background.
     * The class uses the following generic types:
     * Location - A {@link android.location.Location} object containing the current location,
     *            passed as the input parameter to doInBackground()
     * Void     - indicates that progress units are not used by this subclass
     * String   - An address passed to onPostExecute()
     */
    protected class GetAddressTask extends AsyncTask<Location_Address, Void, String> {

        // Store the context passed to the AsyncTask when the system instantiates it.
        Context localContext;

        // Constructor called by the system to instantiate the task
        public GetAddressTask(Context context) {

            // Required by the semantics of AsyncTask
            super();

            // Set a Context for the background task
            localContext = context;
        }

        
        /**
         * Get a geocoding service instance, pass latitude and longitude to it, format the returned
         * address, and return the address to the UI thread.
         */
        @Override
        protected String doInBackground(Location_Address... params) {
            /*
             * Get a new geocoding service instance, set for localized addresses. This example uses
             * android.location.Geocoder, but other geocoders that conform to address standards
             * can also be used.
             */
            Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

            // Get the current location from the input parameter list
            Location location = params[0].getLocation();
            String sAddress=params[0].getAddress();
            // Create a list to contain the result address
            List <Address> addresses = null;

            // Try to get an address for the current location. Catch IO or network problems.
            try {

                /*
                 * Call the synchronous getFromLocation() method with the latitude and
                 * longitude of the current location. Return at most 1 address.
                 */
            	if (location != null)
                addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1
                );
            	else if (sAddress != null)
            	{
            		addresses=geocoder.getFromLocationName(sAddress, 2);        			
            	}

                // Catch network or other I/O problems.
                } catch (IOException exception1) {

                    // Log an error and return an error message
                    Log.e(LocationUtils.APPTAG, mUser.getString(R.string.IO_Exception_getFromLocation));

                    // print the stack trace
                    exception1.printStackTrace();
                    String sError=mUser.getString(R.string.IO_Exception_getFromLocation);
                    // Return an error message
                    return (sError);

                // Catch incorrect latitude or longitude values
                } catch (IllegalArgumentException exception2) {

                    // Construct a message containing the invalid arguments
                    String errorString = mUser.getString(
                            R.string.illegal_argument_exception,
                            location.getLatitude(),
                            location.getLongitude()
                    );
                    // Log the error and print the stack trace
                    Log.e(LocationUtils.APPTAG, errorString);
                    exception2.printStackTrace();

                    //
                    return errorString;
                }
                // If the reverse geocode returned an address
                if (addresses != null && addresses.size() > 0) {

                    // Get the first address
                    Address address0 = addresses.get(0);

                    // Format the first line of address
                    String sLatLng="@"+mUser.getString(R.string.latlng_format,
                    						address0.getLatitude(), address0.getLongitude());
                    String street="";
                    if (address0.getMaxAddressLineIndex() < 1) return sLatLng;
                    	street=address0.getAddressLine(0);
                    String city=address0.getAdminArea();
                    if (city==null) city=address0.getLocality();
                    
                    String addressText = street+"@"+city+ sLatLng;
                    // Return the text
                    return addressText;

                // If there aren't any addresses, post a message
                } else {
                  return mUser.getString(R.string.no_address_found);
                }
        }

        /**
         * A method that's called once doInBackground() completes. Set the text of the
         * UI element that displays the address. This method runs on the UI thread.
         */
        @Override
        protected void onPostExecute(String address) {

            // Turn off the progress bar
            //mActivityIndicator.setVisibility(View.GONE);
        	if (userAddressReader != null) 
        		userAddressReader.onAddressReady(address);
            
            // Set the address in the UI
           // mAddress.setText(address);
        	//if (showWindow != null)
        		//((TextView)showWindow).setText(address);
        	locationString=address;
        }
    }

    /**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            mUser,//this,
            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(((FragmentActivity)mUser).getSupportFragmentManager(), LocationUtils.APPTAG);
        }
    }

    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

	public void setLastKnownLocation()
	{
		if (lastLocation != null) return;
		//String bestProvider=LocationManager.get
	}
}
