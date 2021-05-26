package com.example.e_commerceapp2;

public class Product {
    private String image;
    private String name;
    private int price;
    private int stock;
    private String brand;
    private String description;
    private String category;

    //Needed for Firebase
    public Product(){}

    public Product(String image, String name, int price, int stock, String brand, String description, String category) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.brand = brand;
        this.description = description;
        this.category = category;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
