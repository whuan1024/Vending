package com.cloudminds.vending.vo;

import android.graphics.drawable.Drawable;

public class Commodity {

    private String name;
    private int price;
    private Drawable image;

    //这个构造到时候可以删掉，直接用set赋值
    public Commodity(String name, int price, Drawable image) {
        this.name = name;
        this.price = price;
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

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Commodity{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", image=" + image +
                '}';
    }
}
