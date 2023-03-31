package com.example.model;

public class Doctor {
    private String name;
    private String address;
    private String tel;
    private String email;
    private String speciality;
    private String uid;

    private String token;
    private String profilePic;
    public String getUid() {
        return uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Doctor(){
        //needed for firebase
    }


    public Doctor(String name, String address, String tel, String email, String speciality) {
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.email = email;
        this.speciality = speciality;
    }
    public Doctor(String name, String address, String tel, String email, String speciality, String uid) {
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.email = email;
        this.speciality = speciality;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }
}
