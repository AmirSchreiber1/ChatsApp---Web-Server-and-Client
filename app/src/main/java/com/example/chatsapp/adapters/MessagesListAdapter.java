package com.example.chatsapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatsapp.R;
import com.example.chatsapp.entities.Message;

import java.util.List;

public class MessagesListAdapter extends RecyclerView.Adapter<MessagesListAdapter.MessageViewHolder>{
    class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTimeCreated;
        private final TextView tvContent;
        private final LinearLayout wholeMessageLayout;

        private MessageViewHolder(View itemView) {
            super(itemView);
            tvTimeCreated = itemView.findViewById(R.id.tvTimeCreated);
            tvContent = itemView.findViewById(R.id.tvContent);
            wholeMessageLayout = itemView.findViewById(R.id.wholeMessageLayout);
        }
    }

    private final LayoutInflater mInflater;
    private List<Message> messages; // Cached copy of messages
    Context context;
    String logged_in_username;

    public MessagesListAdapter(Context context, String logged_in_username) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.logged_in_username = logged_in_username;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.sent_message_layout, parent, false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        if (messages != null) {
            final Message current = messages.get(position);
            // Determining the color of the message - green or white - according to the sender:
            if (current.getSenderUserName().equals(logged_in_username)) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.wholeMessageLayout.getLayoutParams();
                params.leftMargin = 550;
                params.width = 500;
                holder.wholeMessageLayout.setLayoutParams(params);
                int sentMessageColor = ResourcesCompat.getColor(context.getResources(), R.color.sentMessageColor, null);
                holder.wholeMessageLayout.setBackgroundColor(sentMessageColor);
                holder.tvContent.setBackgroundColor(sentMessageColor);
            } else {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.wholeMessageLayout.getLayoutParams();
                params.leftMargin = 10;
                params.width = 500;
                holder.wholeMessageLayout.setLayoutParams(params);
                holder.wholeMessageLayout.setBackgroundColor(0xFFFFFFFF);
                holder.tvContent.setBackgroundColor(0xFFFFFFFF);
            }
            String messageTime = current.getTimeCreated().substring(16,21);
            holder.tvTimeCreated.setText(messageTime);
            holder.tvContent.setText(current.getContent());
        }
    }

    public void setMessages(List<Message> s){
        messages = s;
        // This method NOTIFIES THE ADAPTER that the data has changed, and that the RecyclerView should be updated!
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (messages != null)
            return messages.size();
        else return 0;
    }

    public List<Message> getMessages() { return messages; }

}
