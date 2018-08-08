package com.mc.virtuali;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.system.ErrnoException;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import com.theartofdev.edmodo.cropper.CropImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.THRESH_TOZERO;
import static org.opencv.imgproc.Imgproc.threshold;

/**
 * Created by adc on 5/4/18.
 */

public class Screen1 extends AppCompatActivity {

    List<Intent> allIntents = new ArrayList<>();
    Button extract;
    public int wid, high;
    ImageButton rotate_left,rotate_right;
    int CAMERA_REQUEST=1888,GALLERY_REQUEST=1000;
    public int rotate_img = 0;
    private TessOCR mTessOCR;
    public Bitmap captureBmp;
    private Uri mCropImageUri;
    private CropImageView mCropImageView;
    private ProgressDialog mProgressDialog;
    public static final String lang = "eng";
    private static final String TAG = Screen1.class.getSimpleName();
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString()+"/Tess";
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    //    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if (requestCode == CAMERA_REQUEST) {
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                mCropImageView.setImageBitmap(photo);

                final File file = getTempFile(this);
                try {
                    captureBmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file) );
//                    Bitmap bit = rotate(captureBmp);

                    //////////////////////////////////////////////////////////////OPENCV////////////////////////////

                    Log.d("OCR","heyy1");
                    Mat imageMat = new Mat();
                    Utils.bitmapToMat(captureBmp, imageMat);
                    Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGR2GRAY);
                    threshold(imageMat, imageMat, 130, 255, THRESH_BINARY);
                    //Photo.fastNlMeansDenoising(imageMat, imageMat);

                    //Imgproc.adaptiveThreshold(imageMat, imageMat, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 15, 40);
                    Utils.matToBitmap(imageMat, captureBmp);
                    wid=captureBmp.getWidth();
                    high=captureBmp.getHeight();

                    mCropImageView.setImageBitmap(captureBmp);
                    // do whatever you want with the bitmap (Resize, Rename, Add To Gallery, etc)
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            if(requestCode == GALLERY_REQUEST){
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = getPickImageResultUri(data);

                    // For API >= 23 we need to check specifically that we have permissions to read external storage,
                    // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
                    boolean requirePermissions = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                            isUriRequiresPermissions(imageUri)) {

                        // request permissions and handle the result in onRequestPermissionsResult()
                        requirePermissions = true;
                        mCropImageUri = imageUri;
                        Bitmap bmp=uriToBitmap(mCropImageUri);
                        Mat imageMat = new Mat();
                        Utils.bitmapToMat(bmp, imageMat);
                        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGR2GRAY);
                        threshold(imageMat, imageMat, 130, 255, THRESH_BINARY);
                        //Photo.fastNlMeansDenoising(imageMat, imageMat);

                        //Imgproc.adaptiveThreshold(imageMat, imageMat, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 15, 40);
                        Utils.matToBitmap(imageMat, bmp);
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    }

                    if (!requirePermissions) {
                        mCropImageUri = imageUri;
                        Bitmap bmp=uriToBitmap(mCropImageUri);
                        Mat imageMat = new Mat();
                        Utils.bitmapToMat(bmp, imageMat);
                        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGR2GRAY);
                        threshold(imageMat, imageMat, 130, 255, THRESH_BINARY);
                        //Photo.fastNlMeansDenoising(imageMat, imageMat);

                        //Imgproc.adaptiveThreshold(imageMat, imageMat, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 15, 40);
                        Utils.matToBitmap(imageMat, bmp);

                        wid=bmp.getWidth();
                        high=bmp.getHeight();
                        mCropImageView.setImageBitmap(bmp);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mCropImageView.setImageUriAsync(mCropImageUri);
        } else {
            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCameraActivity() {
        try{
            String imagePath = DATA_PATH + "/imgs";
            File dir = new File(imagePath);
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen1);

        extract = findViewById(R.id.extract);
        rotate_left = findViewById(R.id.rotate_left);
        rotate_right = findViewById(R.id.rotate_right);
        mCropImageView = findViewById(R.id.CropImageView);
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };
        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        } else {
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v("Main", "ERROR: Creation of directory " + path + " on sdcard failed");
                    break;
                } else {
                    Log.v("Main", "Created directory " + path + " on sdcard");
                }
            }

        }
        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = getAssets();

                InputStream in = assetManager.open(lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                // Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                // Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }


        }
        mTessOCR =new TessOCR();

        Bundle bundle = getIntent().getExtras();
        String op = bundle.getString("option");
        Intent option = null;

        if(op.equals("camera")){
//            Uri outputFileUri = getCaptureImageOutputUri();

//            option = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(option, 200);

//            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(i,200);
//
//            startCameraActivity();
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(getTempFile(this)));
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
        else
        if(op.equals("gallery")){
            PackageManager packageManager = getPackageManager();

            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
            for (ResolveInfo res : listGallery) {
                Intent intent = new Intent(galleryIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(res.activityInfo.packageName);
                allIntents.add(intent);
            }
            Intent mainIntent = allIntents.get(allIntents.size() - 1);
            for (Intent intent : allIntents) {
                if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                    mainIntent = intent;
                    break;
                }
            }
            allIntents.remove(mainIntent);

            // Create a chooser from the main intent
            Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

            // Add all other intents
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

            option = chooserIntent;
            startActivityForResult(option, GALLERY_REQUEST);
        }

        rotate_left.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                rotate_img = rotate_img - 90;
                Bitmap bmp = rotate(captureBmp,rotate_img);
//                if(rotate_img%180 == 0)
//                {
//                    bmp.setHeight(high);
//                    bmp.setWidth(wid);
//                }
                mCropImageView.setImageBitmap(bmp);
            }
        });

        rotate_right.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                rotate_img = rotate_img + 90;
                Bitmap bmp = rotate(captureBmp,rotate_img);
//                if(rotate_img%180 == 0)
//                {
//                    bmp.setHeight(high);
//                    bmp.setWidth(wid);
//                }
                mCropImageView.setImageBitmap(bmp);
            }
        });

        extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.screen2);

                Bitmap cropped = mCropImageView.getCroppedImage(500, 500);
                if (cropped != null){
                    mCropImageView.setImageBitmap(cropped);
                }
                else
                    Log.i("null ","hai :p");
                //mImage.setImageBitmap(converted);
                doOCR(convertColorIntoBlackAndWhiteImage(cropped) );
            }
        });

    }

    public Bitmap rotate(Bitmap myBitmap, int deg){
        Log.i("hi","there");
        Display d = getWindowManager().getDefaultDisplay();
        int x = d.getWidth();
        int y = d.getHeight();

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(myBitmap, y, x, true);

        Matrix matrix = new Matrix();
        matrix.postRotate(deg); // anti-clockwise by 90 degrees
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap.getWidth(),
                scaledBitmap .getHeight(), matrix, true);
//        mCropImageView.setImageBitmap(rotatedBitmap);
        return rotatedBitmap;
    }

    private File getTempFile(Context context){
        //it will return /sdcard/image.tmp
        final File path = new File( Environment.getExternalStorageDirectory(), context.getPackageName() );
        if(!path.exists()){
            path.mkdir();
        }
        return new File(path, "image.tmp");
    }

    public void run_ocr(){
        setContentView(R.layout.screen2);

        Bitmap cropped = mCropImageView.getCroppedImage(500, 500);
        if (cropped != null)
            mCropImageView.setImageBitmap(cropped);

        //mImage.setImageBitmap(converted);
        doOCR(convertColorIntoBlackAndWhiteImage(cropped) );
    }

    public void back_btn(){
        finish();
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (e.getCause() instanceof ErrnoException) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public void doOCR(final Bitmap bitmap) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, "Processing",
                    "Please wait...", true);
            // mResult.setVisibility(V.ViewISIBLE);


        }
        else {
            mProgressDialog.show();
        }

        new Thread(new Runnable() {
            public void run() {

                final String result = mTessOCR.getOCRResult(bitmap).toLowerCase();


                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (result != null && !result.equals("")) {
                            String s = result.trim();
//                            text.setText(result);
                            Log.i("screen1",s);

                            Intent intent = new Intent(getApplicationContext(),Screen2.class);
                            intent.putExtra("text",s);
                            startActivity(intent);


                        }

                        mProgressDialog.dismiss();
                    }

                });

            };
        }).start();
    }

    public void onCropImageClick(View view) {
        Bitmap cropped = mCropImageView.getCroppedImage(500, 500);
        if (cropped != null)
            mCropImageView.setImageBitmap(cropped);

        //mImage.setImageBitmap(converted);
        doOCR(convertColorIntoBlackAndWhiteImage(cropped) );

    }

    private Bitmap convertColorIntoBlackAndWhiteImage(Bitmap orginalBitmap) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
                colorMatrix);

        Bitmap blackAndWhiteBitmap = orginalBitmap.copy(
                Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setColorFilter(colorMatrixFilter);

        Canvas canvas = new Canvas(blackAndWhiteBitmap);
        canvas.drawBitmap(blackAndWhiteBitmap, 0, 0, paint);

        return blackAndWhiteBitmap;
    }

    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;



        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

