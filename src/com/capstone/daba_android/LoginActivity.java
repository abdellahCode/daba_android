package com.capstone.daba_android;

import com.capstone.utils.Login;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2565C7")));
		SharedPreferences sp = this.getSharedPreferences(getString(R.string.sharedPreferencesName), Context.MODE_PRIVATE);
		String loginStatus = sp.getString("loginStatus", "Not Logged");
		Log.i("daba", "the loginStatus: " + loginStatus);
		if(loginStatus.equals("AUTHENTICATED")){
			Log.i("daba", "the user: " + sp.getString("username", "noone") + " with id: " + sp.getString("user_id", "-1"));
			LoginActivity.this.startActivity(new Intent(this, HomeActivity.class));
			this.finish();
		}
		//Log.i("daba", "the loginStatus: ");
		final Activity activity = LoginActivity.this;
		final Context c = this;
		TextView register = (TextView) findViewById(R.id.register);
		register.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
				
				
			}
		});
		final EditText username = (EditText) findViewById(R.id.username);
		final EditText password = (EditText) findViewById(R.id.password);
		Button login = (Button)findViewById(R.id.loginButton);
		login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Login login = new Login(activity, c, null);
				String[] userpass = {username.getText().toString(), password.getText().toString()};
				login.execute(userpass);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
