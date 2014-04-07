package com.capstone.daba_android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "Daba.db";
	//the videos table
	private static final String VIDEOS_TABLE = "VIDEOS";
	private static final String V_ID = "_id";
	private static final String VIDEOS_USER = "user_id";
	private static final String VIDEOS_ID = "video_id";
	private static final String VIDEOS_TITLE = "title";
	private static final String VIDEOS_URL = "url";
	private static final String VIDEOS_UP = "up";
	private static final String VIDEOS_DOWN = "down";
	private static final String VIDEOS_VIEWS = "views";
	private static final String VIDEOS_THUMBNAIL = "thumbnail";
	private static final String VIDEOS_DATETIME = "datetime";

	//the locations table
	private static final String LOCATIONS_TABLE = "LOCATIONS";
	private static final String L_ID = "_id";
	private static final String LOCATIONS_ID = "location_id";
	private static final String LOCATIONS_LONGITUDE = "lng";
	private static final String LOCATIONS_LATITUDE = "lat";
	private static final String LOCATIONS_NAME = "name";
	private static final String LOCATIONS_CITY = "city";
	private static final String LOCATIONS_COUNTRY = "country";


	//locationvideo table
	private static final String LOCATIONVIDEO_TABLE = "LOCATIONVIDEO";
	private static final String LV_ID = "_id";
	private static final String LOCATION = "LOCATION";
	private static final String VIDEO = "VIDEO";


	//profile table
	private static final String PROFILE_TABLE = "PROFILES";
	private static final String P_ID = "_id";
	private static final String USERNAME = "username";
	private static final String FIRSTNAME = "firstname";
	private static final String LASTNAME = "lastname";
	private static final String U_ID = "id";




	private static final String CREATE_VIDEOS_TABLE = "create table " + VIDEOS_TABLE + " ( " + V_ID + " integer primary key " +
			"autoincrement, " + VIDEOS_USER + " INTEGER , " + VIDEOS_ID + " TEXT, " + VIDEOS_DATETIME + " TEXT, " + VIDEOS_TITLE + " TEXT, " + VIDEOS_URL + " TEXT, " + VIDEOS_THUMBNAIL + " TEXT," + VIDEOS_UP + " INTEGER , " +
			VIDEOS_DOWN + " INTEGER , " + VIDEOS_VIEWS + " INTEGER ); ";

	private static final String CREATE_LOCATIONS_TABLE = "create table " + LOCATIONS_TABLE + " ( " + L_ID + " integer primary key " +
			"autoincrement, " + LOCATIONS_LONGITUDE + " REAL , " + LOCATIONS_LATITUDE + " REAL, " + LOCATIONS_NAME + " TEXT, " + LOCATIONS_ID + " TEXT, " + LOCATIONS_CITY + " TEXT , " +
			LOCATIONS_COUNTRY + " TEXT ); ";

	private static final String CREATE_LOCATIONVIDEO_TABLE = "create table " + LOCATIONVIDEO_TABLE + " ( " + LV_ID + " integer primary key " +
			"autoincrement, " + LOCATION + " INTEGER , " + VIDEO + " INTEGER ); ";
	
	private static final String CREATE_PROFILES_TABLE = "create table " + PROFILE_TABLE + " ( " + P_ID + " integer primary key " +
			"autoincrement, " + USERNAME + " TEXT , " + FIRSTNAME + " TEXT, "+ LASTNAME + " TEXT, " + U_ID +" INTEGER ); ";


	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_VIDEOS_TABLE);
		db.execSQL(CREATE_LOCATIONS_TABLE);
		db.execSQL(CREATE_LOCATIONVIDEO_TABLE);
		db.execSQL(CREATE_PROFILES_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + VIDEOS_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + LOCATIONS_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_LOCATIONVIDEO_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_PROFILES_TABLE);
		onCreate(db);
	}

	public Cursor getVideos(){		
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "select * from " + VIDEOS_TABLE;
		Cursor videos = db.rawQuery(query, null);

		return videos;
	}

	public int getVideosCount(){		
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "select * from " + VIDEOS_TABLE;
		Cursor videos = db.rawQuery(query, null);

		return videos.getCount();
	}

	public String[] getCityCountryByVideo(String v_id){
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "select city, country from " + LOCATIONS_TABLE + " where " + LOCATIONS_ID + " in (select " + LOCATION + " from " + LOCATIONVIDEO_TABLE + " where " + VIDEO + " = "+ v_id +")";
		Log.d("daba", "query: " + query);
		Cursor location = db.rawQuery(query, null);
		//location.moveToFirst();
		while(location.moveToNext()){
			if(location.getString(location.getColumnIndex("city")).length() > 0 && location.getString(location.getColumnIndex("country")).length() > 0){
				break;
			}
		}
		return new String[]{location.getString(location.getColumnIndex("city")), location.getString(location.getColumnIndex("country"))};
	}

	public void deleteVideos(){
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "delete from " + VIDEOS_TABLE;
		db.execSQL(query);
		query = "delete from " + LOCATIONS_TABLE;
		db.execSQL(query);
		query = "delete from " + LOCATIONVIDEO_TABLE;
		db.execSQL(query);

	}

	public void addVideos(JSONArray feed){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues v_cv = new ContentValues();
		ContentValues l_cv = new ContentValues();
		ContentValues lv_cv = new ContentValues();
		ContentValues p_cv = new ContentValues();

		try {
			for(int i = 0; i < feed.length(); i++){
				if(feed.getJSONObject(i).getString("model").equals("Daba.video")){
					Log.d("daba", "title: " + feed.getJSONObject(i).getJSONObject("fields").getString("title"));
					v_cv.put("title", feed.getJSONObject(i).getJSONObject("fields").getString("title"));
					v_cv.put("views", feed.getJSONObject(i).getJSONObject("fields").getString("views"));
					v_cv.put("up", feed.getJSONObject(i).getJSONObject("fields").getString("up"));
					v_cv.put("down", feed.getJSONObject(i).getJSONObject("fields").getString("down"));
					v_cv.put("url", feed.getJSONObject(i).getJSONObject("fields").getString("docfile"));
					v_cv.put("user_id", feed.getJSONObject(i).getJSONObject("fields").getString("user"));
					v_cv.put("thumbnail", feed.getJSONObject(i).getJSONObject("fields").getString("thumbnail"));
					v_cv.put("datetime", feed.getJSONObject(i).getJSONObject("fields").getString("timeOfUpload"));
					v_cv.put("video_id", feed.getJSONObject(i).getString("pk"));

					db.insert(VIDEOS_TABLE, null, v_cv);
					v_cv.clear();
				}
				else if(feed.getJSONObject(i).getString("model").equals("Daba.locationmetadata")){
					Log.d("daba", "city: " + feed.getJSONObject(i).getJSONObject("fields").getString("city"));
					l_cv.put("lng", feed.getJSONObject(i).getJSONObject("fields").getString("longitude"));
					l_cv.put("lat", feed.getJSONObject(i).getJSONObject("fields").getString("latitude"));
					l_cv.put("name", feed.getJSONObject(i).getJSONObject("fields").getString("name"));
					l_cv.put("city", feed.getJSONObject(i).getJSONObject("fields").getString("city"));
					l_cv.put("country", feed.getJSONObject(i).getJSONObject("fields").getString("country"));
					l_cv.put("location_id", feed.getJSONObject(i).getString("pk"));
					db.insert(LOCATIONS_TABLE, null, l_cv);
					l_cv.clear();
				}
				else if(feed.getJSONObject(i).getString("model").equals("Daba.locationvideo")){
					Log.d("daba", "location: " + feed.getJSONObject(i).getJSONObject("fields").getString("location"));
					lv_cv.put("location", feed.getJSONObject(i).getJSONObject("fields").getString("location"));
					lv_cv.put("video", feed.getJSONObject(i).getJSONObject("fields").getString("video"));
					db.insert(LOCATIONVIDEO_TABLE, null, lv_cv);
					lv_cv.clear();
				}
				
				else if(feed.getJSONObject(i).getString("model").equals("Daba.profile")){
					Log.d("daba", "profile: " + feed.getJSONObject(i).getJSONObject("fields").getString("username"));
					p_cv.put("location", feed.getJSONObject(i).getJSONObject("fields").getString("username"));
					p_cv.put("location", feed.getJSONObject(i).getJSONObject("fields").getString("first_name"));
					p_cv.put("location", feed.getJSONObject(i).getJSONObject("fields").getString("last_name"));
					p_cv.put("video", feed.getJSONObject(i).getString("pk"));
					db.insert(PROFILE_TABLE, null, p_cv);
					p_cv.clear();
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			db.close();
		}
	}

}

