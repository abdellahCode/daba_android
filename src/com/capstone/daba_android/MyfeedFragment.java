package com.capstone.daba_android;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.capstone.utils.DefaultFeed;
import com.capstone.utils.ImageLoader;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MyfeedFragment extends ListFragment {
	Database db = null;
	View showVideosView;
	ListAdapter la;
	String video_url = null;
	public ImageLoader imageLoader; 
	Context context;
	public ListFragment fragment = this;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		context = getActivity();
		showVideosView = inflater.inflate(R.layout.myfeed_fragment, container, false);
		
		Log.d("tabs", "onCreateView feed");
		return (showVideosView);
	}


	public void onStart(){
		super.onStart();
		Log.d("tabs", "onStart feed");
		imageLoader=new ImageLoader(getActivity());

	}
	
	public void onPause(){
		super.onPause();
		Log.d("tabs", "onPause feed");
	}

	
	public void onResume(){
		super.onResume();
		Log.d("tabs", "onResume feed");
		DefaultFeed df = new DefaultFeed(this, context, null);
		df.execute("");		
		db = new Database(getActivity());
		Cursor c = db.getVideos();
		la = new mycursoradapter(getActivity(), R.layout.feed_row, c, new String[] {"title", "url"},
				new int[]{R.id.title, R.id.url});
		setListAdapter(la);

		ListView lv = getListView();
		lv.setFocusable(false);
//		lv.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View row, int position,
//					long id) {
//				// TODO Auto-generated method stub
//				TextView url = (TextView) row.findViewById(R.id.url);
//				Log.d("daba", "the item: " + position + " url:" + url.getText().toString());				
//				String video = url.getText().toString();
//				Intent i = new Intent(getActivity(), VideoActivity.class);
//				i.putExtra("url", video);
//				getActivity().startActivity(i);
//				//VideoView vv = (VideoView) arg0.findViewById(R.id.video);
//				//vv.start();
//				//Log.d("daba", "starting.." + vv.get);	
//
//			}
//
//
//		});




	}


	public class ViewHolder {
		public TextView title;
		public TextView url;
		public TextView city;
		public TextView country;
		public VideoView vv;
		public ImageView iv;
		public ImageView info;
		//public ImageView playVideo;
		public TextView report;
		public TextView userDetails;
		public TextView datetime;

	}
	class mycursoradapter extends SimpleCursorAdapter{
		Context context;

		public mycursoradapter(Context context, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to, 2);
			// TODO Auto-generated constructor stub
			Log.i("myadapter", "constructor");
			this.context = context;
		}

		@Override
		public void bindView(View view, final Context context, Cursor cursor) {
			ViewHolder holder;
			if(db==null)
				db = new Database(context);
			Log.i("daba", "bindView");
//			try{
				if (view != null) {
					holder = new ViewHolder();
					holder.title = (TextView) view.findViewById(R.id.title);
					holder.city = (TextView) view.findViewById(R.id.city);
					holder.country = (TextView) view.findViewById(R.id.country);
					holder.url = (TextView) view.findViewById(R.id.url);
					holder.iv = (ImageView) view.findViewById(R.id.thumb);
					holder.datetime = (TextView) view.findViewById(R.id.datetime);
					holder.report = (TextView) view.findViewById(R.id.report);
					holder.userDetails = (TextView) view.findViewById(R.id.userDetails);
					holder.info = (ImageView) view.findViewById(R.id.info);
					//holder.playVideo = (ImageView) view.findViewById(R.id.play);
					//holder.vv = (VideoView) view.findViewById(R.id.video);
					view.setTag(holder);
				} else {
					holder = (ViewHolder) view.getTag();
				}


				holder.title.setText(cursor.getString(cursor.getColumnIndex("title")));
				Date date = null;
				try {
					date = new SimpleDateFormat("yyy-MM-dd HH:mm:ss").parse(cursor.getString(cursor.getColumnIndex("datetime")).replace("T", " ").replace("Z", ""));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("date", "not working");
				}
				long datetime = date.getTime();
				String timenow = android.text.format.DateUtils.getRelativeTimeSpanString(datetime).toString();
				final String url = cursor.getString(cursor.getColumnIndex("url"));
				holder.datetime.setText(timenow);
				holder.url.setText(cursor.getString(cursor.getColumnIndex("url")));
				video_url = cursor.getString(cursor.getColumnIndex("url"));
				Log.d("daba", "video id: " + cursor.getString(cursor.getColumnIndex("video_id")));
				String[] location = db.getCityCountryByVideo(cursor.getString(cursor.getColumnIndex("video_id")));
				holder.city.setText(location[0] + ", ");
				holder.country.setText(location[1]);
				Log.d("db", "the user ID: " + cursor.getString(cursor.getColumnIndex("user_id")));
				Cursor user = db.getUser(cursor.getString(cursor.getColumnIndex("user_id")));
				user.moveToFirst();
				holder.userDetails.setText("By: " + user.getString(user.getColumnIndex("username")));
				Log.d("db", "after sttext");
				//Uri uri = Uri.parse("http://192.168.154.1:8000/media/" + cursor.getString(cursor.getColumnIndex("url")));
				//Uri uri = Uri.parse("http://www.dabanit.com/media/documents/2014/03/05/Video_20140302_035701.mp4");
				//holder.vv.setVideoURI(uri);
				//String videoFile = "http://www.dabanit.com/media/documents/2014/03/05/Video_20140302_035701.mp4";

				class DownloadImage extends AsyncTask<String, Void, Bitmap>{
					ImageView iv;
					public DownloadImage(ImageView iv){
						this.iv = iv;
					} 
					@Override
					protected Bitmap doInBackground(String... params) {
						// TODO Auto-generated method stub
						String urldisplay = params[0];
						Bitmap mIcon11 = null;
						try {
							InputStream in = new java.net.URL(urldisplay).openStream();
							mIcon11 = BitmapFactory.decodeStream(in);
						} catch (Exception e) {
							Log.e("Error", e.getMessage());
							e.printStackTrace();
						}
						return mIcon11;
					}
					protected void onPostExecute(Bitmap result) {
						iv.setImageBitmap(result);
					}

				}
				imageLoader.DisplayImage("http://www.dabanit.com/daba_server/media/" + cursor.getString(cursor.getColumnIndex("thumbnail")), holder.iv);
				
				holder.iv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "playing the video", Toast.LENGTH_SHORT).show();
						//String video = holder.url.getText().toString();
						Intent i = new Intent(getActivity(), VideoActivity.class);
						i.putExtra("url", url);
						getActivity().startActivity(i);
					}
				});
				
				holder.info.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "getting more info", Toast.LENGTH_SHORT).show();
					}
				});
				
//			}catch(Exception e){
//				Log.e("daba", "-> "+e.getMessage());
//			}
		}
	}
}
