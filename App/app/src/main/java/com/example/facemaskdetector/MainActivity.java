package com.example.facemaskdetector;

import android.app.AlertDialog;
//import android.content.Context;
import android.content.ClipData;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import android.support.design.widget.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.List;
//import android.widget.Toast;
//import android.support.constraint.solver.widgets.*;
//import androidx.appcompat.widget.Toolbar;



//import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    // request the codes for camera identification and permission requests

    /*
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1000;
    private static final int CAMERA_REQUEST_CODE = 10001;
    */

    // User Interface elements
    /* private ImageView imageView; */

    ImageView imageView;
    Button b;
    //Button b, bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b = (Button) findViewById(R.id.bSelect);
        //bt = (Button) findViewById(R.id.bSelectDataset);
        imageView = (ImageView) findViewById(R.id.imageView);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        /*bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
            }
        }); */
    }

    /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // to initialize the user interface aspects(front end design)
        initializeUIElements();
    }
    private void initializeUIElements() {
        imageView = findViewById(R.id.imageView);
        Button takeapicture = findViewById(R.id.bTakePicture);
        // Button uploaddataset = findViewById(R.id.bUploadDataset);

        // clicklistener added to button to check for camera permission availability
        takeapicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasPermission()){ // access method to check if camera permissions can be requested
                    openCamera(); } // permission granted then access openCamera method
                    else{ // if it doesn't have permission
                        requestPermission(); // then access method to request for permission
                    }

            }
        });
    } */


    /*@Override
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data){
        if (requestCode == CAMERA_REQUEST_CODE) { //if code is equal to camera request code
            // get bitmap of image
            Bitmap photo = (Bitmap)
         Objects.requireNonNull(Objects.requireNonNull(data).getExtras()).get("data");
            // bitmap is set as imageView
            imageView.setImageBitmap(photo);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (hasAllPermissions(grantResults)) {
                openCamera(); //access openCamera method
                }
                else{
                    requestPermission(); // access the method to request camera permissions
                }
        }
    } */


    /* class to checked if all required permissions are granted or not
     * return false if result is permission denied
     * else return true that all permissions have been granted
     */


    /*private boolean hasAllPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED)
                return false;
        }
        return true;
    } */

    // if android version is marshmallow or greater then request permission
    /*
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // checking if camera permission can be requested or not
            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Camera Permission Necessary", Toast.LENGTH_SHORT).show();
            }
            // Requesting permission code from camera permission
            requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_REQUEST_CODE);
        }
    } */

    // built in cameraIntent to take a picture from the camera


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    } */


    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //startActivityForResult(intent, 2);
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setType("image/*");
                    //startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();

                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                //if (requestCode == 1) {
                Bitmap photo = (Bitmap)
                        Objects.requireNonNull(Objects.requireNonNull(data).getExtras()).get("data");
                // bitmap is set as imageView
                imageView.setImageBitmap(photo);

                // To select one image, click choose from gallery and click gallery
                // To select multiple images, click choose from gallery and click images and hold down and choose images
            } else if (requestCode == 2) {

                /* Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w("path of image from gallery", picturePath+"");
                imageView.setImageBitmap(thumbnail); */

                final ImageView imageView = findViewById(R.id.imageView);
                final List<Bitmap> bitmaps = new ArrayList<>();
                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        try {
                            InputStream is = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            bitmaps.add(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Uri imageUri = data.getData();
                    try {
                        InputStream is = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        bitmaps.add(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (final Bitmap b : bitmaps) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(b);
                                }
                            });
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }

        }
    }
}



    /* private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    } */


    /* checking whether camera has permission or not
     * if android version is less than marshmallo version then return true
     * else returns if permission of camera is equal to granted or not
     */
    /*private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    } */

