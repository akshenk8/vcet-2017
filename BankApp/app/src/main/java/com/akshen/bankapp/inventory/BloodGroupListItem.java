package com.akshen.bankapp.inventory;

public class BloodGroupListItem {

    public enum BloodGroup {
        OP("OP"),ON("ON"),AP("AP"),AN("AN"),BP("BP"),BN("BN"),ABP("ABP"),ABN("ABN");
        private String bloodGroup;

        BloodGroup(String bloodGroup) {
            this.bloodGroup = bloodGroup;
        }

        public String getBloodGroup() {
            return bloodGroup;
        }
    }

    private String bloodGroupDisplayString;
    private int quantity;
    private int change;
    private BloodGroup bloodGroup;

    public BloodGroupListItem(BloodGroup bloodGroup, String bg, int qty) {
        this.bloodGroup=bloodGroup;
        bloodGroupDisplayString = bg;
        quantity = qty;
        change = 0;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public String getBloodGroupDisplayString() {
        return bloodGroupDisplayString;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isChanged() {
        return change != 0;
    }

    public void saved() {
        change = 0;
    }

    public void revertChanges() {
        quantity -= change;
        change = 0;
    }

    public void incrementQuantity() {
        if (quantity != Integer.MAX_VALUE) {
            quantity++;
            change++;
        }
    }

    public void decrementQuantity() {
        if (quantity > 0) {
            quantity--;
            change--;
        }
    }
}
