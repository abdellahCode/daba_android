package com.capstone.daba_android;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.capstone.utils.Follow_Location;;

public class MapFragment extends Fragment {
	private GoogleMap map;
	private com.google.android.gms.maps.MapFragment fragment;
	private MapView mapview;
	AlertDialog.Builder builder;
	private LatLng latlng = null;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.locations_layout, container, false);
		 mapview = (MapView) v.findViewById(R.id.mapView);
		 mapview.onCreate(savedInstanceState);
		 map = mapview.getMap();

		return v;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void onResume(){
		super.onResume();
		mapview.onResume();
		
		builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Follow Location:");
		builder.setPositiveButton("Follow", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Follow_Location fl = new Follow_Location(getActivity(), getActivity(), latlng);
				fl.execute("");
				Toast.makeText(getActivity(), "following...", Toast.LENGTH_SHORT).show();
			}
		});
		builder.setNegativeButton("Cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		Log.d("daba", "setting the clicks...");
		map.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng point) {
				// TODO Auto-generated method stub
				Log.d("daba", "long click");
				latlng = point;
				Toast.makeText(getActivity(), "That's a long hold XD", Toast.LENGTH_SHORT).show();
				builder.create().show();

			}
			
			
		});
		map.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				// TODO Auto-generated method stub
				Log.d("daba", "	click");
				Toast.makeText(getActivity(), "That's a click XD", Toast.LENGTH_SHORT).show();					
			}
		});
	}

	//	@Override
	//	public void onMapClick(LatLng point) {
	//		// TODO Auto-generated method stub
	//		Toast.makeText(getActivity(), "That's a click * XD", Toast.LENGTH_SHORT).show();		
	//	}
}
