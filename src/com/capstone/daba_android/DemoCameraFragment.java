/***
7  Copyright (c) 2013 CommonsWare, LLC

  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package com.capstone.daba_android;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import com.commonsware.cwac.camera.CameraFragment;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraUtils;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.commonsware.cwac.camera.PictureTransaction;

public class DemoCameraFragment extends CameraFragment implements
OnSeekBarChangeListener {
	private static final String KEY_USE_FFC = "com.commonsware.cwac.camera.demo.USE_FFC";
	private MenuItem singleShotItem=null;
	private MenuItem autoFocusItem=null;
	private MenuItem takePictureItem=null;
	private MenuItem flashItem=null;
	private boolean singleShotProcessing=false;
	private SeekBar zoom=null;
	private long lastFaceToast=0L;
	String flashMode=null;
	private String videoFileName = null;
	Context context = null;

	//Just creating a new instance of the current Fragment
	static DemoCameraFragment newInstance(boolean useFFC) {
		DemoCameraFragment f=new DemoCameraFragment();
		Bundle args=new Bundle();

		args.putBoolean(KEY_USE_FFC, useFFC);
		f.setArguments(args);
		Log.d("daba", "instance");
		return(f);
	}

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		
		//Make sure menu option accessible
		setHasOptionsMenu(true);
		//new builder
		SimpleCameraHost.Builder builder=
			new SimpleCameraHost.Builder(new DemoCameraHost(getActivity()));
		//setting the host
		setHost(builder.useFullBleedPreview(true).build());
		Log.d("daba", "create");
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		View cameraView=
			super.onCreateView(inflater, container, savedInstanceState);
		View results=inflater.inflate(R.layout.fragment, container, false);

		((ViewGroup)results.findViewById(R.id.camera)).addView(cameraView);
		zoom=(SeekBar)results.findViewById(R.id.zoom);
		zoom.setKeepScreenOn(true);
		Log.d("daba", "createview");
		videoFileName = this.getHost().getVideoFilename();
		return(results);
	}
	public void onResume() {
		super.onResume();		
		context = getActivity();
		final Button startRecording = (Button) getActivity().findViewById(R.id.startRecording);
		final Button stopRecording = (Button) getActivity().findViewById(R.id.stopRecording);
		final ImageButton videosetdata = (ImageButton) getActivity().findViewById(R.id.next);
		startRecording.setOnClickListener(new OnClickListener() {
			

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					record();
					getActivity().invalidateOptionsMenu();
					Toast.makeText(getActivity(), "Starting..", Toast.LENGTH_SHORT).show();
					startRecording.setVisibility(View.GONE);
					stopRecording.setVisibility(View.VISIBLE);
				}
				catch (Exception e) {
					Log.e(getClass().getSimpleName(),
							"Exception trying to record", e);
					Toast.makeText(getActivity(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}

			}
		});

		stopRecording.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					stopRecording();
					getActivity().invalidateOptionsMenu();
					Toast.makeText(getActivity(), "Stopping..", Toast.LENGTH_SHORT).show();
					stopRecording.setVisibility(View.GONE);
					startRecording.setVisibility(View.VISIBLE);
				}
				catch (Exception e) {
					Log.e(getClass().getSimpleName(),
							"Exception trying to stop recording", e);
					Toast.makeText(getActivity(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}

			}
		});
		
		videosetdata.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity(), VideoSetData.class);
				getActivity().startActivity(i);
				
				
			}
		});
		Log.d("daba", "resume");
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.camera, menu);

		if (isRecording()) {
			menu.findItem(R.id.record).setVisible(false);
			menu.findItem(R.id.stop).setVisible(true);
		}

		takePictureItem=menu.findItem(R.id.camera);
		singleShotItem=menu.findItem(R.id.single_shot);
		singleShotItem.setChecked(getContract().isSingleShotMode());
		autoFocusItem=menu.findItem(R.id.autofocus);
		flashItem=menu.findItem(R.id.flash);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.camera:
			if (singleShotItem.isChecked()) {
				singleShotProcessing=true;
				takePictureItem.setEnabled(false);
			}

			PictureTransaction xact=new PictureTransaction(getHost());

			if (flashItem.isChecked()) {
				xact.flashMode(flashMode);
			}

			takePicture(xact);

			return(true);

		case R.id.record:
			try {
				record();
				getActivity().invalidateOptionsMenu();
			}
			catch (Exception e) {
				Log.e(getClass().getSimpleName(),
						"Exception trying to record", e);
				Toast.makeText(getActivity(), e.getMessage(),
						Toast.LENGTH_LONG).show();
			}

			return(true);

		case R.id.stop:
			try {
				stopRecording();
				getActivity().invalidateOptionsMenu();
			}
			catch (Exception e) {
				Log.e(getClass().getSimpleName(),
						"Exception trying to stop recording", e);
				Toast.makeText(getActivity(), e.getMessage(),
						Toast.LENGTH_LONG).show();
			}

			return(true);

		case R.id.autofocus:
			takePictureItem.setEnabled(false);
			autoFocus();

			return(true);

		case R.id.single_shot:
			item.setChecked(!item.isChecked());
			getContract().setSingleShotMode(item.isChecked());

			return(true);

		case R.id.show_zoom:
			item.setChecked(!item.isChecked());
			zoom.setVisibility(item.isChecked() ? View.VISIBLE : View.GONE);

			return(true);

		case R.id.flash:
			item.setChecked(!item.isChecked());

			return(true);
		}

		return(super.onOptionsItemSelected(item));
	}

	boolean isSingleShotProcessing() {
		return(singleShotProcessing);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			zoom.setEnabled(false);
			zoomTo(zoom.getProgress()).onComplete(new Runnable() {
				@Override
				public void run() {
					zoom.setEnabled(true);
				}
			}).go();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// ignore
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// ignore
	}

	Contract getContract() {
		return((Contract)getActivity());
	}

	interface Contract {
		boolean isSingleShotMode();

		void setSingleShotMode(boolean mode);
	}

	class DemoCameraHost extends SimpleCameraHost implements
	Camera.FaceDetectionListener {
		boolean supportsFaces=false;

		public DemoCameraHost(Context _ctxt) {
			super(_ctxt);
		}

		@Override
		public boolean useFrontFacingCamera() {
			return(getArguments().getBoolean(KEY_USE_FFC));
		}

		@Override
		public boolean useSingleShotMode() {
			return(singleShotItem.isChecked());
		}

		@Override
		public void saveImage(PictureTransaction xact, byte[] image) {
			if (useSingleShotMode()) {
				singleShotProcessing=false;

				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						takePictureItem.setEnabled(true);
					}
				});

				DisplayActivity.imageToShow=image;
				startActivity(new Intent(getActivity(), DisplayActivity.class));
			}
			else {
				super.saveImage(xact, image);
			}
		}

		@Override
		public void autoFocusAvailable() {
			autoFocusItem.setEnabled(true);
			if (supportsFaces)
				startFaceDetection();
		}

		@Override
		public void autoFocusUnavailable() {
			stopFaceDetection();
			if (supportsFaces)
				autoFocusItem.setEnabled(false);
		}

		@Override
		public void onCameraFail(CameraHost.FailureReason reason) {
			super.onCameraFail(reason);

			Toast.makeText(getActivity(),
					"Sorry, but you cannot use the camera now!",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public Parameters adjustPreviewParameters(Parameters parameters) {
			flashMode=
				CameraUtils.findBestFlashModeMatch(parameters,
						Camera.Parameters.FLASH_MODE_RED_EYE,
						Camera.Parameters.FLASH_MODE_AUTO,
						Camera.Parameters.FLASH_MODE_ON);

			if (doesZoomReallyWork() && parameters.getMaxZoom() > 0) {
				zoom.setMax(parameters.getMaxZoom());
				zoom.setOnSeekBarChangeListener(DemoCameraFragment.this);
			}
			else {
				zoom.setEnabled(false);
			}

			if (parameters.getMaxNumDetectedFaces() > 0) {
				supportsFaces=true;
			}
			else {
				Toast.makeText(getActivity(),
						"Face detection not available for this camera",
						Toast.LENGTH_LONG).show();
			}

			return(super.adjustPreviewParameters(parameters));
		}

		@Override
		public void onFaceDetection(Face[] faces, Camera camera) {
			if (faces.length > 0) {
				long now=SystemClock.elapsedRealtime();

				if (now > lastFaceToast + 10000) {
					Toast.makeText(getActivity(), "I see your face!",
							Toast.LENGTH_LONG).show();
					lastFaceToast=now;
				}
			}
		}

		@Override
		@TargetApi(16)
		public void onAutoFocus(boolean success, Camera camera) {
			super.onAutoFocus(success, camera);

			takePictureItem.setEnabled(true);
		}
	}
}