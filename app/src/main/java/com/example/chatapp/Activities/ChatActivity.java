package com.example.chatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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


    boolean notify = false;

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
        mName.setText(mIntent.getStringExtra("Name"));


        mStatus = findViewById(R.id.mStatus);
        mImageView = findViewById(R.id.mUserImage);
        mImageButton = findViewById(R.id.mSend);
        mMassage = findViewById(R.id.mEditForMessage);

        mHisId = mIntent.getStringExtra("Id");
        //---------------------------------------------------------------------------------------------
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Users");
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Chats").child(mHisId).child("mName").setValue(mIntent.getStringExtra("Name"));


        Query UserQuery = mDatabaseReference.orderByChild("mId").equalTo(mHisId);
        //----------------------------------------------------------------------------------------------

        UserQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String Name = "" + ds.child("mName").getValue();

                    String mStatus1 = "" + ds.child("mStatus").getValue();

                    String mTyping = "" + ds.child("mTyping").getValue();


                    if (mTyping.equalsIgnoreCase(mMyId)) {
                        mStatus.setText("Typing....");
                    } else {


                        if (mStatus1.equalsIgnoreCase("Online")) {
                            mStatus.setText(mStatus1);
                        } else {

                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(mStatus1));
                            String Date = DateFormat.format("dd/mm/yyyy hh:mm aa", cal).toString();
                            mStatus.setText("Last Seen at: " + Date);
                        }

                    }


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
                notify = true;
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


        //-----------------------------------------
        mMassage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() == 0) {
                    CheckTyping("noOne");
                } else {
                    CheckTyping(mHisId);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        ReadMessage();
        SeenMessage();


        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.smoothScrollToPosition(
                                    mRecyclerView.getAdapter().getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });
    }

    private void SeenMessage() {
        mUserRefForSeen = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Chats").child(mHisId).child("Messages");
        SeenListener = mUserRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelChat modelChat = ds.getValue(ModelChat.class);
                    if (modelChat.getmSender().equals(mHisId) && modelChat.getmReceiver().equals(mMyId)) {
                        HashMap<String, Object> IsSeen = new HashMap<>();
                        IsSeen.put("mIsSeen", true);
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Chats").child(mHisId).child("Messages");
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
                    RecyclerView.LayoutManager manager = new LinearLayoutManager(ChatActivity.this, RecyclerView.VERTICAL, false);
                    manager.scrollToPosition(mChat.size() - 1);
                    mRecyclerView.setLayoutManager(manager);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(final String aMessage) {
        String aTime = String.valueOf(System.currentTimeMillis());
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> map = new HashMap<>();
        map.put("mSender", mMyId);
        map.put("mReceiver", mHisId);
        map.put("mMessage", aMessage);
        map.put("mTime", aTime);
        map.put("mIsSeen", false);
        HashMap<String, Object> map2 = new HashMap<>();
        mDatabaseReference.child("Users").child(FirebaseAuth.getInstance().getUid()).child("Chats").child(mHisId).child("Messages").push().setValue(map);
        mDatabaseReference.child("Users").child(mHisId).child("Chats").child(mMyId).child("Messages").push().setValue(map);


        map2.put("mTime", aTime);
        map2.put("mLastMessage", aMessage);
        mDatabaseReference.child("Users").child(mHisId).child("Chats").child(mMyId).updateChildren(map2);
        mDatabaseReference.child("Users").child(FirebaseAuth.getInstance().getUid()).child("Chats").child(mHisId).updateChildren(map2);

    }

    private void Checkstatus(String mStatus) {

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mMyId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("mStatus", mStatus);
        mDatabaseReference.updateChildren(map);
    }

    private void CheckTyping(String mTyping) {

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mMyId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("mTyping", mTyping);
        mDatabaseReference.updateChildren(map);
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
        Checkstatus("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String mTime = String.valueOf(System.currentTimeMillis());
        Checkstatus(mTime);
        CheckTyping("noOne");
        mUserRefForSeen.removeEventListener(SeenListener);

    }

    @Override
    protected void onResume() {
        Checkstatus("Online");
        super.onResume();

    }


}
