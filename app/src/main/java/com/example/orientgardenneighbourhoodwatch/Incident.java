package com.example.orientgardenneighbourhoodwatch;

public class Incident {
    private final String stolen_item;
    private final String description;
    private final String house_number;

    public Incident(String stole_item, String description, String house_number) {
        this.stolen_item = stole_item;
        this.description = description;
        this.house_number = house_number;
    }

    public String getStolenItem() {
        return stolen_item;
    }

    public String getDescription() {
        return description;
    }

    public String getHouse_number() {
        return house_number;
    }
}
