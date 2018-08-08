package com.mc.virtuali;

/**
 * Created by kb on 2/25/18.
 */

public class Recording {

    String Uri, fileName;
    boolean isPlaying = false;


    public Recording(String uri, String fileName, boolean isPlaying) {
        Uri = uri;
        this.fileName = fileName;
        this.isPlaying = isPlaying;
    }

    public String getUri() {
        return this.Uri;
    }

    public String getFileName() {
        return this.fileName;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public void setPlaying(boolean playing){
        this.isPlaying = playing;
    }
}
