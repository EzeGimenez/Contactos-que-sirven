package com.visoft.network;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.visoft.network.Objects.ProUser;
import com.visoft.network.Objects.User;
import com.visoft.network.Util.Constants;
import com.visoft.network.Util.Database;
import com.visoft.network.Util.GlideApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsActivity extends AppCompatActivity {
    private ArrayList<User> contacts;
    private FirebaseAuth mAuth;
    private DatabaseReference contactsRef, userRef;
    private int j, i;
    StorageReference storage;
    private ListViewAdapter adapter;

    //Componentes gráficas
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        listView = findViewById(R.id.listViewContacts);

        mAuth = FirebaseAuth.getInstance();

        contactsRef = Database.getDatabase()
                .getReference(Constants.FIREBASE_CONTACTS_CONTAINER_NAME)
                .child(mAuth.getCurrentUser().getUid());

        contactsRef.keepSynced(true);

        userRef = Database.getDatabase()
                .getReference(Constants.FIREBASE_USERS_CONTAINER_NAME);

        storage = FirebaseStorage.getInstance().getReference();

        Toolbar toolbar = findViewById(R.id.ToolbarContacts);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black_transparent)));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (contacts != null && adapter != null) {
            contacts.clear();
            adapter.notifyDataSetChanged();
        }
        populateContacts();
    }

    private void setAdapter() {
        if (i == j) {
            if (contacts.size() > 0) {
                adapter = new ListViewAdapter(this, R.layout.profile_search_result_row, contacts);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplication(), ProfileActivity.class);
                        intent.putExtra("user", contacts.get(position));
                        startActivity(intent);
                    }
                });
            }
        }
    }

    private void getProUserFromUID(String uid) {
        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                j++;
                User user = dataSnapshot.getValue(User.class);
                if (user == null || user.getIsPro()) {
                    ProUser proUser = dataSnapshot.getValue(ProUser.class);
                    if (proUser != null) {
                        contacts.add(proUser);
                    }
                } else {
                    contacts.add(user);
                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private class ListViewAdapter extends ArrayAdapter<User> {
        private LayoutInflater inflater;
        private List<User> list;

        public ListViewAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
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
                convertView = inflater.inflate(R.layout.profile_search_result_row, null);

                holder.ivPic = convertView.findViewById(R.id.ivProfilePic);
                holder.tvUsername = convertView.findViewById(R.id.tvUsername);
                holder.tvRubro = convertView.findViewById(R.id.tvRubro);
                holder.tvNumReviews = convertView.findViewById(R.id.tvNumReviews);
                holder.ratingBar = convertView.findViewById(R.id.ratingBar);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            User user = list.get(position);
            String subRubro = "";
            if (user.getIsPro()) {
                ProUser proUser = (ProUser) user;
                int id = getResources().getIdentifier(proUser.getRubroEspecifico(),
                        "string",
                        getPackageName());
                subRubro = getResources().getString(id);
            }

            holder.tvUsername.setText(user.getUsername());
            holder.tvRubro.setText(subRubro);
            holder.tvNumReviews.setText(user.getNumberReviews() + " " + getString(R.string.reviews));
            holder.ratingBar.setRating(user.getRating());

            if (user.getHasPic()) {
                StorageReference userRefStorage = storage.child(Constants.FIREBASE_USERS_CONTAINER_NAME + "/" + user.getUid() + user.getImgVersion() + ".jpg");
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
