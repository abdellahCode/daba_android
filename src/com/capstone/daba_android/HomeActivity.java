package com.capstone.daba_android;


import android.os.Bundle;
import android.content.SharedPreferences;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.Toast;

public class HomeActivity extends Activity implements OnTabChangeListener{
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_layout);
		context = this;

		TabHost tabhost = (TabHost)findViewById(R.id.tabhost);
		tabhost.setup();

		//setting the feed tab.
		TabHost.TabSpec Spec = tabhost.newTabSpec("feed");
		Spec.setContent(R.id.tab1);
		Spec.setIndicator("Feed");
		tabhost.addTab(Spec);

		//setting the video recording activity
		TabWidget tw = (TabWidget) findViewById(android.R.id.tabs);
		Spec = tabhost.newTabSpec("start");

		Spec.setContent(R.id.tab3);
		Spec.setIndicator("Start");
		tabhost.addTab(Spec);

		//setting the profile tab.
		Spec = tabhost.newTabSpec("profile");
		Spec.setContent(R.id.tab3);
		Spec.setIndicator("Profile");
		tabhost.addTab(Spec);
		
		
		tw.getChildAt(1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("daba", "haaa");
				HomeActivity.this.startActivity(new Intent(HomeActivity.this, MainActivity.class));
			}
		});
		
//		tw.getChildAt(0).setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("daba", "haaa");
				
//				Fragment f = new MyfeedFragment();
//				FragmentTransaction ft = getFragmentManager().beginTransaction();
//				ft.replace(android.R.id.tabcontent, f);
//				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//				ft.commit();

//			}
//		});


	}

	@Override
	protected void onStart() {
		super.onStart();
		SharedPreferences sp = this.getSharedPreferences(getString(R.string.sharedPreferencesName), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		try{
			editor.putString("username", getIntent().getExtras().getString("username"));
			editor.putString("user_id", getIntent().getExtras().getString("user_id"));
			editor.putString("loginStatus", "AUTHENTICATED");
			editor.putString("sessionid", getIntent().getExtras().getString("sessionid"));
			editor.commit();
		}
		catch(NullPointerException npe){
			Log.e("daba", "null exception");
		}
		Log.i("daba", "the loginStatus: " + sp.getString("loginStatus", "Not Logged"));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		
	}

}
