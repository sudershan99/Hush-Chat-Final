package com.example.hushnew;
/**
 User model class
 **/
public class Users {
    public String name;
    public String image;
    public String status;
    public String thumbimg;

    public Users(String name, String image, String status,String thumbimg) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.thumbimg = thumbimg;

    }
    public Users(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumbimg() {
        return thumbimg;
    }

    public void setThumbimg(String thumbimg) {
        this.thumbimg = thumbimg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}