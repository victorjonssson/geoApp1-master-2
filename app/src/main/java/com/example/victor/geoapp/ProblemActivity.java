package com.example.victor.geoapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ProblemActivity extends AppCompatActivity {
    Button submitButton;
    EditText beskrivning;
    String server_url = "http://10.0.2.2:80/gavle/update_info.php";
    Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);

        //Behöver en locationmanager för att hitta vår
        //GPS provider m.m.
        LocationManager locationManager;
        //Hämta enhetens gps
        String svcName = Context.LOCATION_SERVICE;
        //Hämta provider
        locationManager = (LocationManager) getSystemService(svcName);
        String provider = LocationManager.GPS_PROVIDER;
        //här kommer felhantering
        //sätt upp själva positions lyssnaren
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            //För att appen ska fråga telefonen om tillåtelse
            // att använda GPS:en när den installeras
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.INTERNET }
                        ,10
                );
            }
            return;
        }
        locationManager.requestLocationUpdates(provider, 0, 0, new
                LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                    }
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle
                            extras) {
                    }
                    @Override
                    public void onProviderEnabled(String provider) {
                    }
                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                });
        //Hämta senste longitud och latitud

        loc = locationManager.getLastKnownLocation(provider);
        //metod som visar vår nya position
        updateWithNewLocation(loc);

        submitButton = (Button)findViewById(R.id.btnsubmit);
        beskrivning = (EditText)findViewById(R.id.anteckningar);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String Response) {


                            }
                        }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(ProblemActivity.this, "Error..", Toast.LENGTH_SHORT).show();
                    }
                }
                ){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        //OM vi fått någon GPS POS
                        if (loc != null) {
                            //FÖr att få åtta decimaler
                            DecimalFormat df = new DecimalFormat("#.########");
                            params.put("longitud", df.format(loc.getLongitude()));
                            params.put("latitud", df.format(loc.getLatitude()));
                        }
                        else {
                            params.put("longitud", "0");
                            params.put("latitud", "0");
                        }
                        params.put("beskrivning", beskrivning.getText().toString());
                        return params;
                    }
                };

                MySingleton.getInstance(ProblemActivity.this).addToRequestQueue(stringRequest);

            };
        });
    }
    //metoden updateWithNewLocation för att skriva ut vår position
    private void updateWithNewLocation(Location loc){
        //Hämta TextView från layouten
        TextView myLocationText;
        myLocationText = (TextView)findViewById(R.id.myLocationText);
        //Skapa texten till vår TextView
        String latlongText = "No location found";
        //om vi fått någon position ska detta skrivas
        if(loc != null){
            //hämta latitud och longitud
            double lat = loc.getLatitude();
            double lng = loc.getLongitude();
            latlongText = "Lat: "+ lat + " Long: "+ lng;
        }//här slutar if-satsen
        //sätt texten för TextView i layouten
        myLocationText.setText("Your current position is: \n"+ latlongText);
    }

    //metod för att ta oss till help
    public void actback (View v) {
        //intent för att anropa aktivitet
        Intent intent = new Intent(this, help.class);
        startActivity(intent);
    }}
