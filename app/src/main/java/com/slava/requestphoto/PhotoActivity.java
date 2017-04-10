package com.slava.requestphoto;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Slava on 10.04.2017.
 */

public class PhotoActivity extends Activity {

    private static final int PERMISSION_REQUEST_CODE=123;
    private static final int REQUEST_CAMERA = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String PATH = "path";



    private ImageView mImageView;
    String mCurrentPhotoPath;
    Button photoButton;
    Button galleryButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) this.findViewById(R.id.imageView);
        if(savedInstanceState!=null){
            mCurrentPhotoPath = savedInstanceState.getString(PATH);
            Log.d(PATH,""+mCurrentPhotoPath);
        }

        photoButton = (Button) this.findViewById(R.id.buttonCamera);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            TakePhotoActivity.getPhoto(PhotoActivity.this,
                    REQUEST_CAMERA, TakePhotoActivity.SOURCE.CAMERA);
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



}
