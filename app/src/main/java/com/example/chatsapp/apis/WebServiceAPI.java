package com.example.chatsapp.apis;

import com.example.chatsapp.UserDetails;
import com.example.chatsapp.server_response.ChatResponse;
import com.example.chatsapp.server_response.MessageResponse;
import com.example.chatsapp.server_response.MessageToServer;
import com.example.chatsapp.server_response.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WebServiceAPI {
    @GET("Chats")
    Call<List<ChatResponse>> getChats(@Header("Authorization") String token);

    @POST("Chats")
    Call<Void> addContact(@Header("Authorization") String token, @Body User user);

    @DELETE("Chats/{id}")
    Call<Void> deleteChatByID(@Header("Authorization") String token ,@Path("id") String chatID);

    @POST("Chats/{id}/Messages")
    Call<Void> sendMessage(@Header("Authorization") String token, @Path("id") String chatID, @Body MessageToServer message);

    @GET("Chats/{id}/Messages")
    Call<List<MessageResponse>> getMessagesOfChat(@Header("Authorization") String token, @Path("id") String chatID);

    @POST("Tokens")
    Call<String> getToken(@Body UserDetails userDetails);

    @POST("Users")
    Call<Void> registerUser(@Body UserDetails userDetails);
}
