package com.example.chatsapp.apis;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.chatsapp.ChatsActivity;
import com.example.chatsapp.LoginActivity;
import com.example.chatsapp.R;
import com.example.chatsapp.UserDetails;
import com.example.chatsapp.entities.Chat;
import com.example.chatsapp.viewmodels.ChatsViewModel;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class UsersAPI {
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;

    public UsersAPI(String serverAddress) {
        retrofit = new Retrofit.Builder().baseUrl(serverAddress).addConverterFactory(ScalarsConverterFactory.create()).addConverterFactory(GsonConverterFactory.create()).build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void getToken(UserDetails userDetails, Context context, TextView tvErrors, TextView tvUsername, TextView tvPassword, String usernameTyped, String serverAddress) {
        Call<String> call = webServiceAPI.getToken(userDetails);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) { //user logged in successfully
                    userDetails.setToken(response.body());
                    tvErrors.setVisibility(TextView.INVISIBLE); //clear error message if any
                    //clear filled fields:
                    tvUsername.setText((""));
                    tvPassword.setText("");
                    // 3. Moving to the Chats Activity:
                    Intent i = new Intent(context, ChatsActivity.class);


                    // Passing the logged-in username, serverAddress and token to the Chats Activity:
                    i.putExtra("username", usernameTyped);
                    i.putExtra("serverAddress", serverAddress);
                    i.putExtra("token", userDetails.getToken());

                    //clearing previous user chats from dao so it won't be seen (even if for only a second) by the current user:
                    ChatsViewModel chatsViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ChatsViewModel.class);
                    chatsViewModel.setRepository(context, serverAddress, userDetails.getToken());
                    chatsViewModel.clearRoomChats();

                    int REQUEST_CODE = 1;
                    ((Activity)context).startActivityForResult(i, REQUEST_CODE);
                    //context.startActivity(i);
                } else { //wrong credentials:
                    userDetails.setToken(null);
                    tvErrors.setText("Wrong username or password");
                    tvErrors.setVisibility(TextView.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //must override hence is here
            }
        });
    }

    public void registerUser(UserDetails userDetails, Context context, TextView tvErrors, TextView tvUsername, TextView tvDisplayName, TextView tvPassword, TextView tvConfirmPassword, ImageView ivProfilePicture, String serverAddress) {
        Call<Void> call = webServiceAPI.registerUser(userDetails);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) { //user registered successfully
                    // If here, user filled the registration form correctly, and there isn't a user with the same username
                    // first, clear filled fields (so if user comes back to this page, he won't have to do it by himself):
                    tvUsername.setText("");
                    tvDisplayName.setText("");
                    tvPassword.setText("");
                    tvConfirmPassword.setText("");
                    ivProfilePicture.setImageResource(R.drawable.ic_upload_photo);
                    // moving to the Login Activity:
                    Intent i = new Intent(context, LoginActivity.class);
                    i.putExtra("serverAddress", serverAddress);
                    int REQUEST_CODE = 1;
                    ((Activity)context).startActivityForResult(i, REQUEST_CODE);
                } else { //such a user already exists:
                    tvErrors.setText("Username "+ userDetails.getUsername() + " already exists");
                    tvErrors.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //must override hence is here
            }
        });
    }


}
