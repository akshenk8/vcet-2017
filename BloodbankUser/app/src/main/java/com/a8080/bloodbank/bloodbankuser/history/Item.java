package com.a8080.bloodbank.bloodbankuser.history;

/**
 * Created by DELL_PC on 10/14/2017.
 */

public class Item {
    private String name;
    private String timestamp;
    private String quantity;

    public Item(String name, String timestamp, String quantity) {
        this.name = name;
        this.timestamp = timestamp;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getQuantity() {
        return quantity;
    }
}
