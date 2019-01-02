package com.visoft.network.MainPageContacts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.visoft.network.Objects.User;
import com.visoft.network.Objects.UserPro;
import com.visoft.network.Profiles.ProfileActivity;
import com.visoft.network.R;
import com.visoft.network.Util.Constants;
import com.visoft.network.Util.Database;
import com.visoft.network.Util.GlideApp;
import com.visoft.network.funcionalidades.AccountManager;
import com.visoft.network.funcionalidades.GsonerUser;
import com.visoft.network.funcionalidades.HolderCurrentAccountManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainContactsFragment extends Fragment {
    StorageReference storage;
    private ArrayList<UserPro> contacts;
    private DatabaseReference contactsRef, userProRef;
    private int j, i;
    private ListViewAdapter adapter;

    //Componentes gráficas
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.listViewContacts);

        AccountManager.ListenerRequestResult listener = new AccountManager.ListenerRequestResult() {
            @Override
            public void onRequestResult(boolean result, int requestCode, Bundle data) {
                if (result) {
                    User user = (User) data.getSerializable("user");
                    contactsRef = Database.getDatabase()
                            .getReference(Constants.FIREBASE_CONTACTS_CONTAINER_NAME)
                            .child(user.getUid());

                    contactsRef.keepSynced(true);

                    userProRef = Database.getDatabase()
                            .getReference(Constants.FIREBASE_USERS_PRO_CONTAINER_NAME);

                    String mock = "asd";
                    userProRef.child("mock").setValue(mock).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            userProRef.child("mock").removeValue();

                            storage = FirebaseStorage.getInstance().getReference();

                            if (contacts != null && adapter != null) {
                                contacts.clear();
                                adapter.notifyDataSetChanged();
                            }
                            populateContacts();
                        }
                    });


                }
            }
        };
        AccountManager accountManager = HolderCurrentAccountManager.getCurrent(listener);
        accountManager.getCurrentUser(1);
    }

    private void populateContacts() {
        contacts = new ArrayList<>();
        contactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> map = (HashMap<String, Boolean>) dataSnapshot.getValue();
                if (map != null) {
                    for (String k : map.keySet()) {
                        getProUserFromUID(k);
                        i++;
                    }
                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAdapter() {
        if (i == j) {
            if (contacts.size() > 0) {
                adapter = new ListViewAdapter(getContext(), R.layout.pro_user_layout, contacts);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                        intent.putExtra("user", contacts.get(position));
                        startActivity(intent);
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

    private class ListViewAdapter extends ArrayAdapter<UserPro> {
        private LayoutInflater inflater;
        private List<UserPro> list;

        ListViewAdapter(@NonNull Context context, int resource, @NonNull List<UserPro> objects) {
            super(context, resource, objects);
            this.inflater = LayoutInflater.from(context);
            this.list = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            final ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.pro_user_layout, null);

                holder.ivPic = convertView.findViewById(R.id.ivProfilePic);
                holder.tvUsername = convertView.findViewById(R.id.tvUsername);
                holder.tvRubro = convertView.findViewById(R.id.tvRubro);
                holder.tvNumReviews = convertView.findViewById(R.id.tvNumReviews);
                holder.ratingBar = convertView.findViewById(R.id.ratingBar);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            UserPro user = list.get(position);
            String subRubro = "";
            int id = getResources().getIdentifier(user.getRubroEspecifico(),
                    "string",
                    getActivity().getPackageName());
            subRubro = getResources().getString(id);


            holder.tvUsername.setText(user.getUsername());
            holder.tvRubro.setText(subRubro);
            holder.tvNumReviews.setText(user.getNumberReviews() + " " + getString(R.string.reviews));
            holder.ratingBar.setRating(user.getRating());

            if (user.getHasPic()) {
                StorageReference userRefStorage = storage.child(Constants.FIREBASE_USERS_PRO_CONTAINER_NAME + "/" + user.getUid() + user.getImgVersion() + ".jpg");
                GlideApp.with(getContext())
                        .load(userRefStorage)
                        .into(holder.ivPic);
            }


            return convertView;
        }

        private class ViewHolder {
            CircleImageView ivPic;
            TextView tvUsername, tvRubro, tvNumReviews;
            SimpleRatingBar ratingBar;
        }
    }
}