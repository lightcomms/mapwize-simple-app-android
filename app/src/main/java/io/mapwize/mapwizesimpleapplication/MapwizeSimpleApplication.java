package io.mapwize.mapwizesimpleapplication;

import android.app.Application;

import io.mapwize.mapwizeformapbox.AccountManager;
import io.slms.cordova.vlc.VLCPlugin;

/**
 * Created by xme on 20/02/2018.
 */

public class MapwizeSimpleApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        AccountManager.start(this, "011d1d9b26c8f034cd9c9d76637a75c3"); // PASTE YOU MAPWIZE API KEY HERE !!! This is a demo key, giving you access to the demo building. It is not allowed to use it for production. The key might change at any time without notice. Get your key by signin up at mapwize.io
        //5a8b1432c0b1600013546407
        //1f04d780dc30b774c0c10f53e3c7d4ea
    }
}
