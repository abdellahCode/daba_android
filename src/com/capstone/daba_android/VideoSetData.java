package com.capstone.daba_android;

import java.io.File;
import java.io.IOException;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.capstone.utils.*;

public class VideoSetData extends Activity {
	LocationManager lm;
	String provider;
	TextView loc;
	EditText videoTitle;
	String lat;
	String lng;
	UploadPost t;
	Context context;
	String decision = "NOT OK";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_set_data);
		final File currentVideo = utils.lastFileModified();
		Toast.makeText(getApplicationContext(), "this is the file: " + currentVideo.getName(), Toast.LENGTH_SHORT).show();
		Log.d("daba", "this is the file: " + currentVideo.getName());
		context = this;
		ImageView iv = (ImageView) findViewById(R.id.thumbnail);
		Bitmap thumb = null;
		try {
			thumb = ThumbnailUtils.createVideoThumbnail(currentVideo.getCanonicalPath(),
					MediaStore.Images.Thumbnails.MINI_KIND);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		iv.setImageBitmap(thumb);
		videoTitle = (EditText) findViewById(R.id.videoTitle);

		Button b = (Button) findViewById(R.id.submit);

		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub


				new AsyncTask<Void, String, String>(){
					String response;
					
					@Override
					protected void onPreExecute() {
						super.onPreExecute();
						Toast.makeText(context, "starting..", Toast.LENGTH_SHORT).show();
					}
					@Override
					protected String doInBackground(Void... params) {
						// TODO Auto-generated method stub
						try {
							response = utils.myUpload(currentVideo.getCanonicalPath(), currentVideo.getName(), VideoSetData.this, videoTitle.getText().toString(), lat, lng);
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return response;
					}

					@Override
					protected void onPostExecute(String result) {
						Log.d("daba", "status code: " + result);
						if(result.equals("200")){
							decision = "OK";
							Toast.makeText(context, "done", Toast.LENGTH_LONG).show();
							Intent i = new Intent(context, HomeActivity.class);
							context.startActivity(i);
							finish();
						}
						else{
							decision = "NOT OK";
							Toast.makeText(context, "error uploading", Toast.LENGTH_LONG).show();
							
						}
					}
				}.execute();
			}});

	}

	public void onResume(){
		super.onResume();

		new AsyncTask<String, String, String>(){

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				lm = (LocationManager) getSystemService(LOCATION_SERVICE);
				Criteria c = new Criteria();
				//Location l = lm.getLastKnownLocation(lm.getBestProvider(c, false));
				Location l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if(l!=null){
					Log.d("daba", "workig..: " + l.getLatitude() + " -- and: " + l.getLongitude());
					lat = l.getLatitude() + "";
					lng = l.getLongitude() + "";
				}

				else
					Log.d("daba", "sorry..");
				return null;
			}}.execute("");



			//		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
			//		if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			//			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			//			  startActivity(intent);
			//		}
			//		
			//		Criteria criteria = new Criteria();
			//		
			//		provider = lm.getBestProvider(criteria, false);
			//		Location location = lm.getLastKnownLocation(provider);
			//		
			//		if(location != null){
			//			Log.i("daba", "provider selected: " + provider);
			//			Toast.makeText(getApplicationContext(), "workig..: " + location.getLatitude() + " -- and: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
			//			//onLocationChanged(location);			
			//		}
			//		else{
			//			Toast.makeText(getApplicationContext(), "sorry..", Toast.LENGTH_SHORT).show();
			//			Log.i("daba", "not working...sorry for me");
			//		}

			//lm.requestLocationUpdates(provider, 400, 1, this);

	}

	@Override
	protected void onPause() {
		super.onPause();
		//lm.removeUpdates(this);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video_set_data, menu);
		return true;
	}

	//	@Override
	//	public void onLocationChanged(Location location) {
	//		// TODO Auto-generated method stub
	//		double lat = location.getLatitude();
	//		double lng = location.getLongitude();
	//		loc.setText("lat: " + String.valueOf(lat) + " -- and lng: " + String.valueOf(lng));
	//	}
	//
	//	@Override
	//	public void onProviderDisabled(String provider) {
	//		// TODO Auto-generated method stub
	//		Toast.makeText(this, "Disabled provider " + provider,
	//		        Toast.LENGTH_SHORT).show();
	//	}
	//
	//	@Override
	//	public void onProviderEnabled(String provider) {
	//		// TODO Auto-generated method stub
	//		Toast.makeText(this, "Enabled new provider " + provider,
	//		        Toast.LENGTH_SHORT).show();
	//	}
	//
	//	@Override
	//	public void onStatusChanged(String provider, int status, Bundle extras) {
	//		// TODO Auto-generated method stub
	//		
	//	}



}
