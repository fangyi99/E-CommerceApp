package com.example.e_commerceapp2;

public class Cart {

    private String image, product;
//    private String details;
    private int price, quantity, totalPrice;

    //Needed for Firebase
    public Cart(){}

    public Cart(String image, String product, int quantity, int price, int totalPrice ) {
        this.image = image;
        this.product = product;
//        this.details = details;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTotalPrice() {
        return (getPrice()*getQuantity()) ;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
