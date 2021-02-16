package com.maochun.voiceinput;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final Integer RecordAudioRequestCode = 1;

    private SpeechRecognizer    mSpeechRecognizer;
    private boolean             mSpeechRecognizerStart = false;
    private Intent              mSpeechRecognizerIntent;
    private Button              mVoiceInputButton;

    private TextView            mVoiceTextView;

    private EditText            mLengthEditText;
    private EditText            mWidthEditText;
    private EditText            mHeightEditText;

    private RecognitionListener mRecognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {
            Log.i("TEST", "Beginning of speech");
        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {
            Log.i("TEST", "End of speech");
        }

        @Override
        public void onError(int i) {
            Log.e("TEST", "Speech recognizer error " + i);
        }

        @Override
        public void onResults(Bundle bundle) {
            ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (data.size() > 0) {
                Log.i("TEST", data.get(0));
                mVoiceTextView.setText(data.get(0));

                parsingVoiceMsg(data.get(0));
            }
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

        mVoiceInputButton = findViewById(R.id.buttonVoiceInput);
        mVoiceTextView = findViewById(R.id.textViewVoiceMsg);

        mLengthEditText = findViewById(R.id.editTextLength);
        mWidthEditText = findViewById(R.id.editTextWidth);
        mHeightEditText = findViewById(R.id.editTextTextHeight);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(mRecognitionListener);

        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        //parsingVoiceMsg("長20寬33高55");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpeechRecognizer.destroy();
    }

    public void onVoiceInputButtonClick(View v){
        if (mSpeechRecognizerStart){
            mVoiceInputButton.setText("Start Voice Input");
            mSpeechRecognizer.stopListening();
            mSpeechRecognizerStart = false;
        }else{
            mVoiceInputButton.setText("Stop Voice Input");
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            mSpeechRecognizerStart = true;
        }
    }

    private void parsingVoiceMsg(String msg){
        Integer length=0, width=0, height=0;

        try {
            String[] lenArr = msg.split("長");
            if (lenArr.length > 1) {
                length = NumberFormat.getInstance().parse(lenArr[1]).intValue();
            }
            String[] widArr = msg.split("寬");
            if (widArr.length > 1) {
                width = NumberFormat.getInstance().parse(widArr[1]).intValue();
            }
            String[] hArr = msg.split("高");
            if (hArr.length > 1) {
                height = NumberFormat.getInstance().parse(hArr[1]).intValue();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        mLengthEditText.setText(length.toString());
        mWidthEditText.setText(width.toString());
        mHeightEditText.setText(height.toString());

        Log.i("TEST", "length=" + length + " width=" + width + " height=" + height);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }
    }
}