package com.example.navjot.demoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;


class WifiStateNotifier {

    private static final String LOG_TAG = "WifiStateNotifier";
    private final WifiStateListener mStateListener;
    private final Context mContext;
    private WifiManager mWifiManager;
    private WifiReceiver mReceiver;
    private AlertDialog mDialog;

    WifiStateNotifier(Context scanActivity, WifiStateListener listener) {
        Log.d(LOG_TAG, "WifiStateNotifier constructor");
        mStateListener = listener;
        mContext = scanActivity;
    }

    void startWifiMonitor() {
        Log.d(LOG_TAG, "startWifiMonitor()");
        if (mReceiver == null) {
            mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            Log.d(LOG_TAG, "onResume: registerReceiver");
            mReceiver = new WifiReceiver();
            mContext.registerReceiver(mReceiver, filter);
        }
        enableWifi();
    }

    void stopWifiMonitor() {
        Log.d(LOG_TAG, "stopWifiMonitor()");
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }

    void enableWifi() {
        Log.d(LOG_TAG, "enableWifi");
        if (!mWifiManager.isWifiEnabled()) {
            Log.d(LOG_TAG, "enableWifi: building alert");
            if (mDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle(R.string.enable_wifi_title);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.enable_positive_text,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mWifiManager.setWifiEnabled(true);
                                mStateListener.onAcceptEnable();
                            }
                        });
                builder.setNegativeButton(R.string.enable_negative_text,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mStateListener.onDeclineEnable();
                            }
                        });
                mDialog = builder.create();
            }
            mDialog.show();
        }
    }

    interface WifiStateListener {
        void onDisconnected();

        void onConnected();

        void onDeclineEnable();

        void onAcceptEnable();
    }

    private class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
                    mStateListener.onDisconnected();
                } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                    mStateListener.onConnected();
                }
            }
        }
    }

}
