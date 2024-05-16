package com.example.chatsapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.chatsapp.apis.UsersAPI;
import com.example.chatsapp.viewmodels.ChatsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity {

    String serverAddress = "http://10.0.2.2:5000/api/";
    boolean isProfilePictureSet = false;

    String fireBaseToken = "";
    private Socket mSocket;

    String profilePictureChanged() {
        // Creating variables to present the error message, if needed to, or the name of the file (if one really was uploaded):
        String currProfilePictureError = "";

        // Checking if a file wasn't uploaded:
        if (!isProfilePictureSet) {
            currProfilePictureError = "No file was uploaded! ";
        }

        return currProfilePictureError;
    }


    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;
    private static final String NOTIFICATION_PERMISSION = "android.permission.NOTIFICATION";

    private boolean isNotificationPermissionGranted() {
        // Getting the notification permission status:
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Returning the permission status:
        return notificationManager.areNotificationsEnabled();
    }

    private void requestNotificationPermission() {
        // Prompting the built-in pop-up for asking the notification permission:
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST_CODE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // First thing to do in onCreate - Check if notification permission is granted:
        if (!isNotificationPermissionGranted()) {
            // If not, request it, using the function the we've created:
            requestNotificationPermission();
        }

        //initialize the fireBase Messaging instance for this device
        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            fireBaseToken = newToken;

        });

/*        try {
            mSocket = IO.socket("http://10.0.2.2:5000");
        } catch (URISyntaxException e) {
            Toast.makeText(this, "socketIOError!", Toast.LENGTH_SHORT).show();
        }
        mSocket.connect();
        mSocket.on("msg", msgReload);*/


        FloatingActionButton fab = findViewById(R.id.fabSettings);
        SettingsFABOperation.setupFab(fab, this);

        ImageView ivProfilePicture = findViewById(R.id.idProfilePicture);
        isProfilePictureSet = false;

        ActivityResultLauncher<Intent> pickMedia = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Uri uri = data.getData();
                    Log.d("PhotoPicker", "Selected URI: " + uri);

                    // Load the selected image into the ImageView using Glide
                    Glide.with(this).load(uri).into(ivProfilePicture);

                    // Indicate that the profile picture is set:
                    isProfilePictureSet = true;

                } else {
                    Log.d("PhotoPicker", "No media selected");
                    // Indicate that the profile picture is not set:
                    isProfilePictureSet = false;
                }
            }
        });

        Button btnPickImage = findViewById(R.id.idBtnPickImage);

        btnPickImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickMedia.launch(intent);
        });

        Button btnMoveToLogin = findViewById(R.id.idBtnMoveToLogin);
        btnMoveToLogin.setOnClickListener(view -> {
            // When clicking on the button, we want to move the Login Activity:
            //Intent i = new Intent(this, LoginActivity.class);
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("serverAddress", serverAddress);
            i.putExtra("fireBaseToken", fireBaseToken);
            int REQUEST_CODE = 1;
            startActivityForResult(i, REQUEST_CODE);
        });

        Button btnRegister = findViewById(R.id.idBtnRegister);
        btnRegister.setOnClickListener(view -> {
            // When clicking on the button, we want to check the fields inserted, update the DB, and move to the Login Activity:
            // 1. Checking whether the fields inserted are legal:
            // First, retrieving the fields inserted:

            TextView tvUsername = findViewById(R.id.idEdtUsername);
            String usernameTyped = tvUsername.getText().toString();

            TextView tvPassword = findViewById(R.id.idEdtPassword);
            String passwordTyped = tvPassword.getText().toString();

            TextView tvConfirmPassword = findViewById(R.id.idEdtConfirmPassword);
            String confirmPasswordTyped = tvConfirmPassword.getText().toString();

            TextView tvDisplayName = findViewById(R.id.idEdtDisplayName);
            String displayNameTyped = tvDisplayName.getText().toString();

            TextView tvErrors = findViewById(R.id.tvErrors);

            // Now, checking the legality of the fields inserted:
            String currProfilePictureError = profilePictureChanged();
            String currUsernameError = RegistrationValidityChecks.usernameChanged(usernameTyped);
            String currPasswordError = RegistrationValidityChecks.passwordChanged(passwordTyped);
            String currConfirmPasswordError = RegistrationValidityChecks.confirmPasswordChanged(passwordTyped, confirmPasswordTyped);
            String currDisplayNameError = RegistrationValidityChecks.displayNameChanged(displayNameTyped);

            // Now, setting the error message in the appropriate (red) TextView:
            String currErrorMessage = currProfilePictureError + currUsernameError + currPasswordError + currConfirmPasswordError + currDisplayNameError;
            tvErrors.setText(currErrorMessage);

            boolean isRegistrationLegal = currErrorMessage.equals("");

            // If there are indeed errors, we want to display them (make the TextView visible, with its red background):
            if (!isRegistrationLegal) {
                tvErrors.setVisibility(View.VISIBLE);
            } else {
                tvErrors.setVisibility(View.INVISIBLE);

                // only in this case, we want to send data to the server and see if such a username already exist or not (so it won't save a user with no image, etc.)
                UsersAPI usersAPI = new UsersAPI(serverAddress);
                String profilePic = profilePicToBase64(ivProfilePicture);
                UserDetails userDetails = new UserDetails(usernameTyped, passwordTyped, displayNameTyped, profilePic);
                userDetails.setFireBaseToken(fireBaseToken);
                usersAPI.registerUser(userDetails, this, tvErrors, tvUsername, tvDisplayName, tvPassword, tvConfirmPassword, ivProfilePicture, serverAddress);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            // Check if the user has granted the notification permission after the pop-up
            if (isNotificationPermissionGranted()) {
                // Notification Permission granted - No need to do anything special...
            } else {
                // Notification Permission not granted - indicating that the user has rejected receiving notifications:
                Toast.makeText(this, "In the Pop-Up screen, you've rejected notifications!", Toast.LENGTH_SHORT).show();
            }
        }


        int REQUEST_CODE = 1;
        if (requestCode == REQUEST_CODE && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                //serverAddress = data.getExtras().getString("serverAddress");
                serverAddress = data.getStringExtra("serverAddress");
                //Toast.makeText(this, "here", Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, "Server address: " + serverAddress, Toast.LENGTH_LONG).show();

            }
        }
    }

    private String profilePicToBase64(ImageView ivProfilePicture){
        BitmapDrawable drawable = (BitmapDrawable) ivProfilePicture.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ImageView ivProfilePicture = findViewById(R.id.idProfilePicture);
        ivProfilePicture.setImageResource(R.drawable.ic_upload_photo);
        isProfilePictureSet = false; //necessary so it won't be falsely true due to previous register

        // Making a toast with the server address:
        //Toast.makeText(this, "Server address: " + serverAddress, Toast.LENGTH_LONG).show();
    }

    private Emitter.Listener msgReload = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            ChatsViewModel CVM = new ViewModelProvider((ViewModelStoreOwner) getApplicationContext()).get(ChatsViewModel.class);
            CVM.reload();
        }

    };
}