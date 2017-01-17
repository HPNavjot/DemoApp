package com.example.navjot.demoapp;

import android.os.Bundle;

public class WifiDevice {
    private Bundle mBundle;
    public static final String EXTRA_SSID = "ssid";
    public static final String EXTRA_RSSI = "rssi";
    public static final String EXTRA_CAPS = "capabilities";
    public static final String EXTRA_BSSID = "bssid";
    public static final String EXTRA_TIMESTAMP = "timestamp";
    public static final String EXTRA_FRIENDLY_NAME = "friendlyName";
    public static final String EXTRA_VENUE_NAME = "venueName";

    WifiDevice() {
        mBundle = new Bundle();
    }

    public String getSsid() {
        return mBundle.getString(EXTRA_SSID);
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public long getRssi() {
        return mBundle.getLong(EXTRA_RSSI);
    }

    public long getCapabilties() {
        return mBundle.getLong(EXTRA_CAPS);
    }

    public long getBssid() {
        return mBundle.getLong(EXTRA_BSSID);
    }

    public long getTimestamp() {
        return mBundle.getLong(EXTRA_TIMESTAMP);
    }

    public long getFriendlyName() {
        return mBundle.getLong(EXTRA_FRIENDLY_NAME);
    }

    public long getVenueName() {
        return mBundle.getLong(EXTRA_VENUE_NAME);
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
