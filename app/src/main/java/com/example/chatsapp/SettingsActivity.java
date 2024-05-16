package com.example.chatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        TextView tvServerAddress = findViewById(R.id.idEdtServerAddress);

        Button btnChangeServerAddress = findViewById(R.id.idBtnServerAddress);
        btnChangeServerAddress.setOnClickListener(view -> {
            // When clicking on the button, we want to change the server address:
            String enteredAddress = tvServerAddress.getText().toString();
            if (enteredAddress.length() > 0) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("serverAddress",enteredAddress);
                    setResult(Activity.RESULT_OK, returnIntent);
                Toast.makeText(this, "Changing Server's Address", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnFlipTheme = findViewById(R.id.idBtnFlipTheme);
        btnFlipTheme.setOnClickListener(view -> {
            // When clicking on the button, we want to flip the theme:
            Toast.makeText(this, "Flipping Theme", Toast.LENGTH_SHORT).show();
            // ...

            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            int newNightMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES
                    ? AppCompatDelegate.MODE_NIGHT_NO
                    : AppCompatDelegate.MODE_NIGHT_YES;

            AppCompatDelegate.setDefaultNightMode(newNightMode);
            recreate();
        });
    }
}