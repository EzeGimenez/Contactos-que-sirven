package com.visoft.network.tab_contacts;

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
import com.google.firebase.database.ValueEventListener;
import com.visoft.network.R;
import com.visoft.network.funcionalidades.AccountManager;
import com.visoft.network.funcionalidades.GsonerUser;
import com.visoft.network.funcionalidades.HolderCurrentAccountManager;
import com.visoft.network.objects.User;
import com.visoft.network.objects.UserPro;
import com.visoft.network.profiles.ProfileActivity;
import com.visoft.network.util.Constants;
import com.visoft.network.util.Database;

import java.util.ArrayList;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class MainContactsFragment extends Fragment {

    private ArrayList<UserPro> contacts;
    private DatabaseReference contactsRef, userProRef;
    private int j, i;
    private boolean populated;

    //Componentes gráficas
    private RecyclerView rvContacts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvContacts = view.findViewById(R.id.listViewContacts);
    }

    @Override
    public void onResume() {
        super.onResume();
        AccountManager accountManager = HolderCurrentAccountManager.getCurrent(new AccountManager.ListenerRequestResult() {
            @Override
            public void onRequestResult(boolean result, int requestCode, Bundle data) {
                if (result && requestCode == 1 && !populated) {
                    User user = (User) data.getSerializable("user");
                    populated = true;
                    contactsRef = Database.getDatabase()
                            .getReference(Constants.FIREBASE_CONTACTS_CONTAINER_NAME)
                            .child(user.getUid());

                    contactsRef.keepSynced(true);

                    populateContacts();
                }
            }
        });

        userProRef = Database.getDatabase()
                .getReference(Constants.FIREBASE_USERS_PRO_CONTAINER_NAME);

        User user = accountManager.getCurrentUser(1);
        if (user != null && !populated) {
            populated = true;

            contactsRef = Database.getDatabase()
                    .getReference(Constants.FIREBASE_CONTACTS_CONTAINER_NAME)
                    .child(user.getUid());

            contactsRef.keepSynced(true);
            populateContacts();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        rvContacts.setAdapter(null);
        populated = false;
    }

    private void populateContacts() {
        contacts = new ArrayList<>();
        contactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    yesFavs();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        i++;
                        getProUserFromUID(d.getKey());
                    }
                    setAdapter();
                } else {
                    noFavs();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void noFavs() {
        getView().findViewById(R.id.noFavs).setVisibility(View.VISIBLE);
        rvContacts.setVisibility(View.GONE);
    }

    private void yesFavs() {
        rvContacts.setVisibility(View.VISIBLE);
        getView().findViewById(R.id.noFavs).setVisibility(View.GONE);
    }

    private void setAdapter() {
        if (i == j) {
            if (contacts.size() > 0) {
                FlexibleAdapter<UserPro> adapter = new FlexibleAdapter<>(contacts);
                rvContacts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                rvContacts.setAdapter(adapter);
                adapter.addListener(new FlexibleAdapter.OnItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position) {
                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                        intent.putExtra("user", contacts.get(position));
                        startActivity(intent);

                        return false;
                    }
                });
            }
        }
    }

    private void getProUserFromUID(String uid) {
        userProRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                j++;
                UserPro user = (UserPro) GsonerUser.getGson().fromJson(dataSnapshot.getValue(String.class), User.class);
                contacts.add(user);
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}