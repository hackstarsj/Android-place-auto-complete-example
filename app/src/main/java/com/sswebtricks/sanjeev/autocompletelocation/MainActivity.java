    package com.sswebtricks.sanjeev.autocompletelocation;

    import android.app.ProgressDialog;
    import android.net.Uri;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.AutoCompleteTextView;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.android.volley.DefaultRetryPolicy;
    import com.android.volley.RequestQueue;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.Volley;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.net.URLEncoder;

    public class MainActivity extends AppCompatActivity {

        private ProgressDialog progressDialog;
        TextView tlat;
        TextView tlong;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            tlat=(TextView)findViewById(R.id.lat);
            tlong=(TextView)findViewById(R.id.lng);
            progressDialog=new ProgressDialog(this);
            AutoCompleteTextView autocompleteView = (AutoCompleteTextView)findViewById(R.id.autocomplete);
            autocompleteView.setAdapter(new LocationAdapter(getApplicationContext(), R.layout.list_item));
            autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get data associated with the specified position

                    // in the list (AdapterView)
                    String description = (String) parent.getItemAtPosition(position);
                    FindLatLong(description);
                    Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
                }
            });
        }
        private void FindLatLong(String description) {

            progressDialog.setMessage("Fetching Details..");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = Uri.parse("https://maps.googleapis.com/maps/api/geocode/json")
                    .buildUpon()
                    .appendQueryParameter("key","YOUR API KEY")
                    .appendQueryParameter("address", URLEncoder.encode(description))
                    .build().toString();
            StringRequest sr = new StringRequest(com.android.volley.Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        if(jsonObj.getJSONArray("results")!=null) {
                            JSONArray destination_addresses = jsonObj.getJSONArray("results");
                            JSONObject geometry= (JSONObject) destination_addresses.get(0);
                            String lat=String.format("%.4f",geometry.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                            String lng=String.format("%.4f",geometry.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                            Log.d("element", response.toString());
                            tlat.setText("Lat : "+lat);
                            tlong.setText("Long : "+lng);

                        }
                        else{
                            Toast.makeText(MainActivity.this, "Error in finding ", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();

                    }
                    Log.d("Response", response);

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Log.d("Errors", String.valueOf(error));
                }
            });
            queue.add(sr);
            sr.setRetryPolicy(new DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }

    }
