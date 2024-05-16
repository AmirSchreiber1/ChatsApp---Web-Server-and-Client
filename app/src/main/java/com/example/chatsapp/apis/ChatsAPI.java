package com.example.chatsapp.apis;

import android.app.Application;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;

import com.example.chatsapp.R;
import com.example.chatsapp.daos.ChatsDao;
import com.example.chatsapp.entities.Chat;
import com.example.chatsapp.server_response.ChatResponse;
import com.example.chatsapp.server_response.User;
import com.example.chatsapp.viewmodels.ChatsViewModel;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatsAPI {
    private MutableLiveData<List<Chat>> chatsListData;
    private ChatsDao dao;
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;

    public ChatsAPI(MutableLiveData<List<Chat>> chatsListData, ChatsDao dao, String serverAddress) {
        this.chatsListData = chatsListData;
        this.dao = dao;
        retrofit = new Retrofit.Builder().baseUrl(serverAddress).addConverterFactory(GsonConverterFactory.create()).build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void getChats(String token) {
        Call<List<ChatResponse>> call = webServiceAPI.getChats("Bearer " + token);
        call.enqueue(new Callback<List<ChatResponse>>() {
            @Override
            public void onResponse(Call<List<ChatResponse>> call, Response<List<ChatResponse>> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        dao.clear();
                        List<ChatResponse> chatResponses = response.body();
                        if (chatResponses != null) {
                            for (ChatResponse chatResponse : chatResponses) {
                                String lastMessageContent = null;
                                String lastMessageTime = null;
                                if (chatResponse.getLastMessage() != null) {
                                    lastMessageContent = chatResponse.getLastMessage().getContent();
                                    lastMessageTime = chatResponse.getLastMessage().getCreated();
                                }
                                Chat chat = new Chat(chatResponse.getId(), chatResponse.getUser().getDisplayName(), lastMessageContent, lastMessageTime, chatResponse.getUser().getProfilePic());
                                dao.insert(chat);
                            }
                        }
                        chatsListData.postValue(dao.index());
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<List<ChatResponse>> call, Throwable t) {
                //must override hence is here
            }
        });
    }

    public void addContact(String token, User user, TextView tvErrors, EditText idEdtContactUserName, Context context) {
        Call<Void> call = webServiceAPI.addContact("Bearer " + token, user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    tvErrors.setBackgroundColor(context.getResources().getColor(R.color.sentMessageColor));
                    tvErrors.setText("Contact added successfully");
                    tvErrors.setVisibility(TextView.VISIBLE);
                    idEdtContactUserName.setText(""); //clear addContact field after successfully adding a user
                } else { //no such user exists:
                    tvErrors.setBackgroundColor(context.getResources().getColor(R.color.errorColor));
                    tvErrors.setText("The user you are referring to does not exist in our records");
                    tvErrors.setVisibility(TextView.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //must override hence is here
            }
        });
    }

    public void deleteChatByID(String token, String id, ChatsViewModel chatsViewModel) {
        Call<Void> call = webServiceAPI.deleteChatByID("Bearer " + token, id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    chatsViewModel.reload();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //must override hence is here
            }
        });
    }

}
