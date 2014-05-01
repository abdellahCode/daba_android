package com.capstone.daba_android;

import java.io.File;
import java.io.IOException;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mms.exif.ExifInterface.GpsTrackRef;
import com.capstone.utils.*;

public class VideoSetData extends Activity {
	LocationManager lm;
	String provider;
	TextView loc;
	EditText videoTitle;
	String lat_;
	String lng_;
	UploadPost t;
	Context context;
	String decision = "NOT OK";
	ProgressBar loading;
	Button button;
	ProgressDialog pd = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_set_data);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0E46AA")));
		final File currentVideo = utils.lastFileModified();
		Toast.makeText(getApplicationContext(), "this is the file: " + currentVideo.getName(), Toast.LENGTH_SHORT).show();
		Log.d("daba", "this is the file: " + currentVideo.getName());
		context = this;
		ImageView iv = (ImageView) findViewById(R.id.thumbnail);
		Bitmap thumb = null;
		LocationService ls = new LocationService(getApplicationContext());
		if(ls.canGetLocation()){
			lat_ = String.valueOf(ls.getLocation().getLatitude());
			lng_ = String.valueOf(ls.getLocation().getLongitude());
		}
		else{
			ls.showSettingsAlert();
		}
		
		
		
		try {
			MediaMetadataRetriever m = new MediaMetadataRetriever();
			m.setDataSource(currentVideo.getAbsolutePath());
			thumb = m.getFrameAtTime();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		iv.setImageBitmap(thumb);
		videoTitle = (EditText) findViewById(R.id.videoTitle);
		loading = (ProgressBar) findViewById(R.id.loading);
		button = (Button) findViewById(R.id.submit);
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
						//Toast.makeText(context, "starting..", Toast.LENGTH_SHORT).show();
						//loading.setVisibility(View.VISIBLE);
						//button.setEnabled(false);
						pd = new ProgressDialog(context);
						pd.setMessage("Posting..");
						pd.setCancelable(false);
						pd.setIndeterminate(true);
						pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						pd.show();
					}
					@Override
					protected String doInBackground(Void... params) {
						// TODO Auto-generated method stub
						try {
							response = utils.myUpload(currentVideo.getCanonicalPath(), currentVideo.getName(), VideoSetData.this, videoTitle.getText().toString(), lat_, lng_);

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
							pd.dismiss();
							//Toast.makeText(context, "done", Toast.LENGTH_LONG).show();
							Intent i = new Intent(context, HomeActivity.class);
							i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							context.startActivity(i);
							finish();
							//loading.setVisibility(View.INVISIBLE);
						}
						else{
							decision = "NOT OK";
							Toast.makeText(context, "error uploading", Toast.LENGTH_LONG).show();
							loading.setVisibility(View.INVISIBLE);
							button.setEnabled(true);
							pd.dismiss();
						}
					}
				}.execute();
			}});

	}

	public void onResume(){
		super.onResume();

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
}
