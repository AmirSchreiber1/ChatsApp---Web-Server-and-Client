package com.example.chatsapp.apis;

import android.widget.EditText;

import androidx.lifecycle.MutableLiveData;

import com.example.chatsapp.daos.MessagesDao;
import com.example.chatsapp.entities.Chat;
import com.example.chatsapp.entities.Message;
import com.example.chatsapp.server_response.ChatResponse;
import com.example.chatsapp.server_response.MessageResponse;
import com.example.chatsapp.server_response.MessageToServer;
import com.example.chatsapp.viewmodels.MessagesViewModel;
import com.google.android.material.button.MaterialButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessagesAPI {

    private MutableLiveData<List<Message>> messagesListData;
    private MessagesDao dao;
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;

    public MessagesAPI(MutableLiveData<List<Message>> messagesListData, MessagesDao dao, String serverAddress) {
        this.messagesListData = messagesListData;
        this.dao = dao;
        retrofit = new Retrofit.Builder().baseUrl(serverAddress).addConverterFactory(GsonConverterFactory.create()).build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void getMessagesOfChat(String token, String chatID, EditText etMessage, MaterialButton sendButton) {
        Call<List<MessageResponse>> call = webServiceAPI.getMessagesOfChat("Bearer " + token, chatID);
        call.enqueue(new Callback<List<MessageResponse>>() {
            @Override
            public void onResponse(Call<List<MessageResponse>> call, Response<List<MessageResponse>> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        dao.clear(chatID);
                        List<MessageResponse> messageResponses = response.body();
                        if (messageResponses != null) {
                            Message[] messages = new Message[messageResponses.size()];
                            int i = 0;
                            for (MessageResponse messageResponse : messageResponses) {
                                Message message = new Message(
                                        messageResponse.getSender().getUsername(),
                                        messageResponse.getCreated(),
                                        messageResponse.getContent()
                                );
                                message.setChatID(chatID);
                                messages[i] = message;
                                i++;
                            }
                            dao.insert(messages);
                        }
                        messagesListData.postValue(dao.get(chatID));
                    }).start();
                } else {
                    //if here, chat was deleted.
                    //disable editText box for sending message and sendButton functionality until another chat is chosen:
                    etMessage.setEnabled(false);
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<List<MessageResponse>> call, Throwable t) {
                //must override hence is here
            }
        });
    }

    public void sendMessage(MessageToServer messageToServer, String token, String chatID, MessagesViewModel messagesViewModel, EditText etMessage, MaterialButton sendButton, Socket socket) {
        Call<Void> call = webServiceAPI.sendMessage("Bearer " + token, chatID, messageToServer);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    messagesViewModel.reload(etMessage, sendButton);
                    socket.emit("msgSent","");
                } else {
                    //if here, chat was deleted.
                    //disable editText box for sending message and sendButton functionality until another chat is chosen:
                    etMessage.setEnabled(false);
                    sendButton.setEnabled(false);
                }
            }
            @Override
            public void onFailure(Call<Void>call, Throwable t) {
                //must override hence is here
            }
        });
    }
}
