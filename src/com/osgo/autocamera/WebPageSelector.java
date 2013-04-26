package com.osgo.autocamera;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.text.Editable;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class WebPageSelector extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_page_selector);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_page_selector, menu);
		
		return true;
	}
	
	@Override
	public void onBackPressed() {
		Intent i  = new Intent(this, CameraActivity.class);
		startActivity(i);
    	finish();
	}
	
	
	public void onClick(View v) {

		Intent i  = new Intent(this, CameraActivity.class);
		
		
		EditText edit = (EditText) findViewById(R.id.editText1);
		String valur = edit.getText().toString();
		
		 i.putExtra("server", valur );
		
		 startActivity(i);
	    	finish();
	}

}
