package com.semsix.android.hindiocr;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.sax.StartElementListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Declaring Widgets
    Button btn;

    //isPermissionGranted to grant permission
    boolean isPermissionGranted;
    private static final int WRITE_PERMISSION=1;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        //Initializing Widgets
        btn = (Button) findViewById(R.id.btn);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted=false;
        }
        else{
            isPermissionGranted=true;
        }

        //Defining Click Events on "btn"
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating and launching intent for Main2Activity
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                            AlertDialog.Builder alertBuilder=new AlertDialog.Builder(MainActivity.this);
                            alertBuilder.setCancelable(true);
                            alertBuilder.setIcon(R.drawable.storage);
                            alertBuilder.setMessage("We need permission to read/write the images");
                            alertBuilder.setTitle("PERMISSION");
                            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);
                                }
                            });
                            alertBuilder.create().show();
                        }
                        else{
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);
                        }
                    }
                }
                if (isPermissionGranted){
                    Intent intnt = new Intent(MainActivity.this, Main2Activity.class);
                    startActivity(intnt);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_PERMISSION:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    isPermissionGranted=true;
                    Intent intnt = new Intent(MainActivity.this, Main2Activity.class);
                    startActivity(intnt);
                }
                break;
        }
    }
}