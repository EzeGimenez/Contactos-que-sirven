package com.visoft.network;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class SpecificChatFragment extends Fragment {
    private String chatID;
    private List<Message> messageList;
    private DatabaseReference database;
    private User receiver;
    private ArrayAdapter adapter;

    //Componentes gráficas
    private ListView listView;
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
        }

        etChat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                Objects.requireNonNull(imm).hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        etChat.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (keyboardShown(etChat.getRootView())) {
                    listView.setSelection(adapter.getCount() - 1);
                } else {
                    //etChat.clearFocus();
                }
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

        DatabaseReference messagesRef = Database
                .getDatabase()
                .getReference(Constants.FIREBASE_MESSAGES_CONTAINER_NAME)
                .child(chatID);

        messagesRef.keepSynced(true);

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (messageList.size() == 0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Message message = ds.getValue(Message.class);
                        if (message != null) {
                            messageList.add(message);
                        }
                    }
                    setAdapter();
                } else {
                    Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                    DataSnapshot index = it.next();
                    while (it.hasNext()) {
                        index = it.next();
                    }
                    Message message = index.getValue(Message.class);
                    adapter.add(message);
                }
                listView.setSelection(adapter.getCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean keyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    private void setAdapter() {
        adapter = new ListViewChatsAdapter(getContext(), R.layout.message_sent_change, messageList);
        listView.setAdapter(adapter);
    }

    private class ListViewChatsAdapter extends ArrayAdapter<Message> {
        private List<Message> messages;
        private LayoutInflater inflater;

        public ListViewChatsAdapter(@NonNull Context context, int resource, @NonNull List<Message> messages) {
            super(context, resource, messages);
            inflater = LayoutInflater.from(context);
            this.messages = messages;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final ViewHolder holder;

            Message msg = messages.get(position);
            String authorUID = msg.getAuthor();

            if (authorUID.equals(receiver.getUid())) {
                //Received message

                //Previous one is same person
                if (position > 0 && messages.get(position - 1).getAuthor().equals(receiver.getUid())) {
                    convertView = inflater.inflate(R.layout.message_received_cont, null);
                } else {
                    convertView = inflater.inflate(R.layout.message_received_change, null);
                }
            } else {
                //Sent message
                //Previous one is same person
                if (position > 0 && !messages.get(position - 1).getAuthor().equals(receiver.getUid())) {
                    convertView = inflater.inflate(R.layout.message_sent_cont, null);
                } else {
                    convertView = inflater.inflate(R.layout.message_sent_change, null);
                }
            }

            holder = new ViewHolder();
            holder.tvText = convertView.findViewById(R.id.tvText);
            holder.tvTimeStamp = convertView.findViewById(R.id.tvTimeStamp);
            convertView.setTag(holder);


            holder.tvText.setText(msg.getText());
            holder.tvTimeStamp.setText(DateFormat.format("HH:mm",
                    msg.getTimeStamp()));

            return convertView;
        }

        private class ViewHolder {
            TextView tvText, tvTimeStamp;
        }

    }

}
