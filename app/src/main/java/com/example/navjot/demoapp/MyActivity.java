package com.example.navjot.demoapp;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;

public class MyActivity extends Activity {
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final EditText mTextView = (EditText) findViewById(R.id.editText);
        mTextView.setText(getIntent().getExtras().getString("MyData"));
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.getText().clear();
                enableWifi();
            }
        });
        enableWifi();
    }

    private void enableWifi(){
        final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Enable WiFi");
            builder.setCancelable(true);
            builder.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    wifiManager.setWifiEnabled(true);
                }
            });
            builder.setNegativeButton("May Later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast t1 = Toast.makeText(context,"Oops can't work without Wifi", Toast.LENGTH_LONG);
                    t1.show();
                    finish();
                }
            });
            AlertDialog mDialog = builder.create();
            mDialog.show();

        }
    }

}
