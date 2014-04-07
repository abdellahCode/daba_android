package com.capstone.daba_android;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PersonalFragment extends Fragment {
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		view = inflater.inflate(R.layout.personal_fragment, container, false);
		SharedPreferences sp = getActivity().getSharedPreferences(getString(R.string.sharedPreferencesName), Context.MODE_PRIVATE);
		String username = sp.getString("username", "John Doe");
		String firstname = sp.getString("firstname", "John Doe");
		String lastname = sp.getString("lastname", "John Doe");
		
		TextView user = (TextView) view.findViewById(R.id.username);
		user.setText(username);
		TextView first = (TextView) view.findViewById(R.id.firstname);
		first.setText(firstname);
		TextView last = (TextView) view.findViewById(R.id.lastname);
		last.setText(lastname);
		
		Log.d("daba", "profile...");
		return (view);
	}
	
	public void onStar(){
		super.onStart();
	}
}

