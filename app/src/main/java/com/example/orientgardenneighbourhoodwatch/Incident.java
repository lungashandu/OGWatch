package com.example.orientgardenneighbourhoodwatch;

public class Incident {
    public String description;
    public String houseNumber;
    public String stolenItem;

    public Incident() {

    }

    public Incident(String description, String houseNumber, String stolenItem) {
        this.description = description;
        this.houseNumber = houseNumber;
        this.stolenItem = stolenItem;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getStolenItem() {
        return stolenItem;
    }

    public void setStolenItem(String stolenItem) {
        this.stolenItem = stolenItem;
    }
}
