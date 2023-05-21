package com.example.orientgardenneighbourhoodwatch;

public class Incident {
    public String description;
    public String houseNumber;
    public String stolenItem;
    public String imageUrl;

    public Incident() {

    }

    public Incident(String description, String houseNumber, String stolenItem, String imageUrl) {
        this.description = description;
        this.houseNumber = houseNumber;
        this.stolenItem = stolenItem;
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getStolenItem() {
        return stolenItem;
    }

    public String getImageUrl() {
        return imageUrl;
    }

}
