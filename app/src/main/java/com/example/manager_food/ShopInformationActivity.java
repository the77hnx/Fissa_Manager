package com.example.manager_food;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ShopInformationActivity extends AppCompatActivity {

    private ImageView facebook, gmail, whatsapp;
    private TextView Name_MagasinTv, Nmber_EnrigestrementTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopinformation);

        Name_MagasinTv = findViewById(R.id.namemagpend);
        Nmber_EnrigestrementTv = findViewById(R.id.numberEnrigestrementpend);
        facebook = findViewById(R.id.facebook);
        gmail = findViewById(R.id.gmail);
        whatsapp = findViewById(R.id.whatsapp);

        Intent intent = getIntent();

        String Name_Magasin = intent.getStringExtra("nom_magasin");
        String Nmber_Enrigestrement = intent.getStringExtra("n_enregistrement");

        Name_MagasinTv.setText(Name_Magasin);
        Nmber_EnrigestrementTv.setText(Nmber_Enrigestrement);


        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Set click listeners for social media icons
        facebook.setOnClickListener(v -> openFacebookMessenger());
        gmail.setOnClickListener(v -> openGmail());
        whatsapp.setOnClickListener(v -> openWhatsApp());
    }

    private void openFacebookMessenger() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/messages/e2ee/t/7976922995674316"));
            startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/messages/e2ee/t/7976922995674316"));
            startActivity(intent);
        }
    }

    private void openGmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"akrambelhadi2002@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        intent.putExtra(Intent.EXTRA_TEXT, "Message Here");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // No email app found
        }
    }

    private void openWhatsApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("whatsapp://send?phone=+213780241499&text=Message%20Here"));
            startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=+213780241499&text=Message%20Here"));
            startActivity(intent);
        }
    }
}
