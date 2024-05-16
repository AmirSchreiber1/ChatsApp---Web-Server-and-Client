package com.example.chatsapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SettingsFABOperation {
    public static void setupFab(FloatingActionButton fab, final Context context) {
        fab.setOnClickListener(view -> {
            // When clicking on the Settings FAB, we want to move to SettingsActivity:
            Intent i = new Intent(context, SettingsActivity.class);
            int REQUEST_CODE = 1;
            ((Activity)context).startActivityForResult(i, REQUEST_CODE);
        });
    }
}
