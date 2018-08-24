package com.opnay.qrscanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements Detector.Processor<Barcode>, SurfaceHolder.Callback {

    private TextView tv;
    private SurfaceView sfv;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.textView);
        sfv = findViewById(R.id.surfaceView);

        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        barcodeDetector.setProcessor(this);

        cameraSource = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setRequestedPreviewSize(1024, 768).setAutoFocusEnabled(true)
                .build();

        sfv.getHolder().addCallback(this);
    }

    // Detector.Processor<Barcode>
    @Override
    public void release() {}
    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        final SparseArray<Barcode> barcodes = detections.getDetectedItems();

        if (barcodes.size() != 0) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < barcodes.size(); i++)
                sb.append(barcodes.valueAt(i).rawValue).append("\n");
            tv.post(() -> tv.setText(sb.toString()));
        }
    }

    // SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1024);
                return;
            }
            cameraSource.start(sfv.getHolder());
        } catch (IOException e) {
            Log.e("MainActivity", e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) { cameraSource.stop(); }
}
