package com.example.chatapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.chatapp.Adapters.UserAdapter;
import com.example.chatapp.Models.UsersModel;
import com.example.chatapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    DatabaseReference mDatabaseReference;
    UserAdapter mUserAdapter;
    ArrayList<UsersModel> mUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users Data");
        mUsers = new ArrayList<>();
        instViews();
    }
    public void onStart() {
        super.onStart();
        LoadUsers();
    }

    private void instViews() {
        mRecyclerView = findViewById(R.id.RecUser);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this , RecyclerView.VERTICAL , false);
        mRecyclerView.setLayoutManager(layoutManager);

    }

    void LoadUsers() {
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    UsersModel imageUploadInfo = postSnapshot.getValue(UsersModel.class);

                    mUsers.add(imageUploadInfo);
                }

                 mUserAdapter = new UserAdapter(MainActivity.this,mUsers);

                mRecyclerView.setAdapter(mUserAdapter);


                // Hiding the progress dialog.
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.
            }
        });

    }
}
