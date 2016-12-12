package com.example.navjot.demoapp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.widget.Toast;
import android.util.Log;

public class DiscoveryService implements AutoCloseable{

    public DiscoveryService() {

    }


    @Override
    public void close() throws Exception {

    }
}
