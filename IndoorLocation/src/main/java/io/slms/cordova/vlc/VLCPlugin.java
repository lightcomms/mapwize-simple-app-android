package io.slms.cordova.vlc;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.location.Location;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Logger;

import io.indoorlocation.core.IndoorLocation;
import io.indoorlocation.core.IndoorLocationProvider;

/**
 * <p>                          Example class to call vlc command and link callbacks
 * <p><strong>Description :</strong>
 * <p><strong>File :</strong>       VLCPlugin.java
 * <p><strong>Author :</strong>     Xavier Melendez
 * <p><strong>Date :</strong>       10/05/2017
 * <p><strong>Company :</strong>     slms
 */
public final class VLCPlugin extends IndoorLocationProvider
{
    private static boolean started=false;
    private static IndoorLocation lastLocation = new IndoorLocation(new Location(VLCPlugin.class.getName()),0.0);

    @SuppressLint({"StaticFieldLeak"})
    private static VLCPlugin vlcPlugin =null;
    private final Application application;
    private final Handler handler;
    private final String vlcApiKey;
    private final static  java.util.logging.Logger logger = Logger.getLogger(VLCPlugin.class.getName());

    public static VLCPlugin init(@NonNull Application application,@NonNull String vlcAPIKey){
        if(vlcPlugin != null) {
            throw new IllegalStateException("AccountManager already started");
        } else {
            vlcPlugin = new VLCPlugin(application,vlcAPIKey);
            return vlcPlugin;
        }
    }
    public static VLCPlugin getVlcPlugin(){
        return vlcPlugin;
    }
    private VLCPlugin (Application application, String vlcAPIKey){
        this.application=application;
        this.handler = new android.os.Handler(getApplication().getMainLooper());
        this.vlcApiKey=vlcAPIKey;
    }

    @Keep
    static IPCCallbacks ipcCallbacks = new IPCCallbacks() {
        @Override
        public void onError(JSONObject jsonObject) {
            final JSONObject localObj = jsonObject;
            getVlcPlugin().handler.post(new Runnable() {
                @Override
                public void run() {
                    getVlcPlugin().dispatchOnProviderError(new Error(localObj.toString()));
                }
            });
        }

        @Override
        public void onProcessStopped(JSONObject jsonObject) {
            started = false;
            getVlcPlugin().handler.post(new Runnable() {
                @Override
                public void run() {
                    getVlcPlugin().dispatchOnProviderStopped();
                }
            });
        }

        @Override
        public void onProcessStarted(JSONObject jsonObject) {
            started = true;
            getVlcPlugin().handler.post(new Runnable() {
                @Override
                public void run() {
                    getVlcPlugin().dispatchOnProviderStarted();
                }
            });
        }

        @Override
        public void onNewMessage(JSONObject jsonObject) {
            String vlcId="";
            try {
                vlcId = jsonObject.getString("data");
            } catch (JSONException e) {
                getVlcPlugin().handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getVlcPlugin().dispatchOnProviderError(new Error("Empty VLC data"));
                    }
                });
                return;
            }
            // get the location from the location service
            final IndoorLocation localLocation=  getVlcPlugin().getLocationFromServerWithVLCId(vlcId,getVlcPlugin().vlcApiKey);
            if (true || localLocation.getLatitude() != lastLocation.getLatitude()
                    || localLocation.getLongitude() != lastLocation.getLongitude()
                    || localLocation.getFloor() != lastLocation.getFloor())
            {
                lastLocation.setTime(localLocation.getTime());
                lastLocation.set(localLocation);
                lastLocation.setFloor(localLocation.getFloor());
                getVlcPlugin().handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getVlcPlugin().dispatchIndoorLocationChange(localLocation);
                    }
                });
            }else{
                lastLocation.setTime(localLocation.getTime());
                lastLocation.set(localLocation);
                lastLocation.setFloor(localLocation.getFloor());
            }
        }
    };

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
        Sequencer.start(application.getApplicationContext(), "camback", 100);
    }

    @Override
    public void stop() {
        Sequencer.stop(application.getApplicationContext());
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    public Application getApplication() {
        return application;
    }

    private IndoorLocation getLocationFromServerWithVLCId(String idVLC,String key){
        // TODO 19/02/2018 : replace these lines with an http request
        logger.severe("XME: New vlc message :: "+idVLC);
        IndoorLocation newLocation = new IndoorLocation(lastLocation,lastLocation.getFloor());
        if (idVLC.startsWith("0x71")){
            newLocation.setLatitude(37.457304);
            newLocation.setLongitude(-115.482597);
            newLocation.setTime(System.currentTimeMillis());
            newLocation.setFloor(0.0);
            logger.severe("XME : BlackBox location");
        }else{
            newLocation.setLatitude(48.88288288288288);
            newLocation.setLongitude(2.1645722015939795);
            newLocation.setTime(System.currentTimeMillis());
            newLocation.setFloor(7.0);
            logger.severe("XME : Lucibel HQ location");

        }
        return newLocation;
    }
}
