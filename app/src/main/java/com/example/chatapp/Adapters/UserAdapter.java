package com.example.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Activities.ChatActivity;
import com.example.chatapp.Models.UsersModel;
import com.example.chatapp.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.adapter> {
    Context mContext;
    ArrayList<UsersModel> mUsersModels;

    public UserAdapter(Context mContext, ArrayList<UsersModel> mUsersModels) {
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
        holder.mMail.setText(mUsersModels.get(position).getmEmail());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, ChatActivity.class);
                mIntent.putExtra("Id" , mUsersModels.get(position).getId());
                mIntent.putExtra("Name" ,mUsersModels.get(position).getmName() );

                mContext.startActivity(mIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsersModels.size();
    }

    class adapter extends RecyclerView.ViewHolder {

        TextView mName,mMail;
        public adapter(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.mName);
            mMail = itemView.findViewById(R.id.mMail);

        }
    }

}
