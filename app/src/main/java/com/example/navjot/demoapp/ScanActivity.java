package com.example.navjot.demoapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navjot.demoapp.Discovery.DiscoveryListener;
import com.example.navjot.demoapp.Sample.DetailActivity;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends Activity implements WifiStateNotifier.WifiStateListener {

    private static final String LOG_TAG = "ScanActivity";
    private final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;
    private final Context mContext = this;
    private StableArrayAdapter mAdapter;
    private ListView mScanList;
    private WifiStateNotifier mWifiStateManager;
    private Discovery mDiscovery;
    private DiscoveryEvents mEvents;
    private List<WifiDevice> mWifiDevices;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mWifiStateManager = new WifiStateNotifier(this, this);
        mDiscovery = new Discovery(this);
        mEvents = new DiscoveryEvents();
        mScanList = (ListView) findViewById(R.id.listView);
        mWifiDevices = new ArrayList<>();
        updateList();

        mScanList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(mContext, DetailActivity.class);
                intent.putExtras(mWifiDevices.get(position).getBundle());
                startActivity(intent);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void updateList() {
        mAdapter = new StableArrayAdapter(this, mWifiDevices);
        mScanList.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        mWifiStateManager.startWifiMonitor();
        if (checkPermission() && mWifiStateManager.isEnabled()) {
            mDiscovery.startDiscovery(mEvents, mWifiStateManager.getWifiManager());
            mWifiStateManager.startScan();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "onPause()");
        mDiscovery.stopDiscovery(mEvents);
        mWifiStateManager.stopWifiMonitor();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mDiscovery.close();
        Log.d(LOG_TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onDisabled() {
        Log.d(LOG_TAG, "onDisabled()");
        mScanList.setAdapter(null);
        mDiscovery.stopDiscovery(mEvents);
        mAdapter.notifyDataSetChanged();
        mWifiStateManager.enableWifi();
    }

    @Override
    public void onEnabled() {
        // Update the scan list
        mDiscovery.startDiscovery(mEvents, mWifiStateManager.getWifiManager());
        updateList();
    }

    @Override
    public void onDeclineEnable() {
        Toast t1 = Toast.makeText(mContext, R.string.no_scan_without_wifi, Toast.LENGTH_LONG);
        t1.show();
        finish();
    }

    @Override
    public void onAcceptEnable() {
        Log.d(LOG_TAG, "onAcceptEnable()");
    }

    @Override
    public void onScanResultAvailable(List<ScanResult> scanResults) {
        Log.d(LOG_TAG, "got the result: " + scanResults.size());
        if (mWifiStateManager.isEnabled()) {

            // Clear previous results
            mWifiDevices.clear();

            for (ScanResult result : scanResults) {
                WifiDevice.Builder builder = new WifiDevice.Builder();
                builder.setSsid(result.SSID);
                builder.setBssid(result.BSSID);
                builder.setCapabilities(result.capabilities);
                builder.setTimestamp(result.timestamp);
                builder.setRssi(result.level);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    builder.setFriendlyName(result.operatorFriendlyName);
                    builder.setVenueName(result.venueName);
                }
                WifiDevice device = builder.build();
                mWifiDevices.add(device);
            }
            if (mAdapter != null) {
                mAdapter = new StableArrayAdapter(this, mWifiDevices);
            } else {
                Log.d(LOG_TAG, "mAdapter is null");
            }
            Log.d(LOG_TAG, "mAdapter set");
            mScanList.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    @TargetApi(23)
    private boolean checkPermission() {

        List<String> permissionsList = new ArrayList<>();

        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (permissionsList.size() > 0) {
            this.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                for (int grant : grantResults) {
                    Log.d(LOG_TAG, "granted: " + permissions[grant]);
                }
                if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        || (permissions.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    mWifiStateManager.startWifiMonitor();
                    mDiscovery.startDiscovery(mEvents, mWifiStateManager.getWifiManager());
                    mWifiStateManager.startScan();
                } else {
                    // Permission Denied
                    Toast.makeText(mContext, "Permission denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Scan Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.connect();
        AppIndex.AppIndexApi.start(mClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mClient, getIndexApiAction());
        mClient.disconnect();
    }

    private class StableArrayAdapter extends ArrayAdapter<WifiDevice> {

        final Context mContext;
        final LayoutInflater mInflater;

        StableArrayAdapter(Context context, List<WifiDevice> objects) {
            super(context, R.layout.two_items, objects);
            //super(context, android.R.layout.simple_list_item_2, objects);
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WifiDevice device = getItem(position);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.two_items, parent, false);
            }
            TextView ssid = (TextView) convertView.findViewById(R.id.ssid);
            ssid.setText(device.getSsid());
            TextView level = (TextView) convertView.findViewById(R.id.bssid);
            level.setText(device.getBssid());
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    private class DiscoveryEvents implements DiscoveryListener {

        @Override
        public void onScanStart() {
            Toast.makeText(mContext, "Scan started", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onScanStopped() {
            Toast.makeText(mContext, "Scan stopped", Toast.LENGTH_SHORT).show();
        }
    }
}
