package com.example.chatapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogInActivity extends AppCompatActivity {


    @BindView(R.id.ccp)
    CountryCodePicker ccp;
    @BindView(R.id.mPhone)
    TextInputEditText mPhone;
    @BindView(R.id.mNext)
    Button mNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        logIn();


    }


    @OnClick(R.id.mNext)
    public void onViewClicked() {
        Intent mIntent = new Intent(this, Verfication.class);
        mIntent.putExtra("mPhone", "+" + ccp.getSelectedCountryCode() + mPhone.getText().toString().replaceFirst("^0+(?!$)", ""));
        startActivity(mIntent);
        finish();
    }


    private void logIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Intent mIntent = new Intent(this, MainActivity.class);
            startActivity(mIntent);
            finish();
        }
    }





}
