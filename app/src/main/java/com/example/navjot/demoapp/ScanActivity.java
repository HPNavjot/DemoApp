package com.example.navjot.demoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.navjot.demoapp.Sample.DetailActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ScanActivity extends Activity implements WifiStateNotifier.WifiStateListener {
    private static final String LOG_TAG = "ScanActivity";
    private final Context mContext = this;
    private String[] mValues;
    private ListView mScanList;
    private WifiStateNotifier mWifiStateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mWifiStateManager = new WifiStateNotifier(this, this);
        mScanList = (ListView) findViewById(R.id.listView);
        mValues = new String[]{"Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile"};
        updateList();
        mScanList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "onCreate: selected position: " + position + ", item:" +
                        mValues[position]);

                Intent intent = new Intent();
                intent.setClass(mContext, DetailActivity.class);
                intent.putExtra("EXTRA_NAME", mValues[position]);

                startActivity(intent);
            }
        });
    }

    private void updateList() {
        final ArrayList<String> list = new ArrayList<>();
        Log.d(LOG_TAG, "updateList()");
        Collections.addAll(list, mValues);

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                list);
        mScanList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        mWifiStateManager.startWifiMonitor();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "onPause()");
        mWifiStateManager.stopWifiMonitor();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onDisabled() {
        Log.d(LOG_TAG, "onDisabled()");
        mScanList.setAdapter(null);
        mWifiStateManager.enableWifi();
    }

    @Override
    public void onEnabled() {
        // Update the scan list
        updateList();
    }

    @Override
    public void onDeclineEnable() {
        Toast t1 = Toast.makeText(mContext, R.string.no_scan_without_wifi,
                Toast.LENGTH_LONG);
        t1.show();
        finish();
    }

    @Override
    public void onAcceptEnable() {
        Log.d(LOG_TAG, "onAcceptEnable()");
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        final HashMap<String, Integer> mIdMap = new HashMap<>();

        StableArrayAdapter(Context context,
                List<String> objects) {
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
}
