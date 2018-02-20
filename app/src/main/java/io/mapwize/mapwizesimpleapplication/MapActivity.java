package io.mapwize.mapwizesimpleapplication;

import android.Manifest;
import android.content.pm.PackageManager;
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
import io.mapwize.mapwizeformapbox.api.Api;
import io.mapwize.mapwizeformapbox.api.ApiCallback;
import io.mapwize.mapwizeformapbox.model.Venue;
import io.slms.cordova.vlc.VLCPlugin;

import static io.mapwize.mapwizeformapbox.api.Api.getVenue;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private MapwizePlugin mapwizePlugin;
    private VLCPlugin vlcLocationProvider;
    private java.util.logging.Logger logger = Logger.getLogger(MapActivity.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VLCPlugin.init(getApplication(),"anapikeyforvlc");
        vlcLocationProvider = VLCPlugin.getVlcPlugin();
        vlcLocationProvider.start();
        Mapbox.getInstance(this, "pk.eyJ1IjoibWFwd2l6ZSIsImEiOiJjamNhYnN6MjAwNW5pMnZvMnYzYTFpcWVxIn0.veTCqUipGXCw8NwM2ep1Xg"); // PASTE YOU MAPBOX API KEY HERE !!! This is a demo key. It is not allowed to use it for production. The key might change at any time without notice. Get your key by signing up at mapbox.com
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        Api.getAccess("011d1d9b26c8f034cd9c9d76637a75c3", new ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                logger.severe("XME : YEAH!");

            }

            @Override
            public void onFailure(Throwable throwable) {
                logger.severe("XME : OH!NO!");

            }
        });
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                // ça s'affiche!!
                logger.severe("map ready");
                MapOptions options = new MapOptions.Builder().showUserPositionControl(true)
                        .build();



                mapwizePlugin = new MapwizePlugin(mapView, mapboxMap, options);
                mapwizePlugin.setLocationProvider(vlcLocationProvider);
                getVenue("http://mwz.io/v/lucibel",new ApiCallback<Venue>(){

                    @Override
                    public void onSuccess(Venue venue) {
                        Logger.getAnonymousLogger().severe("XME Error"+venue.getAlias());
                        mapwizePlugin.centerOnVenue(venue);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Logger.getAnonymousLogger().severe("XME Error"+throwable.getLocalizedMessage());
                    }
                });

            }
        });


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 6);
            }
        }

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
        //vlcLocationProvider.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        //vlcLocationProvider.stop();
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