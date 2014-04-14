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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Color;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraFragment;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.DeviceProfile;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;


public class DemoCameraFragment extends CameraFragment {
	android.hardware.Camera camera;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		View content=inflater.inflate(R.layout.camera, container, false);
		CameraView cameraView=(CameraView)content.findViewById(R.id.camera);
		Log.d("daba", "the cameraview attrs: " + cameraView.getHeight() + " -- " + cameraView.getWidth());
		setCameraView(cameraView);


		//		SimpleCameraHost.Builder builder = new SimpleCameraHost.Builder(getActivity());
		//		builder.useFullBleedPreview(false);
		//		
		//		setHost(builder.build());

		final ImageButton record = (ImageButton) content.findViewById(R.id.record);
		final ImageButton next = (ImageButton) content.findViewById(R.id.next);
		next.setEnabled(false);
		record.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(record.getTag().toString().equals("Hi!")){
					try {
						record();
						record.setTag("By!");
						record.setBackgroundColor(Color.DKGRAY);
						next.setEnabled(true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else{
					try {
						stopRecording();
						record.setTag("Hi!");
						record.setBackgroundColor(Color.TRANSPARENT);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		});


		next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isRecording())
					Toast.makeText(getActivity(), "Finish recording first", Toast.LENGTH_SHORT).show();
				else{
					Intent i = new Intent(getActivity(), VideoSetData.class);
					getActivity().startActivity(i);
				}
			}
		});

		if(isRecording()){


		}



		return(content);
	}


}