package com.example.navjot.demoapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

class Discovery implements Closeable {
    private final String TAG = "Discovery";
    private final ServiceConnection mServiceConnection;
    private final List<Runnable> mDiscoveryActions;
    private DiscoveryService.Binder mService;
    private Context mContext;

    public Discovery(Context context) {
        mContext = context;
        mDiscoveryActions = new ArrayList<>();
        mServiceConnection = new ServiceConnection();
        Intent intent = new Intent(mContext, DiscoveryService.class);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    void startDiscovery(final DiscoveryListener listener, final WifiManager manager) throws NullPointerException {
        Log.d(TAG, "startDiscovery");
        setDiscoveryAction(new Runnable() {
            @Override
            public void run() {
                mService.startDiscovery(listener, manager);
            }
        });
    }

    void stopDiscovery(final DiscoveryListener listener) throws NullPointerException {
        Log.d(TAG, "stopDiscovery");
        setDiscoveryAction(new Runnable() {
            @Override
            public void run() {
                mService.stopDiscovery(listener);
            }
        });
    }

    private void setDiscoveryAction(Runnable action) {
        mDiscoveryActions.add(action);
        if (mService != null) {
            while (!mDiscoveryActions.isEmpty()) {
                mDiscoveryActions.remove(0).run();
            }
        }
    }

    @Override
    public void close() {
        mContext.unbindService(mServiceConnection);
    }

    interface DiscoveryListener {
        void onScanStart();

        void onScanStopped();
    }

    private class ServiceConnection implements android.content.ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof DiscoveryService.Binder) {
                Log.d(TAG, "onServiceConnect: DiscoveryService connected: " + name);
                mService = (DiscoveryService.Binder) service;
                while (!mDiscoveryActions.isEmpty()) {
                    mDiscoveryActions.remove(0).run();
                }
            } else {
                Log.d(TAG, "onServiceDisconnect: Bound with unexpected service " + name);
                close();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected: DiscoveryService disconnected: " + name);
            close();
        }
    }

}


