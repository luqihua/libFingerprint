package com.lu.fingerprint;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private FingerprintDialog fingerprintDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharePreferenceTool.getInstance().init(this);
        init();
        initView();
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }


    private void initView() {
        if (Build.VERSION.SDK_INT >= 23) {
            fingerprintDialog = new FingerprintDialog(this);
        }
    }

    private void init() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void encrypt(View v) {
       fingerprintDialog.encrypt();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void decrypt(View v) {
        fingerprintDialog.decrypt();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void xml(View v) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
