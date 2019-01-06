package com.visoft.network.tab_contacts;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.visoft.network.R;
import com.visoft.network.funcionalidades.AccountManager;
import com.visoft.network.funcionalidades.GsonerUser;
import com.visoft.network.funcionalidades.HolderCurrentAccountManager;
import com.visoft.network.objects.User;
import com.visoft.network.objects.UserPro;
import com.visoft.network.profiles.ProfileActivity;
import com.visoft.network.util.Constants;
import com.visoft.network.util.Database;
import com.visoft.network.util.GlideApp;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainContactsFragment extends Fragment {

    private ArrayList<UserPro> contacts;
    private DatabaseReference contactsRef, userProRef;
    private int j, i;
    private boolean populated;

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
        listView.setAdapter(null);
        populated = false;
    }

    private void populateContacts() {
        contacts = new ArrayList<>();
        contactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    i++;
                    getProUserFromUID(d.getKey());
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
                ListViewAdapter adapter = new ListViewAdapter(getContext(), R.layout.pro_user_layout, contacts);
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
        private StorageReference storage;

        ListViewAdapter(@NonNull Context context, int resource, @NonNull List<UserPro> objects) {
            super(context, resource, objects);
            this.inflater = LayoutInflater.from(context);
            this.list = objects;
            storage = FirebaseStorage.getInstance().getReference();
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
            String subRubro;
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