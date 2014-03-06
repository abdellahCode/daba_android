package com.capstone.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.HttpClient;

import android.os.Environment;
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
		String upLoadServerUri = "http://www.dabanit.com/daba_server/list/";
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
	
	public int myUpload(){
		
		HttpClient client;
		
		return 0;
	}


}
