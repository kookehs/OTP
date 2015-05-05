package com.mrcornman.otp.models;

public class UserItem {

    // Constants
    public static final int GENDER_MALE = 0;
    public static final int GENDER_FEMALE = 1;

    // Data
    private String id;
    private String name;
    private int gender;
    private int age;
    private String imageUrl;
    private String dreLandingPageUrl;

    public UserItem(String id){
        this.id = id;
    }

    public UserItem(){}

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDreLandingPageUrl() {
        return dreLandingPageUrl;
    }

    public void setDreLandingPageUrl(String dreLandingPageUrl) {
        this.dreLandingPageUrl = dreLandingPageUrl;
    }

    @Override
    public String toString() {
        // Jonathan (21 M)
        return name + " (" + age + " " + (gender == GENDER_MALE ? "M" : "F") + ")";
    }
}
