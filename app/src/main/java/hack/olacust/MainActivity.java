package hack.olacust;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Button x = (Button)findViewById(R.id.button);
        TextView tv = (TextView)findViewById(R.id.textView);
        tv.setText("LOPER");
        sms1 x33 = new sms1();

        //HttpClient httpclient = new DefaultHttpClient();
        //String x5 = "http://sandbox-t.olacabs.com/v1/products?pickup_lat=12.9491416&pickup_lng=77.64298";


            /*URL ur = new URL("http://sandbox-t.olacabs.com/v1/products?pickup_lat=12.9491416&pickup_lng=77.64298");
            HttpURLConnection con = (HttpURLConnection) ur.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-APP-TOKEN", "a083d4911e4242b9a22d728105ff712c"); */

            x.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View view) {

                    SmsManager sms = SmsManager.getDefault();
                    GPSTracker gps = new GPSTracker(MainActivity.this);
                    String lat = String.valueOf(gps.getLatitude());
                    String lon = String.valueOf(gps.getLongitude());
                    sms.sendTextMessage("9900211221", null, lat + " " + lon+"x", null, null);
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    String url = "http://sandbox-t.olacabs.com/v1/products?pickup_lat=12.9491416&pickup_lng=77.64298";
                    StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // response
                                    Log.d("Response", response);
                                    try {

                                        JSONObject jobj = new JSONObject(response);
                                        JSONArray menu = new JSONArray(jobj.getString("categories"));
                                        Log.w("TESTING",menu.getJSONObject(0).getString("id"));

                                    }
                                    catch (Exception e)
                                    {

                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO Auto-generated method stub
                                    Log.d("ERROR","error => "+error.toString());
                                }
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String>  params = new HashMap<String, String>();
                            params.put("X-APP-Token", "a083d4911e4242b9a22d728105ff712c");
                            //params.put("Accept-Language", "fr");

                            return params;
                        }
                    };
                    queue.add(postRequest);

                }


            });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
