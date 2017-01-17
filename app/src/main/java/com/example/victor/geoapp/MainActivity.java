package com.example.victor.geoapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOptions;
import com.esri.android.map.MapView;
import com.esri.core.geometry.CoordinateConversion;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // The MapView.
    MapView mMapView = null;

    // The basemap switching menu items.
    MenuItem mStreetsMenuItem = null;
    MenuItem mTopoMenuItem = null;
    MenuItem mGrayMenuItem = null;
    MenuItem mOceansMenuItem = null;

    // Create MapOptions for each type of basemap.
    final MapOptions mTopoBasemap = new MapOptions(MapOptions.MapType.TOPO);
    final MapOptions mStreetsBasemap = new MapOptions(MapOptions.MapType.STREETS);
    final MapOptions mGrayBasemap = new MapOptions(MapOptions.MapType.GRAY);
    final MapOptions mOceansBasemap = new MapOptions(MapOptions.MapType.OCEANS);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //

        // Get MapView from layout XML
        // Alternatively, create new MapView object, set a basemap, then call setContentView(mMapView);
        MapView mMapView = (MapView) findViewById(R.id.map);
        // Create and add a GraphicsLayer
        GraphicsLayer graphicsLayer = new GraphicsLayer();
        mMapView.addLayer(graphicsLayer);

        // create a point marker symbol (red, size 10, of type circle)
        SimpleMarkerSymbol simpleMarker = new SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE);

        SpatialReference SP_WGS_84 = SpatialReference.create(102100);
        Point point = CoordinateConversion.decimalDegreesToPoint("60.66738190754173 17.15213656425476", SP_WGS_84);

        // create a point at x=-302557, y=7570663 (for a map using meters as units; this depends         // on the spatial reference)
        //detta är i ett metersystem (gps (WGS84))
        //hämta kordinaterna från http://twcc.fr

        //Point pointGeometry = new Point(-1909457.44, 8549477.86);

        // create a graphic with the geometry and marker symbol
        //använd den pin samt vilken punkt du valt för att sakpa ett graphiskt objekt
        Graphic pointGraphic = new Graphic(point, simpleMarker);

        // add the graphic to the graphics layer
        //lägg till det graphiska objektet i ditt lager
        graphicsLayer.addGraphic(pointGraphic);


        //

        //för att hämta long och lat
        //behöver en locationmaneger frö att hitta gps provider m.m.
        LocationManager locationManager;
        //hämta enhetens gps
        String svcName = Context.LOCATION_SERVICE;
        //hämta provider
        locationManager = (LocationManager) getSystemService(svcName);
        String provider = LocationManager.GPS_PROVIDER;
        //här kommer felhantering
        //sättt upp själva positions lyssnaren
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            //för att appen ska fråga efter tillåtelse att använda gpspositionen
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET}
                        ,10
                );
            }
            return;
        }

        locationManager.requestLocationUpdates(provider, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });




        // Retrieve the map and initial extent from XML layout
        mMapView = (MapView) findViewById(R.id.map);
        // Enable map to wrap around date line.
        mMapView.enableWrapAround(true);
    }
    private void updateWithNewLocation(Location loc){
        //hämta textview från layout
        TextView myLocationText;
        //myLocationText = (TextView)findViewById(R.id.m);
        //skapa text till min textview
        String latlongText = "No location found";
        //om vi fått någon location
        if (loc != null){
            //hämta lat och long
            double  lat = loc.getLatitude();
            double lng = loc.getLongitude();
            latlongText = "lat: "+ lat + " long: "+ lng;

        }
        //sätt texten för TextView i layouten
        //myLocationText.setText("your current position is: n\n"+ latlongText);
    }
    //




    protected void onPause() {
        super.onPause();
        //mMapView.pause();
    }

    protected void onResume() {
        super.onResume();
       //mMapView.unpause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        // Get the basemap switching menu items.
        mStreetsMenuItem = menu.getItem(0);
        mTopoMenuItem = menu.getItem(1);
        mGrayMenuItem = menu.getItem(2);
        mOceansMenuItem = menu.getItem(3);

        // Also set the topo basemap menu item to be checked, as this is the default.
        mTopoMenuItem.setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item selection.
        switch (item.getItemId()) {
            case R.id.World_Street_Map:
                mMapView.setMapOptions(mStreetsBasemap);
                mStreetsMenuItem.setChecked(true);
                return true;
            case R.id.World_Topo:
                mMapView.setMapOptions(mTopoBasemap);
                mTopoMenuItem.setChecked(true);
                return true;
            case R.id.Gray:
                mMapView.setMapOptions(mGrayBasemap);
                mGrayMenuItem.setChecked(true);
                return true;
            case R.id.Ocean_Basemap:
                mMapView.setMapOptions(mOceansBasemap);
                mOceansMenuItem.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //metod för att ta oss till problem
    public void actreportproblem (View v){
        //för att anropa nästa aktivitet görs en intent
        Intent intent = new Intent(this,ProblemActivity.class);
        startActivity(intent);
    }

    //metod för att ta oss till help
    public void acthelp(View v){
        //intent för att anropa aktivitet
        Intent intent = new Intent(this,help.class);
        startActivity(intent);
    }



}
