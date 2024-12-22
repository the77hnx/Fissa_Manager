package com.example.manager_food;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.manager_food.DBHelper.DBHelper;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;

public class AddNewCategoryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private EditText etName;
    private ImageView imageView;
    private Button btnSave;
    private Uri imageUri; // To store the selected image URI
    private OkHttpClient client;

    private static final String PHP_URL = "https://www.fissadelivery.com/fissa/Manager/Add_category.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_category);

        // Initialize UI elements
        imageView = findViewById(R.id.imageViewupcatadd);
        etName = findViewById(R.id.etfullnamecat);
        btnSave = findViewById(R.id.btnSavecat);
        client = new OkHttpClient();

        // Check and request permissions
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        // Set up click listeners
        imageView.setOnClickListener(v -> {
            if (checkAndRequestPermissions()) {
                openImageChooser();
            }
        });

        DBHelper dbHelper = new DBHelper(this);
        String userId = dbHelper.getUserId();
        Log.d("user id = ", userId) ;

        btnSave.setOnClickListener(v -> {
            // Handle save action
            String name = etName.getText().toString().trim();

            if (validateInput(name)) {
                // Save the data to server
                saveCategoryToServer(name, userId);
            } else {
                // Show validation errors
                etName.setError("Name cannot be empty");
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri); // Display the selected image in the ImageView
            imageView.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateInput(String name) {
        // Validate that name is not empty
        return !name.isEmpty();
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

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveCategoryToServer(String name ,String userId) {
        new SaveCategoryTask(name, userId).execute();
    }

    private class SaveCategoryTask extends AsyncTask<Void, Void, String> {
        private String name;
        private String userId;

        SaveCategoryTask(String name, String userId ) {
            this.name = name;
            this.userId = userId;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(PHP_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Create post data
                String postData = "name=" + name;
                postData += "&userId=" + userId;
                // Append imageUri if needed in the future
                // postData += "&imageUri=" + imageUri.toString();

                OutputStream os = connection.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "Success";
                } else {
                    return "Failed";
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(AddNewCategoryActivity.this, "Category saved: " + result, Toast.LENGTH_SHORT).show();
            if ("Success".equals(result)) {
                Intent intent = new Intent(AddNewCategoryActivity.this, ShowShopDetailsActivity.class);
                startActivity(intent);
                finish(); // Close the activity
            }
        }
    }
}
