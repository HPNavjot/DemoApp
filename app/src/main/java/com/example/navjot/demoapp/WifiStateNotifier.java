package com.example.navjot.demoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;


public class WifiStateNotifier {

    private static final String LOG_TAG = "WifiStateNotifier";
    private final WifiStateListener mStateListener;
    private final Context mContext;
    private WifiManager mWifiManager;
    private WifiReceiver mReceiver;
    private AlertDialog mDialog;

    public WifiStateNotifier(Context context, WifiStateListener listener) {
        Log.d(LOG_TAG, "WifiStateNotifier constructor");
        mStateListener = listener;
        mContext = context;
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    void startWifiMonitor() {
        Log.d(LOG_TAG, "startWifiMonitor()");
        if (mReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            mReceiver = new WifiReceiver();
            Log.d(LOG_TAG, "startWifiMonitor: registerReceiver");
            mContext.registerReceiver(mReceiver, filter);
        }
        enableWifi();
    }

    void stopWifiMonitor() {
        Log.d(LOG_TAG, "stopWifiMonitor()");
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
            Log.d(LOG_TAG, "stopWifiMonitor() unregistered");
        }
    }

    public boolean isEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    void enableWifi() {
        Log.d(LOG_TAG, "enableWifi");
        if (!mWifiManager.isWifiEnabled()) {
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
            Log.d(LOG_TAG, "enableWifi: show wifi alert dialog");
            mDialog.show();
        }
    }

    interface WifiStateListener {
        void onDisabled();

        void onEnabled();

        void onDeclineEnable();

        void onAcceptEnable();
    }

    private class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
                    mStateListener.onDisabled();
                } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    mStateListener.onEnabled();
                }
            }
        }
    }

}
