package com.example.blooddonorapp;

public class DonorRegisterDetails {
    public String name, con, email, city,password,imageURL;

    public DonorRegisterDetails(String name, String con, String email,String city, String password, String imageURL){
        this.name = name;
        this.con = con;
        this.email = email;
        this.city=city;
        this.password=password;
        this.imageURL=imageURL;
    }
}
