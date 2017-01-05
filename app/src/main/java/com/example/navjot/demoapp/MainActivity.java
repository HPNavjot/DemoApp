package com.example.navjot.demoapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    final int RESULT_ACT_CODE = 1;
    Button mBtnLaunchActivity, mBtnLaunchService, mBtnLaunchResultActivity;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnLaunchActivity = (Button) findViewById(R.id.btnLaunchActivity);
        mBtnLaunchActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScanActivity();
            }
        });

        mBtnLaunchService = (Button) findViewById(R.id.btnLaunchService);
        mBtnLaunchService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myService = new Intent(getApplicationContext(), Discovery.class);
                startService(myService);

            }
        });

        mBtnLaunchResultActivity = (Button) findViewById(R.id.btnLaunchResultActivity);
        mBtnLaunchResultActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myResultActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(myResultActivity,RESULT_ACT_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_ACT_CODE) {
            if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), R.string.pressed_back_btn, Toast.LENGTH_SHORT).show();
            } else if ( resultCode == Activity.RESULT_OK) {
                Toast.makeText(getApplicationContext(), R.string.login_successful, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startScanActivity(){
        Intent mIntent = new Intent(getApplicationContext(), ScanActivity.class);
        mIntent.putExtra("MyData","New activity page");
        startActivity(mIntent);
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        final MyAsyncTask mTask =  new MyAsyncTask();
        Integer max = 100;
        mProgressDialog.setMax(max);
        mTask.execute(max);

        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mProgressDialog.getMax() != mProgressDialog.getProgress()){
                    Toast.makeText(getApplicationContext(), "Cancelled the progressDialog", Toast.LENGTH_SHORT).show();
                    mTask.cancel(true);
                    return;
                }
            }
        });
    }

    class MyAsyncTask extends AsyncTask<Integer,Integer,Integer>{

        @Override
        protected Integer doInBackground(Integer ...params) {
            //Toast.makeText(getApplicationContext(), "Starting the thing", Toast.LENGTH_SHORT).show();
            for (Integer i=0;i<params[0];){
                i++;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Toast.makeText(getApplicationContext(), "Dismissed the progressDialog, starting the activity: " +
                            mProgressDialog.getProgress(), Toast.LENGTH_SHORT).show();
                }
                this.publishProgress(i);
            }
            return 0;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressDialog.setProgress(values[0]);
            if (mProgressDialog.getMax() == values[0]){
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),mProgressDialog.getMax()+", Reached max: "+ values[0],Toast.LENGTH_SHORT).show();
            }

        }
    }
}
