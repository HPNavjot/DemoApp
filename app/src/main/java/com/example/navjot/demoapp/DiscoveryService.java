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

public class DiscoveryService extends Service {

    WifiStateReceiver mReceiver;
    public DiscoveryService() {

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mReceiver = new WifiStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private class WifiStateReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                if (state == BluetoothAdapter.STATE_ON){
                    Toast.makeText(getApplicationContext(), "BT ON", Toast.LENGTH_SHORT).show();
                }
                else if (state == BluetoothAdapter.STATE_OFF){
                    Toast.makeText(getApplicationContext(), "BT OFF", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
