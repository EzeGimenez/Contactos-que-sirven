package com.visoft.jobfinder;

import android.content.Context;
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
import com.visoft.jobfinder.Objects.ChatOverview;
import com.visoft.jobfinder.Objects.User;
import com.visoft.jobfinder.Util.Constants;
import com.visoft.jobfinder.Util.Database;
import com.visoft.jobfinder.Util.GlideApp;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllChatsFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    private List<User> chatUsers;
    private List<String> chatUIDS;
    private List<ChatOverview> chatOverviews;

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
        chatUsers = new ArrayList<>();
        chatUIDS = new ArrayList<>();

        database
                .child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                .child(mAuth.getCurrentUser().getUid()).keepSynced(true);

        database
                .child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                .child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            chatUIDS.add(ds.getKey());
                            ChatOverview chatOverview = ds.getValue(ChatOverview.class);
                            if (chatOverview != null) {
                                chatOverviews.add(ds.getValue(ChatOverview.class));
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
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        chatUsers.add(user);
                    }
                    setAdapter();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void setAdapter() {
        if (chatUsers.size() == chatUIDS.size()) {
            ListViewChatsAdapter adapter = new ListViewChatsAdapter(getContext(), R.layout.chat_overview_layout, chatOverviews, chatUsers);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Fragment fragment = new SpecificChatFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("chatid", chatOverviews.get(position).getChatID());
                    bundle.putSerializable("receiver", chatUsers.get(position));
                    fragment.setArguments(bundle);

                    getFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.ContainerFragmentChats, fragment, Constants.SPECIFIC_CHAT_FRAGMENT_TAG)
                            .addToBackStack(Constants.ALL_CHATS_FRAGMENT_NAME)
                            .commit();
                }
            });
        }
    }

    private class ListViewChatsAdapter extends ArrayAdapter<ChatOverview> {
        private List<ChatOverview> chats;
        private List<User> users;
        private LayoutInflater inflater;

        public ListViewChatsAdapter(@NonNull Context context, int resource, @NonNull List<ChatOverview> chats, List<User> users) {
            super(context, resource, chats);
            inflater = LayoutInflater.from(context);
            this.chats = chats;
            this.users = users;
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

            ChatOverview chat = chats.get(position);
            User user = users.get(position);

            holder.tvUsername.setText(user.getUsername());
            holder.tvLastMessage.setText(chat.getLastMessage());
            holder.tvTimeStamp.setText(DateFormat.format("HH:mm",
                    chat.getTimeStamp()));

            if (user.getHasPic()) {
                StorageReference storage = FirebaseStorage.getInstance().getReference();

                StorageReference userRef = storage.child(Constants.FIREBASE_USERS_CONTAINER_NAME + "/" + user.getUid() + user.getImgVersion() + ".jpg");
                GlideApp.with(getContext())
                        .load(userRef)
                        .into(holder.ivPic);
            }

            return convertView;
        }

        private class ViewHolder {
            CircleImageView ivPic;
            TextView tvUsername, tvLastMessage, tvTimeStamp;
        }

    }
}
