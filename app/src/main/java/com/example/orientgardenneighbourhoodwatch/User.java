package com.example.orientgardenneighbourhoodwatch;

public class User {
    public String displayName;
    public String email;
    public String houseNumber;

    public User(String displayName, String email, String houseNumber) {
        this.displayName = displayName;
        this.email = email;
        this.houseNumber = houseNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }
}
