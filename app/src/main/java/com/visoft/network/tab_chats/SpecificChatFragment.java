package com.visoft.network.tab_chats;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.gson.Gson;
import com.visoft.network.R;
import com.visoft.network.funcionalidades.GsonerMessages;
import com.visoft.network.objects.Message;
import com.visoft.network.objects.MessageContractFinished;
import com.visoft.network.objects.ViewHolderChats;


public class SpecificChatFragment extends Fragment {

    private DatabaseReference database;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<String, ViewHolderChats> recyclerViewAdapter;
    private boolean isFinished;
    private String uid;

    private static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_specific_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.listViewSpecificChat);
        isFinished = getArguments().getBoolean("isFinished");
        uid = getArguments().getString("receiverUid");

        recyclerViewAdapter = new ListViewChatsAdapter(String.class, 0, ViewHolderChats.class, database);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(recyclerViewAdapter.getItemCount());
            }
        });

        /*if (isFinished) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            recyclerView
                    .setLayoutParams(lp);
        }*/

        final View activityRootView = getView();
        activityRootView.getViewTreeObserver().
                addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                        if (heightDiff > dpToPx(getActivity(), 200)) {
                            recyclerView.smoothScrollToPosition(recyclerViewAdapter.getItemCount());
                        }
                    }
                });
    }

    public void setDatabase(DatabaseReference f) {
        this.database = f;
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
                case 4:
                    return new ViewHolderChats(inflater.inflate(R.layout.message_contract_finished, parent, false));
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

            if (msg instanceof MessageContractFinished) {
                return 4;
            }

            Message last = null;
            if (position > 0) {
                last = gson.fromJson(getItem(position - 1), Message.class);
            }

            if (authorUID.equals(uid)) {
                if (position > 0 && last.getAuthor().equals(uid)) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                if (position > 0 && !last.getAuthor().equals(uid)) {
                    return 2;
                } else {
                    return 3;
                }
            }
        }
    }
}
