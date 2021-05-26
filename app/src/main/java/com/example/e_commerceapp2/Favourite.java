package com.example.e_commerceapp2;

public class Favourite {

    private String image, product;
    private int price;

    //Needed for Firebase
    public Favourite(){}

    public Favourite(String image, String product, int price) {
        this.image = image;
        this.product = product;
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
