package com.osgo.autocamera;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
public class CameraActivity extends Activity {

	protected Camera mCamera;
	protected CameraPreview mPreview;
	protected MediaRecorder mMediaRecorder;
	protected Picturetask takePictures;
	protected SharedPreferences prefs;
	protected Context mContext;
	protected FrameLayout preview;
	protected ApiService Send;

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final String PREFS = "CAMERA_APP";
	public static int numPhoto=0;
	private static final String BROADCAST_PREVIEW = "refresh";
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

	long timeout;
	GPSTracker gps;
	
	static String photoName = "null";
	
	
	
	String WebPage = "http://jorgebern.com/json.php";
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		//Return the preference in the PREFS file
		prefs = getSharedPreferences(PREFS, 0);
		timeout = prefs.getLong("TIMEOUT", 60000);
		gps = new GPSTracker(this);
		
		Send = new ApiService("", "", this);

		
		// Create an instance of Camera
		if(checkCameraHardware(this)){
			mCamera = getCameraInstance();
		}
		
		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		
		preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		mPreview.surfaceChanged(null, 0, 0, 0);

		// Add a listener to the Capture button
		final Button captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// get an image from the camera
				takePictures.execute(null);
				captureButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// get an image from the camera
						takePictures.cancelPicture();
						captureButton.setOnClickListener(null);
					}
				});
			}
		});
		
		// Add a listener to the Capture button
				final Button captureButtonL = (Button) findViewById(R.id.button_capture);
				captureButton.setOnLongClickListener( new View.OnLongClickListener() {
					
							@Override
							public boolean onLongClick(View v) {
								registerForContextMenu(captureButtonL);
								return false;
							}
						});
					
				Bundle extras = getIntent().getExtras();
				 
				 //60000 = 1 minuto
				 //600000 = 10 minutes
				 //1800000 = 30 minutes
				if(extras != null) {
				 
					 int value = extras.getInt("valuesI");
						 if(value == 1) {
							 timeout = 60000;
						 } else if(value == 10) {
							 timeout = 600000;
						 } else if(value == 30) {
							 timeout = 1800000;
						 }
					
					if (extras.getString("server") != null) {
						
						WebPage =  extras.getString("server");
					}
					
				}
		takePictures = new Picturetask(timeout);
	}
	
	
	@Override
	protected void onStop()
	{
	    unregisterReceiver(refreshPreview);
	    super.onStop();
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
	    super.onCreateContextMenu(menu, v, menuInfo);
	 
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_main, menu);
	}
	
	
	public String getDeviceName() {
		  String manufacturer = Build.MANUFACTURER;
		  String model = Build.MODEL;
		  
		  if (model.startsWith(manufacturer)) {
		    return model ;
		  } else {
			  return manufacturer + ", " +model ;
		  }
		}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	 
		Intent i;
	    switch (item.getItemId()) {
	        case R.id.CtxLblOpc1:
	           
	        	i = new Intent(this, SelectMinutes.class);
	        	i.putExtra("Values", 1);
	        	startActivity(i);
	        	finish();
	            return true;
	        case R.id.CtxLblOpc2:
	        	i = new Intent(this, SelectMinutes.class);
	        	i.putExtra("Values", 2);
	        	startActivity(i);
	        	finish();
	           
	            return true;
	        
	        case R.id.CtxLblOpc3:
	        	i = new Intent(this, WebPageSelector.class);
	        	i.putExtra("Values", 2);
	        	startActivity(i);
	        	finish();
	           
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	protected void resetPreview(){
		mCamera.stopPreview();
		mPreview = new CameraPreview(this, mCamera);
		preview.addView(mPreview);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		takePictures.cancelPicture();
//		takePictures.cancel(true);
		releaseMediaRecorder();       // if you are using MediaRecorder, release it first
		releaseCamera();              // release the camera immediately on pause event
		
	}
	
	@Override
	public void onStart() {
        super.onStart();
        registerReceiver(refreshPreview, new IntentFilter(BROADCAST_PREVIEW));
    }

	protected void releaseMediaRecorder(){
		if (mMediaRecorder != null) {
			mMediaRecorder.reset();   // clear recorder configuration
			mMediaRecorder.release(); // release the recorder object
			mMediaRecorder = null;
			mCamera.lock();           // lock camera for later use
		}
	}

	protected void releaseCamera(){
		if (mCamera != null){
			mCamera.release();        // release the camera for other applications
			mCamera = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
		Camera c = null;
		
		
		try {
			
			c = Camera.open();
			
			// attempt to get a Camera instance
		}
		catch (Exception e){
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	protected PictureCallback mPicture = new PictureCallback() {

		private String TAG = "PictureCallBack";
		
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			if(data == null)
				return;
			
			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			
			
			if (pictureFile == null){
				Log.d(TAG, "Error creating media file, check storage permissions: ");
				return;
			}
			
			Log.i("onPickture", "1");
			photoName = Base64.encodeToString(data, RESULT_CANCELED); 
			
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				
				fos.close();
				
				Log.i("onPickture", "2");
				
				// Tell the media scanner about the new file so that it is
		        // immediately available to the user.
				MediaScannerConnection.scanFile(mContext,
						new String[] { pictureFile.toString() }, null,
						new MediaScannerConnection.OnScanCompletedListener() {
					public void onScanCompleted(String path, Uri uri) {
						

						
						Log.i("ExternalStorage", "Scanned " + path + ":");
						Log.i("ExternalStorage", "-> uri=" + uri);
					}
				});
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}
			Log.i("onPickture", "3");
			
		}
	};
	
	//TODO
	private class Picturetask extends AsyncTask<String, Void, Boolean> {

		private long timeout;
		private volatile boolean wait = true; 
		
		public Picturetask(long timeout){
			
			this.timeout = timeout;
		}
		
		@Override                             //????
		protected Boolean doInBackground(String... arg0) {
			try {
				takePicture();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		private void takePicture() throws JSONException, IOException, InterruptedException{
			
			
			while(wait) {
				Log.i("takePicture", "1");
				CameraActivity.this.mCamera.takePicture(null, null, mPicture);
				Log.i("takePicture", "2");

				
				Log.i("Json", "1");
				JSONObject pic = new JSONObject();
				try {
					pic.put("Latitude", gps.getLatitude());
					pic.put("Longitude", gps.getLongitude());
					pic.put("DeviceName", getDeviceName());
					pic.put("photo", photoName);
					Log.i("Json", "2");
					
					Send.sendRequest(WebPage, "POST", pic);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.i("Json", "3");
				
				
				
				Log.i("takePicture", "3");
				
				Intent i = new Intent(BROADCAST_PREVIEW);
				sendBroadcast(i);
				try {
					Thread.sleep(timeout);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
					break;
				}
			}
		}	
		
		public void cancelPicture(){
			this.wait = false;
		}
	}

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
		
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		
		
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "murrionApc");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		
		if (type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"IMG_"+ timeStamp + ".jpg");
			
		} else if(type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"VID_"+ timeStamp + ".mp4");
		} else {
			return null;
		}

		numPhoto++;
		return mediaFile;
	}
	
	private BroadcastReceiver refreshPreview = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			mPreview.surfaceChanged(null, 0, 0, 0);
			
		}
	};

}