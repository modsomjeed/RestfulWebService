package com.example.restfulwebservice;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	ProgressDialog mProgressDialog;
	TextView outputData;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
     
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  
        
        outputData = (TextView) findViewById(R.id.output);
        
        final Button GetServerData = (Button) findViewById(R.id.GetServerData);
         
        GetServerData.setOnClickListener(new OnClickListener() {
            
			@Override
			public void onClick(View arg0) {
				
				// WebServer Request URL 172.16.1.39
				String serverURL = "http://192.168.1.38:8080/opdclaim/rest/opdclaim/authenticate";
				
				// Use AsyncTask execute Method To Prevent ANR Problem
				mProgressDialog = new ProgressDialog(MainActivity.this);
				mProgressDialog.setMessage("Please wait ...");
				mProgressDialog.setIndeterminate(false);
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		        new LongOperation().execute(serverURL);
			}
        });    
         
    }
    
    private class LongOperation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			try {
				 
	            // 1. create HttpClient
	            HttpClient httpclient = new DefaultHttpClient();
	            
	            // 2. make POST request to the given URL
	            HttpPost httpPost = new HttpPost(params[0]);
	 
	            String json = "";
	            StringBuilder strBuilder = new StringBuilder();
	 
	            // 3. build jsonObject
	            JSONObject jsonObject = new JSONObject();
	            jsonObject.accumulate("username", "admin");
	            jsonObject.accumulate("password", "password");
	 
	            // 4. convert JSONObject to JSON to String
	            json = jsonObject.toString();
	 
	            // 5. set json to StringEntity
	            StringEntity se = new StringEntity(json);
	 
	            // 6. set httpPost Entity
	            httpPost.setEntity(se);
	 
	            // 7. Set some headers to inform server about the type of the content   
	            httpPost.setHeader("Accept", "application/json");
	            //httpPost.setHeader("Content-type", "application/json");
	 
	            // 8. Execute POST request to the given URL
	            HttpResponse httpResponse = httpclient.execute(httpPost);
	 
	            // 9. receive response as inputStream
	            //inputStream = httpResponse.getEntity().getContent();
	            HttpEntity entity = httpResponse.getEntity();
				String data = EntityUtils.toString(entity);
	 
				if (httpResponse.getStatusLine().getStatusCode() == 200) {

					JSONObject jsObj = new JSONObject(data);
					
					strBuilder.append("user_id = " + jsObj.getString("user_id") +"\n");
					strBuilder.append("username = " + jsObj.getString("username")+"\n");
					strBuilder.append("email = " + jsObj.getString("email")+"\n");
					strBuilder.append("password = " + jsObj.getString("password")+"\n");
					strBuilder.append("firstname = " + jsObj.getString("firstname")+"\n");
					strBuilder.append("lastname = " + jsObj.getString("lastname")+"\n");
					strBuilder.append("is_admin = " + jsObj.getString("is_admin")+"\n");
					
					outputData.setText(strBuilder.toString());
				}
	 
	        } catch (Exception e) {
	            Log.d("POS", e.getLocalizedMessage());
	        }
			
			return "Executed";
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(mProgressDialog != null){
				mProgressDialog.show();
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			
			if(mProgressDialog != null){
				mProgressDialog.dismiss();
			}
			
			
		}
	}

	
}
