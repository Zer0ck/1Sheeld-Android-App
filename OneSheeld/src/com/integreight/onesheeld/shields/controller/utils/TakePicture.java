package com.integreight.onesheeld.shields.controller.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.integreight.onesheeld.R;

public class TakePicture extends Activity implements SurfaceHolder.Callback {
	// a variable to store a reference to the Image View at the main.xml file
	// private ImageView iv_image;
	// a variable to store a reference to the Surface View at the main.xml file
	private SurfaceView sv;

	// a bitmap to display the captured image
	private Bitmap bmp;
	FileOutputStream fo;

	// Camera variables
	// a surface holder
	private SurfaceHolder sHolder;
	// a variable to control the camera
	private Camera mCamera;
	// the camera parameters
	private Parameters parameters;
	private String FLASH_MODE ;

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_shield_surface_holder);

		// check if this device has a camera
		if (checkCameraHardware(getApplicationContext())) {
			// get the Image View at the main.xml file
			// iv_image = (ImageView) findViewById(R.id.imageView);

			// get the Surface View at the main.xml file
			Bundle extras = getIntent().getExtras();
			String flash_mode = extras.getString("FLASH");
			FLASH_MODE = flash_mode;

			sv = (SurfaceView) findViewById(R.id.camera_preview);

			// Get a surface
			sHolder = sv.getHolder();

			// add the callback interface methods defined below as the Surface
			// View
			// callbacks
			sHolder.addCallback(this);

			// tells Android that this surface will have its data constantly
			// replaced
			sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		} else {
			// display in long period of time
			Toast.makeText(getApplicationContext(),
					"Your Device dosen't have a Camera !", Toast.LENGTH_LONG)
					.show();
		}

	}

	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// get camera parameters
		parameters = mCamera.getParameters();
		if (FLASH_MODE == null || FLASH_MODE.isEmpty())
		{
			FLASH_MODE = "auto";
		}
		parameters.setFlashMode(FLASH_MODE);

		// set camera parameters
		mCamera.setParameters(parameters);
		mCamera.startPreview();

		// sets what code should be executed after the picture is taken
		Camera.PictureCallback mCall = new Camera.PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				// decode the data obtained by the camera into a Bitmap
				Log.d("ImageTakin", "Done");

				bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

				File imagesFolder = new File(
						Environment.getExternalStorageDirectory(), "OneSheeld");
				imagesFolder.mkdirs(); // <----
				File image = new File(imagesFolder, System.currentTimeMillis()
						+ ".jpg");

				// write the bytes in file
				try {
					fo = new FileOutputStream(image);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					fo.write(bytes.toByteArray());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// remember close de FileOutput
				try {
					fo.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mCamera.stopPreview();
				// release the camera
				mCamera.release();
				Toast.makeText(getApplicationContext(),
						"Your Picture has been taken !", Toast.LENGTH_LONG)
						.show();
				
				finish();

			}
		};

		mCamera.takePicture(null, null, mCall);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
		// to draw the preview.
		mCamera = getCameraInstance();
		try {
			mCamera.setPreviewDisplay(holder);

		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// stop the preview
		/*
		 * mCamera.stopPreview(); // release the camera mCamera.release();
		 */
		// unbind the camera from this object
		mCamera = null;
	}

}