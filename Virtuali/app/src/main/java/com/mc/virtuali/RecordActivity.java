package com.mc.virtuali;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {

    private Chronometer chronometer;
    private ImageView imageViewRecord, imageViewPlay, imageViewStop;
    private SeekBar seekBar;
    private LinearLayout linearLayoutRecorder, linearLayoutPlay;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private String fileName = null;
    private int lastProgress = 0;
    private Handler mHandler = new Handler();
    private boolean isPlaying = false;
    private TextView mTextMessage;
    private int RECORD_AUDIO_REQUEST_CODE =123 ;
    private boolean isSaved = false;
    private static final String TAG = "Virtuali";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_record:
                    Intent i1 = new Intent(RecordActivity.this, RecordActivity.class);
                    startActivity(i1);
                    return true;
                case R.id.navigation_list_patient:
                    Intent i2 = new Intent(RecordActivity.this, PatientNotesList.class);
//                    i2.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    startActivity(i2);
//                    mTextMessage.setText(R.string.title_list);
                    return true;
                case R.id.navigation_list_doctor:
                    Intent i3 = new Intent(RecordActivity.this, DoctorNotesList.class);
//                    i2.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    startActivity(i3);
//                    mTextMessage.setText(R.string.title_list);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getPermissionToRecordAudio();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        initViews();
        selectMode();

    }


    private void initViews() {

        /** setting up the toolbar  **/


        linearLayoutRecorder = (LinearLayout) findViewById(R.id.linearLayoutRecorder);
        chronometer = (Chronometer) findViewById(R.id.chronometerTimer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        imageViewRecord = (ImageView) findViewById(R.id.imageViewRecord);
        imageViewStop = (ImageView) findViewById(R.id.imageViewStop);
        imageViewPlay = (ImageView) findViewById(R.id.imageViewPlay);
        linearLayoutPlay = (LinearLayout) findViewById(R.id.linearLayoutPlay);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        imageViewRecord.setOnClickListener(this);
//        imageViewRecord.setOnTouchListener(this);
//        imageViewRecord.performClick();
        imageViewStop.setOnClickListener(this);
        imageViewPlay.setOnClickListener(this);

    }

    private void selectMode(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecordActivity.this);
        alertDialog.setTitle("Doctor/Patient");
        alertDialog.setMessage("Select a mode !!");
        alertDialog.setIcon(R.drawable.ic_save_black_24dp);
        alertDialog.setNegativeButton("Doctor", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fileName = "doc_";

            }
        });
        alertDialog.setPositiveButton("Patient", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fileName = "pat_";
            }
        });

        alertDialog.show();

    }


    @Override
    public void onClick(View view) {
        if( view == imageViewRecord ){
            prepareforRecording();
            startRecording();
//            Toast.makeText(RecordActivity.this, "Touch and Hold to record", Toast.LENGTH_LONG).show();
        }else if( view == imageViewStop ){
            prepareforStop();
            stopRecording();
            if(isSaved){
                Toast.makeText(getApplicationContext(), "Recording saved successfully.", Toast.LENGTH_SHORT).show();

            }
        }else if( view == imageViewPlay ){
            if( !isPlaying && fileName != null ){
                isPlaying = true;
                startPlaying();
            }else{
                isPlaying = false;
                stopPlaying();
            }
        }
    }

//    @Override
//    public boolean onTouch(View view, MotionEvent motionEvent) {
//        if(view == imageViewRecord){
//            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
//                prepareforRecording();
//                startRecording();
//            }
//            else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
//                prepareforStop();
//                stopRecording();
//            }
////            else if(motionEvent.getAction() == MotionEvent.ACTION_BUTTON_PRESS)
//
//        }
//        return false;
//    }


    private void prepareforRecording() {
        TransitionManager.beginDelayedTransition(linearLayoutRecorder);
        imageViewRecord.setVisibility(View.VISIBLE);
        imageViewRecord.setVisibility(View.GONE);
        imageViewStop.setVisibility(View.VISIBLE);
        linearLayoutPlay.setVisibility(View.GONE);
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
//fileName is global string. it contains the Uri to the recently recorded audio.
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        //making the imageview pause button
        imageViewPlay.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);

        seekBar.setProgress(lastProgress);
        mPlayer.seekTo(lastProgress);
        seekBar.setMax(mPlayer.getDuration());
        seekUpdation();
        chronometer.start();

        /** once the audio is complete, timer is stopped here**/
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imageViewPlay.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                isPlaying = false;
                chronometer.stop();
//                imageViewPlay.setVisibility(View.GONE);
            }
        });

        /** moving the track as per the seekBar's position**/
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if( mPlayer!=null && fromUser ){
                    //here the track's progress is being changed as per the progress bar
                    mPlayer.seekTo(progress);
                    //timer is being updated as per the progress of the seekbar
                    chronometer.setBase(SystemClock.elapsedRealtime() - mPlayer.getCurrentPosition());
                    lastProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    private void seekUpdation() {
        if(mPlayer != null){
            int mCurrentPosition = mPlayer.getCurrentPosition() ;
            seekBar.setProgress(mCurrentPosition);
            lastProgress = mCurrentPosition;
        }
        mHandler.postDelayed(runnable, 100);
    }

    private void startRecording() {
        //we use the MediaRecorder class to record
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        File root = android.os.Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + "/Virtuali/VoiceNotes/");
        if (!file.exists()) {
            file.mkdirs();
        }

        String name = String.valueOf(System.currentTimeMillis() + ".mp3");
        if(fileName.split("_")[0].equals("pat"))
            fileName +=  root.getAbsolutePath() + "/Virtuali/PatientVoiceNotes/" + name;
        else
            fileName +=  root.getAbsolutePath() + "/Virtuali/DoctorVoiceNotes/" + name;
        Log.d(TAG,fileName);
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastProgress = 0;
        seekBar.setProgress(0);
//        stopPlaying();
        //starting the chronometer
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();


    }


    private void prepareforStop() {
        TransitionManager.beginDelayedTransition(linearLayoutRecorder);
        imageViewRecord.setVisibility(View.VISIBLE);
        imageViewStop.setVisibility(View.GONE);
//        linearLayoutPlay.setVisibility(View.VISIBLE);
    }

    private void stopRecording() {


        try{
            mRecorder.stop();
            mRecorder.release();
        }catch (Exception e){
            e.printStackTrace();
        }
        mRecorder = null;
        //starting the chronometer
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());

        //showing the play button
//        Toast.makeText(getApplicationContext(), "Recording saved successfully.", Toast.LENGTH_SHORT).show();


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecordActivity.this);
        alertDialog.setTitle("Save?");
        String name  = fileName.split("/")[6];
        alertDialog.setMessage(name);
        alertDialog.setIcon(R.drawable.ic_save_black_24dp);
        alertDialog.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                File delete_file = new File(fileName);
                boolean flag = false;
                try {
                    flag = delete_file.delete();
                    if (flag) {
                        isSaved = false;
                        Toast.makeText(getApplicationContext(), "Voice Note discarded", Toast.LENGTH_SHORT).show();
                    } else {
                        isSaved = true;
                        Toast.makeText(getApplicationContext(), "Voice Note couldn't be discarded", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    isSaved = true;
                    Log.d(TAG, "File could not be deleted");
                }

            }
        });
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                isSaved = true;
                Toast.makeText(getApplicationContext(), "Recording saved successfully.", Toast.LENGTH_SHORT).show();

            }
        });

//        alertDialog.setNeutralButton("Play", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                linearLayoutPlay.setVisibility(View.VISIBLE);
//            }
//        });

        alertDialog.show();


//        if(!isSaved){
//            recreate();
//        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }

    }

    private void stopPlaying() {
        try{
            mPlayer.release();
        }catch (Exception e){
            Log.d(TAG,"here");
            e.printStackTrace();
        }
        mPlayer = null;
        //showing the play button
        imageViewPlay.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
        chronometer.stop();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToRecordAudio() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RECORD_AUDIO_REQUEST_CODE);

        }
    }

    // Callback with the request from calling requestPermissions(...)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.length == 3 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED){

                //Toast.makeText(this, "Record Audio permission granted", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "You must give permissions to use this app. App is exiting.", Toast.LENGTH_SHORT).show();
                finishAffinity();
            }
        }

    }




//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()){
//            case R.id.item_list:
//                Intent intent = new Intent(this, PatientNotesList.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//
//        }
//
//    }


}
