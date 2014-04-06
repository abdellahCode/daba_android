package com.capstone.daba_android;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends Activity {
	ProgressDialog pd = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		
		String url = getIntent().getExtras().getString("url");
		Log.d("daba", "the video url: " + url);
		VideoView vv = (VideoView) findViewById(R.id.video);
		Uri uri = Uri.parse("http://www.dabanit.com/media/documents/2014/03/05/Video_20140302_035701.mp4");
		vv.setVideoURI(uri);
		MediaController mc = new MediaController(this);
		mc.setAnchorView(vv);
		vv.setMediaController(mc);
		vv.requestFocus();
		
		pd = new ProgressDialog(this);
		pd.setMessage("Loading..");
		pd.setCancelable(false);
		pd.setIndeterminate(true);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
		vv.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				pd.dismiss();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */


}
