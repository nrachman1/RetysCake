package com.example.cakerety.model;

import java.io.Serializable;

public class Pastry implements Serializable {

    public String id, pastryName, pathImage;
    public long priceSmall, priceBig;

    public Pastry() {
    }

    public Pastry(String id, String pastryName, String pathImage, long priceSmall, long priceBig) {
        this.id = id;
        this.pastryName = pastryName;
        this.pathImage = pathImage;
        this.priceSmall = priceSmall;
        this.priceBig = priceBig;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPastryName() {
        return pastryName;
    }

    public void setPastryName(String pastryName) {
        this.pastryName = pastryName;
    }

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }

    public long getPriceSmall() {
        return priceSmall;
    }

    public void setPriceSmall(long priceSmall) {
        this.priceSmall = priceSmall;
    }

    public long getPriceBig() {
        return priceBig;
    }

    public void setPriceBig(long priceBig) {
        this.priceBig = priceBig;
    }
}
