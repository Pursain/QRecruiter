package com.example.harry.qrecruiter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ScanActivity extends AppCompatActivity {
    private static final String TAG = "ScanActivity";

    private TextView textView;
    private SurfaceView cameraPreview;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    private boolean foundQR = false;
    private String positionTitle;
    private String URLURL;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case RequestCameraPermissionID:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        URLURL = getIntent().getStringExtra("URL");

        positionTitle = getIntent().getStringExtra("position");

        textView = (TextView)findViewById(R.id.textView_scan);

        cameraPreview = (SurfaceView) findViewById(R.id.surfaceView);
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1200, 500).setAutoFocusEnabled(true).build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ScanActivity.this, new String[]
                            {Manifest.permission.CAMERA}, RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if(!foundQR && qrcodes.size() != 0){
                    foundQR = true;
                    Log.d(TAG, "receiveDetections: " + qrcodes.valueAt(0).displayValue);
                    PostRequestAsyncTask postRequest = new PostRequestAsyncTask();
                    postRequest.execute(qrcodes.valueAt(0).displayValue);
                }
            }
        });
    }

    public void onClickBack(View view){
        finish();
    }

    private class PostRequestAsyncTask extends AsyncTask<String, Void, String>{
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!"failed".equals(result)){
                Log.d(TAG, "onPostExecute: success");
                Toast.makeText(ScanActivity.this, "Applicant successfully processed", Toast.LENGTH_LONG).show();
            }else{
                Log.d(TAG, "onPostExecute: failed");
                Toast.makeText(ScanActivity.this, "Something broke, oof", Toast.LENGTH_LONG).show();
            }
            finish();
        }
        @Override
        protected String doInBackground(String... strings) {
            String uniqueID = strings[0];
            String result;
            try{
                result = makePostRequest("https://" + URLURL +".ngrok.io/api/v1/recruiters/addUserToRole" +
                        "/" + uniqueID + "/" + positionTitle, "");
                Log.d(TAG, "doInBackground: id: " + uniqueID);
                Log.d(TAG, "doInBackground: position title: " + positionTitle);
                Log.d(TAG, "doInBackground: result: " + result);

            }catch(Exception e){
                Log.d(TAG, "doInBackground: failed: " + e.getMessage());
                return "failed";
            }
            return result;
        }
    }

    public static String makePostRequest(String URLString, String parameters) throws IOException{
        Log.d(TAG, "makePostRequest: begin post request on : " + URLString+parameters);
        URL url = new URL(URLString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(
                connection.getOutputStream(), "UTF-8"));
        writer.write(URLString + parameters);
        writer.flush();
        writer.close();

        Log.d(TAG, "makePostRequest: response code : " + connection.getResponseCode());

        String tempLine;
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        while((tempLine = bufferedReader.readLine()) != null){
            stringBuffer.append(tempLine);
        }
        bufferedReader.close();

        connection.disconnect();

        Log.d(TAG, "makePostRequest: object: " + stringBuffer.toString());
        Log.d(TAG, "makePostRequest: end post request");
        return stringBuffer.toString();
    }
}