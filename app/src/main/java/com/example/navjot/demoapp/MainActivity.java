package com.example.navjot.demoapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    Button mBtnLaunchActivity, mBtnLaunchService;
    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnLaunchActivity = (Button) findViewById(R.id.btnLaunchActivity);
        mBtnLaunchActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
            }
        });

        mBtnLaunchService = (Button) findViewById(R.id.btnLaunchService);
        mBtnLaunchService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Clicked on launch service btn", Toast.LENGTH_SHORT).show();
                Intent myService = new Intent(getApplicationContext(), MyService.class);
                startService(myService);

            }
        });
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
                Intent mIntent = new Intent(getApplicationContext(), MyActivity.class);
                mIntent.putExtra("MyData","New activity page");
                startActivity(mIntent);

            }
        });

    }

    class MyAsyncTask extends AsyncTask<Integer,Integer,Integer>{

        @Override
        protected Integer doInBackground(Integer ...params) {
            //Toast.makeText(getApplicationContext(), "Starting the thing", Toast.LENGTH_SHORT).show();
            for (Integer i=0;i<params[0];){
                i++;
                try{
                    Thread.sleep(10);
                }catch (InterruptedException e){
                    Toast.makeText(getApplicationContext(), "Dismissed the progressDialog, starting the activity: "+mProgressDialog.getProgress(), Toast.LENGTH_SHORT).show();
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
