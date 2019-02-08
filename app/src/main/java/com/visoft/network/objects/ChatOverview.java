package com.visoft.network.objects;

import android.text.format.DateFormat;
import android.view.View;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.visoft.network.R;
import com.visoft.network.tab_chats.AllChatsFragment;
import com.visoft.network.util.Constants;
import com.visoft.network.util.GlideApp;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;

public class ChatOverview extends AbstractFlexibleItem<ViewHolderChatOverview> implements Serializable {
    private String receiver, author, chatID;
    private String lastMessage;
    private long timeStamp;
    private boolean finished;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiverUID(String receiverUID) {
        this.receiver = receiverUID;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChatOverview) {
            return ((ChatOverview) o).getAuthor().equals(author) && ((ChatOverview) o).getReceiver().equals(receiver);
        }
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_overview_layout;
    }

    @Override
    public ViewHolderChatOverview createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolderChatOverview(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolderChatOverview holder, int position, List<Object> payloads) {
        AllChatsFragment.ChatOverViewFlexibleAdapter customA = (AllChatsFragment.ChatOverViewFlexibleAdapter) adapter;

        User other;
        Map<String, User> map = customA.getAct().getMapUIDUser();

        other = map.get(receiver);
        if (other == null) {
            other = map.get(author);
        }

        holder.tvName.setText(other != null ? other.getUsername() : null);
        holder.tvMessage.setText(getLastMessage());
        holder.tvTimeStamp.setText(DateFormat.format("HH:mm",
                getTimeStamp()));

        if (other != null && other.getHasPic()) {
            StorageReference storage = FirebaseStorage.getInstance().getReference();
            StorageReference userRef = storage.child(Constants.FIREBASE_USERS_PRO_CONTAINER_NAME + "/" + other.getUid() + other.getImgVersion() + ".jpg");
            GlideApp.with(customA.getAct())
                    .load(userRef)
                    .into(holder.ivPic);
        } else {
            holder.ivPic.setImageDrawable(customA.getAct().getResources().getDrawable(R.drawable.profile_pic));
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}