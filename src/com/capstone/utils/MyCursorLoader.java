package com.capstone.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.capstone.daba_android.Database;
import com.capstone.daba_android.R;
import com.capstone.daba_android.VideoActivity;

public class MyCursorLoader extends SimpleCursorAdapter{
	Context context;
	Database db = null;
	String video_url;
	public ImageLoader imageLoader; 
	public MyCursorLoader(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to, 2);
		// TODO Auto-generated constructor stub
		Log.i("myadapter", "constructor");
		this.context = context;
		db = new Database(context);
		imageLoader=new ImageLoader(context);
	}
	public class ViewHolder {
		public TextView title;
		public TextView url;
		public TextView city;
		public TextView country;
		public VideoView vv;
		public ImageView iv;
		public ImageView info;
		public TextView userDetails;
	}

	@Override
	public void bindView(View view, final Context context, Cursor cursor) {
		ViewHolder holder;
		Log.i("daba", "bindView");
		if(db==null)
			db = new Database(context);

		//try{
			if (view != null) {
				holder = new ViewHolder();
				holder.title = (TextView) view.findViewById(R.id.title);
				holder.city = (TextView) view.findViewById(R.id.city);
				holder.country = (TextView) view.findViewById(R.id.country);
				holder.url = (TextView) view.findViewById(R.id.url);
				holder.userDetails = (TextView) view.findViewById(R.id.userDetails);
				holder.iv = (ImageView) view.findViewById(R.id.thumb);
				//holder.vv = (VideoView) view.findViewById(R.id.video);
				video_url = cursor.getString(cursor.getColumnIndex("url"));
				holder.info =(ImageView) view.findViewById(R.id.info);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}


			holder.title.setText(cursor.getString(cursor.getColumnIndex("title")));
			holder.url.setText(cursor.getString(cursor.getColumnIndex("url")));
			final String url = cursor.getString(cursor.getColumnIndex("url"));
			Log.d("daba", "video id: " + cursor.getString(cursor.getColumnIndex("video_id")));
			String[] location = db.getCityCountryByVideo(cursor.getString(cursor.getColumnIndex("video_id")));
			holder.city.setText(location[0]);
			holder.country.setText(location[1]);
			Cursor user = db.getUser(cursor.getString(cursor.getColumnIndex("user_id")));
			user.moveToFirst();
			holder.userDetails.setText("By: " + user.getString(user.getColumnIndex("username")));
			
			//String videoFile = "http://www.dabanit.com/media/documents/2014/03/05/Video_20140302_035701.mp4";

			//imageLoader.DisplayImage("http://192.168.154.1:8000/media/" + cursor.getString(cursor.getColumnIndex("thumbnail")), holder.iv);
			imageLoader.DisplayImage("http://www.dabanit.com/media/" + cursor.getString(cursor.getColumnIndex("thumbnail")), holder.iv);
			holder.iv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(context, "playing the video", Toast.LENGTH_SHORT).show();
					//String video = holder.url.getText().toString();
					Intent i = new Intent(context, VideoActivity.class);
					i.putExtra("url", url);
					context.startActivity(i);
				}
			});
			
			holder.info.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(context, "getting more info", Toast.LENGTH_SHORT).show();
				}
			});
//		}catch(Exception e){
//			Log.e("callnote", "-> "+e.getMessage());
//		}
	}
}

