package com.medroid.acnescanner;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.FilenameFilter;

public class MainActivity extends ActionBarActivity {
    private ImageView tripImage;
    private String mCurrentPhotoPath;
    private String workDir;
    private File imageFileName;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tripImage = (ImageView)this.findViewById(R.id.imageView);
        Button takePhotoButton = (Button)this.findViewById(R.id.pic);

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupCamera();
            }
        });


//        takePhotoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openCamera();
//            }
//        });

    }

    public void openCamera()
    {
        Intent cam = new Intent(this, AndroidCameraExample.class);
        startActivity(cam);
    }
    public void setupCamera()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                mCurrentPhotoPath = photoFile.getPath();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "no space to save the picture", Toast.LENGTH_SHORT).show();
                return;
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                Log.e("Uri: ", "" + Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

            bitmapOptions.inJustDecodeBounds = true;
            int targetW = tripImage.getWidth();
            int targetH = tripImage.getHeight();
            BitmapFactory.decodeFile(mCurrentPhotoPath, bitmapOptions);
            int photoW = bitmapOptions.outWidth;
            int photoH = bitmapOptions.outHeight;
            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
            // Decode the image file into a Bitmap sized to fill the View
            bitmapOptions.inJustDecodeBounds = false;
            bitmapOptions.inSampleSize = scaleFactor;
            bitmapOptions.inPurgeable = true;

            Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bitmapOptions);


            byte[] byteArray=getBytesFromBitmap(imageBitmap);

            // Bitmap imageBitmap = (Bitmap).get("data");
            tripImage.setImageBitmap(imageBitmap);
            tripImage.setRotation(90);


            //if there is previus image compare them in new intent
            String preName = previusImage(workDir);
            Log.i("yesterday image path: ",preName);
            if (preName != null)
            {
                //there in image from yesterday
                Intent intent = new Intent(this,ComapreActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("today",imageFileName.toString());
                bundle.putString("yesterday",preName);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }


    static final String[] EXTENSIONS = new String[]{
            "gif", "jpg", "bmp", "png" // and other formats you need
    };

    // filter to identify images based on their extensions
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

    public String previusImage(String path)
    {
        File dir = new File(path);
        File list[];
        String pre = null;
        if (dir.isDirectory()) { // make sure it's a directory
            list = dir.listFiles(IMAGE_FILTER);
            if (list.length <= 1)   //if there is one image this is the image just take
                return null;
            pre = list[0].toString();
            if (pre.compareTo(imageFileName.toString()) == 0)  //prevent take the image just taken
                pre = list[1].toString();
            for (final File f : list) {
                if (f.toString().compareTo(pre) > 0 && f.compareTo(imageFileName)!= 0)
                    pre = f.toString();
            }
            return pre;
        }
        return null;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "JPEG_" + timeStamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES);
        File storageDir = getApplicationContext().getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);
        workDir = storageDir.toString();
        File image = File.createTempFile(
                imageName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        Log.e("image_name = " ,image.getName());
        imageFileName = image;
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    // convert from bitmap to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }


    private Camera openFrontFacingCameraGingerbread() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("info: ","Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
