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
public class Login extends AsyncTask<String, Integer, String>{
	public Activity loginActivity;
	String line = "";
	String returned = "";
	ProgressDialog pd = null;
	Context context;
	String[] creds = new String[2];
	String sessionid = null;


	public Login(Activity activity, Context c, ProgressDialog dialog){
		this.loginActivity = activity;	
		context = c;
	}
	/*Show the loading dialog*/
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pd = new ProgressDialog(context);
		pd.setMessage("Authenticating..");
		pd.setCancelable(false);
		pd.setIndeterminate(true);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}
	/*Construct the HTTP request, load parameters and execute it, read HTTP response, and decicde on the next action*/
	@Override
	protected String doInBackground(String... params) {
		HttpParams httparams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httparams, 20000);
		HttpConnectionParams.setSoTimeout(httparams, 25000);
		HttpClient httpclient = new DefaultHttpClient(httparams);
		HttpPost httppost = new HttpPost("http://192.168.154.1:8000/login/");
		//HttpPost httppost = new HttpPost("http://127.0.0.1:8000/login/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
		nameValuePairs.add(new BasicNameValuePair("username", params[0]));  
		nameValuePairs.add(new BasicNameValuePair("password", params[1])); 

		creds = params;
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
				//for(int j = 0; j < response.getAllHeaders().length; j++)
				Log.d("daba", response.getAllHeaders()[4].getValue());
				
				StringTokenizer st = new StringTokenizer(response.getAllHeaders()[4].getValue(), ";");
				sessionid = st.nextToken().substring(10);
				Log.d("daba", "the sessionid: " + sessionid);
				
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
		String loginStatus = null, user_id = null, username = null;
		try {
				json = new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				loginStatus = (String) json.get("loginStatus");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("Login","this is the loginStatus: "+result);
			if(loginStatus.equalsIgnoreCase("INCORRECT CREDS")){
				Toast.makeText(context, "Bad email or password", Toast.LENGTH_LONG).show();
			}
			else if(loginStatus.equalsIgnoreCase("TIMEOUT")){
				//setContentView(R.layout.activity_login);
				Toast.makeText(context.getApplicationContext(), "Network Problem", Toast.LENGTH_LONG).show();
			}

			else if(result.length() == 0){
				Toast.makeText(context.getApplicationContext(), "Network Problem", Toast.LENGTH_LONG).show();			
			}
			else if(loginStatus.equalsIgnoreCase("AUTHENTICATED")){
				try {
					user_id = (String) json.get("user_id");
					username = (String) json.get("username");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(context, "You are in", Toast.LENGTH_SHORT).show();
				this.loginActivity.finish();
				Intent i = new Intent(context, HomeActivity.class);
				//		Intent i = new Intent(this, Dashboard.class);
				i.putExtra("username", username);
				i.putExtra("user_id", user_id);
				i.putExtra("sessionid", sessionid);
				context.startActivity(i);

				// TODO Auto-generated catch block
			}
			else{
				Toast.makeText(context, "WRONG: " + result, Toast.LENGTH_SHORT).show();
			}
			if(pd != null)
				pd.dismiss();
		
	}
}