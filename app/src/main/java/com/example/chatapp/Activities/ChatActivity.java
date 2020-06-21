package com.example.chatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.chatapp.Adapters.chatAdapter;
import com.example.chatapp.Models.ModelChat;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    TextView mName, mStatus;
    ImageButton mImageButton;
    ImageView mImageView;
    Intent mIntent;
    FirebaseAuth mFirebaseAuth;
    String mMyId, mHisId;
    EditText mMassage;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    //-to Check Seen
    ValueEventListener SeenListener;
    DatabaseReference mUserRefForSeen;

    ArrayList<ModelChat> mChat;
    chatAdapter mChatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Instviews();
    }

    private void Instviews() {
        mIntent = getIntent();
        mToolbar = findViewById(R.id.mToolBar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("");
        //----------------------------------------------------------------------------------------------
        mRecyclerView = findViewById(R.id.ChatRec);
        mName = findViewById(R.id.mName);
        mStatus = findViewById(R.id.mStatus);
        mImageView = findViewById(R.id.mUserImage);
        mImageButton = findViewById(R.id.mSend);
        mMassage = findViewById(R.id.mEditForMessage);

        mHisId = mIntent.getStringExtra("Id");
        //---------------------------------------------------------------------------------------------
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Users Data");

        Query UserQuery = mDatabaseReference.orderByChild("id").equalTo(mHisId);
        //----------------------------------------------------------------------------------------------

        UserQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String Name = "" + ds.child("mName").getValue();
                    mName.setText(Name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //----------------------------------------------------------------------------------------------
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aMessage = mMassage.getText().toString().trim();

                if (TextUtils.isEmpty(aMessage)) {
                    Toast.makeText(ChatActivity.this, "Cannot Send Empty Message", Toast.LENGTH_SHORT).show();

                } else {

                    sendMessage(aMessage);
                    mMassage.setText("");

                }
            }


        });


        //-Rec For Messamge

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        mRecyclerView.setLayoutManager(manager);

        ReadMessage();
        SeenMessage();
    }

    private void SeenMessage() {
        mUserRefForSeen = FirebaseDatabase.getInstance().getReference("Chat");
        SeenListener = mUserRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelChat modelChat = ds.getValue(ModelChat.class);
                    if(modelChat.getmSender().equals(mHisId) && modelChat.getmReceiver().equals(mMyId)){
                        HashMap<String,Object> IsSeen = new HashMap<>();
                        IsSeen.put("mIsSeen",true);
                        ds.getRef().updateChildren(IsSeen);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ReadMessage() {
        mChat = new ArrayList<>();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Chat");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelChat modelChat = ds.getValue(ModelChat.class);

                    if (modelChat.getmSender().equals(mMyId) && modelChat.getmReceiver().equals(mHisId) ||
                            modelChat.getmSender().equals(mHisId) && modelChat.getmReceiver().equals(mMyId)) {
                        mChat.add(modelChat);
                    }
                    mChatAdapter = new chatAdapter(ChatActivity.this, mChat);
                    mChatAdapter.notifyDataSetChanged();
                    mRecyclerView.setAdapter(mChatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String aMessage) {
        String aTime = String.valueOf(System.currentTimeMillis());
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> map = new HashMap<>();
        map.put("mSender", mMyId);
        map.put("mReceiver", mHisId);
        map.put("mMessage", aMessage);
        map.put("mTime", aTime);
        map.put("mIsSeen", false);
        mDatabaseReference.child("Chat").push().setValue(map);
    }

    void CheckUserStatus() {
        FirebaseUser mUser = mFirebaseAuth.getCurrentUser();
        if (mUser != null) {
            mMyId = mUser.getUid();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckUserStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUserRefForSeen.removeEventListener(SeenListener);
    }
}
