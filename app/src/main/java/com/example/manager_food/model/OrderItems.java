package com.example.manager_food.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderItems implements Parcelable {
    private String itemName;
    private double itemPrice;
    private int itemQuantity;

    public OrderItems(String itemName, double itemPrice, int itemQuantity) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemQuantity = itemQuantity;
    }

    protected OrderItems(Parcel in) {
        itemName = in.readString();
        itemPrice = in.readDouble();
        itemQuantity = in.readInt();
    }

    public static final Creator<OrderItems> CREATOR = new Creator<OrderItems>() {
        @Override
        public OrderItems createFromParcel(Parcel in) {
            return new OrderItems(in);
        }

        @Override
        public OrderItems[] newArray(int size) {
            return new OrderItems[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemName);
        dest.writeDouble(itemPrice);
        dest.writeInt(itemQuantity);
    }

    // Getters
    public String getItemName() {
        return itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }
}
