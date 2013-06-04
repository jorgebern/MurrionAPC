package com.osgo.autocamera;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/**
 * @author JorgeBern
 *
 * This class, connect with the php server and send the data.
 * 
 * @version 1
 */
public class ApiService {
	
	
	//------------------------
	//VARIABLES
	//------------------------
	public static final String BROADCAST_ASSIGMENTS = "com.osgo.verifired.AssignmentList.syncassignments";
    public static final String BROADCAST_SURVEY= "com.osgo.verifired.Survey.syncsurvey";
    public static final String BROADCAST_SYNC= "com.osgo.verifired.sync";
    public static final String BUG_API = "c5db6163";
    
	private static final String DEBUG_TAG = null;
	private String API = "";
	public static final String SUBMIT_SURVEY = "com.osgo.verifired.Survey.submit";
	public static final String SAVE_SURVEY = "com.osgo.verifired.Survey.savedb";
	public static final String SUBMIT_IMAGE = "com.osgo.verifired.Survey.submitimage";
	private static String API_KEY;
	private Context context;
	
	
	
	//------------
	//CONSTRUCTOR
	//------------
	public ApiService(String key, String api, Context context){
        API_KEY = key;
        API = api;
        this.context = context;
	}
	
	//---------------------------------
	//METHODS
	//---------------------------------
		
	
	//-------------------------------
	//PUBLIC METHODS
	//-------------------------------
	/**
	 *  Reads an InputStream and converts it to a String.
	 * @param stream
	 * @param len
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
          builder.append(line);
        }
		return builder.toString();
	}
	
	//TODO
		//WORKS
		//IMPROVE
		//---------------------------------------------------------------------------------------------------
	/**
	 * This method conect, using POST with the php code, sending the Photo and the data.
	 * @param file Photo
	 * @param url Server direction
	 * @param DeviceName Information about the device
	 * @param gps information about the location
	 * @throws IOException 
	 * @throws ClientProtocolException
	 */
		public void sendFile(File file, String url, String DeviceName, String gps) throws IOException, ClientProtocolException {

		    HttpClient httpClient = new DefaultHttpClient();
		    HttpPost postRequest = new HttpPost(this.changeUri(url));
		    
		    MultipartEntity multipartContent = new MultipartEntity();
		    
		    //In PHP code, read the data in order
		    multipartContent.addPart("DeviceName", new StringBody(DeviceName));
		    multipartContent.addPart("coordenates", new StringBody(gps));
		    multipartContent.addPart("file", new FileBody(file));

		    postRequest.setEntity(multipartContent);
		    Log.i("Sending file", "Execute");
		    //Send the data to php server
		    //bottleneck
		    httpClient.execute(postRequest);
		    Log.i("Sending file", "Done");
		}

	//------------------------------------------------------------------------------------------------------------
		
		/*public void submitMedia(Map<String, Object> input){
			
			String url = (String) input.get("URL");
			String path = (String) input.get("MEDIA");
			String name = (String) input.get("MEDIA_NAME");

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			post.setEntity(readyMedia(path, name));
			
			HttpResponse response;
			try {
				response = client.execute(post);
				HttpEntity resEntity = response.getEntity();
		        String responseStr = EntityUtils.toString(resEntity);
		        JSONObject results = new JSONObject(responseStr);
		        
		        if(results!=null){
		        	if(results.get("result").equals("success")){
		        		
		            }
		        }
			} catch (ClientProtocolException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
		}*/
	
	//------------------------------
	//PRIVATE METHODS
	//------------------------------
	
	/**
	 * Change the server direction and add the protocol if is needed
	 * @param url Starting direction
	 * @return Return the new direction with the protocol
	 */
	private String changeUri(String url) {
		
		String[] api = url.split("//");
		
		if(!api[0].equals("http:")){
			url = "http://" + url;
		}

		return url;
		
	}
						

	private MultipartEntity readyMedia(String path, String name){

		FileBody fileBody = new FileBody(new File(path));
		MultipartEntity reqEntity = new MultipartEntity();
		try {	
			if(name.contains(".jpg")){
				reqEntity.addPart("photo", fileBody);
				reqEntity.addPart("image_type", new StringBody("image/jpeg"));
			} else if (name.contains(".mp4")){
				reqEntity.addPart("video", fileBody);
				reqEntity.addPart("video_type", new StringBody("video/mp4"));
			} 
			
			reqEntity.addPart("name", new StringBody(name));
		} catch (Exception e){
			
		}
    
        return reqEntity;		
	}

	
	//-----------------------------------------------------------------------------------------------
	
}
