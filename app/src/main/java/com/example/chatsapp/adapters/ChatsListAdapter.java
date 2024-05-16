package com.example.chatsapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chatsapp.R;
import com.example.chatsapp.RecyclerViewInterface;
import com.example.chatsapp.entities.Chat;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ChatViewHolder> {
    Context context;
    // For the onItemClick():
    private final RecyclerViewInterface recyclerViewInterface;

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDisplayName;
        private final TextView tvLastMessage;
        private final TextView tvTimeLastMessageSent;
        private final ImageView tvProfilePic;

        private ChatViewHolder(View itemView) {
            super(itemView);
            tvDisplayName = itemView.findViewById(R.id.tvDisplayName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTimeLastMessageSent = itemView.findViewById(R.id.tvTimeLastMessageSent);
            tvProfilePic = itemView.findViewById(R.id.tvProfilePic);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemLongClick(position);
                }
                return true;
            });
        }
    }

    private final LayoutInflater mInflater;
    private List<Chat> chats; // Cached copy of chats

    public ChatsListAdapter(Context context, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.chat_layout, parent, false);
        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        if (chats != null) {
            final Chat current = chats.get(position);
            //holder.tvUsername.setText(current.getUsername());
            holder.tvDisplayName.setText(current.getDisplayName());
            holder.tvLastMessage.setText(current.getLastMessageContent());
            String lastMessageTime = null;
            if (current.getLastMessageTime() != null) {
                lastMessageTime = current.getLastMessageTime().substring(16,21);
            }
            holder.tvTimeLastMessageSent.setText(lastMessageTime);
            Bitmap profilePic = getImageBitmapFromBase64(current);
            holder.tvProfilePic.setImageBitmap(profilePic);
        }
    }

    public void setChats(List<Chat> s) {
        chats = s;
        // This method NOTIFIES THE ADAPTER that the data has changed, and that the RecyclerView should be updated!
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (chats != null) return chats.size();
        else return 0;
    }

    public List<Chat> getChats() {
        return chats;
    }

    private Bitmap getImageBitmapFromBase64(Chat chat) {
        String base64String = chat.getProfilePic();
        if (base64String.substring(0,4).equals("data")) {
            base64String = chat.getProfilePic().substring(22);
        }
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
