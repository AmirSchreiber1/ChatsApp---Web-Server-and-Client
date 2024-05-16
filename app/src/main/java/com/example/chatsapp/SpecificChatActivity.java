package com.example.chatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
// importing the Message class we've created!
import com.example.chatsapp.adapters.MessagesListAdapter;
import com.example.chatsapp.entities.Message;
import com.example.chatsapp.server_response.MessageToServer;
import com.example.chatsapp.viewmodels.ChatsViewModel;
import com.example.chatsapp.viewmodels.MessagesViewModel;
import com.google.android.material.button.MaterialButton;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SpecificChatActivity extends AppCompatActivity {
    private String serverAddress;
    private String token;
    private String chatID;
    private String logged_in_username;
    private MessagesViewModel messagesViewModel;
    private EditText etMessage;
    private MaterialButton sendButton;
    private Socket mSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_chat);

        //get serverAddress:
        serverAddress = getIntent().getExtras().getString("serverAddress");
        token = getIntent().getExtras().getString("token");
        chatID = getIntent().getExtras().getString("id");
        logged_in_username = getIntent().getStringExtra("username");

        //create the view model:
        messagesViewModel = new ViewModelProvider(this).get(MessagesViewModel.class);
        messagesViewModel.setRepository(this, serverAddress, token, chatID);

        try {
            mSocket = IO.socket("http://10.0.2.2:5000");
        } catch (URISyntaxException e) {
            Toast.makeText(this, "socketIOError!", Toast.LENGTH_SHORT).show();
        }
        mSocket.connect();
        mSocket.on("msg", msgReload);
        // Getting the list view:
        RecyclerView lstMessages = findViewById(R.id.lstMessages);

        // Create a MessagesListAdapter object, and set it as the adapter for the RecyclerView:
        final MessagesListAdapter adapter = new MessagesListAdapter(this, logged_in_username);
        lstMessages.setAdapter(adapter);


        // Set the layout manager for the RecyclerView:
        // In other words, this determines that the items will be displayed in a linear way, one after the other!
        lstMessages.setLayoutManager(new LinearLayoutManager(this));

        // when scrolling up (and reaching the top) in the chats screen, reload chats list and then stop the refreshing display.
        SwipeRefreshLayout refreshLayout = findViewById(R.id.messagesRefreshLayout);
        refreshLayout.setOnRefreshListener(() -> {
            messagesViewModel.reload(etMessage, sendButton);
            refreshLayout.setRefreshing(false);
        });

        // whenever the messages changes:
        messagesViewModel.get().observe(this, messages -> {
            List<Message> reversedList = messagesViewModel.get(chatID);
            Collections.reverse(reversedList);
            adapter.setMessages(reversedList); //assign adapter the messages of this chat
            refreshLayout.setRefreshing(false);
            //when opening a chat, scroll to bottom of messages (if any):
            if (!(messagesViewModel.get(chatID).isEmpty())) {
                lstMessages.smoothScrollToPosition(messagesViewModel.get(chatID).size());
            }
        });

        etMessage = findViewById(R.id.etMessage);
        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> {
            String message = etMessage.getText().toString();
            if (message.length() > 0 ) {
                MessageToServer messageToServer = new MessageToServer(message);
                messagesViewModel.sendMessage(messageToServer, messagesViewModel, etMessage, sendButton, mSocket);
                etMessage.setText(""); //clear edit text field
                lstMessages.smoothScrollToPosition(messagesViewModel.get(chatID).size());

/*
                mSocket.emit("msgSent","");
*/
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        messagesViewModel.reload(etMessage, sendButton);
    }

    private String getCurrentTime() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z (zzzz)");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+03:00"));
        return dateFormat.format(currentDate);
    }

    private Emitter.Listener msgReload = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            messagesViewModel.reload(etMessage, sendButton);
        }
    };
}