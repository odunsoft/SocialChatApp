package com.odunyazilim.socialcchatt.Model;

public class User {

    String profilename, profileimage, country, city, uid, gender, about, age, email,token;


    public User() {

    }

    public User(String profilename, String profileimage, String country, String city, String uid, String gender, String about, String age, String email, String token) {
        this.profilename = profilename;
        this.profileimage = profileimage;
        this.country = country;
        this.city = city;
        this.uid = uid;
        this.gender = gender;
        this.about = about;
        this.age = age;
        this.email = email;
        this.token = token;
    }


    public String getProfilename() {
        return profilename;
    }

    public void setProfilename(String profilename) {
        this.profilename = profilename;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
