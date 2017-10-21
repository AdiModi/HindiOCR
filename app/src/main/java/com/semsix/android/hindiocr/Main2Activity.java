package com.semsix.android.hindiocr;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Main2Activity extends AppCompatActivity {

    //Declaring Widget Objects
    ImageView disp_img;
    ImageButton snap_btn, mail_btn;
    Button ref_btn;
    Spinner takeNum;

    //Declaring Image Objects
    Bitmap captured_img, final_img;

    //Declaring Intent Request Codes
    private static final int CAMERA_REQUEST=0, REFER_REQUEST_CODE=1;

    //Status Bits
    boolean hasCamera=false;

    //Declaring Variables
    String fileName="", number="";
    String []numbers;

    //Declaring File Variables
    File folderHindiOCR, rootPath, folderNumber;
    FileOutputStream imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        initialize();

        //Checking for the System for Camera
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            hasCamera=true;

        //Setting onClick Event to Launch the Camera Activity for Result
        snap_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasCamera)
                    Toast.makeText(Main2Activity.this, "Sorry, but your Phone doesn't have a Camera", Toast.LENGTH_SHORT).show();
                else {
                    Intent cam_intnt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cam_intnt, CAMERA_REQUEST);
                }
            }
        });

        takeNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                number=adapterView.getItemAtPosition(i).toString();
                long time= System.currentTimeMillis();
                fileName="IMG_"+number+"_"+time+".png";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Setting onClick Event to send a Mail
        mail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sendMail()){
                    Toast.makeText(Main2Activity.this, "Try Again", Toast.LENGTH_SHORT).show();
                }
                else{
                    finish();
                }
            }
        });

        ref_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intnt=new Intent(Main2Activity.this, Main3Activity.class);
                startActivityForResult(intnt, REFER_REQUEST_CODE);
            }
        });
    }

    public void initialize(){
        disp_img=(ImageView)findViewById(R.id.disp_img);
        snap_btn=(ImageButton)findViewById(R.id.snap_btn);
        mail_btn=(ImageButton)findViewById(R.id.mail_btn);
        ref_btn=(Button)findViewById(R.id.ref_btn);
        takeNum=(Spinner) findViewById(R.id.takeNum);

        folderNumber=folderHindiOCR=null;

        fileName=number=null;
        numbers=new String[]{"0","1","2","3","4","5","6","7","8","9"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, numbers);
        takeNum.setAdapter(adapter);

        mail_btn.setVisibility(View.INVISIBLE);
        takeNum.setVisibility(View.INVISIBLE);
    }

    public boolean sendMail(){
        boolean flag=false;
        if(!saveFile()){
            Toast.makeText(this, "Please Update Storage Permissions", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent mail_intent = new Intent(Intent.ACTION_SEND);
            mail_intent.setType("*/*");
            mail_intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"HindiOCR@gmail.com"});
            mail_intent.putExtra(Intent.EXTRA_SUBJECT, new String("Digit: " + number));
            mail_intent.putExtra(Intent.EXTRA_TEXT, new String("I am Willing to Contibute to Your OCR Database"));
            mail_intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+folderNumber.getAbsolutePath()+"/"+fileName));
            startActivity(Intent.createChooser(mail_intent, "Please select mail"));
            flag=true;
        }
        return flag;
    }

    public boolean saveFile(){
        boolean flag=false;

        rootPath=Environment.getExternalStorageDirectory();
        folderHindiOCR=new File(rootPath, "HindiOCR");
        if(!folderHindiOCR.exists()){
            folderHindiOCR.mkdir();
        }
        for (int i=0;i<10;i++) {
            folderNumber=new File(folderHindiOCR, numbers[i]);
            if (!folderNumber.exists()){
                folderNumber.mkdir();
            }
        }

        folderNumber=new File(folderHindiOCR, number);

        try {
            File temp=new File(folderNumber, fileName);
            if(temp.exists()){
                temp.delete();
            }
            imgPath=new FileOutputStream(folderNumber.getAbsolutePath()+"/"+fileName);
            final_img.compress(Bitmap.CompressFormat.PNG, 100, imgPath);
            flag=true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return flag;
    }

    //Cropping and Applying Filters to the Image
    public Bitmap applyFilters(Bitmap captured_img){
        int width=captured_img.getWidth();
        int height=captured_img.getHeight();
        Bitmap img=Bitmap.createBitmap(captured_img, 0, (int)((height-width)/2), width, width);
        float[] hsv = new float[3];
        for( int col = 0; col < img.getWidth(); col++ ) {
            for( int row = 0; row < img.getHeight(); row++ ) {
                Color.colorToHSV (img.getPixel(col,row ), hsv);
                if( hsv[1] > 0.085f ) {
                    img.setPixel(col, row, 0xffffffff);
                } else {
                    img.setPixel(col, row, 0xff000000);
                }
            }
        }
        return img;
    }

    //Getting return from Camera Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==CAMERA_REQUEST && resultCode==RESULT_OK){
            Bundle extras=data.getExtras();
            captured_img=(Bitmap)extras.get("data");

            final_img=applyFilters(captured_img);

            disp_img.setImageBitmap(final_img);
            snap_btn.setVisibility(View.INVISIBLE);
            takeNum.setVisibility(View.VISIBLE);
            mail_btn.setVisibility(View.VISIBLE);
        }
        else if(requestCode!=REFER_REQUEST_CODE){
            Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
        }
    }
}