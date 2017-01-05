package com.example.navjot.demoapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DiscoveryService extends Service {

    private final String TAG = "DiscoveryService";
    Binder mBinder = new Binder();

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(TAG, "Someone is trying to bind me");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Someone is trying to unbind me");
        return super.onUnbind(intent);
    }

    public class Binder extends android.os.Binder {
        public void startDiscovery(Discovery.DiscoveryListener listener) {
            listener.onScanStart();
            Log.d(TAG, "Binder: startDiscovery");
        }

        public void stopDiscovery(Discovery.DiscoveryListener listener) {
            Log.d(TAG, "Binder: stopDiscovery");
            listener.onScanComplete();
        }
    }

}
