package com.visoft.network.tab_chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.visoft.network.R;
import com.visoft.network.funcionalidades.AccountManager;
import com.visoft.network.funcionalidades.GsonerUser;
import com.visoft.network.funcionalidades.HolderCurrentAccountManager;
import com.visoft.network.objects.ChatOverview;
import com.visoft.network.objects.User;
import com.visoft.network.objects.UserNormal;
import com.visoft.network.objects.UserPro;
import com.visoft.network.util.Constants;
import com.visoft.network.util.Database;
import com.visoft.network.util.GlideApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllChatsFragment extends Fragment {
    private DatabaseReference database;
    private List<String> chatUIDS;
    private List<ChatOverview> chatOverviews;
    private HashMap<String, User> mapUIDUser;
    private AccountManager accountManager;
    private User current;

    //Componentes gráficas
    private ListView listView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        database = Database.getDatabase().getReference();
        accountManager = HolderCurrentAccountManager.getCurrent(new AccountManager.ListenerRequestResult() {
            @Override
            public void onRequestResult(boolean result, int requestCode, Bundle data) {
                if (current == null && result) {
                    current = (User) data.get("user");
                    populateChats();
                }
            }
        });
        current = accountManager.getCurrentUser(1);
        if (current != null) {
            populateChats();
        }

        return inflater.inflate(R.layout.fragment_all_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.listViewChats);
    }

    private void populateChats() {
        final DatabaseReference userChatRef = database
                .child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                .child(current.getUid());

        Query q = userChatRef;
        q.keepSynced(true);

        userChatRef
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chatOverviews = new ArrayList<>();
                        chatUIDS = new ArrayList<>();
                        mapUIDUser = new HashMap<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            chatUIDS.add(ds.getKey());
                            ChatOverview chatOverview = ds.getValue(ChatOverview.class);
                            if (chatOverview != null) {
                                chatOverviews.add(chatOverview);
                            }
                        }
                        if (chatUIDS.isEmpty()) {
                            noChats();
                        } else {
                            getUsers();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void noChats() {
        listView.setVisibility(View.GONE);
        getView().findViewById(R.id.tvNoMessages).setVisibility(View.VISIBLE);
    }

    private void getUsers() {
        DatabaseReference userNormalRef = database.child(Constants.FIREBASE_USERS_NORMAL_CONTAINER_NAME);
        for (String uid : chatUIDS) {
            userNormalRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserNormal user = (UserNormal) GsonerUser.getGson().fromJson(dataSnapshot.getValue(String.class), User.class);
                    if (user != null) {
                        mapUIDUser.put(user.getUid(), user);
                        setAdapter();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        DatabaseReference userProRef = database.child(Constants.FIREBASE_USERS_PRO_CONTAINER_NAME);
        for (String uid : chatUIDS) {
            userProRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserPro user = (UserPro) GsonerUser.getGson().fromJson(dataSnapshot.getValue(String.class), User.class);
                    if (user != null) {
                        mapUIDUser.put(user.getUid(), user);
                        setAdapter();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void setAdapter() {
        if (getContext() != null) {

            ListViewChatsAdapter adapter = new ListViewChatsAdapter(getContext(), R.layout.chat_overview_layout, chatOverviews);
            Collections.sort(chatOverviews, new ChatOverviewComparator());
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getContext(), SpecificChatActivity.class);

                    ChatOverview chatOverview = chatOverviews.get(position);

                    User user;
                    if (accountManager.getCurrentUser(1).getUid().equals(chatOverview.getAuthor())) {
                        user = mapUIDUser.get(chatOverview.getReceiver());
                    } else {
                        user = mapUIDUser.get(chatOverview.getAuthor());
                    }

                    intent.putExtra("receiver", user);
                    intent.putExtra("chatid", chatOverview.getChatID());

                    startActivity(intent);
                }
            });
        }
    }

    private class ListViewChatsAdapter extends ArrayAdapter<ChatOverview> {
        private List<ChatOverview> chats;
        private LayoutInflater inflater;

        ListViewChatsAdapter(@NonNull Context context, int resource, @NonNull List<ChatOverview> chats) {
            super(context, resource, chats);
            inflater = LayoutInflater.from(context);
            this.chats = chats;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.chat_overview_layout, null);

                holder = new ViewHolder();
                holder.ivPic = convertView.findViewById(R.id.ivProfilePic);
                holder.tvUsername = convertView.findViewById(R.id.tvUsername);
                holder.tvLastMessage = convertView.findViewById(R.id.tvLastMessage);
                holder.tvTimeStamp = convertView.findViewById(R.id.timeStamp);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ChatOverview chatOverview = chats.get(position);
            User user;
            if (accountManager.getCurrentUser(1).getUid().equals(chatOverview.getAuthor())) {
                user = mapUIDUser.get(chatOverview.getReceiver());
            } else {
                user = mapUIDUser.get(chatOverview.getAuthor());
            }

            if (user != null) {
                holder.tvUsername.setText(user.getUsername());
                holder.tvLastMessage.setText(chatOverview.getLastMessage());
                holder.tvTimeStamp.setText(DateFormat.format("HH:mm",
                        chatOverview.getTimeStamp()));
                holder.ivPic.setImageDrawable(getResources().getDrawable(R.drawable.profile_pic));

                if (user.getHasPic()) {
                    StorageReference storage = FirebaseStorage.getInstance().getReference();
                    StorageReference userRef = storage.child(Constants.FIREBASE_USERS_PRO_CONTAINER_NAME + "/" + user.getUid() + user.getImgVersion() + ".jpg");
                    GlideApp.with(getContext())
                            .load(userRef)
                            .into(holder.ivPic);
                }
            }
            return convertView;
        }

        private class ViewHolder {
            CircleImageView ivPic;
            TextView tvUsername, tvLastMessage, tvTimeStamp;
        }

    }

    private class ChatOverviewComparator implements Comparator<ChatOverview> {

        @Override
        public int compare(ChatOverview p1, ChatOverview p2) {

            long result = p1.getTimeStamp() - p2.getTimeStamp();

            if (result > 0) {
                return -1;
            } else if (result < 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}