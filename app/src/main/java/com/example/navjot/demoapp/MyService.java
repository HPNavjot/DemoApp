package com.example.navjot.demoapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
    private final String TAG = "MyService";
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "My Service on Create", Toast.LENGTH_SHORT).show();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service on Destroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service has started", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Service on Start Command");

        try{
            Log.d(TAG, "Service sleeping");
            for (int i=0; i<10;) {
                i++;
                Log.d(TAG, "i="+i);
                Thread.sleep(100);
            }
            Log.d(TAG, "Service woke up1");
        }catch (InterruptedException e){}
        this.onDestroy();
        return super.onStartCommand(intent, flags, startId);
    }
}
