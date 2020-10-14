package com.peagainc.hutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.peagainc.hutech.adapter.MobileFaceNetUtil;
import com.peagainc.hutech.mobilefacenet.MobileFaceNet;
import com.peagainc.hutech.mtcnn.Align;
import com.peagainc.hutech.mtcnn.Box;
import com.peagainc.hutech.mtcnn.MTCNN;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Vector;


import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class DetectFaceActivity extends AppCompatActivity {

    private static final int IMAGE_FORMAT = ImageFormat.NV21;
    private static final int CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private SurfaceView surfaceView;
    private CircularProgressButton cirCheckFace;

    private Camera camera;
    private Camera.Size size;
    private int displayDegree;

    private byte[] data;

    private MTCNN mtcnn;
    private MobileFaceNet mfn;
    private Bitmap imageInput;
    private Bitmap imageTrain;
    private Bitmap bitmapCrop1;
    private Bitmap bitmapCrop2;

    private TextView txtResult;

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detect_face);
        try {
            mtcnn = new MTCNN(getAssets());
            mfn = new MobileFaceNet(getAssets());
            Log.d("TAG", "Okie");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG", "Fails");
        }
        initView();
        addEvent();
    }

    private void initView() {
        surfaceView = findViewById(R.id.surfaceView);
        cirCheckFace = findViewById(R.id.cirCheckFace);
        txtResult = findViewById(R.id.txtResult);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                openCamera(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                releaseCamera();
            }
        });
    }



    private void openCamera(SurfaceHolder holder) {
        releaseCamera();
        camera = Camera.open(CAMERA_ID);
        Camera.Parameters parameters = camera.getParameters();
        displayDegree = setCameraDisplayOrientation(CAMERA_ID, camera);

        size = getOptimalSize(parameters.getSupportedPreviewSizes(), surfaceView.getWidth(), surfaceView.getHeight());
        parameters.setPreviewSize(size.width, size.height);

        parameters.setPreviewFormat(IMAGE_FORMAT);
        camera.setParameters(parameters);

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytesData, Camera camera) {
                data = bytesData;
                camera.addCallbackBuffer(data);
            }
        });

        camera.startPreview();

    }

    private synchronized void releaseCamera() {
        if (camera != null) {
            try {
                camera.setPreviewCallback(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                camera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                camera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            camera = null;
        }
    }

    private void addEvent() {
        cirCheckFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageInput = convertBitmap(data, camera);
                imageTrain = imageInput;
                faceCrop(imageInput, imageInput);
                if(bitmapCrop1!=null && bitmapCrop2 != null)
                {
                    Float Acc = faceCompare(bitmapCrop1, bitmapCrop2);
                    if(Acc > 0.7){
                        Toast.makeText(getApplicationContext(), "Checkin Successful", Toast.LENGTH_LONG).show();
                        openCheckinActivity(Acc);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Can't Detect Face", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Can't Detect Face", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void openCheckinActivity(Float Acc) {
        CheckinActivity.inputImage = imageInput;
        CheckinActivity.trainImage = imageTrain;
        CheckinActivity.inputCrop = bitmapCrop1;
        CheckinActivity.result = Acc;
        startActivity(new Intent(DetectFaceActivity.this, CheckinActivity.class));
    }

    private int setCameraDisplayOrientation(int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int displayDegree;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayDegree = (info.orientation + degrees) % 360;
            displayDegree = (360 - displayDegree) % 360;  // compensate the mirror
        } else {
            displayDegree = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(displayDegree);
        return displayDegree;
    }

    private static Camera.Size getOptimalSize(@NonNull List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }

    private Bitmap convertBitmap(byte[] data, Camera camera) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        YuvImage yuvimage = new YuvImage(
                data,
                camera.getParameters().getPreviewFormat(),
                previewSize.width,
                previewSize.height,
                null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);
        byte[] rawImage = baos.toByteArray();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
        Matrix m = new Matrix();
        m.setRotate(-displayDegree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    private void faceCrop(Bitmap bitmap1, Bitmap bitmap2) {

        Bitmap bitmapTemp1 = bitmap1.copy(bitmap1.getConfig(), false);
        Bitmap bitmapTemp2 = bitmap2.copy(bitmap2.getConfig(), false);
        long start = System.currentTimeMillis();

        Vector<Box> boxes1 = mtcnn.detectFaces(bitmapTemp1, bitmapTemp1.getWidth() / 5);
        Log.d("TAG", String.valueOf(start));
        long end = System.currentTimeMillis();

        Vector<Box> boxes2 = mtcnn.detectFaces(bitmapTemp2, bitmapTemp2.getWidth() / 5);
        Log.d("TAG", "Error");

        if (boxes1.size() == 0 || boxes2.size() == 0) {
            Toast.makeText(this, "No Face Detected", Toast.LENGTH_LONG).show();
            return;
        }

        Box box1 = boxes1.get(0);
        Box box2 = boxes2.get(0);

        bitmapTemp1 = Align.face_align(bitmapTemp1, box1.landmark);
        bitmapTemp2 = Align.face_align(bitmapTemp2, box2.landmark);
        boxes1 = mtcnn.detectFaces(bitmapTemp1, bitmapTemp1.getWidth() / 5);
        boxes2 = mtcnn.detectFaces(bitmapTemp2, bitmapTemp2.getWidth() / 5);
        box1 = boxes1.get(0);
        box2 = boxes2.get(0);

        box1.toSquareShape();
        box2.toSquareShape();
        box1.limitSquare(bitmapTemp1.getWidth(), bitmapTemp1.getHeight());
        box2.limitSquare(bitmapTemp2.getWidth(), bitmapTemp2.getHeight());
        Rect rect1 = box1.transform2Rect();
        Rect rect2 = box2.transform2Rect();

        bitmapCrop1 = MobileFaceNetUtil.crop(bitmapTemp1, rect1);
        bitmapCrop2 = MobileFaceNetUtil.crop(bitmapTemp2, rect2);
    }

    private float faceCompare(Bitmap bitmapCrop1, Bitmap bitmapCrop2) {
        if (bitmapCrop1 == null || bitmapCrop2 == null) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }
        float same = mfn.compare(bitmapCrop1, bitmapCrop2);
        String text = "Accuracy：" + same;
        if (same > MobileFaceNet.THRESHOLD) {
            text = text + "，" + "True";
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        } else {
            text = text + "，" + "False";
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        }
        return same;
    }
    public static  Bitmap downloadImage(String url) {
        Bitmap bitmap = null;
        InputStream stream = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        try {
            stream = getHttpConnection(url);
            bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
            stream.close();
        }
        catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("downloadImage"+ e1.toString());
        }
        return bitmap;
    }

    // Makes HttpURLConnection and returns InputStream

    public static InputStream getHttpConnection(String urlString)  throws IOException {

        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("downloadImage" + ex.toString());
        }
        return stream;
    }


}