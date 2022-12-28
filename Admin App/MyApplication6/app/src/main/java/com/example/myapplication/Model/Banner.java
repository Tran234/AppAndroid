package com.example.myapplication.Model;

public class Banner {
    private String image,name;

    public Banner( String name,String image) {

        this.name = name;
        this.image = image;
    }

    public Banner() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
