package com.example.cameratest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Result extends Activity implements OnClickListener {
	TextView result;
	Button returnButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		result = (TextView) findViewById(R.id.result);
		Bundle newBundle = getIntent().getExtras();
		result.setText(newBundle.getString("measureValue"));
		//result.setText(newBundle.getString("measureR") + "\n\n" + newBundle.getString("measureG") + "\n\n" + newBundle.getString("measureB") + "\n\n" + newBundle.getString("measureValue"));
		returnButton = (Button) findViewById(R.id.return_init);
		returnButton.setOnClickListener(this);
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
		if (clickedButton.getId() == R.id.return_init) {
			Intent i = new Intent();
			//set next activity
			i.setClass(Result.this, com.example.cameratest.CameraActivity.class);
			//start activity
			startActivity(i);
		}
	}

}
