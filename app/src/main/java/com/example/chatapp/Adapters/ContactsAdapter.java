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
                mIntent.putExtra("Phone", mItems.get(position).getmPhone());
                mIntent.putExtra("Image", mItems.get(position).getmImage());

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
