package com.visoft.network.tab_chats;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.visoft.network.R;
import com.visoft.network.util.Constants;

public class ChatsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chats, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Fragment allChatsFragment = new AllChatsFragment();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.ContainerFragmentChats, allChatsFragment, Constants.ALL_CHATS_FRAGMENT_NAME)
                .commit();
    }

}
