package com.example.navjot.demoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.navjot.demoapp.Sample.DetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScanActivity extends Activity {
    final Context mContext = this;
    WifiManager mWifiManager;
    WifiReceiver mReciever;
    ArrayAdapter<String> mListitems;
    List<String> myDynamicList;
    String[] mValues;
    ListView mScanList;
    AlertDialog mDialog;
    private static final String LOG_TAG = "ScanActivity";

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mScanList = (ListView) findViewById(R.id.listView);
        myDynamicList = new ArrayList<>();
        mListitems = new ArrayAdapter<>(this, R.layout.item);
        mValues = new String[]{"Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile"};
        updateList();
        mScanList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "selected position: "+position+", item:"+mValues[position], Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "onCreate: selected position: "+position+", item:"+mValues[position]);

                Intent intent = new Intent();
                intent.setClass(mContext, DetailActivity.class);
                intent.putExtra("EXTRA_NAME", mValues[position]);

                startActivity(intent);
            }
        });
    }

    private void updateList() {
        final ArrayList<String> list = new ArrayList<String>();
        Log.d(LOG_TAG, "updateList()");

        if(mDialog != null && mDialog.isShowing()) {
            Log.d(LOG_TAG, "updateList: dissmiss dialog");
            mDialog.dismiss();
        }

        for (String mValue : mValues) {
            list.add(mValue);
        }

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        mScanList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWifiManager =  (WifiManager) getSystemService(Context.WIFI_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        Log.d(LOG_TAG, "onResume: registerReceiver");
        mReciever = new WifiReceiver();
        registerReceiver(mReciever, filter);
        enableWifi();
    }

    private void enableWifi() {
        Log.d(LOG_TAG, "enableWifi");
        if (!mWifiManager.isWifiEnabled() ){
            Log.d(LOG_TAG, "enableWifi: building  alert");
            if (mDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle(R.string.enable_wifi_title);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.enable_positive_text,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mWifiManager.setWifiEnabled(true);
                                Log.d(LOG_TAG, "enableWifi: set true, calling updateList");
                                //updateList();
                            }
                        });
                builder.setNegativeButton(R.string.enable_negative_text,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast t1 = Toast.makeText(mContext, R.string.no_scan_without_wifi,
                                        Toast.LENGTH_LONG);
                                t1.show();
                                finish();
                            }
                        });
                mDialog = builder.create();
            }
            mScanList.setAdapter(null);
            mDialog.show();
        }
    }

    private class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION == intent.getAction()) {
                Log.d(LOG_TAG, "WifiReceiver: something changed in wifi");
                if (mWifiManager.getWifiState() ==  WifiManager.WIFI_STATE_DISABLED) {
                    enableWifi();
                } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                    if (mDialog != null && mDialog.isShowing()) {
                        Log.d(LOG_TAG, "WifiReceiver: cancel dialog");
                        mDialog.cancel();
                    }
                    Log.d(LOG_TAG, "WifiReceiver: calling updateList");
                    updateList();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "onPause()");
        if (mReciever != null) {
            unregisterReceiver(mReciever);
            Log.d(LOG_TAG, "onPause: unregisterReceiver");
        }
        if (mDialog != null) {
            Log.d(LOG_TAG, "onPause: dismiss dialog");
            mDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy()");
        super.onDestroy();
    }
}
