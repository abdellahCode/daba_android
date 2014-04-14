package com.capstone.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.capstone.daba_android.R;
import com.capstone.daba_android.R.string;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Follow_Location extends AsyncTask<String, String, String> {
	public Activity loginActivity;
	String line = "";
	String returned = "";
	ProgressDialog pd = null;
	Context context;
	LatLng point = null;
	String sessionid = null;


	public Follow_Location(Activity activity, Context c, LatLng point){
		this.loginActivity = activity;	
		context = c;
		this.point = point;
	}
	/*Show the loading dialog*/
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pd = new ProgressDialog(context);
		pd.setMessage("Following..");
		pd.setCancelable(true);
		pd.setIndeterminate(true);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}
	/*Construct the HTTP request, load parameters and execute it, read HTTP response, and decicde on the next action*/
	@Override
	protected String doInBackground(String... params) {
		HttpParams httparams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httparams, 5000);
		HttpConnectionParams.setSoTimeout(httparams, 15000);
		HttpClient httpclient = new DefaultHttpClient(httparams);
		HttpPost httppost = new HttpPost("http://192.168.154.1:8000/lfollow/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
		nameValuePairs.add(new BasicNameValuePair("lat", point.latitude + ""));  
		nameValuePairs.add(new BasicNameValuePair("lng", point.longitude + ""));
		SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sharedPreferencesName), Context.MODE_PRIVATE);
		nameValuePairs.add(new BasicNameValuePair("id", sp.getString("user_id", "-1"))); 


		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}  
		HttpResponse response = null;
		try {
			Log.i("daba", "Before Execute"); 
			response = httpclient.execute(httppost);
			Log.i("daba", "After  Execute, status code: "+response.getStatusLine().getStatusCode());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.i("daba", "ClientProtocolException");
		} catch (IOException e) { 
			e.printStackTrace();
			returned = "TIMEOUT";
			Log.i("daba", "IOException: "+returned);

		}
		finally{
			//httpclient.getConnectionManager().shutdown();
		}
		BufferedReader rd = null;
		Log.i("daba", "before status");
		int statusCode = 0;
		try{
			statusCode = response.getStatusLine().getStatusCode();
		}
		catch(NullPointerException n){
			Log.i("daba", "No status code, network problem");
		}
		Log.i("Login", "after status: "+statusCode);
		if(statusCode == 401){
			Log.i("daba", "401");
			returned = "USER_PASS_WRONG";			
		}
		else if(statusCode == 403){
			Log.i("Login", "403");
			returned = "API_KEY_CHANGED";
		}
		else if(statusCode == 200){
			Log.i("daba", "200");
			try {
				rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			} catch (IllegalStateException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}catch(NullPointerException n){
				Log.i("daba", "NULL in RESPONSE");

			}
			try {
				while((line = rd.readLine()) != null){
					returned = returned + line;	
					Log.i("daba", "this is it again"+line);
				}
				Log.d("daba", "heeeeeeeeeeere...");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(NullPointerException n){
				Log.i("Login", "NULL RESPONSE");

			}}
		Log.i("daba", "before returning: "+returned);
		return returned;
	}



	@Override
	protected void onPostExecute(String result) {
		JSONObject json = null;
		String status = null;
		try {
			json = new JSONObject(result);
			status = (String) json.get("status");

			if(status.equalsIgnoreCase("ok")){
				Toast.makeText(context, "started following", Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(context, "following failed, please try again", Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(context, "following failed, please try again", Toast.LENGTH_LONG).show();
		}

		if(pd != null)
			pd.dismiss();

	}

}
