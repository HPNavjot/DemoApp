package com.example.navjot.demoapp;

import android.os.Bundle;

public class WifiDevice {
    private Bundle mBundle;
    public static final String EXTRA_SSID = "ssid";
    private static final String EXTRA_RSSI = "rssi";
    private static final String EXTRA_CAPS = "capabilities";
    private static final String EXTRA_BSSID = "bssid";
    private static final String EXTRA_TIMESTAMP = "timestamp";
    private static final String EXTRA_FRIENDLY_NAME = "friendlyName";
    private static final String EXTRA_VENUE_NAME = "venueName";

    private WifiDevice() {
        mBundle = new Bundle();
    }

    public String getSsid() {
        return mBundle.getString(EXTRA_SSID);
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public int getRssi() {
        return mBundle.getInt(EXTRA_RSSI);
    }

    public String getCapabilties() {
        return mBundle.getString(EXTRA_CAPS);
    }

    public String getBssid() {
        return mBundle.getString(EXTRA_BSSID);
    }

    public long getTimestamp() {
        return mBundle.getLong(EXTRA_TIMESTAMP);
    }

    public String getFriendlyName() {
        return mBundle.getString(EXTRA_FRIENDLY_NAME);
    }

    public String getVenueName() {
        return mBundle.getString(EXTRA_VENUE_NAME);
    }

    @Override
    public String toString() {
        return getSsid();
    }

    public static class Builder {
        private final WifiDevice mWifiDevice;

        public Builder() {
            mWifiDevice = new WifiDevice();
        }

        public Builder(Bundle bundle) {
            this();
            mWifiDevice.mBundle.putAll(bundle);
        }

        public Builder(WifiDevice  device) {
            this(device.mBundle);
        }

        public Builder(Builder builder) {
            this(builder.mWifiDevice);
        }

        public Builder setSsid(String ssid) {
            mWifiDevice.mBundle.putString(EXTRA_SSID, ssid);
            return this;
        }

        public Builder setRssi(int rssi) {
            mWifiDevice.mBundle.putInt(EXTRA_RSSI, rssi);
            return this;
        }

        public Builder setCapabilities(String caps) {
            mWifiDevice.mBundle.putString(EXTRA_CAPS, caps);
            return this;
        }

        public Builder setBssid(String bssid) {
            mWifiDevice.mBundle.putString(EXTRA_BSSID, bssid);
            return this;
        }

        public Builder setTimestamp(long timestamp) {
            mWifiDevice.mBundle.putLong(EXTRA_TIMESTAMP, timestamp);
            return this;
        }

        public Builder setFriendlyName(CharSequence name) {
            mWifiDevice.mBundle.putCharSequence(EXTRA_FRIENDLY_NAME, name);
            return this;
        }

        public Builder setVenueName(CharSequence venue) {
            mWifiDevice.mBundle.putCharSequence(EXTRA_VENUE_NAME, venue);
            return this;
        }

        public WifiDevice build() {
            return  new Builder(this).mWifiDevice;
        }


    }
}
