package com.example.navjot.demoapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class DiscoveryService extends Service {

    private final String TAG = "DiscoveryService";
    Boolean isScanning;
    Binder mBinder = new Binder();
    Runnable mDiscoveryTask;
    WifiManager mWifiManager;
    List<WifiDevice> mScanResults;
    Discovery.DiscoveryListener mListener;
    Timer mTimer;
    Handler mHandler;
    final int RESCAN_AFTER = 4000;
    final int FIRST_SCAN_OFFSET = 0;
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        isScanning = false;
        mScanResults = new ArrayList<>();
        mTimer = new Timer();
        mHandler = new Handler(Looper.getMainLooper());
        mDiscoveryTask  = new Runnable() {
            @Override
            public void run() {
                mWifiManager.startScan();
                Log.d(TAG, "scanning task");
                mHandler.postDelayed(this, RESCAN_AFTER);
            }
        };
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
        public void startDiscovery(Discovery.DiscoveryListener listener, WifiManager manager) {
            mWifiManager = manager;
            isScanning = true;
            mListener = listener;
            mListener.onScanStart();
//            mTimer.schedule(mDiscoveryTask, FIRST_SCAN_OFFSET, RESCAN_AFTER);
            mHandler.postDelayed(mDiscoveryTask, RESCAN_AFTER);
            Log.d(TAG, "Binder: startDiscovery");
        }

        public void stopDiscovery(Discovery.DiscoveryListener listener) {
            Log.d(TAG, "Binder: stopDiscovery");
            isScanning = false;
            mHandler.removeCallbacks(mDiscoveryTask);
            listener.onScanStopped();
        }
    }


}
