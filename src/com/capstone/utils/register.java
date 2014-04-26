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
import com.capstone.daba_android.HomeActivity;
import com.capstone.daba_android.LoginActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import org.json.*;

import android.widget.Toast;

/*This class sends the login request, and depending on the HTTP response,
 we decide to log the user in or keep them on the login activity*/
public class register extends AsyncTask<String, Integer, String>{
	public Activity registerActivity;
	String line = "";
	String returned = "";
	Context context = null;
	ProgressDialog pd = null;



	public register(Activity activity, Context c, ProgressDialog dialog){
		this.registerActivity = activity;	
		this.context = c;
	}
	/*Show the loading dialog*/
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pd = new ProgressDialog(registerActivity);
		pd.setMessage("Registering..");
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
		//HttpPost httppost = new HttpPost("http://192.168.154.1:8000/login/");
		HttpPost httppost=new HttpPost("http://www.dabanit.com/daba_server/register/");
		//HttpPost httppost = new HttpPost("http://127.0.0.1:8000/login/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);  
		nameValuePairs.add(new BasicNameValuePair("username", params[0]));  
		nameValuePairs.add(new BasicNameValuePair("firstName", params[1])); 
		nameValuePairs.add(new BasicNameValuePair("lastName", params[2]));
		nameValuePairs.add(new BasicNameValuePair("email", params[3]));
		nameValuePairs.add(new BasicNameValuePair("password", params[4]));
		nameValuePairs.add(new BasicNameValuePair("city", params[5]));
		nameValuePairs.add(new BasicNameValuePair("country", params[6]));
		nameValuePairs.add(new BasicNameValuePair("birthdate", "1991-1-1"));


		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}  
		HttpResponse response = null;
		try {
			Log.i("Login", "Before Execute"); 
			response = httpclient.execute(httppost);
			Log.i("Login", "After  Execute, status code: "+response.getStatusLine().getStatusCode());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.i("Login", "ClientProtocolException");
		} catch (IOException e) { 
			e.printStackTrace();
			returned = "TIMEOUT";
			Log.i("Login", "IOException: "+returned);

		}
		finally{
			//httpclient.getConnectionManager().shutdown();
		}
		BufferedReader rd = null;
		Log.i("Login", "before status");
		int statusCode = 0;
		try{
			statusCode = response.getStatusLine().getStatusCode();
		}
		catch(NullPointerException n){
			Log.i("Login", "No status code, network problem");
		}
		Log.i("Login", "after status: "+statusCode);
		if(statusCode == 401){
			Log.i("Login", "401");
			returned = "USER_PASS_WRONG";			
		}
		else if(statusCode == 403){
			Log.i("Login", "403");
			returned = "API_KEY_CHANGED";
		}
		else if(statusCode == 200){
			Log.i("Login", "200");
			try {
				rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			} catch (IllegalStateException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}catch(NullPointerException n){
				Log.i("Login", "NULL in RESPONSE");

			}
			try {
				while((line = rd.readLine()) != null){
					returned = returned + line;	
					Log.i("Login", "this is it again"+line);
				}
				Log.d("daba", "heeeeeeeeeeere...");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(NullPointerException n){
				Log.i("Login", "NULL RESPONSE");

			}}
		Log.i("login", "before returning: "+returned);
		return returned;
	}



	@Override
	protected void onPostExecute(String result) {
		JSONObject json = null;
		String registerStatus;
		try {
			json = new JSONObject(result);
			registerStatus = (String) json.get("registerStatus");
			Log.i("register","this is the registerStatus: "+result);
			if(registerStatus.equalsIgnoreCase("success")){
				Toast.makeText(context, "registered", Toast.LENGTH_LONG).show();
				this.registerActivity.finish();
				Intent i = new Intent(context, LoginActivity.class);				
				context.startActivity(i);
			}
			else
				Toast.makeText(context, "registration error", Toast.LENGTH_LONG).show();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(context, "registeration error", Toast.LENGTH_LONG).show();
		}

		if(pd != null)
			pd.dismiss();

	}
}