package com.example.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.chatapp.Activities.ChatActivity;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.Contact> {
    ArrayList<com.example.chatapp.Models.Contact> mItems;
    Context mContext;
    static boolean ChatExist = false;
    static com.example.chatapp.Models.Contact mMyInfo;

    public ContactsAdapter(ArrayList<com.example.chatapp.Models.Contact> mItems, Context mContext) {
        this.mItems = mItems;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Contact onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Contact(LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_shap, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Contact holder, int position) {
        holder.UserName.setText(mItems.get(position).getmName());
        holder.UserPhone.setText(mItems.get(position).getmPhone());
        if (!mItems.get(position).getmImage().equals("")) {
            Glide.with(mContext).load(mItems.get(position).getmImage()).into(holder.UserImage);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, ChatActivity.class);
                mIntent.putExtra("Id", mItems.get(position).getmId());
                mIntent.putExtra("Name", mItems.get(position).getmName());


                DatabaseReference UserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Chats");
                Query query = UserDb.orderByChild("mId").equalTo(mItems.get(position).getmId());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            ChatExist = true;
                            Log.e("Exist", "FFFFFFFFFFFFFFFFFFF");
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
                                mMyInfo = snapshot.getValue(com.example.chatapp.Models.Contact.class);

                                HashMap map = new HashMap();
                                map.put("mImage", mMyInfo.getmImage());
                                map.put("mPhone", mMyInfo.getmPhone());
                                map.put("mId", mMyInfo.getmId());

                                HashMap map2 = new HashMap();
                                map2.put("mImage", mItems.get(position).getmImage());
                                map2.put("mPhone", mItems.get(position).getmPhone());
                                map2.put("mId", mItems.get(position).getmId());
                                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Chats").child(mItems.get(position).getmId()).updateChildren(map2);
                                FirebaseDatabase.getInstance().getReference().child("Users").child(mItems.get(position).getmId()).child("Chats").child(FirebaseAuth.getInstance().getUid()).updateChildren(map);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }


                mContext.startActivity(mIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class Contact extends RecyclerView.ViewHolder {
        Context mContext;
        @BindView(R.id.UserImage)
        CircleImageView UserImage;
        @BindView(R.id.UserName)
        TextView UserName;
        @BindView(R.id.UserPhone)
        TextView UserPhone;
        @BindView(R.id.Layout)
        ConstraintLayout Layout;

        public Contact(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
