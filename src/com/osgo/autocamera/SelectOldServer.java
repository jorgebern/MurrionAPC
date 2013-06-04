package com.osgo.autocamera;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SelectOldServer extends Activity implements OnItemSelectedListener {

	
	private ArrayList<String> lista = new ArrayList<String>();
	Intent i ;
	Spinner spin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_old_server);
		readServers();
		
		spin = (Spinner) findViewById(R.id.spinner1);
		spin.setOnItemSelectedListener(this);

		ArrayAdapter aa = new ArrayAdapter( this, android.R.layout.simple_spinner_item,  lista);
		
		aa.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(aa);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_old_server, menu);
		
		
		return false;
	}

	
	public void readServers() {
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"murrionApc");
 
		//Toast.makeText(mContext, "Uploading Photos, please wait", Toast.LENGTH_LONG).show();
		try {
	         // Apertura del fichero y creacion de BufferedReader para poder
	         // hacer una lectura comoda (disponer del metodo readLine()).
	         archivo = new File (file.getPath() + File.separator +
						"servers.dat");
	         fr = new FileReader (archivo);
	         br = new BufferedReader(fr);
	 
	         // Lectura del fichero
	         String linea;
	         while((linea=br.readLine())!=null) {
	        	lista.add(linea);
	         }
	            
      }
      catch(Exception e){
    	  Log.i("ERROR","Exception 1");
         e.printStackTrace();
      }finally{
         try{                   
            if( null != fr ){  
               fr.close();    
            }
            
         }catch (Exception e2){
            e2.printStackTrace();
         }
      }
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onBackPressed() {
		Intent i  = new Intent(this, CameraActivity.class);
		startActivity(i);
    	finish();
	}
	
	
	
	public void onClick(View v) {
		i = new Intent(this, CameraActivity.class);
		i.putExtra("oldServer", spin.getSelectedItem().toString());
    	startActivity(i);
    	finish();
		
		
	}
}
