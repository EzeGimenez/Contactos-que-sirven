package com.visoft.network.MainPageChats;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.visoft.network.Objects.ChatOverview;
import com.visoft.network.Objects.User;
import com.visoft.network.R;
import com.visoft.network.Util.Constants;
import com.visoft.network.Util.Database;
import com.visoft.network.Util.GlideApp;
import com.visoft.network.funcionalidades.GsonerUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllChatsFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    private List<String> chatUIDS;
    private List<ChatOverview> chatOverviews;
    private HashMap<String, User> mapUIDUser;

    //Componentes gráficas
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        database = Database.getDatabase().getReference();
        populateChats();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.listViewChats);
    }

    private void populateChats() {
        chatOverviews = new ArrayList<>();
        chatUIDS = new ArrayList<>();
        mapUIDUser = new HashMap<>();

        DatabaseReference userChatRef = database
                .child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        userChatRef.keepSynced(true);

        userChatRef
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            chatUIDS.add(ds.getKey());
                            ChatOverview chatOverview = ds.getValue(ChatOverview.class);
                            if (chatOverview != null) {
                                chatOverviews.add(chatOverview);
                            }
                        }
                        getUsers();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getUsers() {
        DatabaseReference userRef = database.child(Constants.FIREBASE_USERS_CONTAINER_NAME);
        for (String uid : chatUIDS) {
            userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = GsonerUser.getGson().fromJson(dataSnapshot.getValue(String.class), User.class);
                    mapUIDUser.put(user.getUid(), user);
                    setAdapter();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void setAdapter() {
        if (getContext() != null) {
            if (mapUIDUser.size() == chatOverviews.size()) {
                ListViewChatsAdapter adapter = new ListViewChatsAdapter(getContext(), R.layout.chat_overview_layout, chatOverviews);
                Collections.sort(chatOverviews, new ChatOverviewComparator());
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getContext(), SpecificChatActivity.class);

                        ChatOverview chatOverview = chatOverviews.get(position);

                        User user;
                        if (mAuth.getCurrentUser().getUid().equals(chatOverview.getAuthor())) {
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

    }

    public void refresh() {
        populateChats();
    }

    private class ListViewChatsAdapter extends ArrayAdapter<ChatOverview> {
        private List<ChatOverview> chats;
        private LayoutInflater inflater;

        public ListViewChatsAdapter(@NonNull Context context, int resource, @NonNull List<ChatOverview> chats) {
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
            if (mAuth.getCurrentUser().getUid().equals(chatOverview.getAuthor())) {
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
                    StorageReference userRef = storage.child(Constants.FIREBASE_USERS_CONTAINER_NAME + "/" + user.getUid() + user.getImgVersion() + ".jpg");
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
