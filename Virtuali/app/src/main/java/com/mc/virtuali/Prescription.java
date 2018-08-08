package com.mc.virtuali;

public class Prescription {

    String uri;
    String filename;
    public Prescription(String uri, String filename){
        this.uri = uri;
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public String getUri() {
        return uri;
    }
}
