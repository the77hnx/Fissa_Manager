package com.example.manager_food;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.manager_food.DBHelper.DBHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;

public class ShopMainActivity extends AppCompatActivity {

    private TextView basket, shopname, valwallet, valtoday,valweek, valmonth,cat_num,item_num, accepted_order, rejected_order, today_orders, monthly_orders, eval_res;
    private Switch offerSwitch;
    private Button addProductButton;
    private OkHttpClient client;

    private static final String PREFS_NAME = "MyPrefs";
    private static final String OFFER_STATUS_KEY = "offer_status";

    private static final String fetchStoreDataURL = "https://www.fissadelivery.com/fissa/Manager/Fetch_Magasin_Information.php";
    private static final String updateStoreStatusURL = "https://www.fissadelivery.com/fissa/Manager/Status_Magasin.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopmain);

        // Initialize UI components
        basket = findViewById(R.id.basketmain);
        shopname = findViewById(R.id.shopnamemain);
        valwallet = findViewById(R.id.valwallet);
        valtoday = findViewById(R.id.valtoday);
        valweek = findViewById(R.id.valweek);
        valmonth = findViewById(R.id.valmonth);
        accepted_order = findViewById(R.id.accepted_order);
        rejected_order = findViewById(R.id.rejected_order);
        today_orders = findViewById(R.id.today_orders);
        monthly_orders = findViewById(R.id.monthly_orders);
        eval_res = findViewById(R.id.eval_res);
        offerSwitch = findViewById(R.id.offer_switch_main);
        cat_num = findViewById(R.id.cat_num);
        item_num = findViewById(R.id.item_num);
        client = new OkHttpClient();

        // Load the saved offer switch state
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean offerStatus = preferences.getBoolean(OFFER_STATUS_KEY, false);
        offerSwitch.setChecked(offerStatus);

        // Set initial state of the basket TextView
        updateBasketText(offerSwitch.isChecked());
        DBHelper dbHelper = new DBHelper(this);
        String userId = dbHelper.getUserId();
        Log.d("user id = ", userId) ;
        // Fetch store data from the server
        fetchStoreData(userId);
        showStoreStatus(userId); // Call to fetch and display current store status


        // Set up listener for the Switch
        offerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateBasketText(isChecked);
            saveOfferSwitchState(isChecked); // Save state when it changes
            updateStoreStatus(isChecked, userId);    // Update status on the server
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView_sm);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    startActivity(new Intent(ShopMainActivity.this, ShopMainActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_following) {
                    Intent intent = new Intent(ShopMainActivity.this, ShowShopDetailsActivity.class);
                    intent.putExtra("OFFER_STATUS", offerSwitch.isChecked());
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.navigation_basket) {
                    startActivity(new Intent(ShopMainActivity.this, OurOrdersActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    startActivity(new Intent(ShopMainActivity.this, EditProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void fetchStoreData(String userId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                // URL for your PHP script
                URL url = new URL(fetchStoreDataURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Prepare the POST data
                String postData = "user_id=" + URLEncoder.encode(userId, "UTF-8");

                // Send the POST data
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(postData);
                writer.flush();
                writer.close();

                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                String response = result.toString();

                handler.post(() -> {
                    if (response != null) {
                        Log.d("Server Response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            // Safely fetch the values, handling missing keys with defaults
                            String shopName = jsonObject.optString("shop_name");
                            String walletValue = jsonObject.optString("wallet_value");
                            String walletMonthlyValue = jsonObject.optString("wallet_monthly_value");
                            String walletWeeklyValue = jsonObject.optString("wallet_weekly_value");
                            String walletDailyValue = jsonObject.optString("wallet_daily_value");
                            String todaysOrders = jsonObject.optString("todays_orders");
                            String monthlyOrders = jsonObject.optString("monthly_orders");
                            String acceptedOrders = jsonObject.optString("accepted_orders");
                            String cancelledOrders = jsonObject.optString("cancelled_orders");
                            String storeEvaluation = jsonObject.optString("Evaluation");
                            String numProducts = jsonObject.optString("num_products");
                            String numCat = jsonObject.optString("num_cat");

                            // Update the UI components
                            shopname.setText(shopName);
                            valwallet.setText(walletValue);
                            valtoday.setText(walletDailyValue);
                            valweek.setText(walletWeeklyValue);
                            valmonth.setText(walletMonthlyValue);
                            today_orders.setText(todaysOrders);
                            monthly_orders.setText(monthlyOrders);
                            accepted_order.setText(acceptedOrders);
                            rejected_order.setText(cancelledOrders);
                            eval_res.setText(storeEvaluation);
                            cat_num.setText(numCat);
                            item_num.setText(numProducts);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Error parsing data", e.getMessage());
                            Toast.makeText(ShopMainActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ShopMainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(ShopMainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show());
            }
        });
    }
    private void showStoreStatus(String userId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String urlWithParams = fetchStoreDataURL + "?user_id=" + URLEncoder.encode(userId, "UTF-8");
                URL url = new URL(urlWithParams);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);

                // Prepare the POST data
                String postData = "user_id=" + URLEncoder.encode(userId, "UTF-8");

                // Send the POST data
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(postData);
                writer.flush();
                writer.close();

                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                String response = result.toString();

                handler.post(() -> {
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.optString("Statut_magasin");
                            updateStoreStatusUI(status);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ShopMainActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ShopMainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(ShopMainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show());
            }
        });
    }
    private void updateStoreStatus(boolean isOpen, String userId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String urlWithParams = updateStoreStatusURL + "?user_id=" + URLEncoder.encode(userId, "UTF-8");
                URL url = new URL(urlWithParams);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Prepare the POST data
                String statutMagasin = isOpen ? "مفتوح" : "مغلق";
                String postData = "user_id=" + URLEncoder.encode(userId, "UTF-8") +
                        "&statut_magasin=" + URLEncoder.encode(statutMagasin, "UTF-8");

                // Send the POST data
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(postData);
                writer.flush();
                writer.close();

                int responseCode = connection.getResponseCode();
                Log.d("Update Store Status", "Sending status: " + statutMagasin + ", Response code: " + responseCode);

                boolean success = responseCode == HttpURLConnection.HTTP_OK;

                handler.post(() -> {
                    if (success) {
                        updateStoreStatusUI(statutMagasin); // Update UI with the new status
                        saveOfferSwitchState(isOpen); // Save the state to SharedPreferences
                        Toast.makeText(ShopMainActivity.this, "Store status updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ShopMainActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error message", e.getMessage());
                handler.post(() -> Toast.makeText(ShopMainActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateStoreStatusUI(String status) {
        // Update the UI based on the store status
        if ("مفتوح".equals(status)) {
            offerSwitch.setChecked(true);
            updateBasketText(true); // Update basket text to "مفتوح"
        } else {
            offerSwitch.setChecked(false);
            updateBasketText(false); // Update basket text to "مغلق"
        }
    }
    private void updateBasketText(boolean isChecked) {
        if (isChecked) {
            basket.setText("مفتوح");
            basket.setTextColor(getResources().getColor(R.color.green));
        } else {
            basket.setText("مغلق");
            basket.setTextColor(getResources().getColor(R.color.red));
        }
    }
    private void saveOfferSwitchState(boolean isChecked) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(OFFER_STATUS_KEY, isChecked);
        editor.apply();
    }
}
