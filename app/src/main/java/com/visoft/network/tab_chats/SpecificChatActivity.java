package com.visoft.network.tab_chats;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.visoft.network.R;
import com.visoft.network.custom_views.CustomDialog;
import com.visoft.network.funcionalidades.HolderCurrentAccountManager;
import com.visoft.network.funcionalidades.Messenger;
import com.visoft.network.objects.ChatOverview;
import com.visoft.network.objects.User;
import com.visoft.network.profiles.ProfileActivity;
import com.visoft.network.profiles.UserReviewActivity;
import com.visoft.network.util.Constants;
import com.visoft.network.util.Database;
import com.visoft.network.util.GlideApp;

import de.hdodenhof.circleimageview.CircleImageView;


public class SpecificChatActivity extends AppCompatActivity {
    public static boolean isRunning;
    private User receiver;
    private Messenger m;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specific_chat_activity);

        receiver = (User) getIntent().getSerializableExtra("receiver");

        final DatabaseReference database = Database.getDatabase().getReference();
        String chatID = getIntent().getStringExtra("chatid");

        final DatabaseReference messagesRef = Database
                .getDatabase()
                .getReference(Constants.FIREBASE_MESSAGES_CONTAINER_NAME)
                .child(chatID);

        final ChatOverview c = (ChatOverview) getIntent().getSerializableExtra("chatOverview");

        RelativeLayout container = findViewById(R.id.ContainerSpecificChat);
        SpecificChatFragment fragment = new SpecificChatFragment();
        fragment.setDatabase(messagesRef);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFinished", c.isFinished());
        bundle.putString("receiverUid", receiver.getUid());
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.ContainerSpecificChat, fragment)
                .commit();

        if (!c.isFinished()) {
            m = new Messenger(this, HolderCurrentAccountManager.getCurrent(null).getCurrentUser(1).getUid(), receiver.getUid(), (ViewGroup) findViewById(R.id.rootView), container, database);
            m.setNotify(false);
            findViewById(R.id.tvFinalizarContrato).setVisibility(View.GONE);
        }

        ((TextView) findViewById(R.id.tvReceiver)).setText(receiver.getUsername());
        CircleImageView ivPic = findViewById(R.id.ivPic);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (receiver.getIsPro()) {

            findViewById(R.id.ContainerReceiver).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SpecificChatActivity.this, ProfileActivity.class);
                    intent.putExtra("user", receiver);
                    startActivity(intent);
                }
            });

            if (!c.isFinished()) {
                findViewById(R.id.tvFinalizarContrato).setVisibility(View.VISIBLE);
                findViewById(R.id.tvFinalizarContrato).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final CustomDialog dialog = new CustomDialog(SpecificChatActivity.this);
                        dialog.setTitle(getString(R.string.pudo_concretar));
                        dialog.setPositiveIcon(getResources().getDrawable(R.drawable.ic_check_black_24dp));
                        dialog.setNegativeIcon(getResources().getDrawable(R.drawable.ic_close_black_24dp));

                        dialog.setPositiveButton("", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(SpecificChatActivity.this, UserReviewActivity.class);
                                intent.putExtra("user", receiver);
                                startActivity(intent);
                                m.finishChat();
                                finish();
                            }
                        });

                        dialog.setNegativeButton("", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                m.finishChat();
                                finish();
                            }
                        });
                        dialog.show();
                    }
                });
            } else {
                findViewById(R.id.tvFinalizarContrato).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.tvFinalizarContrato).setVisibility(View.GONE);
        }

        if (receiver.getHasPic()) {
            StorageReference storage = FirebaseStorage.getInstance().getReference();

            StorageReference userRef = storage.child(Constants.FIREBASE_USERS_PRO_CONTAINER_NAME + "/" + receiver.getUid() + receiver.getImgVersion() + ".jpg");
            GlideApp.with(this)
                    .load(userRef)
                    .into(ivPic);
        } else {
            ivPic.setImageDrawable(getResources().getDrawable(R.drawable.profile_pic));
        }

        if (!c.isFinished()) {
            database.child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                    .child(HolderCurrentAccountManager.getCurrent(null).getCurrentUser(1).getUid())
                    .child(receiver.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ChatOverview c = dataSnapshot.getValue(ChatOverview.class);
                            if (c != null && c.isFinished()) {
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}