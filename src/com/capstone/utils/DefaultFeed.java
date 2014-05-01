package com.capstone.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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

import com.capstone.daba_android.Database;
import com.capstone.daba_android.HomeActivity;
import com.capstone.daba_android.MapFragment;
import com.capstone.daba_android.R;


import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.*;

import android.widget.ListAdapter;
import android.widget.Toast;

/*This class sends the login request, and depending on the HTTP response,
 we decide to log the user in or keep them on the login activity*/
public class DefaultFeed extends AsyncTask<String, Integer, String>{
	public ListFragment myfeedfragment;
	String line = "";
	String returned = "";
	ProgressDialog pd = null;
	Context context;
	//String[] creds = new String[2];


	public DefaultFeed(ListFragment feedfragment, Context c, ProgressDialog dialog){
		this.myfeedfragment = feedfragment;	
		context = c;
	}
	/*Show the loading dialog*/
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pd = new ProgressDialog(context);
		pd.setMessage("refreshing..");
		pd.setCancelable(true);
		pd.setIndeterminate(true);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		//pd.show();
		myfeedfragment.getActivity().setProgressBarIndeterminateVisibility(true);
	}
	/*Construct the HTTP request, load parameters and execute it, read HTTP response, and decicde on the next action*/
	@Override
	protected String doInBackground(String... params) {
		HttpParams httparams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httparams, 5000);
		HttpConnectionParams.setSoTimeout(httparams, 15000);
		HttpClient httpclient = new DefaultHttpClient(httparams);
		//HttpPost httppost = new HttpPost("http://192.168.154.1:8000/lfeed/");
		HttpPost httppost = new HttpPost("http://www.dabanit.com/daba_server/lfeed/");
		SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sharedPreferencesName), Context.MODE_PRIVATE);
		Log.d("daba", "the session id feed: " + sp.getString("sessionid", "None"));
		//httppost.addHeader("sessionid", sp.getString("sessionid", "None"));
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
		nameValuePairs.add(new BasicNameValuePair("id", sp.getString("user_id", "-1")));  
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
		JSONArray json = null;
		
		try {
				json = new JSONArray(result);
				Database db = new Database(context);
				db.deleteVideos();
				db.addVideos(json);
				Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
				myfeedfragment.getActivity().setProgressBarIndeterminateVisibility(false);
				if(pd != null)
					pd.dismiss();
				Cursor c = db.getVideos();
				
				ListAdapter la = new MyCursorLoader(myfeedfragment.getActivity(), R.layout.feed_row, c, new String[] {"title", "url"},
						new int[]{R.id.title, R.id.url});
				
				myfeedfragment.setListAdapter(la);
				myfeedfragment.getListView().invalidateViews();
				myfeedfragment.setListAdapter(la);
				myfeedfragment.getListView().setFocusable(false);
				MapFragment mf = (MapFragment) myfeedfragment.getActivity().getFragmentManager().findFragmentById(R.id.map_container);
				mf.refresh();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				if(pd != null)
					//pd.dismiss();
				try{
				myfeedfragment.getActivity().setProgressBarIndeterminateVisibility(false);
				
				MapFragment mf = (MapFragment) myfeedfragment.getActivity().getFragmentManager().findFragmentById(R.id.map_container);
				mf.refresh();
				e.printStackTrace();
				Toast.makeText(context, "Network problem..", Toast.LENGTH_LONG).show();
				}
				catch(NullPointerException a){
					
				}
			}
			
				

				// TODO Auto-generated catch block
			
	}
}