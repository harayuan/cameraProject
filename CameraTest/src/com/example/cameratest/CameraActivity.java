package com.example.cameratest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

public class CameraActivity extends Activity implements OnClickListener, SeekBar.OnSeekBarChangeListener {

	private PreviewSurface myPreviewSurface;
	private TouchView myTouchView;
	//private Handler mHandler = new Handler();  

	Bitmap focus;
	int displayWidth;
	int displayHeight;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFullScreen();
		setContentView(R.layout.activity_camera);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		displayWidth = size.x;
		displayHeight = size.y;

		myPreviewSurface = new PreviewSurface(this);
		FrameLayout myFrameLayout = (FrameLayout) findViewById(R.id.preview);
		myFrameLayout.addView(myPreviewSurface);

		myTouchView = new TouchView(this, displayWidth, displayHeight, myPreviewSurface);
		if (myPreviewSurface.getCamera() == null)
			Log.i("camera", "Init: null camera fuck you");
		myFrameLayout.addView(myTouchView);

		// FrameLayout toolBar = (FrameLayout) findViewById(R.id.toolBar);
		// toolBar.setLayoutParams(new
		// RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, displayHeight /
		// 10));

		ImageButton autoFocusButton = (ImageButton) findViewById(R.id.autoFocusButton);
		autoFocusButton.setOnClickListener(this);

		SeekBar seekBar = (SeekBar) findViewById(R.id.zoomRate);
		seekBar.setOnSeekBarChangeListener(this);

		//mHandler.postDelayed(runnable, 750);
	}

	private void setFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/*
	final Runnable runnable = new Runnable() {
		public void run() {
			myPreviewSurface.getCamera().autoFocus(tempAutoFocusCallback);
			mHandler.postDelayed(this, 2000);
		}
	};
	*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		ImageButton clickedButton = (ImageButton) v;
		if (clickedButton.getId() == R.id.autoFocusButton) {
			//mHandler.removeCallbacks(runnable);
			myPreviewSurface.getCamera().autoFocus(myAutoFocusCallback);
			Log.e("camera", "autoFocus");
		}
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		SeekBar seekBar = (SeekBar) findViewById(R.id.zoomRate);
		seekBar.setMax(myPreviewSurface.getCamera().getParameters().getMaxZoom());

		seekBar.setKeyProgressIncrement(1);

		if (arg1 >= 1) {
			if (myPreviewSurface.getCamera().getParameters().isSmoothZoomSupported()) {
				myPreviewSurface.getCamera().startSmoothZoom(arg1);
			} else if (myPreviewSurface.getCamera().getParameters().isZoomSupported()) {
				Camera.Parameters parameters = myPreviewSurface.getCamera().getParameters();
				parameters.setZoom(arg1);
				myPreviewSurface.getCamera().setParameters(parameters);
			}

		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	/*
	Camera.AutoFocusCallback tempAutoFocusCallback = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
		}
	};
	*/

	Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			if (success) {
				camera.takePicture(myShutterCallback, null, myJpegCallback);
			}
		}
	};

	Camera.ShutterCallback myShutterCallback = new Camera.ShutterCallback() {
		public void onShutter() {
		}
	};

	Camera.PictureCallback myJpegCallback = new Camera.PictureCallback() {
		@SuppressLint("NewApi")
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Bitmap cameraBitmap;
			Bitmap capBitmap;
			Bitmap rBitmap;
			Bitmap gBitmap;
			Bitmap bBitmap;
			FileOutputStream outStream = null;
			Intent newIntent = new Intent();
			Bundle newBundle = new Bundle();
			int width;
			int height;
			int cutWidth;
			int cutHeight;
			double[] rRGB;
			double[] gRGB;
			double[] bRGB;
			double[] measureRGB;

			cameraBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			// switch width and height
			height = cameraBitmap.getWidth();
			width = cameraBitmap.getHeight();

			// rotate bitmap
			Matrix m = new Matrix();
			m.setRotate(90);

			// temporarily switch width and height here, otherwise will fail
			cameraBitmap = Bitmap.createBitmap(cameraBitmap, 0, 0, height, width, m, true);
			cutWidth = (int) (myTouchView.getIconWidth() * (width / displayWidth));
			cutHeight = (int) (myTouchView.getIconHeight() * (height / displayHeight));
			rBitmap = rectBitmap(cameraBitmap, myTouchView.getCalRectR().centerX(), myTouchView.getCalRectR().centerY(), 30, 30, width, height);
			gBitmap = rectBitmap(cameraBitmap, myTouchView.getCalRectG().centerX(), myTouchView.getCalRectG().centerY(), 30, 30, width, height);
			bBitmap = rectBitmap(cameraBitmap, myTouchView.getCalRectB().centerX(), myTouchView.getCalRectB().centerY(), 30, 30, width, height);
			capBitmap = rectBitmap(cameraBitmap, myTouchView.getTargetRect().centerX(), myTouchView.getTargetRect().centerY(), cutWidth, cutHeight, width, height);
			Toast.makeText(CameraActivity.this, "iconWidth: " + String.valueOf(myTouchView.getIconWidth()), Toast.LENGTH_SHORT).show();

			rRGB = avgRGB(rBitmap);
			gRGB = avgRGB(gBitmap);
			bRGB = avgRGB(bBitmap);
			// get the target value
			measureRGB = avgRGB(capBitmap);
			// calibrate target value
			

			newBundle.putString("measureR", String.format("R block:\n%.2f, %.2f, %.2f", rRGB[0], rRGB[1], rRGB[2]));
			newBundle.putString("measureG", String.format("G block:\n%.2f, %.2f, %.2f", gRGB[0], gRGB[1], gRGB[2]));
			newBundle.putString("measureB", String.format("B block:\n%.2f, %.2f, %.2f", bRGB[0], bRGB[1], bRGB[2]));
			newBundle.putString("measureValue", String.format("measure value:\nR: %.2f, G: %.2f, B: %.2f", measureRGB[0], measureRGB[1], measureRGB[2]));
			newIntent.setClass(CameraActivity.this, com.example.cameratest.Result.class);
			newIntent.putExtras(newBundle);

			try {
				File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
				if (!path.exists())
					path.mkdirs();

				String timeMillis = String.format("%1$d", System.currentTimeMillis());
				String filename = timeMillis + ".jpg";
				File file = new File(path, filename);
				outStream = new FileOutputStream(file);
				cameraBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
				// flush the original bitmap
				outStream.flush();
				outStream.close();

				// compress bitmap into outStream and flush
				String capFilename = timeMillis + "(1).jpg";
				file = new File(path, capFilename);
				outStream = new FileOutputStream(file);
				capBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
				outStream.flush();
				outStream.close();

				Toast.makeText(CameraActivity.this, "store in SDcard", Toast.LENGTH_SHORT).show();
				Toast.makeText(CameraActivity.this, "file size:" + data.length, Toast.LENGTH_SHORT).show();
				Toast.makeText(CameraActivity.this, "save path:" + path, Toast.LENGTH_SHORT).show();

			} catch (FileNotFoundException e) {
				Toast.makeText(CameraActivity.this, "FileNotFound", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(CameraActivity.this, "IO Error", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} finally {
				startActivity(newIntent);
			}
		}

		// capture bitmap and analyze
		Bitmap rectBitmap(Bitmap bitmap, float centerX, float centerY, int cutWidth, int cutHeight, int cameraWidth, int cameraHeight) {
			Bitmap capBitmap;
			int cutX = (int) (centerX * (cameraWidth / displayWidth));
			int cutY = (int) (centerY * (cameraHeight / displayHeight));
			Log.e("temp", String.format("cutX: %d, cutY: %d", cutX, cutY));
			capBitmap = Bitmap.createBitmap(bitmap, cutX, cutY, cutWidth, cutHeight);
			return capBitmap;
		}

		double[] avgRGB(Bitmap bitmap) {
			double[] rgb = new double[3];
			// start to calculate RGB
			for (int i = 0; i < bitmap.getWidth(); i++) {
				for (int j = 0; j < bitmap.getHeight(); j++) {
					int value = bitmap.getPixel(i, j);
					rgb[0] += (value >> 16) & 0xFF;
					rgb[1] += (value >> 8) & 0xFF;
					rgb[2] += value & 0xFF;
				}
			}
			rgb[0] /= bitmap.getWidth() * bitmap.getHeight();
			rgb[1] /= bitmap.getWidth() * bitmap.getHeight();
			rgb[2] /= bitmap.getWidth() * bitmap.getHeight();
			return rgb;
		}
	};

}
