package org.androidluckyguys;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static String filePath = null;
    public static final String IMAGE_NAME = "FinalImageWithWaterMark";
    private Button captureImageBtn;
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private Uri fileUri; // file url to store image/video
    ImageView parentImageIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureImageBtn = (Button)findViewById(R.id.captureImageBtn);
        captureImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                captureImage();

            }
        });


        checkPermssions();



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activitysdf

                filePath = fileUri.getPath();

                addImageWaterMark();

                    Bitmap bmp = BitmapFactory.decodeFile(filePath);
                    ImageView manualCaptureIv = (ImageView) findViewById(R.id.imageView);
                    manualCaptureIv.setVisibility(View.VISIBLE);
                    manualCaptureIv.setImageBitmap(bmp);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.user_cancelled_image_capture), Toast.LENGTH_SHORT).show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.sorry_failed_to_capture_image), Toast.LENGTH_SHORT).show();
            }
        }


    }



    private void addImageWaterMark() {
        Bitmap bitmap = null;

        try {
            filePath = getFilePath();

            File f = new File(filePath);
            if (f.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                try
                {
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
                    Bitmap output = AppUtils.addWatermark(getResources(), bitmap);
                    /*save image to sdcard*/
                    saveImage(output, IMAGE_NAME);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void checkPermssions() {
        int permission = ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("grant", "Storage Permission denied");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.permission_title))
                        .setTitle(getString(R.string.permission_msg));

                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("grant", "Clicked");
                        makeRequest();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {

                makeRequest();
            }
        }


         permission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("grant", "Storage Permission denied");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.permission_title))
                        .setTitle(getString(R.string.permission_msg));

                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("grant", "Clicked");
                        makeRequest();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {

                makeCameraPermissionRequest();
            }
        }







    }



    protected void makeCameraPermissionRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA},
                500);
    }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                500);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 500: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    Log.i("1", "Permission has been denied by user");

                } else {

                    Log.i("1", "Permission has been granted by user");

               //     addImageWaterMark();

                }
                return;
            }

        }
    }
    private String getFilePath() {

        String fileNameWithFullPath = null;
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot, "WaterMarkImages");
        for (File f : yourDir.listFiles()) {
            if (f.isFile()) {
                String name = f.getName();
                Log.i("file names", name);

                if (name.contains("yuv")) {
                    fileNameWithFullPath = "/storage/emulated/0/WaterMarkImages/" + name;
                }

            }

        }

        return fileNameWithFullPath;
    }
    private void saveImage(Bitmap finalBitmap, String image_name) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root+"/WaterMarkImages/");

        filePath = root+"/WaterMarkImages/"+image_name+".jpg";

        myDir.mkdirs();
        String fname = image_name+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }
    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File("/storage/emulated/0/", "WaterMarkImages");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create  directory");
                return null;
            }
        }


        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "yuv"+ ".jpg");
        }  else {
            return null;
        }

        return mediaFile;
    }


}
