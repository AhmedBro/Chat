package com.example.chatapp.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.MediaAdapter;
import com.example.chatapp.Adapters.chatAdapter;
import com.example.chatapp.Coverting.Iso2Phone;
import com.example.chatapp.Models.Contact;
import com.example.chatapp.Models.ModelChat;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChatActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMG = 1;
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
    private RecyclerView.Adapter mMediaAdapter;
    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();
    static String UTI = "";
    //-to Check Seen
    ValueEventListener SeenListener;
    DatabaseReference mUserRefForSeen;

    ArrayList<ModelChat> mChat;
    chatAdapter mChatAdapter;


    static boolean ChatExist = false;
    static Contact mMyInfo;
    boolean notify = false;
    @BindView(R.id.mSendImage)
    ImageButton mSendImage;
    @BindView(R.id.MediaRec)
    RecyclerView mMedia;
    StorageReference storageReference;
    static ArrayList<Uri> mMediaPath;
    static ArrayList<String> mMediaDownload;
    static String mMessageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        mMediaPath = new ArrayList<>();
        mMediaDownload = new ArrayList<>();
        storageReference = FirebaseStorage.getInstance().getReference();
        Instviews();
        initializeMedia();
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

                if (TextUtils.isEmpty(aMessage) && mMediaPath.size() == 0) {
                    Toast.makeText(ChatActivity.this, "Cannot Send Empty Message", Toast.LENGTH_SHORT).show();

                } else {

                    for (int i = 0; i < mMediaPath.size(); i++) {
                        uploadImage(mMediaPath.get(i));

                    }
                    sendMessage(aMessage);
                    MakeChat();
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
                            try {
                                mRecyclerView.smoothScrollToPosition(
                                        mRecyclerView.getAdapter().getItemCount() - 1);
                            } catch (Exception w) {

                            }

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


        mUserRefForSeen = FirebaseDatabase.getInstance().getReference().child("Users").child(mHisId).child("Chats").child(FirebaseAuth.getInstance().getUid()).child("Messages");
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
                    mChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(final String aMessage) {
        mMessageId = FirebaseDatabase.getInstance().getReference().push().getKey();
        String aTime = String.valueOf(System.currentTimeMillis());
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> map = new HashMap<>();
        map.put("mSender", mMyId);
        map.put("mReceiver", mHisId);
        map.put("mMessage", aMessage);
        map.put("mTime", aTime);
        map.put("mIsSeen", false);
        map.put("mId", mMessageId);




        HashMap<String, Object> map2 = new HashMap<>();
        mDatabaseReference.child("Users").child(FirebaseAuth.getInstance().getUid()).child("Chats").child(mHisId).child("Messages").child(mMessageId).setValue(map);
        mDatabaseReference.child("Users").child(mHisId).child("Chats").child(mMyId).child("Messages").child(mMessageId).setValue(map);


        map2.put("mTime", aTime);
        map2.put("mLastMessage", aMessage);
        mDatabaseReference.child("Users").child(mHisId).child("Chats").child(mMyId).updateChildren(map2);
        mDatabaseReference.child("Users").child(FirebaseAuth.getInstance().getUid()).child("Chats").child(mHisId).updateChildren(map2);
        mediaUriList.clear();
        mMediaAdapter.notifyDataSetChanged();
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

    void MakeChat() {
        DatabaseReference UserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Chats");
        Query query = UserDb.orderByChild("mId").equalTo(mHisId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ChatExist = true;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (!ChatExist) {

            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mMyInfo = snapshot.getValue(Contact.class);

                        HashMap<String, Object> map = new HashMap();
                        map.put("mImage", mMyInfo.getmImage());
                        map.put("mPhone", mMyInfo.getmPhone());
                        map.put("mId", mMyInfo.getmId());
                        map.put("mName", GetUserName(mMyInfo.getmPhone()));

                        HashMap<String, Object> map2 = new HashMap();
                        map2.put("mImage", getIntent().getStringExtra("Image"));
                        map2.put("mPhone", getIntent().getStringExtra("Phone"));
                        map2.put("mId", mHisId);
                        map2.put("mName", getIntent().getStringExtra("Name"));
                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Chats").child(mHisId).updateChildren(map2);
                        FirebaseDatabase.getInstance().getReference().child("Users").child(mHisId).child("Chats").child(FirebaseAuth.getInstance().getUid()).updateChildren(map);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

    String GetUserName(String Phone) {
        String n = null;
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);


        while (cur.moveToNext()) {
            String name = cur.getString(cur.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNo = cur.getString(cur.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER));

            phoneNo = phoneNo.replace(" ", "");
            phoneNo = phoneNo.replace("-", "");
            phoneNo = phoneNo.replace("(", "");
            phoneNo = phoneNo.replace(")", "");
            if (!String.valueOf(phoneNo.charAt(0)).equals("+")) {
                phoneNo = getCountryIso() + phoneNo;
            }
            if (Phone.equals(phoneNo)) {
                n = cur.getString(cur.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                break;
            }


        }
        return n;
    }

    String getCountryIso() {
        String Iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkCountryIso() != null) {
            if (!telephonyManager.getNetworkCountryIso().toString().equals("")) {
                Iso = telephonyManager.getNetworkCountryIso().toString();
            }
        }
        return Iso2Phone.getPhone(Iso);
    }


    @OnClick(R.id.mSendImage)
    public void onViewClicked() {
        openGallery();
    }


    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture(s)"), PICK_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_INTENT) {
                if (data.getClipData() == null) {
                    mediaUriList.add(data.getData().toString());
                    mMediaPath.add(data.getData());

                } else {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                        mMediaPath.add(data.getClipData().getItemAt(i).getUri());
                    }
                }

                mMediaAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initializeMedia() {
        mediaUriList = new ArrayList<>();

        mMedia.setNestedScrollingEnabled(false);
        mMedia.setHasFixedSize(false);
        @SuppressLint("WrongConstant") RecyclerView.LayoutManager mMediaLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.HORIZONTAL, false);
        mMedia.setLayoutManager(mMediaLayoutManager);
        mMediaAdapter = new MediaAdapter(getApplicationContext(), mediaUriList);
        mMedia.setAdapter(mMediaAdapter);
    }


    private void uploadImage(Uri filePath) {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            final StorageReference ref
                    = storageReference
                    .child(
                            "Media/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(ChatActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();

                                    ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {

                                                UpToMedia(task.getResult().toString());


                                                Toast.makeText(ChatActivity.this, UTI, Toast.LENGTH_SHORT).show();

                                            } else {
                                                Toast.makeText(ChatActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    Log.e("Uriiiiiiiiiiiii", UTI);


                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(ChatActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        }
    }

    void AddNote() {
        String NoteKey = mDatabaseReference.push().getKey();

        mDatabaseReference.child(NoteKey).setValue(UTI);

    }

    void UpToMedia(String Url) {
        Log.e("MediaaaaaToooo", Url);
        Log.e("MediaaaaaToooo", mMessageId);
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Chats").child(mHisId).child("Messages").child(mMessageId).child("mMedia").push().setValue(Url);
        FirebaseDatabase.getInstance().getReference().child("Users").child(mHisId).child("Chats").child(mMyId).child("Messages").child(mMessageId).child("mMedia").push().setValue(Url);
    }

}
