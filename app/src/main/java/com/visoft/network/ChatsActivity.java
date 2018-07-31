package com.visoft.network;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.visoft.network.Util.Constants;

public class ChatsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference database;

    //Componentes gráficas
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);


        Toolbar toolbar = findViewById(R.id.ToolbarContacts);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black_transparent)));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Fragment allChatsFragment = new AllChatsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.ContainerFragmentChats, allChatsFragment, Constants.ALL_CHATS_FRAGMENT_NAME)
                .commit();
    }

}
