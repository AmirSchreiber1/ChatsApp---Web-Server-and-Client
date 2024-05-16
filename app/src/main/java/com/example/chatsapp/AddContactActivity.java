package com.example.chatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatsapp.daos.ChatsDao;
import com.example.chatsapp.entities.Chat;
import com.example.chatsapp.server_response.User;
import com.example.chatsapp.viewmodels.ChatsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AddContactActivity extends AppCompatActivity {
    private Chat chat;
    String serverAddress;
    String logged_in_username;
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        //get serverAddress:
        serverAddress = getIntent().getExtras().getString("serverAddress");
        //get logged-in user name:
        logged_in_username = getIntent().getExtras().getString("username");
        //get token:
        token = getIntent().getExtras().getString("token");

        ChatsViewModel chatsViewModel = new ViewModelProvider(this).get(ChatsViewModel.class);
        chatsViewModel.setRepository(this, serverAddress, token);

        Button btnAddContact = findViewById(R.id.idBtnAddContact);
        btnAddContact.setOnClickListener(view -> {
            // When clicking on the button, we want to add the contact:
            EditText idEdtContactUserName = findViewById(R.id.idEdtContactUserName);
            String contact_username = idEdtContactUserName.getText().toString();
            if (contact_username.length() > 0) { //if user has indeed typed another user's username:
                TextView tvErrors = findViewById(R.id.tvErrors);
                String chatUsername = getIntent().getStringExtra("logged_in_username");
                if (chatUsername.equals(contact_username)) {
                    tvErrors.setText("No self-contact allowed.");
                    tvErrors.setBackgroundColor(getResources().getColor(R.color.errorColor));
                    tvErrors.setVisibility(TextView.VISIBLE);
                } else { //if here, user typed a username which isn't is own.
                    // to know if there is such a user, we need to send the data to the server:
                    User user = new User(contact_username, null, null);
                    chatsViewModel.addContact(token, user, tvErrors, idEdtContactUserName, this); // this function also changes displayed error message according to the response
                }
            }
        });
    }
}