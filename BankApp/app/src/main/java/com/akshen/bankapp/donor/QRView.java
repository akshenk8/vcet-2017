package com.akshen.bankapp.donor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class QRView extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    public static final int REQUEST_CODE=101;
    public static final String RESPONSE_DATA ="xmlData";

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
    }

    public void onPause() {
        mScannerView.stopCamera();           // Stop camera on pause
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void handleResult(Result result) {

        //Log.e("handler", result.getText()); // Prints scan results
        //Log.e("handler", result.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        //Toast.makeText(this, "Text Stored", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent();
        intent.putExtra(RESPONSE_DATA,result.getText());
        setResult(RESULT_OK,intent);
        finish();//finishing activity
        //mScannerView.stopCamera();
    }
}
