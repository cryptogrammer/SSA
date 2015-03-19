package com.SSA;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyActivity extends Activity {

    private static final String TAG = MyActivity.class.getSimpleName();

    private WebView mWebView;
    private String doctorID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWebView = new WebView(this);
        setContentView(mWebView);
    }

    @Override
    public void onResume(){
        super.onResume();
        Intent intent = getIntent();
        if(intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)){
            Log.i(TAG, "Detected NFC launching webview");
            NdefMessage[] messages = getNdefMessages(intent);
            byte[] payload = messages[0].getRecords()[0].getPayload();
            String patientId = new String(payload);
            getPatientInfoFor(patientId);
        }else {
            listPatients();
        }
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
    }
    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);

        setIntent(intent);
    }

    @Override
    public void onBackPressed(){
        if(mWebView.canGoBack()){
            mWebView.goBack();
        }
    }
    private NdefMessage[] getNdefMessages(Intent intent){
        NdefMessage[] messages = null;
        if(intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED) ||
                intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)){
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(rawMessages != null){
                messages = new NdefMessage[rawMessages.length];
                for(int i = 0;i<messages.length;i++){
                    messages[i] = (NdefMessage)rawMessages[i];
                }
            }else {
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,empty,empty,empty);
                NdefMessage message = new NdefMessage(new NdefRecord[] { record });
                messages = new NdefMessage[] { message };
            }
        }
        return messages;
    }

    private void getPatientInfoFor(String patientId){
        Log.i(TAG,"Loading patient page");
        String urlString = "http://charlesding.net/forge/loginScreen/adminScreen/index.html?id="+patientId;
        Log.i(TAG,urlString);
        mWebView.loadUrl(urlString);
    }

    private void listPatients(){
        Log.i(TAG,"Loading patient list page");
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority("charlesding.net");
        builder.appendPath("forge");

        String urlString = builder.build().toString();
        mWebView.loadUrl(urlString);
    }
}
