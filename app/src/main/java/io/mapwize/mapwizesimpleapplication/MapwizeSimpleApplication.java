package io.mapwize.mapwizesimpleapplication;

import android.app.Application;

import io.mapwize.mapwizeformapbox.AccountManager;

public class MapwizeSimpleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AccountManager.start(this, "1f04d780dc30b774c0c10f53e3c7d4ea"); // Apikey should be replace by your mapwize api key
    }

}
