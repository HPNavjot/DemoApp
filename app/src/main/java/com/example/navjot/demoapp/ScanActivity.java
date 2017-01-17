package com.example.navjot.demoapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.navjot.demoapp.Discovery.DiscoveryListener;
import com.example.navjot.demoapp.Sample.DetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
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
    private List<String> mWifiDeviceNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mWifiStateManager = new WifiStateNotifier(this, this);
        mDiscovery = new Discovery(this);
        mEvents = new DiscoveryEvents();
        mScanList = (ListView) findViewById(R.id.listView);
        mWifiDevices = new ArrayList<>();
        mWifiDeviceNames = new ArrayList<>();
        updateList();

        mScanList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "selected position: " + position + ", item:" +
                        mWifiDeviceNames.get(position));
                Intent intent = new Intent();
                intent.setClass(mContext, DetailActivity.class);
                intent.putExtras(mWifiDevices.get(position).getBundle());
                startActivity(intent);
            }
        });
    }

    private void updateList() {
        mAdapter = new StableArrayAdapter(this, mWifiDeviceNames);
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
        mWifiDeviceNames.clear();
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
        Log.d(LOG_TAG, "got the result: "+scanResults.size());
        if (mWifiStateManager.isEnabled()) {
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
                mWifiDeviceNames.add(device.toString());
            }
            if (mAdapter != null) {
                mAdapter = new StableArrayAdapter(this, mWifiDeviceNames);
            }
            mScanList.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    @TargetApi(23)
    private boolean checkPermission() {

        List<String> permissionsList = new ArrayList<>();

        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (permissionsList.size() > 0) {
            this.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                for(int grant: grantResults)
                    Log.d(LOG_TAG, "granted: "+permissions[grant]);
                if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        (permissions.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                                grantResults[1] == PackageManager.PERMISSION_GRANTED)){
                    mWifiStateManager.startWifiMonitor();
                    mDiscovery.startDiscovery(mEvents, mWifiStateManager.getWifiManager());
                    mWifiStateManager.startScan();
                }
                else {
                    // Permission Denied
                    Toast.makeText(mContext, "Permission denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        final HashMap<String, Integer> mIdMap = new HashMap<>();

        StableArrayAdapter(Context context, List<String> objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    class DiscoveryEvents implements DiscoveryListener {

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
