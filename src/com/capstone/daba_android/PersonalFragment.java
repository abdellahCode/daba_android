package com.capstone.daba_android;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PersonalFragment extends Fragment {
	View view;
	SharedPreferences sp = null;
	SharedPreferences prefs = null;
	TextView username = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		view = inflater.inflate(R.layout.personal_fragment, container, false);

		username = (TextView) view.findViewById(R.id.username);
		Button logout = (Button) view.findViewById(R.id.logout);
		logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Database db = new Database(getActivity());
				db.deleteVideos();


				sp = getActivity().getSharedPreferences(getString(R.string.sharedPreferencesName), Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = sp.edit();
				Log.d("daba", "profile logout...:" + sp.getString("username", "John Doe"));				
				edit.putString("username", "John Doe");
				edit.putString("user_id", "-1");
				//edit.putString("firstname", "John Doe");
				//edit.putString("lastname", "John Doe");
				edit.putString("loginStatus", "Not Logged");
				edit.commit();
				Log.d("daba", "and...:" + sp.getString("username", "John Doe") + " -- id: " + sp.getString("user_id", "-2"));
				Intent i = new Intent(getActivity(), LoginActivity.class);
				getActivity().startActivity(i);
				getActivity().finish();
				Log.d("daba", "profile logout done...");
				Toast.makeText(getActivity(), "Logging out..", Toast.LENGTH_SHORT).show();
			}
		});
		return (view);
	}

	public void onStar(){
		super.onStart();

		String username = sp.getString("username", "John Doe");
		//String firstname = sp.getString("firstname", "John Doe");
		//String lastname = sp.getString("lastname", "John Doe");

		TextView user = (TextView) view.findViewById(R.id.username);
		user.setText(username);
		//TextView first = (TextView) view.findViewById(R.id.firstname);
		//first.setText(firstname);
		//TextView last = (TextView) view.findViewById(R.id.lastname);
		//last.setText(lastname);

		//Log.d("daba", "profile..." + firstname + " -- " + lastname);

	}


	public void refresh(){
		
		prefs = getActivity().getSharedPreferences(getString(R.string.sharedPreferencesName), Context.MODE_PRIVATE);
		
		username.setText(prefs.getString("username", "username"));
	}
}

