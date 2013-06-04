package com.osgo.autocamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;

import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

/**
 * Server selector, used to take the server name
 * @author JorgeBern
 *
 */
public class WebPageSelector extends Activity {

	
	EditText edit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_page_selector);
		edit = (EditText) findViewById(R.id.editText1);
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

		
		saveServer();
		
		Intent i  = new Intent(this, CameraActivity.class);
		
		String valur = edit.getText().toString();
		
		 i.putExtra("server", valur );
		
		 startActivity(i);
	    	finish();
	}
	
	private void saveServer() {
		
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"murrionApc");
		
		FileWriter mediaFile = null;
		
		PrintWriter pw = null;
		
		try {
			
			mediaFile = new FileWriter(file.getPath() + File.separator +
					"servers.dat", true);
			//Abrimos el fichero y añadimos la linea nueva a nuestro "archivador"
			pw = new PrintWriter(mediaFile);
			pw.println(edit.getText().toString());
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	       try {
	           // Nuevamente aprovechamos el finally para
	           // asegurarnos que se cierra el fichero.
	    	   if (null != mediaFile)
			   	pw.close();
	       } catch (Exception e2) {
	          e2.printStackTrace();
	       }
	    }
			
		
	}
	
	
	
	
	
	

}
