package com.example.cakerety.model;

import java.io.Serializable;

public class Cake implements Serializable {

    public String id, cakeName, pathImage;
    public long price;

    public Cake() {
    }

    public Cake(String id, String cakeName, Long price, String pathImage) {
        this.id = id;
        this.cakeName = cakeName;
        this.price = price;
        this.pathImage = pathImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCakeName() {
        return cakeName;
    }

    public void setCakeName(String cakeName) {
        this.cakeName = cakeName;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }
}
