package com.lokeshlabs.mymappolylineactivity;

import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class GpsLineActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static int LOC_TIMER = 2000;
    MapFragment mapFragment;
    ArrayList<String> maplist = new ArrayList<>();
    Handler handler = new Handler();
    Runnable runnable;
    int count = 0;
    boolean intial = true;
    PolylineOptions mPolyOptions = new PolylineOptions();
    private GoogleMap googleMap;
    private Marker marker;
    public  static  float angle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_line);

        if (googleMap == null) {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        loadMapPoints();

    }

    private void loadMapPoints() {
        maplist.add("17.7218722,83.3151211");
        maplist.add("17.722107,83.314445");
        maplist.add("17.7219026,83.315263");
        maplist.add("17.7225873,83.3131199");
        maplist.add("17.720656,83.311983");
        maplist.add("17.719511,83.310223");
        maplist.add("17.719061,83.309483");
        maplist.add("17.718448,83.308507");
        maplist.add("17.717671,83.307123");
        maplist.add("17.717109,83.306404");
        maplist.add("17.716578,83.305739");
        maplist.add("17.716108,83.305235");
        maplist.add("17.715454,83.304634");
        maplist.add("17.7151423,83.3054252");
        maplist.add("17.714856,83.306096");
        maplist.add("17.715086,83.306847");
        maplist.add("17.715439,83.307995");
        maplist.add("17.715439,83.3079896");
        maplist.add("17.716251,83.309111");
        maplist.add("17.717161,83.30932");
        maplist.add("17.717933,83.309003");

    }
    @Override
    public void onMapReady(GoogleMap gm) {
        LatLng loc = new LatLng(17.6868,  83.2185);
        googleMap = gm;
        LatLng loca = new LatLng(17.7218722, 83.3151211);
        googleMap.addMarker(new MarkerOptions().position(loca)
                .title(""));

        startTracking();
    }


    private void startTracking() {
        if (!intial) {
            handler.removeCallbacks(runnable);
            Toast.makeText(getApplicationContext(), "Reached Destination", Toast.LENGTH_SHORT).show();
        } else {
            handler.postDelayed(new Runnable() {
                public void run() {
                    //do something
//                webserviceCall();
                    count++;
                    drawPoline();
                    runnable = this;
                    handler.postDelayed(runnable, LOC_TIMER);
                }
            }, LOC_TIMER);
        }

    }


    static public void rotateMarker(final Marker marker, final float toRotation, double position) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1000;

        final Interpolator interpolator = new LinearInterpolator();
        Log.d("", "Bearing: " + toRotation);

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;
                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    // Post again 10ms later.
                    handler.postDelayed(this, 10);
                }
            }
        });
    }


    private void drawPoline() {
        if (count < maplist.size() - 1) {
            googleMap.clear();
            String start = maplist.get(count);
            String[] latlong = start.split(",");
            double src_lat = Double.parseDouble(latlong[0]);
            double src_lng = Double.parseDouble(latlong[1]);
            String s = maplist.get(count + 1);
            String[] destination = s.split(",");
            double desc_lat = Double.parseDouble(destination[0]);
            double desc_lng = Double.parseDouble(destination[1]);
            LatLng latLng = new LatLng(desc_lat, desc_lng);
            mPolyOptions.add(latLng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);

            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car)).position(latLng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            marker = googleMap.addMarker(markerOptions);

            CameraPosition position = CameraPosition.builder()
                    .target(new LatLng(src_lat, src_lng))
                    .zoom(15)
                    .tilt(30)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));

            double bearing = bearingBetweenLocations(src_lat,src_lng,desc_lat,desc_lng);
            changePositon(bearing);
//            rotateMarker(marker, 20, bearing);
            //            marker.remove();


            PolylineOptions polyoptions = new PolylineOptions();
            polyoptions.color(Color.BLUE);
            polyoptions.width(6);

            polyoptions.addAll(mPolyOptions.getPoints());
            googleMap.addPolyline(polyoptions);


        } else {
            intial = false;
            Toast.makeText(getApplicationContext(), "Reached Destination", Toast.LENGTH_SHORT).show();
            handler.removeCallbacks(runnable);
        }

    }

    private void changePositon(double positon) {

        float direction = (float) positon;
        Log.e("LocationBearing", "" + direction);

        if (direction==360.0){
            //default
            marker.setRotation(angle);
        }else {
            marker.setRotation(direction);
            angle=direction;
        }

    }


    private double bearingBetweenLocations(
            double lat1, double long1, double lat2, double long2
    ) {

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }



}
