package com.capstone.daba_android;

import java.io.InputStream;

import com.capstone.utils.DefaultFeed;
import com.capstone.utils.ImageLoader;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.VideoView;

public class MyfeedFragment extends ListFragment {
	Database db = null;
	View showVideosView;
	ListAdapter la;
	public ImageLoader imageLoader; 
	Context context;
	public ListFragment fragment = this;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		context = getActivity();
		showVideosView = inflater.inflate(R.layout.myfeed_fragment, container, false);
		Log.d("daba", "onCreateView of myfeed");
		return (showVideosView);
	}


	public void onStart(){
		super.onStart();
		
		imageLoader=new ImageLoader(getActivity());

	}

	public void onResume(){
		super.onResume();
		db = new Database(getActivity());
		Cursor c = db.getVideos();
		la = new mycursoradapter(getActivity(), R.layout.feed_row, c, new String[] {"title", "url"},
				new int[]{R.id.title, R.id.url});
		setListAdapter(la);
		
		
//		Button b = (Button) getActivity().findViewById(R.id.bb);
//		b.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Log.d("daba", "starting...");
//				DefaultFeed df = new DefaultFeed(fragment, getActivity(), null);
//				df.execute("");
//				
//
//			}
//		});
		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View row, int position,
					long id) {
				// TODO Auto-generated method stub
				TextView url = (TextView) row.findViewById(R.id.url);
				Log.d("daba", "the item: " + position + " url:" + url.getText().toString());				
				String video = url.getText().toString();
				Intent i = new Intent(getActivity(), VideoActivity.class);
				i.putExtra("url", video);
				getActivity().startActivity(i);
				//VideoView vv = (VideoView) arg0.findViewById(R.id.video);
				//vv.start();
				//Log.d("daba", "starting.." + vv.get);	

			}


		});




	}
	

	//	@Override
	//	public void onListItemClick(ListView l, View v, int position, long id) {
	//		// TODO Auto-generated method stub
	//		super.onListItemClick(l, v, position, id);			
	//		Log.d("daba", "the item: " + position);
	//		VideoView vv = (VideoView) v.findViewById(R.id.video);
	//		vv.start();
	//		Log.d("daba", "starting..");	
	//	}

	public class ViewHolder {
		public TextView title;
		public TextView url;
		public TextView city;
		public TextView country;
		public VideoView vv;
		public ImageView iv;

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
			try{
				if (view != null) {
					holder = new ViewHolder();
					holder.title = (TextView) view.findViewById(R.id.title);
					holder.city = (TextView) view.findViewById(R.id.city);
					holder.country = (TextView) view.findViewById(R.id.country);
					holder.url = (TextView) view.findViewById(R.id.url);
					holder.iv = (ImageView) view.findViewById(R.id.thumb);
					//holder.vv = (VideoView) view.findViewById(R.id.video);
					view.setTag(holder);
				} else {
					holder = (ViewHolder) view.getTag();
				}


				holder.title.setText(cursor.getString(cursor.getColumnIndex("title")));
				holder.url.setText(cursor.getString(cursor.getColumnIndex("url")));
				Log.d("daba", "video id: " + cursor.getString(cursor.getColumnIndex("video_id")));
				String[] location = db.getCityCountryByVideo(cursor.getString(cursor.getColumnIndex("video_id")));
				holder.city.setText(location[0]);
				holder.country.setText(location[1]);
				//Uri uri = Uri.parse("http://192.168.154.1:8000/media/" + cursor.getString(cursor.getColumnIndex("url")));
				//Uri uri = Uri.parse("http://www.dabanit.com/media/documents/2014/03/05/Video_20140302_035701.mp4");
				//holder.vv.setVideoURI(uri);
				String videoFile = "http://www.dabanit.com/media/documents/2014/03/05/Video_20140302_035701.mp4";
						
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
				imageLoader.DisplayImage("http://192.168.154.1:8000/media/" + cursor.getString(cursor.getColumnIndex("thumbnail")), holder.iv);
				//new DownloadImage((ImageView) holder.iv).execute("http://192.168.154.1:8000/media/" + cursor.getString(cursor.getColumnIndex("thumbnail")));
				//imageView.setVisibility(View.VISIBLE);
				//BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbnail);
				//holder.vv.setBackgroundDrawable(bitmapDrawable);
				//holder.id.setText(cursor.getString(cursor.getColumnIndex("_id")));

			}catch(Exception e){
				Log.e("callnote", "-> "+e.getMessage());
			}
		}
	}
}
