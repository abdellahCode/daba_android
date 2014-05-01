package com.capstone.utils;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class LocationService extends Service implements LocationListener{

	private Context mContext;

	//flag for GPS Status
	boolean isGPSEnabled = false;

	//flag for network status
	boolean isNetworkEnabled = false;

	boolean canGetLocation = false;

	Location location;
	double latitude;
	double longitude;

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 30; //30 metters

	//The minimum time beetwen updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 3; // 3 minute

	//Declaring a Location Manager
	protected LocationManager locationManager;

	public LocationService(Context context) 
	{
		this.mContext = context;
		getLocation();
	}

	public Location getLocation(){

		try{
			locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

			//getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			//getting network status
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled)
			{
				// no network provider is enabled
			}
			else
			{
				this.canGetLocation = true;

				//First get location from Network Provider
				if (isNetworkEnabled)
				{
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

					Log.d("Network", "Network");

					if (locationManager != null)
					{
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						updateGPSCoordinates();
					}
				}

				//if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled)
				{
					if (location == null)
					{
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

						Log.d("GPS Enabled", "GPS Enabled");

						if (locationManager != null)
						{
							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							updateGPSCoordinates();
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			Log.e("Error : Location", "Impossible to connect to LocationManager", e);
		}

		return location;
	}

	public void updateGPSCoordinates()
	{
		if (location != null)
		{
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}
	}

	public void stopUsingGPS()
	{
		if (locationManager != null)
		{
			locationManager.removeUpdates(LocationService.this);
		}
	}
	
	public void showSettingsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        //Setting Dialog Title
        alertDialog.setTitle("Location Alert");

        //Setting Dialog Message
        alertDialog.setMessage("We would like to get your location to keep Daba awesome!");

        //On Pressing Setting button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() 
        {   
            @Override
            public void onClick(DialogInterface dialog, int which) 
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
        {   
            @Override
            public void onClick(DialogInterface dialog, int which) 
            {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
	public boolean canGetLocation()
	{
		return this.canGetLocation;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
