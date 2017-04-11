package com.slava.requestphoto;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by Slava on 07.04.2017.
 */

public class TakePhotoActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE=123;
    private static final int REQUEST_CAMERA = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String PATH = "path";
    private static final String TAG = "myTag";

    private ImageView mImageView;
    String mCurrentPhotoPath;
    Button photoButton;
    Button galleryButton;

    public enum SOURCE {CAMERA, GALLERY}
    private SOURCE source;

    public static void getPhoto(Activity activity, int resultCode, SOURCE source){
        Intent i = new Intent(activity, TakePhotoActivity.class);
        i.putExtra(SOURCE.class.getCanonicalName(), source.name());
        i.putExtra("code", resultCode);
        activity.startActivityForResult(i, resultCode);
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Saved: " + savedInstanceState);

        if (savedInstanceState==null) {

            String choice = getIntent().getStringExtra(SOURCE.class.getCanonicalName());

            if (choice.equals(SOURCE.CAMERA.name())) {
                source = SOURCE.CAMERA;
                //takePicture();
                requestPermission(REQUEST_CAMERA);
            } else if (choice.equals(SOURCE.GALLERY.name())){
                source = SOURCE.GALLERY;
                //getPictureFromGallery();
                requestPermission(RESULT_LOAD_IMAGE);
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PATH,mCurrentPhotoPath);
        outState.putString(SOURCE.class.getCanonicalName(), source.name());
        Log.d("string", ""+mCurrentPhotoPath);
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)        {
        if (resultCode!=RESULT_OK) {
            setResult(resultCode);
            finish();
            return;
        }

        switch (requestCode){
            case REQUEST_CAMERA:
                Intent intentCam= new Intent();
                intentCam.putExtra(SOURCE.CAMERA.name(), mCurrentPhotoPath);
                setResult(RESULT_OK, intentCam);
                finish();
            //loadImageFromFile();
            //galleryAddPic();
         break;

            case RESULT_LOAD_IMAGE:
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mCurrentPhotoPath = cursor.getString(columnIndex);
                    Log.i("PATH", ""+ mCurrentPhotoPath);
                    cursor.close();}
                Intent intentGal= new Intent();
                Log.d("path gallery", ""+mCurrentPhotoPath);
                intentGal.putExtra(SOURCE.GALLERY.name(), mCurrentPhotoPath);
                setResult(RESULT_OK, intentGal);
                finish();
         break;
        }
    }

/*    public void loadImageFromFile(){

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
    }*/


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath =  image.getAbsolutePath();
        return image;
    }
/*    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }*/

    private void getPictureFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    public void requestPermission(int requestCode) {
        switch(requestCode){
            case REQUEST_CAMERA:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                                REQUEST_CAMERA);
                    } else {
                        takePicture();
                    }
                } else {
                    takePicture();
                }
                break;

            case RESULT_LOAD_IMAGE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                RESULT_LOAD_IMAGE);
                    } else {
                        getPictureFromGallery();
                    }
                } else {
                    getPictureFromGallery();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    takePicture();
                } else {
                    finish();
                }
                break;

            case RESULT_LOAD_IMAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getPictureFromGallery();
                } else {
                    finish();
                }
                break;
        }
    }




/*    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }*/

}
