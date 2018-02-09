package com.example.cameratest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Init extends Activity implements OnClickListener {
	Button startButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_init);
		
		startButton = (Button) findViewById(R.id.goto_camera);
		startButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.init, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		Button clickedButton = (Button) v;
		if (clickedButton.getId() == R.id.goto_camera) {
			Intent i = new Intent();
			//set next activity
			i.setClass(Init.this, com.example.cameratest.CameraActivity.class);
			//start activity
			startActivity(i);
		}
		
	}

}
