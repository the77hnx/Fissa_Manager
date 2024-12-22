package com.example.manager_food.Fragement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.manager_food.Adapter.NewOrdersAdapter;
import com.example.manager_food.DBHelper.DBHelper;
import com.example.manager_food.NewOrderActivity;
import com.example.manager_food.R;
import com.example.manager_food.model.OrderItem;
import com.example.manager_food.model.OrderItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class NewOrdersFragment extends Fragment {

    private RecyclerView recyclerViewOrders;
    private NewOrdersAdapter newOrderAdapter;
    private List<OrderItem> orderList;

    private TextView namecategory;
    private RequestQueue requestQueue;

    private OkHttpClient client;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_orders, container, false);

        initializeViews(view);
        setupRecyclerView();
        client = new OkHttpClient();

        DBHelper dbHelper = new DBHelper(getContext());
        String userId = dbHelper.getUserId();
        Log.d("user id = ", userId) ;
        requestQueue = Volley.newRequestQueue(getContext());
        fetchOrders(userId);

        return view;
    }

    private void initializeViews(View view) {
        namecategory = view.findViewById(R.id.cat_text);
        recyclerViewOrders = view.findViewById(R.id.recyclerViewNewOrders);
    }

    private void setupRecyclerView() {
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>();
        newOrderAdapter = new NewOrdersAdapter(getContext(), orderList, order -> {
            // Handle order click here if needed
            Intent intent = new Intent(getContext(), NewOrderActivity.class);
            intent.putExtra("orderId", order.getOrderId());  // Pass the orderId to the next activity
            startActivity(intent);
        });
        recyclerViewOrders.setAdapter(newOrderAdapter);
    }
    public static NewOrdersFragment newInstance(List<OrderItem> orders) {
        NewOrdersFragment fragment = new NewOrdersFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("orders", new ArrayList<>(orders));
        fragment.setArguments(args);
        return fragment;
    }
    private void fetchOrders(String userId) {
        String url = "https://www.fissadelivery.com/fissa/Manager/Fetch_Orders.php";

        int caseNumber = 1;
        RequestBody postData  = new FormBody.Builder()
                .add("user_id", userId)
                .add("case_number", String.valueOf(caseNumber))
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(postData)
                .build();

        Log.d("request", String.valueOf(request));
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONArray ordersArray = new JSONArray(responseBody);
                        Log.d("Server Response", String.valueOf(ordersArray));

                        // Clear previous orders
                        orderList.clear();

                        for (int i = 0; i < ordersArray.length(); i++) {
                            JSONObject orderObj = ordersArray.getJSONObject(i);

                            String customerName = orderObj.getString("Nom_Client");
                            String orderStatus = orderObj.getString("Nom_Statut");
                            String orderDate = orderObj.getString("Date_commande");
                            String orderTime = orderObj.getString("Heure_commande");
                            double orderTotal = orderObj.getDouble("Prix_Demande");
                            String orderId = orderObj.getString("Id_Demandes");
                            String orderMessage = orderObj.getString("info_mag");
                            int idStatutCommande = orderObj.getInt("Id_Statut_Commande");

                            String itemName = orderObj.getString("Nom_Article");
                            int itemQuantity = orderObj.getInt("Quantite");
                            double itemPrice = orderObj.getDouble("Prix");

                            List<OrderItems> items = new ArrayList<>();
                            items.add(new OrderItems(itemName, itemPrice, itemQuantity));

                            OrderItem orderItem = new OrderItem(customerName, orderDate, orderTime, orderId, orderTotal, orderMessage, orderStatus, idStatutCommande, items);

                            orderList.add(orderItem);

                            Log.d("OrderItem", orderItem.toString());
                        }

                        getActivity().runOnUiThread(() -> {
                            newOrderAdapter.updateOrderList(orderList);
                        });
                    } catch (JSONException e) {
                        Log.e("Error", "JSON Parsing error: " + e.getMessage());
                    }
                } else {
                    Log.e("Error", "Server returned error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() -> {
                    Log.e("Error", e.getMessage());
                    Toast.makeText(getContext(), "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


}