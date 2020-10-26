package com.example.chatapp.Activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.ContactsAdapter;
import com.example.chatapp.Coverting.Iso2Phone;
import com.example.chatapp.Models.Contact;
import com.example.chatapp.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsActivity extends AppCompatActivity {

    @BindView(R.id.allContacts)
    RecyclerView mAllContacts;
    ArrayList<Contact> mContacts;
   static ArrayList<Contact> mAppContact;
    ContactsAdapter mContactsAdapter;
    public Contact mContact;
    private ProgressDialog progressDialog;
    String dialogTitle;
    String dialogMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);
        mContacts = new ArrayList<>();
        mAppContact = new ArrayList<>();
        progressDialog = new ProgressDialog( ContactsActivity.this);
        dialogTitle = "Loading...";
        dialogMessage = "Please wait...";
        progressDialog.setTitle(dialogTitle);
        progressDialog.setMessage(dialogMessage);


        InsitRecyclearView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LoadallContacts();
            }
        },50);
    }

    private void LoadallContacts() {

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

            String Image = cur.getString(cur.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            mContact = new Contact(phoneNo,"", name, Image);
            mContacts.add(mContact);
            Log.e("Name  , number" , name + " , " + phoneNo);
            getUserApp(mContact);
        }

    }

    private void getUserApp(Contact Contact) {


        DatabaseReference UserDb = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = UserDb.orderByChild("mPhone").equalTo(Contact.getmPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String Phone = "", Name = "";
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        dataSnapshot.child("mPhone").getValue().toString();
                        Phone = dataSnapshot.child("mPhone").getValue().toString();
                        dataSnapshot.child("mName").getValue().toString();
                        Name = Contact.getmName();
                        if (!dataSnapshot.child("mImage").getValue().toString().equals("")){
                            mContact = new Contact(Phone, dataSnapshot.getKey(), Name, dataSnapshot.child("mImage").getValue().toString());

                        }
                        else {
                            mContact = new Contact(Phone,dataSnapshot.getKey(), Name, "");
                        }


                        mAppContact.add(mContact);
                        mContactsAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private void InsitRecyclearView() {
        progressDialog.show();

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mAllContacts.setLayoutManager(manager);
        mAllContacts.setHasFixedSize(false);
        mAllContacts.setNestedScrollingEnabled(false);
        mContactsAdapter = new ContactsAdapter(mAppContact, ContactsActivity.this);
        mAllContacts.setAdapter(mContactsAdapter);


    }
}