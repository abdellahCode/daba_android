package com.capstone.daba_android;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.os.Build;

public class RegisterActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		final EditText username = (EditText) findViewById(R.id.username);
		final EditText firstname = (EditText) findViewById(R.id.firstname);
		final EditText lastname = (EditText) findViewById(R.id.lastname);
		final EditText email = (EditText) findViewById(R.id.email);
		final EditText password = (EditText) findViewById(R.id.password);
		final EditText city = (EditText) findViewById(R.id.city);
		final EditText country = (EditText) findViewById(R.id.country);
		
		Button register  = (Button) findViewById(R.id.register);
		register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String[] new_user = new String[7];
				new_user[0] = username.getText().toString();
				new_user[1] = firstname.getText().toString();
				new_user[2] = lastname.getText().toString();
				new_user[3] = email.getText().toString();
				new_user[4] = password.getText().toString();
				new_user[5] = city.getText().toString();
				new_user[6] = country.getText().toString();
				
				com.capstone.utils.register send_new_user = new com.capstone.utils.register(RegisterActivity.this, getApplicationContext(), null);
				send_new_user.execute(new_user);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
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



}
