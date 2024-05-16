package com.example.chatsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.chatsapp.adapters.ChatsListAdapter;
import com.example.chatsapp.daos.ChatsDao;
import com.example.chatsapp.entities.Chat;
import com.example.chatsapp.repositories.MessagesRepository;
import com.example.chatsapp.viewmodels.ChatsViewModel;
import com.example.chatsapp.viewmodels.MessagesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatsActivity extends AppCompatActivity implements RecyclerViewInterface {
    private LocalDatabase db;
    private Chat chat;
    private ChatsListAdapter adapter;

    String serverAddress;
    String logged_in_username;
    String token;

    ChatsViewModel chatsViewModel;
    private Socket mSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        //get serverAddress:
        serverAddress = getIntent().getExtras().getString("serverAddress");
        // Making a toast with the server address:
        //Toast.makeText(this, ("Server Address in ChatsActivity: " + serverAddress), Toast.LENGTH_SHORT).show();

        FloatingActionButton fab = findViewById(R.id.fabSettings);
        SettingsFABOperation.setupFab(fab, this);

        //get logged-in user name:
        logged_in_username = getIntent().getExtras().getString("username");
        //get token:
        token = getIntent().getExtras().getString("token");


        //create the view model:
        chatsViewModel = new ViewModelProvider(this).get(ChatsViewModel.class);
        chatsViewModel.setRepository(this, serverAddress, token);

        try {
            mSocket = IO.socket("http://10.0.2.2:5000");
        } catch (URISyntaxException e) {
            Toast.makeText(this, "socketIOError!", Toast.LENGTH_SHORT).show();
        }
        mSocket.connect();
        mSocket.on("msg", msgReload);

        //clear previously saved messages from ROOM local database:
        MessagesRepository messagesRepository = new MessagesRepository(this, serverAddress, token, null);
        messagesRepository.clearLocalMessages();

        // Getting the list view:
        RecyclerView lstChats = findViewById(R.id.lstChats);

        // Create a ChatsListAdapter object, and set it as the adapter for the RecyclerView:
        adapter = new ChatsListAdapter(this, this);
        lstChats.setAdapter(adapter);

        // Set the layout manager for the RecyclerView:
        // In other words, this determines that the items will be displayed in a linear way, one after the other!
        lstChats.setLayoutManager(new LinearLayoutManager(this));

        // when scrolling up (and reaching the top) in the chats screen, reload chats list and then stop the refreshing display.
        SwipeRefreshLayout refreshLayout = findViewById(R.id.chatsRefreshLayout);
        refreshLayout.setOnRefreshListener(() -> {
            chatsViewModel.reload();
            refreshLayout.setRefreshing(false);
        });

        // whenever the chats list changes:
        chatsViewModel.get().observe(this, chats -> {
            adapter.setChats(chats);
            refreshLayout.setRefreshing(false);
        });

        //Add contact:
        // When clicking on the 'Add Contact' FAB, we want to open the AddContactActivity:
        FloatingActionButton floatingActionButton = findViewById(R.id.fabAddContact);
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChatsActivity.this, AddContactActivity.class);
            intent.putExtra("logged_in_username", logged_in_username);
            intent.putExtra("serverAddress", serverAddress);
            intent.putExtra("token", token);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatsViewModel.reload();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, SpecificChatActivity.class);
        //pass the chatID:
        chat = chatsViewModel.get().getValue().get(position);
        intent.putExtra("id", chat.getId());
        //pass the logged_in_username:
        String logged_in_username = getIntent().getStringExtra("username");
        intent.putExtra("username", logged_in_username);
        //pass the server address:
        intent.putExtra("serverAddress", serverAddress);
        //pass the token:
        intent.putExtra("token", token);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {
        chat = chatsViewModel.get().getValue().get(position);
        chatsViewModel.deleteChat(token, chat.getId(), chatsViewModel);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int REQUEST_CODE = 1;
        if (requestCode == REQUEST_CODE && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                serverAddress = data.getExtras().getString("serverAddress");
                //finish();

                // Added according to Hemi's answer in the Moodle Forum:
                //Intent returnIntent = new Intent();
                //returnIntent.putExtra("serverAddress",serverAddress);
                //setResult(Activity.RESULT_OK, returnIntent);
                //finish();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("serverAddress",serverAddress);
                // Making a toast with the data passed to the Signup Activity:
                //Toast.makeText(this, ("11sending from ChatsActivity: " + serverAddress), Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK, returnIntent);

                finish();
            }
        }
    }
    private Emitter.Listener msgReload = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            chatsViewModel.reload();
        }
    };
}