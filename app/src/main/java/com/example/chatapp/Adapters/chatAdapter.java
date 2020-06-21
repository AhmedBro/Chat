package com.example.chatapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.adapter> {
    private final static int MSG_TYPE_LEFT = 1;
    private final static int MSG_TYPE_RIGHT = 0;
    Context mContext;
    ArrayList<ModelChat> mChat;
    FirebaseUser mFirebaseUser;

    public chatAdapter(Context mContext, ArrayList<ModelChat> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public chatAdapter.adapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shap_message_left, parent, false);
            return new adapter(view);

        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shap_message_right, parent, false);
            return new adapter(view);
        }


    }


    @Override
    public void onBindViewHolder(@NonNull final chatAdapter.adapter holder, final int position) {
        // Time

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(mChat.get(position).getmTime()));
        String Date = DateFormat.format("dd/mm/yyyy hh:mm aa", cal).toString();

        // set data

        holder.mMessage.setText(mChat.get(position).getmMessage());
        holder.mTime.setText(Date);

        //Is Seen
        if (position == mChat.size() - 1) {
            if (mChat.get(position).getmIsSeen()) {
                holder.mIsSeen.setText("Seen");
            } else {
                holder.mIsSeen.setText("Delivered");
            }

        } else {
            holder.mIsSeen.setVisibility(View.GONE);
        }


        //Delete Dialog
        holder.mLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setTitle("Delete");
                mBuilder.setMessage("Are you sure to delete this message ???");
                mBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteMessage(position);
                        Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();


                    }
                });

                mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mBuilder.create().show();
                return false;
            }
        });


    }

    private void DeleteMessage(int position) {
        String aTime = mChat.get(position).getmTime();
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("Chat");

        Query mQuery = mDatabaseReference.orderByChild("mTime").equalTo(aTime);
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //for Remove value
//                    ds.getRef().removeValue();

                    // tell to user this message Deleted
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("mMessage", "Deleted");
                    ds.getRef().updateChildren(map);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }


    @Override

    public int getItemViewType(int position) {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getmSender().equals(mFirebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }


    }


    public class adapter extends RecyclerView.ViewHolder {
        TextView mMessage, mTime, mIsSeen;
        LinearLayout mLinearLayout;

        public adapter(@NonNull View itemView) {
            super(itemView);

            mMessage = itemView.findViewById(R.id.mMessage);
            mTime = itemView.findViewById(R.id.mTime);
            mIsSeen = itemView.findViewById(R.id.mSeen);
            mLinearLayout = itemView.findViewById(R.id.mMessageLayout);
        }
    }
}
