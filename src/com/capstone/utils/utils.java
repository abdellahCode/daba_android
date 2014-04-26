package com.capstone.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.capstone.daba_android.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;

public class utils {

	public static File lastFileModified() {

		String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES+"/DABA").getAbsolutePath();
		File fl = new File(dir);
		File[] files = fl.listFiles(new FileFilter() {			
			public boolean accept(File file) {
				return file.isFile();
			}
		});
		long lastMod = Long.MIN_VALUE;
		File choise = null;
		for (File file : files) {
			if (file.lastModified() > lastMod) {
				choise = file;
				lastMod = file.lastModified();
			}
		}
		return choise;
	}

	public static int upLoad2Server(String sourceFileUri) {
		int serverResponseCode = 0;
		String upLoadServerUri = "http://192.168.154.1:8000/list/";
		//String upLoadServerUri = "192.168.56.1/list/";
		String fileName = sourceFileUri;

		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		DataInputStream inStream = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		String responseFromServer = "";

		File sourceFile = new File(sourceFileUri);
		if (!sourceFile.isFile()) {
			Log.e("daba", "Source File Does not exist");
			return 0;
		}
		try { // open a URL connection to the Servlet
			FileInputStream fileInputStream = new FileInputStream(sourceFile);
			URL url = new URL(upLoadServerUri);
			conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			//conn.setRequestProperty("docFile", fileName);
			dos = new DataOutputStream(conn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"docfile\";filename=\""+ fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
			Log.i("daba", "Initial .available : " + bytesAvailable);

			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// read file and write it into form...
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// Responses from the server (code and message)
			serverResponseCode = conn.getResponseCode();
			String serverResponseMessage = conn.getResponseMessage();

			Log.i("daba", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
			// close streams
			Log.i("daba", fileName + " File is written");
			fileInputStream.close();
			dos.flush();
			Log.i("daba", "done.....");
			dos.close();
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			Log.e("daba", "error: " + ex.getMessage(), ex);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//this block will give the response of upload link
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				Log.i("daba", "RES Message: " + line);
			}
			rd.close();
		} catch (IOException ioex) {
			Log.e("daba", "error: " + ioex.getMessage(), ioex);
		}
		return serverResponseCode;  // like 200 (Ok)

	}

	public static String getThumbnail(Bitmap bmp, String name){
		//String file_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES+"/DABA"
		File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES+"/DABA/Thumbnails");
		if(!dir.exists())
			dir.mkdirs();
		File file = new File(dir, name.replace(".mp4", ".png"));
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//bmp = Bitmap.createScaledBitmap(bmp, 320, 320, false);
		bmp.compress(Bitmap.CompressFormat.PNG, 0, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String path = null;
		try {
			path = file.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("dabaupload", "thumb: " + path);
		return path;
	}

	public static String myUpload(String videoPath, String videoName, Context c, String title, String lat, String lng){

		StringBuffer responseBody=new StringBuffer();

		HttpParams httparams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httparams, 5000);
		HttpConnectionParams.setSoTimeout(httparams, 15000);
		httparams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		Log.i("dabaupload", "Ready to upload file...");
		HttpClient client = new DefaultHttpClient(httparams);

		Log.i("dabaupload", "Set remote URL...");
		HttpPost post=new HttpPost("http://www.dabanit.com/daba_server/");
		//HttpPost post=new HttpPost("http://192.168.154.1:8000/");
		
		MultipartEntityBuilder entity = MultipartEntityBuilder.create();
		entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		Log.i("dabaupload", "Adding file(s)...");
		FileBody video = new FileBody(new File(videoPath));
		Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoPath,
				MediaStore.Video.Thumbnails.MINI_KIND);
		
		String thumbnailPath = getThumbnail(thumb, videoName);
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
		HttpResponse response = null;
		BufferedReader bs=null;
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
			return "000";
		}
		return response.getStatusLine().getStatusCode()+"";
	}


}
