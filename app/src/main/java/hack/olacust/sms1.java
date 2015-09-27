package hack.olacust;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codesnippets4all.json.parsers.JsonParserFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class sms1 extends BroadcastReceiver {
    public sms1() {
    }
    final SmsManager sms = SmsManager.getDefault();
    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                        final Object[] pdusObj = (Object[]) bundle.get("pdus");

                        for (int i = 0; i < pdusObj.length; i++) {

                            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                            String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                            final String senderNum = phoneNumber;
                            String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);
                            String x = message.substring(0,3);



                            // Show Alert
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,
                            "senderNum: " + senderNum + ", message: " + message, duration);
                    String lat = "";
                    if(message.charAt(0)==120)
                    {
                    int i1 = 0;
                    for (i1 = 1; message.charAt(i1) != 32; i1++) {
                        lat = lat + message.charAt(i1);
                    }
                    String lon = "";
                    for (i1 = i1 + 1; message.charAt(i1) != 120; i1++) {
                        lon = lon + message.charAt(i1);
                    }
                    i1 = i1 + 1;
                    //Character type = message.charAt(i1);
                    Integer ty = Character.getNumericValue(message.charAt(i1));
                    Log.w(lon, " " + lat);

                    toast.show();
                    SmsManager sms = SmsManager.getDefault();
                    // sms.sendTextMessage(senderNum, null, "Thanks for your response :) We'll get back to you asap", null, null);
                    //    SmsManager sms = SmsManager.getDefault();
                    //GPSTracker gps = new GPSTracker(MainActivity.this);
                    //String lat = String.valueOf(gps.getLatitude());
                    //String lon = String.valueOf(gps.getLongitude());
                    //sms.sendTextMessage("9900211221", null, lat + " " + lon, null, null);
                    RequestQueue queue = Volley.newRequestQueue(context);
                    String url = "http://sandbox-t.olacabs.com/v1/products?pickup_lat=" + lat + "&pickup_lng=" + lon;
                        if(ty==1) {
                            url=url+"&category=sedan";
                        }
                        if(ty==0) {
                            url=url+"&category=mini";
                        }

                    StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // response
                                    Log.d("Response", response);
                                    try {
                                        JSONObject jobj = new JSONObject(response);
                                        JSONArray menu = new JSONArray(jobj.getString("categories"));
                                        Double arr[] = {-1.0,-1.0,-1.0,-1.0,-1.0,-1.0};
                                        int k=0;
                                        for (int i = 0; i < menu.length(); i++) {
                                            // Log.i("lol","d");
                                            JSONObject rec = menu.getJSONObject(i);
                                            String type = rec.getString("id");
                                            Double eta = rec.getDouble("eta");
                                            Double dist = rec.getDouble("distance");
                                            arr[k++] = eta;
                                            arr[k++] = dist;

                                            JSONArray x13 = (JSONArray)rec.get("fare_breakup");
                                            if(x13!=null)
                                            {
                                                Log.w("fsad",x13.toString());
                                                JSONObject temp = x13.getJSONObject(0);
                                                JSONArray surch = temp.getJSONArray("surcharge");
                                                Log.w("fsadd",surch.toString());
                                                JSONObject srch_val = surch.getJSONObject(0);
                                                Double surch_final = srch_val.getDouble("value");
                                                arr[k++] = surch_final;

                                            }
                                            else
                                                arr[k++] = 1.0;

                                            String distance = rec.getString("distance");
                                            Log.d("type",type);
                                            //Log.d("etimea",eta);
                                            //Log.d("diatance",distance);
                                        }
                                        String rett = "";
                                        for(int i =0; arr[i]!=-1&&i<6;i++)
                                        {
                                            rett=rett+","+arr[i].toString();
                                        }

                                        Log.w("TESTING", menu.getJSONObject(0).getString("id"));
                                        final SmsManager smsfinal = SmsManager.getDefault();
                                        smsfinal.sendTextMessage(senderNum, null, "%"+rett, null, null);

                                    } catch (Exception e) {

                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO Auto-generated method stub
                                    Log.d("ERROR", "error => " + error.toString());
                                }
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("X-APP-Token", "a083d4911e4242b9a22d728105ff712c");
                            //params.put("Accept-Language", "fr");

                            return params;
                        }
                    };
                    queue.add(postRequest);
                }
                    else if(message.charAt(0)==121)
                    {
                        int i1 = 1;

                        if(message.charAt(i)==49)
                            continue;
                        for (i1 = 2; message.charAt(i1) != 32; i1++) {
                            lat = lat + message.charAt(i1);
                        }
                        String lon = "";
                        for (i1 = i1 + 1; message.charAt(i1) != 120; i1++) {
                            lon = lon + message.charAt(i1);
                        }
                        i1 = i1 + 1;
                        //Character type = message.charAt(i1);
                        Integer ty = Character.getNumericValue(message.charAt(i1));
                        Log.w(lon, " " + lat);

                        toast.show();
                        SmsManager sms = SmsManager.getDefault();
                        // sms.sendTextMessage(senderNum, null, "Thanks for your response :) We'll get back to you asap", null, null);
                        //    SmsManager sms = SmsManager.getDefault();
                        //GPSTracker gps = new GPSTracker(MainActivity.this);
                        //String lat = String.valueOf(gps.getLatitude());
                        //String lon = String.valueOf(gps.getLongitude());
                        //sms.sendTextMessage("9900211221", null, lat + " " + lon, null, null);
                        RequestQueue queue = Volley.newRequestQueue(context);
                        String url = "http://sandbox-t.olacabs.com/v1/bookings/create?pickup_lat=" + lat + "&pickup_lng=" + lon + "&pickup_mode=NOW&category=";
                        if(ty==1) {
                            url=url+"sedan";
                        }
                        if(ty==0) {
                            url=url+"mini";
                        }
                        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // response
                                        Log.d("Response", response);
                                        try {
                                            JSONObject jobj = new JSONObject(response);
                                         //   Log.w("asdf",jobj.getString("driver_lat"));
                                           // String lat = response.indexOf("driver_lat");
                                            // JSONArray menu = new JSONArray(jobj.getString("categories"));

                                            //  for (int i = 0; i < menu.length(); i++) {
                                                // Log.i("lol","d");
                                            Double lat2 = jobj.getDouble("driver_lat");
                                            Log.w("lasdf",lat2.toString());
                                            String number = jobj.getString("driver_number");
                                            Log.w("asdsfa",number);
                                            String vehi = jobj.getString("cab_number");
                                            Log.w("vehi",vehi);
                                            String car = jobj.getString("car_model");
                                            Integer eta = jobj.getInt("eta");
                                           Double long2 = jobj.getDouble("driver_lng");
                                            String name = jobj.getString("driver_name");
                                            //Log.w("lasdf2",long2.toString());



                                             //   String lat1 = values[7];
                                              //  String lon1 = values[8];
                                               // String vehi_no = values[4];

                                           // String eta = values[6];

                                             //   String number = values[2];
                                            //Log.w("CHECK",number);
                                                //Log.d("type",type);
                                                //Log.d("etimea",eta);
                                                //Log.d("diatance",distance);
                                           // }
                                          //  Log.w("TESTING", menu.getJSONObject(0).getString("id"));
                                            final SmsManager smsfinal = SmsManager.getDefault();
                                            String x9 = ","+car+","+vehi+","+number+","+eta.toString()+","+lat2.toString()+","+long2.toString()+","+name;
                                            Log.w("LOL",x9);
                                           smsfinal.sendTextMessage(senderNum, null,"$"+x9, null, null);

                                        } catch (Exception e) {

                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO Auto-generated method stub
                                        Log.d("ERROR", "error => " + error.toString());
                                    }
                                }
                        ) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("X-APP-Token", "a083d4911e4242b9a22d728105ff712c");
                                params.put("Authorization","Bearer bd54516c8cc64946ac6e689ae6ce2a96");
                                //params.put("Accept-Language", "fr");

                                return params;
                            }
                        };
                        postRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(postRequest);
                    }
                    else if(x.equalsIgnoreCase("ola")){
                        Log.w("ASSS","rrr");

                        final Character resp = message.charAt(4);
                        Character one = '1';
                        Character two = '2';
                        Character three = '3';
                        Character book = 'B';
                        Character book1 = 'b';
                        if(resp.compareTo(book)==0||resp.compareTo(book1)==0)
                        {
                           // Log.w("ASSS","OPOP");
                            RequestQueue queue12 = Volley.newRequestQueue(context);
                            String loc = message.substring(9);
                            String loc1 = loc.replace(" ","");
                            String url1 = "http://10.20.241.245:12345/data/"+loc1;
                            SharedPreferences settings = context.getSharedPreferences("Prefs", 0);
                            final SharedPreferences.Editor editor = settings.edit();
                            editor.putString("user1", senderNum);
                            editor.apply();
                            JsonObjectRequest postRequest1 = new JsonObjectRequest(Request.Method.GET, url1,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            // response
                                            try {
                                                JSONArray ar = (JSONArray) response.getJSONArray("predict");
                                                Log.w("asdf",ar.toString());
                                                final SmsManager smsfinal = SmsManager.getDefault();
                                                editor.putString(senderNum+"1",ar.get(0).toString());
                                                editor.putString(senderNum+"2",ar.get(0).toString());
                                                editor.apply();
                                                String str = "Enter OLA 1 for "+ar.get(0).toString()+", OLA 2 for "+ar.get(1);
                                                smsfinal.sendTextMessage(senderNum, null,str, null, null);


                                            }
                                            catch (Exception e)
                                            {
                                                Log.w("fsd","df");
                                            }
                                        //    JsonParserFactory factory= JsonParserFactory.getInstance();
                                     //       JsonParser parser=factory.newJsonParser();
                                     //       Map jsonData=parser.parse(inputJsonString);

                                      //      String value=(String)jsonData.get("key2");



                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // TODO Auto-generated method stub
                                            Log.d("ERROR", "error => " + error.toString());
                                        }
                                    }

                            );
                            queue12.add(postRequest1);





// Get from the SharedPreferences
                            SharedPreferences settings1 = context.getSharedPreferences("Prefs", 0);
                            Integer homeScore = settings1.getInt("homeScore", 0);

                            Log.w("wasdf",homeScore.toString());
                        }
                        if(resp.compareTo(one)==0)
                        {

                            SharedPreferences settings1 = context.getSharedPreferences("Prefs", 1);
                            String add = settings1.getString(senderNum + "1", "NA");
                            RequestQueue queue12 = Volley.newRequestQueue(context);
                            String loc1 = add.replace(" ", "");
                            String url1 = "http://10.20.241.245:12345/getlocation/"+loc1;
                            JsonObjectRequest postRequest1 = new JsonObjectRequest(Request.Method.GET, url1,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            // response
                                            try {
                                                JSONArray ar = (JSONArray) response.getJSONArray("position");
                                                Log.w("asdf",ar.toString());
                                                Log.w("EPRO",ar.get(0).toString()+" "+ar.get(1).toString());
                                                bookRide(ar.get(0).toString(),ar.get(1).toString(),context,senderNum);
                                               // final SmsManager smsfinal = SmsManager.getDefault();
                                                //editor.putString(senderNum+"1",ar.get(0).toString());
                                                //editor.putString(senderNum+"2",ar.get(0).toString());
                                                //String str = "Enter OLA 1 for "+ar.get(0).toString()+", OLA 2 for "+ar.get(1);
                                                //smsfinal.sendTextMessage(senderNum, null,str, null, null);


                                            }
                                            catch (Exception e)
                                            {
                                                Log.w("fsd","df");
                                            }
                                            //    JsonParserFactory factory= JsonParserFactory.getInstance();
                                            //       JsonParser parser=factory.newJsonParser();
                                            //       Map jsonData=parser.parse(inputJsonString);

                                            //      String value=(String)jsonData.get("key2");



                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // TODO Auto-generated method stub
                                            Log.d("ERROR", "error => " + error.toString());
                                        }
                                    }

                            );
                            queue12.add(postRequest1);




                        }


                    }
                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
        //throw new UnsupportedOperationException("Not yet implemented");
    }
    public void bookRide(String lat,String lng,Context cont,final String senderNum)
    {
        RequestQueue queue = Volley.newRequestQueue(cont);
        Log.w("opo","pop");
        String url = "http://sandbox-t.olacabs.com/v1/bookings/create?pickup_lat=" + lat + "&pickup_lng=" + lng + "&pickup_mode=NOW&category=sedan";

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        try {
                            final SmsManager smsfinal = SmsManager.getDefault();

                            JSONObject jobj = new JSONObject(response);
                            if(jobj.has("status"))
                            {
                                smsfinal.sendTextMessage(senderNum, null,"Sorry, we're working real hard to add more cars. Please try us again soon.", null, null);

                            }
                            else {
                                //   Log.w("asdf",jobj.getString("driver_lat"));
                                // String lat = response.indexOf("driver_lat");
                                // JSONArray menu = new JSONArray(jobj.getString("categories"));

                                //  for (int i = 0; i < menu.length(); i++) {
                                // Log.i("lol","d");
                                //Double lat2 = jobj.getDouble("driver_lat");
                                //Log.w("lasdf", lat2.toString());
                                String number = jobj.getString("driver_number");
                                Log.w("asdsfa", number);
                                String vehi = jobj.getString("cab_number");
                                Log.w("vehi", vehi);
                                //String car = jobj.getString("car_model");
                                Integer eta = jobj.getInt("eta");
                                //Double long2 = jobj.getDouble("driver_lng");
                                //Log.w("lasdf2",long2.toString());


                                //   String lat1 = values[7];
                                //  String lon1 = values[8];
                                // String vehi_no = values[4];

                                // String eta = values[6];

                                //   String number = values[2];
                                //Log.w("CHECK",number);
                                //Log.d("type",type);
                                //Log.d("etimea",eta);
                                //Log.d("diatance",distance);
                                // }
                                //  Log.w("TESTING", menu.getJSONObject(0).getString("id"));
                                String x9 = "Thank you! Vehicle No:" + vehi + " Driver No: " + number + ". ETA: " + eta + " minutes";

                                Log.w("LOL", x9);
                                smsfinal.sendTextMessage(senderNum, null, x9, null, null);
                            }

                        } catch (Exception e) {

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR", "error => " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("X-APP-Token", "a083d4911e4242b9a22d728105ff712c");
                params.put("Authorization","Bearer bd54516c8cc64946ac6e689ae6ce2a96");
                //params.put("Accept-Language", "fr");

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

}

