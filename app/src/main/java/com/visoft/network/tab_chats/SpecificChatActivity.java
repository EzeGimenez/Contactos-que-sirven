package com.visoft.network.tab_chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.visoft.network.R;
import com.visoft.network.funcionalidades.GsonerMessages;
import com.visoft.network.funcionalidades.HolderCurrentAccountManager;
import com.visoft.network.funcionalidades.LoadingScreen;
import com.visoft.network.funcionalidades.Messenger;
import com.visoft.network.objects.Message;
import com.visoft.network.objects.User;
import com.visoft.network.objects.ViewHolderChats;
import com.visoft.network.profiles.ProfileActivity;
import com.visoft.network.util.Constants;
import com.visoft.network.util.Database;
import com.visoft.network.util.GlideApp;

import de.hdodenhof.circleimageview.CircleImageView;


public class SpecificChatActivity extends AppCompatActivity {
    public static boolean isRunning;
    private User receiver;
    private FirebaseRecyclerAdapter<String, ViewHolderChats> recyclerViewAdapter;
    private LoadingScreen loadingScreen;

    //Componentes gráficas
    private RecyclerView recyclerView;

    private static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specific_chat_activity);

        loadingScreen = new LoadingScreen(this, (ViewGroup) findViewById(R.id.rootView));
        receiver = (User) getIntent().getSerializableExtra("receiver");

        DatabaseReference database = Database.getDatabase().getReference();
        recyclerView = findViewById(R.id.listViewSpecificChat);
        loadingScreen.show();

        new Messenger(this, HolderCurrentAccountManager.getCurrent(null).getCurrentUser(1).getUid(), receiver.getUid(), (ViewGroup) findViewById(R.id.rootView), recyclerView, database);

        ((TextView) findViewById(R.id.tvReceiver)).setText(receiver.getUsername());
        CircleImageView ivPic = findViewById(R.id.ivPic);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (receiver.getIsPro()) {
            findViewById(R.id.ContainerReceiver).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SpecificChatActivity.this, ProfileActivity.class);
                    intent.putExtra("user", receiver);
                    startActivity(intent);
                }
            });
        }

        String chatID = getIntent().getStringExtra("chatid");
        if (receiver.getHasPic()) {
            StorageReference storage = FirebaseStorage.getInstance().getReference();

            StorageReference userRef = storage.child(Constants.FIREBASE_USERS_PRO_CONTAINER_NAME + "/" + receiver.getUid() + receiver.getImgVersion() + ".jpg");
            GlideApp.with(this)
                    .load(userRef)
                    .into(ivPic);
        } else {
            ivPic.setImageDrawable(getResources().getDrawable(R.drawable.profile_pic));
        }

        final DatabaseReference messagesRef = Database
                .getDatabase()
                .getReference(Constants.FIREBASE_MESSAGES_CONTAINER_NAME)
                .child(chatID);

        messagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recyclerViewAdapter = new ListViewChatsAdapter(String.class, 0, ViewHolderChats.class, messagesRef);
                recyclerView.setLayoutManager(new LinearLayoutManager(SpecificChatActivity.this));
                recyclerView.setAdapter(recyclerViewAdapter);
                loadingScreen.hide();
                recyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        recyclerView.smoothScrollToPosition(recyclerViewAdapter.getItemCount());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final View activityRootView = findViewById(R.id.rootView);
        activityRootView.getViewTreeObserver().

                addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                        if (heightDiff > dpToPx(getApplication(), 200)) { // if more than 200 dp, it's probably a keyboard...
                            recyclerView.smoothScrollToPosition(recyclerViewAdapter.getItemCount());
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        isRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class ListViewChatsAdapter extends FirebaseRecyclerAdapter<String, ViewHolderChats> {
        private Gson gson;
        private LayoutInflater inflater;

        ListViewChatsAdapter(Class<String> modelClass, int modelLayout, Class<ViewHolderChats> viewHolderClass, Query ref) {
            super(modelClass, modelLayout, viewHolderClass, ref);
            gson = GsonerMessages.getGson();
        }

        @NonNull
        @Override
        public ViewHolderChats onCreateViewHolder(ViewGroup parent, int viewType) {
            inflater = LayoutInflater.from(parent.getContext());
            switch (viewType) {
                case 0: //received message, previous one same person
                    return new ViewHolderChats(inflater.inflate(R.layout.message_received_cont, parent, false));
                case 1: //received message, previous one other person
                    return new ViewHolderChats(inflater.inflate(R.layout.message_received_change, parent, false));
                case 2: //sent message, previous one same person
                    return new ViewHolderChats(inflater.inflate(R.layout.message_sent_cont, parent, false));
                default: //sent message, previous one is user
                    return new ViewHolderChats(inflater.inflate(R.layout.message_sent_change, parent, false));
            }
        }

        @Override
        protected void populateViewHolder(ViewHolderChats holder, String str, int position) {
            Message msg = gson.fromJson(str, Message.class);

            msg.fillHolder(inflater.getContext(), holder);

            holder.setTimeStamp(msg.getTimeStamp());
        }

        @Override
        public int getItemViewType(int position) {
            Message msg = gson.fromJson(getItem(position), Message.class);

            String authorUID = msg.getAuthor();
            if (authorUID.equals(receiver.getUid())) { //Received message
                if (position > 0 && gson.fromJson(getItem(position - 1), Message.class).getAuthor().equals(receiver.getUid())) {
                    return 0;
                } else {
                    return 1;
                }
            } else { //Sent Message
                if (position > 0 && !gson.fromJson(getItem(position - 1), Message.class).getAuthor().equals(receiver.getUid())) {
                    return 2;
                } else {
                    return 3;
                }
            }
        }
    }
}