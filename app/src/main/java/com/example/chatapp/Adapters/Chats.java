package com.example.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Activities.ChatActivity;
import com.example.chatapp.Models.Contact;
import com.example.chatapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Chats extends RecyclerView.Adapter<Chats.adapter> {
    Context mContext;
    ArrayList<Contact> mUsersModels;

    public Chats(Context mContext, ArrayList<Contact> mUsersModels) {
        this.mContext = mContext;
        this.mUsersModels = mUsersModels;
    }

    @NonNull
    @Override
    public adapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_shap, parent, false);
        adapter mAdapter = new adapter(view);
        return mAdapter;
    }

    @Override
    public void onBindViewHolder(@NonNull adapter holder, final int position) {
        holder.mName.setText(mUsersModels.get(position).getmName());
        holder.mMail.setText(mUsersModels.get(position).getmLastMessage());
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        try {
            cal.setTimeInMillis(Long.parseLong(mUsersModels.get(position).getmTime()));
            String Date = DateFormat.format(" hh:mm aa", cal).toString();
            holder.mTime.setText(Date);

        } catch (Exception e) {

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, ChatActivity.class);
                mIntent.putExtra("Id", mUsersModels.get(position).getmId());
                mIntent.putExtra("Name", mUsersModels.get(position).getmName());

                mContext.startActivity(mIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsersModels.size();
    }

    class adapter extends RecyclerView.ViewHolder {

        TextView mName, mMail, mTime;

        public adapter(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.mName);
            mMail = itemView.findViewById(R.id.mLasMessage);
            mTime = itemView.findViewById(R.id.mTime);

        }
    }

}
