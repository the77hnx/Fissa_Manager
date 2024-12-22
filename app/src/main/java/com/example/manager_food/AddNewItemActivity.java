package com.example.manager_food;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.manager_food.DBHelper.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

public class AddNewItemActivity extends AppCompatActivity {
    private static final String PHP_URL = "https://www.fissadelivery.com/fissa/Manager/Add_Product.php";

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private EditText etName, etPrice, etDescription;
    private Spinner spinner;
    private ImageView imageView;
    private Button btnSave;
    private Uri imageUri;
    private OkHttpClient client;

    private ArrayList<String> categoryList;
    private ArrayList<String> categoryIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        // Initialize UI elements
        imageView = findViewById(R.id.imageViewupitemadd);
        etName = findViewById(R.id.etfullnameprod);
        etPrice = findViewById(R.id.etpriceprod);
        etDescription = findViewById(R.id.etdescriptionprod);
        spinner = findViewById(R.id.itemCategorySpinner);
        btnSave = findViewById(R.id.btnsaveitem);
        client = new OkHttpClient();

        DBHelper dbHelper = new DBHelper(this);
        String userId = dbHelper.getUserId();
        Log.d("user id = ", userId) ;

        // Fetch categories from the server
        new FetchCategoriesTask().execute();

        // Image picker
        imageView.setOnClickListener(v -> {
            if (checkAndRequestPermissions()) {
                openImageChooser();
            }
        });

        // Save product and send data to the server
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String price = etPrice.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            int selectedCategoryIndex = spinner.getSelectedItemPosition();
            String categoryId = categoryIds.get(selectedCategoryIndex); // Get category ID

            if (validateInput(name, price, description)) {
                new InsertProductTask(name, price, description, categoryId, userId).execute();
            } else {
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput(String name, String price, String description) {
        if (name.isEmpty() || price.isEmpty() || description.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(price); // Check if price is a valid number
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    // Fetch Categories from the server
    private class FetchCategoriesTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(PHP_URL); // Update URL
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    categoryList = new ArrayList<>();
                    categoryIds = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject category = jsonArray.getJSONObject(i);
                        categoryList.add(category.getString("Nom_Cat"));
                        categoryIds.add(category.getString("Id_Cat"));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddNewItemActivity.this, android.R.layout.simple_spinner_item, categoryList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddNewItemActivity.this, "Error parsing categories.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddNewItemActivity.this, "Failed to fetch categories.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Insert Product into the database
    private class InsertProductTask extends AsyncTask<Void, Void, String> {
        private String name, price, description, categoryId, userId;

        InsertProductTask(String name, String price, String description, String categoryId, String userId) {
            this.name = name;
            this.price = price;
            this.description = description;
            this.categoryId = categoryId;
            this.userId = userId;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(PHP_URL); // Update URL
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                Map<String, String> params = new HashMap<>();
                params.put("Nom_Prod", name);
                params.put("Prix_Prod", price);
                params.put("Desc_Prod", description);
                params.put("Id_Cat", categoryId);
                params.put("userId", userId);

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                writer.write(getPostDataString(params));
                writer.flush();
                writer.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String message = jsonResponse.getString("message");
                    Toast.makeText(AddNewItemActivity.this, message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddNewItemActivity.this, ShowShopDetailsActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddNewItemActivity.this, "Error processing response.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddNewItemActivity.this, "Failed to insert product.", Toast.LENGTH_SHORT).show();
            }
        }

        private String getPostDataString(Map<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) first = false;
                else result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            return result.toString();
        }
    }
}
