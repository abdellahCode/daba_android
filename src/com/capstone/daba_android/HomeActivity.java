package com.capstone.daba_android;

import com.capstone.utils.DefaultFeed;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;

public class HomeActivity extends Activity implements OnTabChangeListener{
	Context context;
	TabHost tabhost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.feed_layout);	
		context = this;
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2565C7")));

		tabhost = (TabHost)findViewById(R.id.tabhost);
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

		//setting the profile tab.
		Spec = tabhost.newTabSpec("locations");
		Spec.setContent(R.id.tab4);
		Spec.setIndicator("Locations");
		tabhost.addTab(Spec);

		tabhost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				Log.d("daba", "tabId: " + tabId);
				if(tabId.equals("feed")){
					Log.d("daba", "in feed");
					invalidateOptionsMenu();
					//ab.set

				}
				else if(tabId.equals("profile")){
					Log.d("daba", "in profile");
					invalidateOptionsMenu();

				}
			}
		});

		tw.getChildAt(1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("daba", "haaa");
				HomeActivity.this.startActivity(new Intent(HomeActivity.this, RecordingActivity.class));
			}
		});

		tw.getChildAt(2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("daba", "haaaha");
				tabhost.setCurrentTab(2);
				PersonalFragment pf = (PersonalFragment) getFragmentManager().findFragmentById(R.id.personal_fragment);
				pf.refresh();
			}
		});
		tw.getChildAt(3).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("daba", "haaaha");
				tabhost.setCurrentTab(3);
				MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.map_container);
				mf.mapAnimation();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		SharedPreferences sp = this.getSharedPreferences(getString(R.string.sharedPreferencesName), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		try{
			editor.putString("username", getIntent().getExtras().getString("username"));
			editor.putString("user_id", getIntent().getExtras().getString("user_id"));
			editor.putString("firstname", getIntent().getExtras().getString("firstname"));
			editor.putString("lastname", getIntent().getExtras().getString("lastname"));
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
		MenuInflater mi = getMenuInflater();
		Log.i("daba", "create");
		menu.clear();
		if(tabhost.getCurrentTab() == 0){
			Log.i("daba", "tab id: " + tabhost.getCurrentTab());
			mi.inflate(R.menu.feed, menu);			
			menu.findItem(R.id.action_refresh).setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					//refresh code...
					//setProgressBarIndeterminateVisibility(true); 

					DefaultFeed df = new DefaultFeed((ListFragment) getFragmentManager().findFragmentById(R.id.feedfragment), context, null);
					df.execute("");	
					return true;
				}
			});

		}
		else if(tabhost.getCurrentTab() == 2){
			Log.i("daba", "tab id: " + tabhost.getCurrentTab());
			mi.inflate(R.menu.profile, menu);
		}
		return true;
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		Log.d("daba", "tabId: " + tabId);
		if(tabId.equals("0")){
			ActionBar ab = getActionBar();
			Button b = new Button(context);
			b.setBackgroundColor(Color.WHITE);
			b.setText("TEST");
			ab.setCustomView(b);

		}
		else{
			ActionBar ab = getActionBar();
			Button b = new Button(context);
			b.setBackgroundColor(Color.GREEN);
			b.setText("OKAY");
			ab.setCustomView(b);
		}
	}

}
