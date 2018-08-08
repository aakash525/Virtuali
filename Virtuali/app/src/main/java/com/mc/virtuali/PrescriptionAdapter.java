package com.mc.virtuali;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.ViewHolder> {

    private ArrayList<Prescription> prescriptionArrayList;
    private Context context;
//    private MediaPlayer mPlayer;
//    private boolean isPlaying = false;
//    private int last_index = -1;

    public PrescriptionAdapter(Context context, ArrayList<Prescription> prescriptionArrayList){
        this.context = context;
        this.prescriptionArrayList = prescriptionArrayList;
        try{
            Log.d("PresAdapter", "size: "+this.prescriptionArrayList.size());
        }
        catch (Exception e){
            Log.d("PresAdapter","Exception occured in size");
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.prescription_item_layout,parent,false);

        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {

//        setUpData(holder,position);
        Prescription prescription = prescriptionArrayList.get(position);
        holder.textViewName.setText(prescription.getFilename());
        Log.d("kb", "Set text: "+prescription.getFilename());
        String uri_file = "";
        uri_file = prescription.getUri();

        final String finalUri_file = uri_file;
        holder.textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Prescription item = prescriptionArrayList.get(position);
//                Uri uri = Uri.parse(finalUri_file);
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                intent.setDataAndType(uri, "text/plain");
//                startActivity(intent);


                try
                {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW);
                    File file = new File(finalUri_file);
                    String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                    String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                    myIntent.setDataAndType(Uri.fromFile(file),mimetype);
                    view.getContext().startActivity(myIntent);
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                    String data = e.getMessage();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return prescriptionArrayList.size();
    }


    private void setUpData(ViewHolder holder, int position) {


//        if( recording.isPlaying() ){
//            holder.imageViewPlay.setImageResource(R.drawable.ic_pause);
//            TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);
//            holder.seekBar.setVisibility(View.VISIBLE);
//            holder.seekUpdation(holder);
//        }else{
//            holder.imageViewPlay.setImageResource(R.drawable.ic_play);
//            TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);
//            holder.seekBar.setVisibility(View.GONE);
//        }


//        holder.manageSeekBar(holder);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

//        ImageView imageViewPlay;
//        SeekBar seekBar;
        TextView textViewName;
//        private String recordingUri;
//        private int lastProgress = 0;
        private Handler mHandler = new Handler();
        ViewHolder holder;

        public ViewHolder(View itemView) {
            super(itemView);

//            imageViewPlay = itemView.findViewById(R.id.imageViewPlay);
//            seekBar = itemView.findViewById(R.id.seekBar);
            textViewName = itemView.findViewById(R.id.textViewPrescriptionname);

//            textViewName.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d("kb",textViewName.getText().toString()+" clicked");
//
//                }
//            });

//            textViewName.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(Intent.ACTION_EDIT);
//                    Uri uri = Uri.parse("file:///sdcard/folder/file.txt");
//                    intent.setDataAndType(uri, "plain/text");
//                    getClass().startActivity(intent);
//
//
//
//                }
//
//            });
        }

//        public void manageSeekBar(RecordingAdapter.ViewHolder holder){
//            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    if( mPlayer!=null && fromUser ){
//                        mPlayer.seekTo(progress);
//                    }
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//
//                }
//            });
//        }
//
//        private void markAllPaused() {
//            for( int i=0; i < recordingArrayList.size(); i++ ){
//                recordingArrayList.get(i).setPlaying(false);
//                recordingArrayList.set(i,recordingArrayList.get(i));
//            }
//            notifyDataSetChanged();
//        }
//
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                seekUpdation(holder);
//            }
//        };
//
//        private void seekUpdation(RecordingAdapter.ViewHolder holder) {
//            this.holder = holder;
//            if(mPlayer != null){
//                int mCurrentPosition = mPlayer.getCurrentPosition() ;
//                holder.seekBar.setMax(mPlayer.getDuration());
//                holder.seekBar.setProgress(mCurrentPosition);
//                lastProgress = mCurrentPosition;
//            }
//            mHandler.postDelayed(runnable, 100);
//        }
//
//        private void stopPlaying() {
//            try{
//                mPlayer.release();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            mPlayer = null;
//            isPlaying = false;
//        }
//
//        private void startPlaying(final Recording audio, final int position) {
//            mPlayer = new MediaPlayer();
//            try {
//                mPlayer.setDataSource(recordingUri);
//                mPlayer.prepare();
//                mPlayer.start();
//            } catch (IOException e) {
//                Log.e("LOG_TAG", "prepare() failed");
//            }
//            //showing the pause button
//            seekBar.setMax(mPlayer.getDuration());
//            isPlaying = true;
//
//            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    audio.setPlaying(false);
//                    notifyItemChanged(position);
//                }
//            });
//        }

    }
}
