package com.capstone.daba_android;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class HomeActivity extends Activity {
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		context = this;
		ImageButton videoRecAct = (ImageButton) findViewById(R.id.videoRecAct);
		videoRecAct.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent recordingActivity = new Intent(context, MainActivity.class);
				context.startActivity(recordingActivity);
				Toast.makeText(getApplicationContext(), "Go get some Dabaz", Toast.LENGTH_SHORT).show();
			}
		});
		
		ImageButton feedAct = (ImageButton) findViewById(R.id.feedAct);
		feedAct.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getFragmentManager().beginTransaction().replace(R.id.container, new MyfeedFragment()).addToBackStack(null).commit();
				Toast.makeText(context, "Feed", Toast.LENGTH_SHORT).show();
				
			}
		});
		
		ImageButton profileAct = (ImageButton) findViewById(R.id.profileAct);
		profileAct.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getFragmentManager().beginTransaction().replace(R.id.container, new PersonalFragment()).addToBackStack(null).commit();
				Toast.makeText(context, "Profile", Toast.LENGTH_SHORT).show();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

}
