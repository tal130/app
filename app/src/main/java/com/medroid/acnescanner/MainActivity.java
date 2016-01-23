package com.medroid.acnescanner;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.FilenameFilter;

import com.google.android.gms.analytics.Tracker;

public class MainActivity extends BaseActivity {
    private ImageView faceImage;
    private ImageView faceImage2;

    public static final  SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private String mCurrentPhotoPath;
    private String workDir;
    private File imageFileName;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the shared Tracker instance.
        Myapplication application = (Myapplication) getApplication();
        mTracker = application.getDefaultTracker();

        //setContentView(R.layout.activity_main);
        ParseObject image = new ParseObject("images");


        /**
         * search for the last image and check if from today
         */
        ParseQuery<ParseObject> query = ParseQuery.getQuery("images");
        query.fromLocalDatastore();
        query.orderByDescending("date");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

                if (e == null) {
                    // Results were successfully found from the local datastore.
                    Date d = new Date();
                    String s = DATE_FORMAT.format(d);
                    s = s.substring(0, 11) + "00:00:00";
                    Log.i("today date: " , s);
                    try {
                        Log.i("today date: " , object.getString("date"));
                        if (DATE_FORMAT.parse(object.getString("date")).after(DATE_FORMAT.parse(s))) {
                            setComparePage();
                        } else {
                            //no picture taken today
                            setMainPage();

                        }
                    } catch (java.text.ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    // show main page. no pictures taken today
                    setMainPage();
                }
            }
        });

        // this will open our camera app
        // its working but when you take picture its automaticly save on the device. need to add preview
//        takePhotoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openCamera();
//            }
//        });

    }

    public void setComparePage()
    {
        Intent intent = new Intent(this,ComapreActivity.class);
        startActivity(intent);
    }


    public void setMainPage()
    {
        setContentView(R.layout.activity_main);


        faceImage = (ImageView) this.findViewById(R.id.imageView);
        Button takePhotoButton = (Button) this.findViewById(R.id.pic);


        //this will open galaxy camera
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupCamera();
            }
        });
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
            //TODO some times crash here "java.lang.RuntimeException: Failure delivering result ResultInfo{who=null, request=1, result=-1, data=null} to activity {com.medroid.acnescanner/com.medroid.acnescanner.MainActivity}: java.lang.NullPointerException"
            int targetW = 0;
            int targetH = 0;
            try {
                targetW = faceImage.getWidth();
                targetH = faceImage.getHeight();
            }catch (Exception e)
            {
                Log.e("RuntimeException","");
                return;
            }
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
            faceImage.setImageBitmap(imageBitmap);
            faceImage.setRotation(-90); // TODO when save image the image was roteted. need to fix to diffrent phones


            // save image to parse
            saveToParse(mCurrentPhotoPath);

            //show compare activity
            Intent intent = new Intent(this,ComapreActivity.class);
            startActivity(intent);
        }
    }

    public void saveToParse(String filePath)
    {
        ParseObject image = new ParseObject("images");
        Date d = new Date();
        String dateString = DATE_FORMAT.format(d);
        image.put("path", filePath);
        image.put("date", dateString);
        image.pinInBackground();
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


    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);

        mTracker.setScreenName("MainActivity~");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to track on much time people spend in the activity
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
