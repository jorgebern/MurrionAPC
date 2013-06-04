package com.osgo.autocamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Used to select the time or the quality
 * @author JorgeBern
 *
 */
public class SelectMinutes extends Activity implements OnItemSelectedListener {

	//------------------------------
	//VARIABLES
	//------------------------------
	int selection = 2;
	private ArrayAdapter<String> listAdapter ;
	Intent i ;
	
	//-------------
	//CONSTRUCTOR
	//-------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_minutes);
		
		final ListView listview = (ListView) findViewById(R.id.listView1);
		
	    final String[] Times = new String[] { "1 Minut", "10 Minutes", "30 Minutes" };
	    final int[] RealTimes = new int[] { 1,10,30};
	    
	    final String[] quality = new String[] { "Low", "Medium", "High" };
	    final int[] RealQuality = new int[] {10,50,100};
	    

	    Bundle extras = getIntent().getExtras();
	    selection = extras.getInt("Values");
	    
	    if(selection == 1)
	    	listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, Times);
	    else
	    	listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, quality);
	    
	    
	    listview.setAdapter(listAdapter);
	    i = new Intent(this, CameraActivity.class);
	    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	
	        @Override
	        public void onItemClick(AdapterView<?> parent, final View view,
	            int position, long id) {
	        	
	        	if(selection == 1) {
	        		i.putExtra("valuesI", RealTimes[position]);
	        	} else {
	        		i.putExtra("valuesS", RealQuality[position]);
	        	}

	        	startActivity(i);
	        	finish();
	        }
	      });
	    
	}
	
	//-----------------------
    //METHODS
    //-----------------------
	/**
	 * Control the back button.
	 */
	@Override
	public void onBackPressed() {
		Intent i  = new Intent(this, CameraActivity.class);
		startActivity(i);
    	finish();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		arg0.getItemAtPosition(arg2);
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}
