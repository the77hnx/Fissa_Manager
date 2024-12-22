package com.example.manager_food;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.manager_food.Adapter.OrderItemsAdapter;
import com.example.manager_food.model.OrderItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CancelledOrderActivity extends AppCompatActivity {

    // Declare TextViews at the class level
    private TextView customerNameTextView;
    private TextView customerPhoneTextView;
    private TextView customerEmailTextView;
    private TextView customerAddressTextView;
    private TextView MessgaeMagasinTextView;
    private TextView OrderPriceTextView;
    private TextView DeliveryPriceTextView;
    private TextView AllPriceTextView;
    private TextView OrderDateTextView;
    private TextView OrderIDTextView;
    private TextView OrderStatusTextView;
    private RecyclerView recyclerView;
    private ImageView customerImageView;
    private OrderItemsAdapter orderItemsAdapter;
    private List<OrderItems> orderItemsList; // Change to List of OrderItems
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cancelled_order);

        // Extract order details from Intent
        String orderId = getIntent().getStringExtra("orderId");
        Log.d("NewOrderActivity", "Received orderId: " + orderId);
         customerNameTextView = findViewById(R.id.customer_name_cancelled_order_det);
         customerPhoneTextView = findViewById(R.id.numbertv_cancelled_order_det);
         customerEmailTextView = findViewById(R.id.emailtv_cancelled_order_det);
         customerAddressTextView = findViewById(R.id.addresstv_cancelled_order_det);
         customerImageView = findViewById(R.id.customer_image_cancelled_order_det);
         MessgaeMagasinTextView = findViewById(R.id.order_message_cancelled_order_det);
         OrderPriceTextView = findViewById(R.id.totalTextView_cancelled_order_det);
         DeliveryPriceTextView = findViewById(R.id.deliveryPriceTextView_cancelled_order_det);
         AllPriceTextView = findViewById(R.id.totalWithDeliveryTextView_cancelled_order_det);
         OrderDateTextView = findViewById(R.id.order_date_cancelled_order_det);
         OrderIDTextView = findViewById(R.id.order_total_cancelled_order_det);
         OrderStatusTextView = findViewById(R.id.order_status_cancelled_order_det);

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recycler_view_cancelled_items_order);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the items list and adapter
        orderItemsList = new ArrayList<>();
        orderItemsAdapter = new OrderItemsAdapter(orderItemsList);
        recyclerView.setAdapter(orderItemsAdapter);

        sendOrderIdToPHP(orderId);
        getOrderDetailsFromPHP(orderId);

    }

    private void sendOrderIdToPHP(String orderId) {
        String url = "https://www.fissadelivery.com/fissa/Manager/Details_Order.php?orderId=" + orderId;

        // Create a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create a StringRequest to send the orderId
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Log or handle the server response if needed
                        Log.d("PHP Response", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response
                Log.e("Volley Error", error.toString());
            }
        });

        // Add the request to the queue
        requestQueue.add(stringRequest);
    }
    private void getOrderDetailsFromPHP(String orderId) {
        String url = "https://www.fissadelivery.com/fissa/Manager/Details_Order.php?orderId=" + orderId;

        // Create a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create a JSON request to get order details
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Extract order details from the JSON response
                            JSONObject order = response.getJSONObject("order");
                            String orderPrice = order.getString("orderPrice");
                            String deliveryPrice = order.getString("deliveryPrice");
                            String orderDate = order.getString("orderDate");
                            String restaurantMessage = order.getString("restaurantMessage");

                            // Get customer details
                            JSONObject customer = order.getJSONObject("customer");
                            String customerName = customer.getString("customerName");
                            String customerNumber = customer.getString("customerNumber");
                            String customerEmail = customer.getString("customerEmail");
                            String customerCoordinates = customer.getString("customerCoordinates");
                            String imagePath = customer.getString("imagePath");

                            // Get order status
                            String orderStatus = order.getString("orderStatus");


                            customerNameTextView.setText(customerName);
                            customerPhoneTextView.setText(customerNumber);
                            customerEmailTextView.setText(customerEmail);
                            customerAddressTextView.setText(customerCoordinates);
                            MessgaeMagasinTextView.setText(restaurantMessage);
                            OrderPriceTextView.setText( " سعر الطلب : " + orderPrice);
                            DeliveryPriceTextView.setText( " سعر التوصيل : " + deliveryPrice);
                            AllPriceTextView.setText(String.valueOf( " السعر الكلي : " + (orderPrice + deliveryPrice) ));
                            OrderDateTextView.setText( " تاريخ الطلب : " + orderDate);
                            OrderIDTextView.setText( " رقم الطلب : " + orderId);
                            OrderStatusTextView.setText( " حالة الطلب : " + orderStatus);
// Load the customer image using Glide
                            String baseUrl = "https://www.fissadelivery.com/fissa/"; // Base URL for the images
                            String fullImagePath = baseUrl + imagePath.replace("../", ""); // Remove '../' if necessary
                            Glide.with(CancelledOrderActivity.this)
                                    .load(fullImagePath)
                                    .into(customerImageView); // Target ImageView

                            // Get order items
                            JSONArray items = order.getJSONArray("items");
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                String itemName = item.getString("itemName");
                                int itemQuantity = item.getInt("itemQuantity");
                                double itemPrice = item.getDouble("itemPrice");

                                // Create OrderItems object and add to the list
                                OrderItems orderItem = new OrderItems(itemName, itemPrice, itemQuantity);
                                orderItemsList.add(orderItem);  // Add each item to the list
                                // You can now use these details to display in your app
                                Log.d("Order Item", "Name: " + itemName + ", Quantity: " + itemQuantity + ", Price: " + itemPrice);
                            }

                            // Notify the adapter that the data has changed
                            orderItemsAdapter.notifyDataSetChanged();

                            // Display other details as needed
                            Log.d("Order Details", "Price: " + orderPrice + ", Delivery Price: " + deliveryPrice);
                            Log.d("Customer Details", "Name: " + customerName + ", Email: " + customerEmail);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response
                Log.e("Volley Error", error.toString());
            }
        });

        // Add the request to the queue
        requestQueue.add(jsonObjectRequest);
    }


}