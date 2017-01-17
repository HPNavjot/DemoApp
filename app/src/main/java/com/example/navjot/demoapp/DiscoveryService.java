package com.example.navjot.demoapp;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class DiscoveryService extends Service {

    private final int RESCAN_AFTER = 4000;
    private final String TAG = "DiscoveryService";
    private Binder mBinder = new Binder();
    private Runnable mDiscoveryTask;
    private WifiManager mWifiManager;
    private List<WifiDevice> mScanResults;
    private Discovery.DiscoveryListener mListener;
    private Timer mTimer;
    private Handler mHandler;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
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
            mListener = listener;
            mListener.onScanStart();
//            mTimer.schedule(mDiscoveryTask, FIRST_SCAN_OFFSET, RESCAN_AFTER);
            mHandler.postDelayed(mDiscoveryTask, RESCAN_AFTER);
            Log.d(TAG, "Binder: startDiscovery");
        }

        public void stopDiscovery(Discovery.DiscoveryListener listener) {
            Log.d(TAG, "Binder: stopDiscovery");
            mHandler.removeCallbacks(mDiscoveryTask);
            listener.onScanStopped();
        }
    }


}
