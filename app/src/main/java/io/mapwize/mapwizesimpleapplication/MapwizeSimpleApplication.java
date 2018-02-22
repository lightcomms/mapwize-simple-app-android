package io.mapwize.mapwizesimpleapplication;

import android.app.Application;

import io.mapwize.mapwizeformapbox.AccountManager;

/**
 * Created by xme on 20/02/2018.
 */

public class MapwizeSimpleApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        AccountManager.start(this, "e2af1248a493cd196fe54b1dbdba8ba8"); // PASTE YOU MAPWIZE API KEY HERE !!! This is a demo key, giving you access to the demo building. It is not allowed to use it for production. The key might change at any time without notice. Get your key by signin up at mapwize.io
        //K9Z62kYrD8F370kM
        //1f04d780dc30b774c0c10f53e3c7d4ea
    }
}
