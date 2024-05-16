package com.example.chatsapp.entities;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.chatsapp.R;

import org.jetbrains.annotations.NotNull;

@Entity
    public class Chat {

        @PrimaryKey @NotNull
        private String id; //isn't auto-generated as it is set by the chatID in the server.
        private String displayName;
        private String profilePic;
        private String lastMessageContent;

        private String lastMessageTime;

        public Chat(String id, String displayName, String lastMessageContent, String lastMessageTime, String profilePic) {
            this.id = id;
            this.displayName = displayName;
            this.lastMessageContent = lastMessageContent;
            this.lastMessageTime = lastMessageTime;
            this.profilePic = profilePic;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getLastMessageContent() {
            return lastMessageContent;
        }

        public void setLastMessageContent(String lastMessageContent) {
            this.lastMessageContent = lastMessageContent;
        }

        public String getLastMessageTime() {
            return lastMessageTime;
        }

        public void setLastMessageTime(String lastMessageTime) {
            this.lastMessageTime = lastMessageTime;
        }
    }
