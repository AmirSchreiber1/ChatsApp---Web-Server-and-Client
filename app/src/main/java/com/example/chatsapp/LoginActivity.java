package com.example.chatsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatsapp.apis.UsersAPI;
import com.example.chatsapp.viewmodels.ChatsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    String serverAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //get serverAddress:
        serverAddress = getIntent().getExtras().getString("serverAddress");

        // Making a toast with the server address:
        //Toast.makeText(this, ("Server Address in LoginActivity UP: " + serverAddress), Toast.LENGTH_SHORT).show();

        FloatingActionButton fab = findViewById(R.id.fabSettings);
        SettingsFABOperation.setupFab(fab, this);

        //in case user changed server address in login screen and then goes back to signup screen, we want the change to be saved and not overriden by the default address.
        //to do so, we pass the current address to the signup screen:
        //Intent returnIntent = new Intent();
        //returnIntent.putExtra("serverAddress",serverAddress);
        // Making a toast with the data passed to the Signup Activity:
        //Toast.makeText(this, ("sending from LoginActivity: " + serverAddress), Toast.LENGTH_SHORT).show();
        //setResult(Activity.RESULT_OK, returnIntent);

        Button btnMoveToRegister = findViewById(R.id.idBtnMoveToRegister);
        btnMoveToRegister.setOnClickListener(view -> {
            // When clicking on the button, we want to move the Login Activity:
/*            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);*/

            Intent returnIntent = new Intent();
            returnIntent.putExtra("serverAddress",serverAddress);
            // Making a toast with the data passed to the Signup Activity:
            //Toast.makeText(this, ("11sending from LoginActivity: " + serverAddress), Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK, returnIntent);

            finish();
        });

        Button btnLogin = findViewById(R.id.idBtnLogin);
        btnLogin.setOnClickListener(view -> {
            // When clicking on the button, we want to check whether the fields inserted match the DB records,
            // and accordingly decide whether we should move to the Chats Activity:

            // 1. Checking if the fields inserted match DB records:
            // First, retrieving the fields inserted:
            TextView tvUsername = findViewById(R.id.idEdtUsername);
            String usernameTyped = tvUsername.getText().toString();

            TextView tvPassword = findViewById(R.id.idEdtPassword);
            String passwordTyped = tvPassword.getText().toString();


            // Checking whether the fields inserted are not empty:
            boolean wereUsernameAndPasswordInserted = !usernameTyped.isEmpty() && !passwordTyped.isEmpty();


            TextView tvErrors = findViewById(R.id.tvErrors);
            if (!wereUsernameAndPasswordInserted) {
                // 1. If the fields inserted are empty, we want to show an error message:
                tvErrors.setText("Please fill in both username and password");
                tvErrors.setVisibility(TextView.VISIBLE);
                return;
            } else {
                // Now, checking whether the fields inserted match DB records:
                boolean credentialsMatch = true;
                //create the userDetails object (which is converted to json by Gson and sent to the server by retrofit).
                UserDetails userDetails = new UserDetails(usernameTyped, passwordTyped, null, null);
                //attaching the fireBaseToken to the user who is trying to log into the app on this device
                if (!(getIntent().getExtras().getString("fireBaseToken") == null )) {
                userDetails.setFireBaseToken(getIntent().getExtras().getString("fireBaseToken"));
                }
                UsersAPI usersAPI = new UsersAPI(serverAddress);
                usersAPI.getToken(userDetails, this, tvErrors, tvUsername, tvPassword, usernameTyped, serverAddress); //get token is the method sending the data to the server and handling the response
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int REQUEST_CODE = 1;
        if (requestCode == REQUEST_CODE && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                serverAddress = data.getExtras().getString("serverAddress");
                //Toast.makeText(this, ("Server Address in LoginActivity: " + serverAddress), Toast.LENGTH_SHORT).show();
            }
        }
    }
}