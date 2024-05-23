package com.example.parivahan;

public class Model {
    private String imageurl;

    public Model(){

    }
    public Model(String imageurl){
        this.imageurl=imageurl;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}

