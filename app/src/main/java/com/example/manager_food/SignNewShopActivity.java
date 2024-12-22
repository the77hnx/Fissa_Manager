package com.example.manager_food;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignNewShopActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSION_CAMERA = 2;
    private static final int REQUEST_PERMISSION_STORAGE = 3;

    private EditText etNumber, etNameMag, etName, etEmail, etPassword, etConfirmPassword, etDescriptionRes, etPlacesRes, etIdNational, etEnregistrement;
    private Button btnPhoneLogin;
    private ImageView selectedImageRes;
    private OkHttpClient client = new OkHttpClient();
    private Uri photoUri;

    private final ActivityResultLauncher<String> selectImageResLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        displayImage(uri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_new_shop);

        initializeViews();

        btnPhoneLogin.setOnClickListener(v -> validateAndSubmit());
    }

    private void initializeViews() {
        etNumber = findViewById(R.id.etnumber);
        etName = findViewById(R.id.etName);
        etNameMag = findViewById(R.id.etNameMag);
        etEmail = findViewById(R.id.etemailsign);
        etPassword = findViewById(R.id.etPasswordsign);
        etConfirmPassword = findViewById(R.id.etconfirmPassword);
        etDescriptionRes = findViewById(R.id.etDescriptionres);
        etPlacesRes = findViewById(R.id.etplacesres);
        etIdNational = findViewById(R.id.etIdNational);
        etEnregistrement = findViewById(R.id.etN0Enrg);
        btnPhoneLogin = findViewById(R.id.btnsendinfores);
        selectedImageRes = findViewById(R.id.selectedImageRes);

        selectedImageRes.setVisibility(View.GONE);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (photoUri != null) {
                displayImage(photoUri);
            } else {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayImage(Uri uri) {
        if (uri != null) {
            selectedImageRes.setImageURI(uri);
            selectedImageRes.setVisibility(View.VISIBLE);
        }
    }

    private void validateAndSubmit() {
        String number = etNumber.getText().toString().trim();
        String namemag = etNameMag.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String description = etDescriptionRes.getText().toString().trim();
        String places = etPlacesRes.getText().toString().trim();
        String enregistrement = etEnregistrement.getText().toString().trim();
        String idNational = etIdNational.getText().toString().trim();

        // Validate inputs
        if (number.isEmpty() || namemag.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || description.isEmpty() || places.isEmpty() ||
                enregistrement.isEmpty() || idNational.isEmpty()) {
            Toast.makeText(this, "All fields and images must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert image to file and upload
        uploadImageAndSubmitData(
                name, namemag, email, password, number, description, places, enregistrement, idNational
        );

        // Optionally, redirect to another activity (like HomeActivity)
        Intent intent = new Intent(SignNewShopActivity.this, ShopInformationActivity.class);
        startActivity(intent);
        finish();

    }

    private void uploadImageAndSubmitData(
            String namemag, String name, String email, String password, String number, String description, String address,
            String enregistrement, String idNational) {

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("etNameMag", namemag)
                .addFormDataPart("etemail", email)
                .addFormDataPart("etPassword", password)
                .addFormDataPart("etnumber", number)
                .addFormDataPart("etName", name)
                .addFormDataPart("etDescriptionres", description)
                .addFormDataPart("etplacesres", address)
                .addFormDataPart("etN0Enrg", enregistrement)
                .addFormDataPart("etIdNational", idNational);

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url("https://www.fissadelivery.com/fissa/Manager/Signup_Magisin.php")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("SignNewShopActivity", "Failed to submit data: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(SignNewShopActivity.this, "Failed to submit: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("SignNewShopActivity", "Form submitted successfully!");
                    runOnUiThread(() -> {
                        Toast.makeText(SignNewShopActivity.this, "Form submitted successfully!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Log.e("SignNewShopActivity", "Error during submission: " + response.message());
                    runOnUiThread(() -> {
                        Toast.makeText(SignNewShopActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}