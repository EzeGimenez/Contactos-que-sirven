package com.visoft.network.tab_chats;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class AllChatsFragment extends Fragment {
    private DatabaseReference database;
    private List<String> chatUIDS;
    private List<ChatOverview> chatOverviews;
    private HashMap<String, User> mapUIDUser;
    private AccountManager accountManager;
    private User current;
    private ValueEventListener listener;
    private DatabaseReference userChatRef;
    private long lastUpdate;
    private ChatOverViewFlexibleAdapter adapter, adapterFinished;
    //Componentes gráficas
    private RecyclerView recyclerView, recyclerViewFinished;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        database = Database.getDatabase().getReference();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (System.currentTimeMillis() - lastUpdate > 1000) {
                    lastUpdate = System.currentTimeMillis();
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

                    if (isVisible()) {
                        if (chatUIDS.isEmpty()) {
                            noChats();
                            noCompleted();
                        } else {
                            yesChats();
                            yesCompleted();
                            getUsers();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        accountManager = HolderCurrentAccountManager.getCurrent(new AccountManager.ListenerRequestResult() {
            @Override
            public void onRequestResult(boolean result, int requestCode, Bundle data) {
                if (result && data != null) {
                    current = (User) data.get("user");
                    userChatRef = database
                            .child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                            .child(current.getUid());

                    userChatRef
                            .addValueEventListener(listener);

                    Query q = userChatRef;
                    q.keepSynced(true);
                }
            }
        });

        current = accountManager.getCurrentUser(1);
        if (current != null) {
            userChatRef = database
                    .child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                    .child(current.getUid());

            userChatRef
                    .addValueEventListener(listener);
            Query q = userChatRef;
            q.keepSynced(true);
        }

        return inflater.inflate(R.layout.fragment_all_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rvChats);
        recyclerViewFinished = view.findViewById(R.id.rvCompleted);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewFinished.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    public HashMap<String, User> getMapUIDUser() {
        return mapUIDUser;
    }

    private void noChats() {
        recyclerView.setVisibility(View.GONE);
        getView().findViewById(R.id.tvNoMessages).setVisibility(View.VISIBLE);
    }

    private void yesChats() {
        recyclerView.setVisibility(View.VISIBLE);
        getView().findViewById(R.id.tvNoMessages).setVisibility(View.GONE);
    }

    private void noCompleted() {
        recyclerViewFinished.setVisibility(View.GONE);
        getView().findViewById(R.id.tvNoCompleted).setVisibility(View.VISIBLE);
    }

    private void yesCompleted() {
        recyclerViewFinished.setVisibility(View.VISIBLE);
        getView().findViewById(R.id.tvNoCompleted).setVisibility(View.GONE);
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

        if (getContext() != null && chatUIDS.size() == mapUIDUser.size()) {

            Iterator<ChatOverview> it = chatOverviews.iterator();
            final List<ChatOverview> finished = new ArrayList<>();
            while (it.hasNext()) {
                ChatOverview c = it.next();
                if (c.isFinished()) {
                    finished.add(c);
                    it.remove();
                }
            }

            if (getView() != null) {
                if (chatOverviews.isEmpty()) {
                    noChats();
                } else {
                    yesChats();
                }

                if (finished.isEmpty()) {
                    noCompleted();
                } else {
                    yesCompleted();
                }
            }

            Collections.sort(chatOverviews, new ChatOverviewComparator());
            Collections.sort(finished, new ChatOverviewComparator());
            if (adapter == null) {
                adapterFinished = new ChatOverViewFlexibleAdapter(this, finished);
                adapter = new ChatOverViewFlexibleAdapter(this, chatOverviews);
                recyclerView.setAdapter(adapter);
                recyclerViewFinished.setAdapter(adapterFinished);
            } else {
                adapter.clear();
                adapter.addItems(0, chatOverviews);
                adapter.notifyDataSetChanged();

                adapterFinished.clear();
                adapterFinished.addItems(0, finished);
                adapterFinished.notifyDataSetChanged();
            }

            adapter.addListener(new FlexibleAdapter.OnItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position) {
                    Intent intent = new Intent(getContext(), SpecificChatActivity.class);

                    ChatOverview chatOverview = chatOverviews.get(position);

                    User user;
                    if (accountManager.getCurrentUser(1).getUid().equals(chatOverview.getAuthor())) {
                        user = mapUIDUser.get(chatOverview.getReceiver());
                    } else {
                        user = mapUIDUser.get(chatOverview.getAuthor());
                    }

                    intent.putExtra("receiver", user);
                    intent.putExtra("chatOverview", chatOverviews.get(position));
                    intent.putExtra("chatid", chatOverview.getChatID());

                    startActivity(intent);
                    return false;
                }
            });

            adapterFinished.addListener(new FlexibleAdapter.OnItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position) {
                    Intent intent = new Intent(getContext(), SpecificChatActivity.class);

                    ChatOverview chatOverview = finished.get(position);

                    User user;
                    if (accountManager.getCurrentUser(1).getUid().equals(chatOverview.getAuthor())) {
                        user = mapUIDUser.get(chatOverview.getReceiver());
                    } else {
                        user = mapUIDUser.get(chatOverview.getAuthor());
                    }

                    intent.putExtra("receiver", user);
                    intent.putExtra("chatOverview", finished.get(position));
                    intent.putExtra("chatid", chatOverview.getChatID());

                    startActivity(intent);
                    return false;
                }
            });
        }
    }

    public class ChatOverViewFlexibleAdapter extends FlexibleAdapter {

        private AllChatsFragment act;

        ChatOverViewFlexibleAdapter(AllChatsFragment act, @Nullable List items) {
            super(items);
            this.act = act;
        }

        public AllChatsFragment getAct() {
            return act;
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