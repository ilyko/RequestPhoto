package com.slava.requestphoto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by Slava on 10.04.2017.
 */

public class PhotoActivity extends Activity {

    private static final int PERMISSION_REQUEST_CODE=123;
    private static final int REQUEST_CAMERA = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String PATH = "path";
//    private static final String PATH = "path";



    private ImageView mImageView;
    String mCurrentPhotoPath;
    Button photoButton;
    Button galleryButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) this.findViewById(R.id.imageView);

        photoButton = (Button) this.findViewById(R.id.buttonCamera);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            TakePhotoActivity.getPhoto(PhotoActivity.this,
                    REQUEST_CAMERA, TakePhotoActivity.SOURCE.CAMERA);

                Log.d("HEY PHOTOACTIVITYRESULT","path "+mCurrentPhotoPath);


            }
        });

        galleryButton = (Button) this.findViewById(R.id.buttonGallery);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePhotoActivity.getPhoto(PhotoActivity.this,
                        RESULT_LOAD_IMAGE, TakePhotoActivity.SOURCE.GALLERY);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if(resultCode == Activity.RESULT_OK){
                mCurrentPhotoPath=data.getStringExtra(TakePhotoActivity.SOURCE.CAMERA.name());

                //test
                loadImageFromFile();
                //galleryAddPic();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        if (requestCode == RESULT_LOAD_IMAGE) {
            if(resultCode == Activity.RESULT_OK){
                mCurrentPhotoPath=data.getStringExtra(TakePhotoActivity.SOURCE.GALLERY.name());

                //test
                loadImageFromFile();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void loadImageFromFile(){

        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        Log.d("loadImageFromFile","View w" + targetW);
        Log.d("loadImageFromFile","View h" + targetH);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        Log.d("loadImageFromFile","Photo w" + photoW);
        Log.d("loadImageFromFile","Photo h" + photoH);

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}
