package com.example.chatapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.Chats;
import com.example.chatapp.Models.Contact;
import com.example.chatapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    DatabaseReference mDatabaseReference;
    Chats mUserAdapter;
    ArrayList<Contact> mUsers;
    FirebaseAuth mFirebaseAuth;
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.Contacts)
    FloatingActionButton Contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Chats");
        mUsers = new ArrayList<>();
        mFirebaseAuth = FirebaseAuth.getInstance();
        TakePermission();
        instViews();
    }

    public void onStart() {
        super.onStart();
        LoadUsers();
    }

    private void instViews() {
        mRecyclerView = findViewById(R.id.RecUser);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

    }

    void LoadUsers() {
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    Contact imageUploadInfo = postSnapshot.getValue(Contact.class);

                        mUsers.add(imageUploadInfo);



                }

                mUserAdapter = new Chats(MainActivity.this, mUsers);

                mRecyclerView.setAdapter(mUserAdapter);


                // Hiding the progress dialog.
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.
            }
        });

    }

    @OnClick(R.id.Contacts)
    public void onViewClicked() {
        Intent mIntent = new Intent(this , ContactsActivity.class);
        startActivity(mIntent);
    }
    private void TakePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
    }
}
