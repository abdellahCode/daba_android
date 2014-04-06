package com.capstone.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.capstone.utils.utils;

import com.capstone.daba_android.R;

public class UploadPost extends AsyncTask<Void, String, String> {
	String videoPath;
	String videoName;
	Context c;
	String title;
	String lat;
	String lng;
	public UploadPost(String videoPath, String videoName, Context c, String title, String lat, String lng){
		this.videoPath = videoPath;
		this.videoName = videoName;
		this.c = c;
		this.lat = lat;
		this.lng = lng;
		
	}

	@Override
	protected String doInBackground(Void... params) {
		// TODO Auto-generated method stub
		StringBuffer responseBody=new StringBuffer();

		Log.i("dabaupload", "Ready to upload file...");
		HttpClient client=new DefaultHttpClient();
		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		Log.i("dabaupload", "Set remote URL...");
		HttpPost post=new HttpPost("http://192.168.154.1:8000/");
		MultipartEntityBuilder entity = MultipartEntityBuilder.create();
		entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		Log.i("dabaupload", "Adding file(s)...");
		FileBody video = new FileBody(new File(videoPath));
		Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoPath,
				MediaStore.Images.Thumbnails.MINI_KIND);
		String thumbnailPath = utils.getThumbnail(thumb, videoName);
		FileBody thumbnail = new FileBody(new File(thumbnailPath));
		SharedPreferences sp = c.getSharedPreferences(c.getString(R.string.sharedPreferencesName), Context.MODE_PRIVATE);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String datetime = sdf.format(new java.util.Date());
		
		Log.d("dabaupload", "Before sending: " + title + " -- " + lat + " -- " + lng + " -- " + sp.getString("user_id", "-1") + " -- " + datetime);
		
		entity.addPart("docfile", video);
		entity.addPart("thumb", thumbnail);
		entity.addTextBody("title", title);
		entity.addTextBody("lat", lat);
		entity.addTextBody("lng", lng);
		entity.addTextBody("id", sp.getString("user_id", "-1"));
		entity.addTextBody("datetime", datetime);

		Log.i("dabaupload", "Set entity...");
		post.setEntity(entity.build());

		BufferedReader bs=null;
		HttpResponse response = null;
		try
		{
			Log.i("dabaupload", "Upload...");
			
			response = client.execute(post);
			HttpEntity hEntity=response.getEntity();
			bs=new BufferedReader(new InputStreamReader(hEntity.getContent()));
			Log.i("dabaupload", "Response length - "+hEntity.getContentLength());
			String s="";
			while(s!=null)
			{
				responseBody.append(s);
				s=bs.readLine();
				Log.i("dabaupload", "Response body - "+s);
			}
			bs.close();
		}
		catch(IOException ioe)
		{
			Log.i("dabaupload", "Error on getting response from Server, "+ioe.toString());
			ioe.printStackTrace();
			responseBody.append("...");
		}
		return response.getStatusLine().getStatusCode()+"";
	}
	
	@Override
	protected void onPostExecute(String result) {
		Log.d("daba", "status code: " + result);
		if(result.equals("200")){
			
			Toast.makeText(c, "done", Toast.LENGTH_LONG).show();
		}
		else
			Toast.makeText(c, "error uploading", Toast.LENGTH_LONG).show();
		
	}
}
