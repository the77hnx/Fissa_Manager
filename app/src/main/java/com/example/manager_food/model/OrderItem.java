package com.example.manager_food.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class OrderItem implements Parcelable {
    private String customerName;
    private String orderDate;
    private String orderTime;

    private String orderId;
    private double orderTotal;
    private String orderMessage;
    private String orderStatus;
    private int idStatutCommande;
    private List<OrderItems> items; // New field for items

    public OrderItem(String customerName, String orderDate, String orderTime, String orderId, double orderTotal,
                     String orderMessage, String orderStatus, int idStatutCommande, List<OrderItems> items) {
        this.customerName = customerName;
        this.orderTime = orderTime;
        this.orderDate = orderDate;
        this.orderId = orderId;
        this.orderTotal = orderTotal;
        this.orderMessage = orderMessage;
        this.orderStatus = orderStatus;
        this.idStatutCommande = idStatutCommande;
        this.items = items; // Initialize new field
    }

    protected OrderItem(Parcel in) {
        customerName = in.readString();
        orderDate = in.readString();
        orderTime = in.readString();
        orderId = in.readString();
        orderTotal = in.readDouble();
        orderMessage = in.readString();
        orderStatus = in.readString();
        idStatutCommande = in.readInt();
        items = in.createTypedArrayList(OrderItems.CREATOR); // Read items list
    }

    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(customerName);
        dest.writeString(orderDate);
        dest.writeString(orderTime);
        dest.writeString(orderId);
        dest.writeDouble(orderTotal);
        dest.writeString(orderMessage);
        dest.writeString(orderStatus);
        dest.writeInt(idStatutCommande);
        dest.writeTypedList(items); // Write items list
    }

    // Getters
    public String getCustomerName() {
        return customerName;
    }

    public String getOrderDate() {
        return orderDate;
    }
    public String getOrderTime() {
        return orderTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public String getOrderMessage() {
        return orderMessage;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public int getIdStatutCommande() {
        return idStatutCommande;
    }

    public List<OrderItems> getItems() {
        return items; // New getter for items
    }
}
