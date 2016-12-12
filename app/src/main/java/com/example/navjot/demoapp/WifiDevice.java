package com.example.navjot.demoapp;

import android.os.Bundle;

public class WifiDevice {
    private Bundle mBundle;

    public static final String EXTRA_TX_POWER = "txPower";
    public static final String EXTRA_SSID = "txPower";
    public static final String EXTRA_RSSI = "rssi";

    public WifiDevice() {
        mBundle = new Bundle();
    }

    public String getSsid() {
        return mBundle.getString(EXTRA_SSID);
    }

    public long getRssi() {
        return mBundle.getLong(EXTRA_RSSI);
    }

    public long getTxpower() {
        return mBundle.getLong(EXTRA_TX_POWER);
    }
}
