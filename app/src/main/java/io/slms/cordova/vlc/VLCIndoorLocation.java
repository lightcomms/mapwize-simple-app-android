package io.slms.cordova.vlc;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;


import io.indoorlocation.core.IndoorLocation;
import io.indoorlocation.core.IndoorLocationProvider;

/**
 * <p>                          Example class to call vlc command and link callbacks
 * <p><strong>Description :</strong>
 * <p><strong>File :</strong>       VLCIndoorLocation.java
 * <p><strong>Author :</strong>     Xavier Melendez
 * <p><strong>Date :</strong>       10/05/2017
 * <p><strong>Company :</strong>     slms
 */
public final class VLCIndoorLocation extends IndoorLocationProvider
{
    private static boolean started=false;
    private static IndoorLocation lastLocation;
    static {
        lastLocation = new IndoorLocation(new Location(VLCIndoorLocation.class.getName()),0.0);
        lastLocation.setBearing(0.1f);
        lastLocation.setAccuracy(0.1f);
    }


    @SuppressLint({"StaticFieldLeak"})
    private static VLCIndoorLocation vlcIndoorLocation =null;
    private final Application application;
    private final Handler handler;
    private final String vlcApiKey;
    //private final static  java.util.logging.Logger logger = Logger.getLogger(VLCIndoorLocation.class.getName());

    public static VLCIndoorLocation init(@NonNull Application application, @NonNull String vlcAPIKey){
        if(vlcIndoorLocation != null) {
            throw new IllegalStateException("VLCIndoorLocation already initialized");
        } else {
            vlcIndoorLocation = new VLCIndoorLocation(application,vlcAPIKey);
            /*try {
                VLCIndoorLocation.ipcCallbacks.onNewMessage(new JSONObject("{\"data\":\"0x71\"}"));
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            return vlcIndoorLocation;
        }
    }

    public static VLCIndoorLocation getVlcIndoorLocation(){
        return vlcIndoorLocation;
    }

    private VLCIndoorLocation(Application application, String vlcAPIKey){
        this.application=application;
        this.handler = new android.os.Handler(application.getMainLooper());
        this.vlcApiKey=vlcAPIKey;

        this.ipcCallbacks= new IPCCallbacks() {
            @Override
            public void onError(JSONObject jsonObject) {
                final JSONObject localObj = jsonObject;
                VLCIndoorLocation.this.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getVlcIndoorLocation().dispatchOnProviderError(new Error(localObj.toString()));
                    }
                });
            }

            @Override
            public void onProcessStopped(JSONObject jsonObject) {
                started = false;
                VLCIndoorLocation.this.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getVlcIndoorLocation().dispatchOnProviderStopped();
                    }
                });
            }

            @Override
            public void onProcessStarted(JSONObject jsonObject) {
                started = true;
                VLCIndoorLocation.this.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getVlcIndoorLocation().dispatchOnProviderStarted();
                    }
                });
            }

            @Override
            public void onNewMessage(JSONObject jsonObject) {
                //logger.severe("XME : NEW MESSAGE");
                String vlcId="";
                try {
                    vlcId = jsonObject.getString("data");
                } catch (JSONException e) {
                    VLCIndoorLocation.this.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            VLCIndoorLocation.this.dispatchOnProviderError(new Error("Empty VLC data"));
                        }
                    });
                    return;
                }
                // get the location from the location service
                final IndoorLocation localLocation=  getVlcIndoorLocation().getHardLocation(vlcId, getVlcIndoorLocation().vlcApiKey);
                if ( true||localLocation.getLatitude() != lastLocation.getLatitude()
                        || localLocation.getLongitude() != lastLocation.getLongitude()
                        || localLocation.getFloor() != lastLocation.getFloor())
                {
                    lastLocation.setLatitude(localLocation.getLatitude());
                    lastLocation.setLongitude(localLocation.getLongitude());
                    lastLocation.setFloor(localLocation.getFloor());
                    lastLocation.setTime(localLocation.getTime());

                    VLCIndoorLocation.this.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            getVlcIndoorLocation().dispatchIndoorLocationChange(localLocation);
                        }
                    });
                }else{
                    lastLocation.setLatitude(localLocation.getLatitude());
                    lastLocation.setLongitude(localLocation.getLongitude());
                    lastLocation.setFloor(localLocation.getFloor());
                    lastLocation.setTime(localLocation.getTime());
                }
            }
        };

    }

    private IPCCallbacks ipcCallbacks ;

    public static String getStatus() {
        return Sequencer.getStatus();
    }
    public static String getVersion() {
        return Sequencer.getEngineVersion();
    }

    @Override
    public boolean supportsFloor() {
        return true;
    }

    @Override
    public void start() {
        //Sequencer.camBack(application.getApplicationContext());
        //logger.severe("XME : STARTED VLC!!");

        Sequencer.start(application.getApplicationContext(), ipcCallbacks,"camback", 100);
    }

    @Override
    public void stop() {
        //logger.severe("XME : STOPPED VLC!!!");
        Sequencer.stop(application.getApplicationContext());
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    private IndoorLocation getLocationFromMapwize(String idVLC,String key){

        return lastLocation;
    }
    private IndoorLocation getHardLocation(String idVLC, String key){
        // TODO 19/02/2018 : replace these lines with an http request
        //logger.severe("XME: New vlc message :: "+idVLC);

        IndoorLocation newLocation = new IndoorLocation(lastLocation,lastLocation.getFloor());
        if (idVLC.startsWith("0x71")){
            newLocation.setLatitude(48.887988338992166);
            newLocation.setLongitude(2.168635725975037);
            newLocation.setTime(System.currentTimeMillis());
            newLocation.setAccuracy(0f);
            newLocation.setBearing(0f);
            newLocation.setFloor(7.0);
            //logger.severe("XME : Xavier Desk");
        }else if (idVLC.startsWith("0x68")) {
            newLocation.setBearing(0f);
            newLocation.setLatitude(48.8880923937327);
            newLocation.setLongitude(2.1684533357620244);
            newLocation.setAccuracy(0f);
            newLocation.setTime(System.currentTimeMillis());
            newLocation.setFloor(7.0);
            //logger.severe("XME : Meeting room");
        }
        return newLocation;
    }
}
