package com.example.e_commerceapp2;

public class Category {

    private String image;
    private String name;

    //Needed for Firebase
    public Category(){}

    public Category(String image, String name){
        this.image = image;
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }
}
