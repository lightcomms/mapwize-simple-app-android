package io.mapwize.mapwizesimpleapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.logging.Logger;

import io.mapwize.mapwizeformapbox.MapOptions;
import io.mapwize.mapwizeformapbox.MapwizePlugin;
import io.slms.cordova.vlc.VLCIndoorLocation;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private MapwizePlugin mapwizePlugin;
    private VLCIndoorLocation vlcLocationProvider;
    private java.util.logging.Logger logger = Logger.getLogger(MapActivity.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoibWFwd2l6ZSIsImEiOiJjamNhYnN6MjAwNW5pMnZvMnYzYTFpcWVxIn0.veTCqUipGXCw8NwM2ep1Xg"); // PASTE YOU MAPBOX API KEY HERE !!! This is a demo key. It is not allowed to use it for production. The key might change at any time without notice. Get your key by signing up at mapbox.com
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        /*Api.getAccess("011d1d9b26c8f034cd9c9d76637a75c3", new ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                logger.severe("XME : YEAH!");

            }

            @Override
            public void onFailure(Throwable throwable) {
                logger.severe("XME : OH!NO!");

            }
        });*/

        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
            }
        }*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //if (!ActivityCompat.requestPermissions(this, Manifest.permission.CAMERA);) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 6);
            //}
        }else attachLocationProvider();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
            attachLocationProvider();
        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 6);
        }
    }

    private void attachLocationProvider(){
        VLCIndoorLocation.init(getApplication(),"anapikeyforvlc");
        vlcLocationProvider = VLCIndoorLocation.getVlcIndoorLocation();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                // ça s'affiche!!
                logger.severe("XME : map ready");
                MapOptions options = new MapOptions.Builder()
                        .showUserPositionControl(true)
                        .build();

                mapwizePlugin = new MapwizePlugin(mapView, mapboxMap, options);
                mapwizePlugin.setLocationProvider(vlcLocationProvider);

                /*getVenue("5a8b1432c0b1600013546407",new ApiCallback<Venue>(){

                    @Override
                    public void onSuccess(Venue venue) {
                        logger.severe("XME : venue "+venue!=null?"OK":"KO");
                        //mapwizePlugin.centerOnVenue(venue);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        logger.severe("XME : Api.getVenue -> Error");
                    }
                });*/

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        if (vlcLocationProvider!=null && !vlcLocationProvider.isStarted()){
            vlcLocationProvider.start();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        if (vlcLocationProvider!=null && vlcLocationProvider.isStarted()){
            vlcLocationProvider.stop();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}