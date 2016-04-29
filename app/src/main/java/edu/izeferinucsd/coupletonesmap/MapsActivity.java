package edu.izeferinucsd.coupletonesmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.LocationListener;
import android.util.Log;

//import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.view.View;
import android.widget.ImageButton;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<LatLng> listOfPoints = new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageButton addFavoriteLocation = (ImageButton) findViewById(R.id.addFavoriteLocation);

        addFavoriteLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("CLICK! CLICK!");
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            //File dir = getFilesDir();
            //File file = new File(dir, "latlngpoints.txt");
           // boolean deleted = file.delete();
            // Modes: MODE_PRIVATE, MODE_WORLD_READABLE, MODE_WORLD_WRITABLE
            FileOutputStream output = openFileOutput("latlngpoints.txt", Context.MODE_PRIVATE);
            DataOutputStream dout = new DataOutputStream(output);
            dout.writeInt(listOfPoints.size()); // Save line count
            for (LatLng point : listOfPoints) {
                dout.writeUTF(point.latitude + "," + point.longitude);
                Log.v("write", point.latitude + "," + point.longitude);
            }
            dout.flush(); // Flush stream ...
            dout.close(); // ... and close.
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
/*
    @Override
    public void onResume() {
        super.onResume();
        try {
            FileInputStream input = openFileInput("latlngpoints.txt");
            DataInputStream din = new DataInputStream(input);
            int sz = din.readInt(); // Read line count
            for (int i = 0; i < sz; i++) {
                String str = din.readUTF();
                Log.v("read", str);
                String[] stringArray = str.split(",");
                double latitude = Double.parseDouble(stringArray[0]);
                double longitude = Double.parseDouble(stringArray[1]);
                listOfPoints.add(new LatLng(latitude, longitude));
            }
            din.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
*/

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
       // LatLng sydney = new LatLng(-34, 151);
       // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.getUiSettings().setZoomControlsEnabled(true);


        try {
            FileInputStream input = openFileInput("latlngpoints.txt");
            DataInputStream din = new DataInputStream(input);
            int sz = din.readInt(); // Read line count
            for (int i = 0; i < sz; i++) {
                String str = din.readUTF();
                Log.v("read", str);
                String[] stringArray = str.split(",");
                double latitude = Double.parseDouble(stringArray[0]);
                double longitude = Double.parseDouble(stringArray[1]);
                listOfPoints.add(new LatLng(latitude, longitude));
            }
            loadMarkers(listOfPoints);
            din.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        System.out.println(Integer.toString(listOfPoints.size()) + " SIZE");


        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("NEW LOCATION"));
                boolean isNew = true;
                //if(!listOfPoints.contains(currentLocation)) {
                //    listOfPoints.add(currentLocation);
               // }

                for(LatLng cmp : listOfPoints){
                    double lat = cmp.latitude;
                    double lng = cmp.longitude;
                    double lat2 = currentLocation.latitude;
                    double lng2 = currentLocation.longitude;

                    if((Math.floor(lat * 10000) == Math.floor(lat2 * 10000)) && (Math.floor(lng * 10000) == Math.floor(lng2 * 10000))) {
                        isNew = false;
                    }
                    else {
                        isNew = true;
                    }
                }

                if(isNew){
                    listOfPoints.add(currentLocation);
                }
                else {
                    System.out.println("EQUALLLLL");
                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            Log.d("test1","ins");
            return;
        }
        else if(mMap != null) {
            Log.d("test2", "outs");
            mMap.setMyLocationEnabled(true);

        }

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
    }

    private void loadMarkers(List<LatLng> locations) {

        for(LatLng location : locations){
            mMap.addMarker(new MarkerOptions().position(location).title("Marker"));
        }
    }


}