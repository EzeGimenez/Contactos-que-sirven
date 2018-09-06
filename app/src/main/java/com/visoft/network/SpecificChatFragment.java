package com.visoft.network;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.visoft.network.Objects.ChatOverview;
import com.visoft.network.Objects.Message;
import com.visoft.network.Objects.User;
import com.visoft.network.Util.Constants;
import com.visoft.network.Util.Database;
import com.visoft.network.Util.GlideApp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class SpecificChatFragment extends Fragment {
    private String chatID;
    private List<Message> messageList;
    private DatabaseReference database;
    private User receiver;
    private FirebaseRecyclerAdapter<Message, ViewHolderChats> adapter1;
    private DatabaseReference messagesRef;

    //Componentes gráficas
    private RecyclerView listView;
    private FloatingActionButton btnSend;
    private EditText etChat;
    private ConstraintLayout msgContainer;
    private CircleImageView ivPic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specific_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatID = getArguments().getString("chatid");
        receiver = (User) getArguments().getSerializable("receiver");
        database = Database.getDatabase().getReference();

        listView = view.findViewById(R.id.listViewSpecificChat);
        ((TextView) view.findViewById(R.id.tvReceiver)).setText(receiver.getUsername());
        btnSend = view.findViewById(R.id.btnSend);
        msgContainer = view.findViewById(R.id.msgContainer);
        etChat = view.findViewById(R.id.etChat);
        ivPic = view.findViewById(R.id.ivPic);

        view.findViewById(R.id.ContainerReceiver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("user", receiver);
                startActivity(intent);
            }
        });

        if (receiver.getHasPic()) {
            StorageReference storage = FirebaseStorage.getInstance().getReference();

            StorageReference userRef = storage.child(Constants.FIREBASE_USERS_CONTAINER_NAME + "/" + receiver.getUid() + receiver.getImgVersion() + ".jpg");
            GlideApp.with(getContext())
                    .load(userRef)
                    .into(ivPic);
        } else {
            ivPic.setImageDrawable(getResources().getDrawable(R.drawable.profile_pic));
        }

        //Hides the keyboard when the edittext loses focus
        etChat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                Objects.requireNonNull(imm).hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(etChat.getText().toString());
            }
        });

        populateMessages();
    }

    private void sendMessage(final String s) {
        etChat.setText("");
        if (s != null && s.length() > 0) {
            final String uidSender = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final String uidReceiver = receiver.getUid();

            final ChatOverview chatOverview = new ChatOverview();

            database
                    .child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                    .child(uidSender)
                    .child(uidReceiver)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ChatOverview chatOverview1 = dataSnapshot.getValue(ChatOverview.class);
                            String chatID;
                            if (chatOverview1 == null) {
                                chatID = database
                                        .child(Constants.FIREBASE_MESSAGES_CONTAINER_NAME)
                                        .push().getKey();
                            } else {
                                chatID = chatOverview1.getChatID();
                            }
                            chatOverview.setAuthor(uidSender);
                            chatOverview.setReceiverUID(uidReceiver);
                            chatOverview.setChatID(chatID);
                            chatOverview.setLastMessage(s);
                            chatOverview.setTimeStamp(new Date().getTime());
                            saveInChats(chatOverview);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void saveInChats(ChatOverview chatOverview) {
        database
                .child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                .child(chatOverview.getAuthor())
                .child(chatOverview.getReceiver())
                .setValue(chatOverview);

        database
                .child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                .child(chatOverview.getReceiver())
                .child(chatOverview.getAuthor())
                .setValue(chatOverview);

        Message message = new Message();
        message.setAuthor(chatOverview.getAuthor());
        message.setTimeStamp(chatOverview.getTimeStamp());
        message.setText(chatOverview.getLastMessage());

        database
                .child(Constants.FIREBASE_MESSAGES_CONTAINER_NAME)
                .child(chatOverview.getChatID())
                .push()
                .setValue(message);
    }

    private void populateMessages() {
        messageList = new ArrayList<>();

        messagesRef = Database
                .getDatabase()
                .getReference(Constants.FIREBASE_MESSAGES_CONTAINER_NAME)
                .child(chatID);

        messagesRef.keepSynced(true);
        setAdapter();
    }

    private void setAdapter() {
        adapter1 = new ListViewChatsAdapter(Message.class, 0, ViewHolderChats.class, messagesRef);

        // Scroll to bottom on new messages
        adapter1.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                listView.smoothScrollToPosition(adapter1.getItemCount());
            }
        });

        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(adapter1);
    }

 /*   private boolean keyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }*/

    private class ViewHolderChats extends RecyclerView.ViewHolder {
        TextView tvText, tvTimeStamp;

        public ViewHolderChats(View itemView) {
            super(itemView);

            tvText = itemView.findViewById(R.id.tvText);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
        }
    }

    private class ListViewChatsAdapter extends FirebaseRecyclerAdapter<Message, ViewHolderChats> {

        public ListViewChatsAdapter(Class<Message> modelClass, int modelLayout, Class<ViewHolderChats> viewHolderClass, Query ref) {
            super(modelClass, modelLayout, viewHolderClass, ref);
        }

        @NonNull
        @Override
        public ViewHolderChats onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            switch (viewType) {
                case 0: //received message, previous one same person
                    return new ViewHolderChats(inflater.inflate(R.layout.message_received_cont, parent, false));
                case 1: //received message, previous one other person
                    return new ViewHolderChats(inflater.inflate(R.layout.message_received_change, parent, false));
                case 2: //sent message, previous one same person
                    return new ViewHolderChats(inflater.inflate(R.layout.message_sent_cont, parent, false));
                case 3: //sent message, previous one is user
                    return new ViewHolderChats(inflater.inflate(R.layout.message_sent_change, parent, false));
                default:
                    return null;
            }
        }

        @Override
        protected void populateViewHolder(ViewHolderChats holder, Message msg, int position) {
            holder.tvText.setText(msg.getText());
            holder.tvTimeStamp.setText(DateFormat.format("HH:mm",
                    msg.getTimeStamp()));
        }

        @Override
        public int getItemViewType(int position) {
            Message msg = getItem(position);

            String authorUID = msg.getAuthor();
            if (authorUID.equals(receiver.getUid())) { //Received message
                if (position > 0 && getItem(position - 1).getAuthor().equals(receiver.getUid())) {
                    return 0;
                } else {
                    return 1;
                }
            } else { //Sent Message
                if (position > 0 && !getItem(position - 1).getAuthor().equals(receiver.getUid())) {
                    return 2;
                } else {
                    return 3;
                }
            }
        }


    }

}
