package com.example.chatapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Verfication extends AppCompatActivity {
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    static String VerId;
    @BindView(R.id.mPhone)
    TextView mPhone;
    @BindView(R.id.mCode)
    PinEntryEditText mCode;
    static String mUserPhone;
    ProgressDialog mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verfication);
        ButterKnife.bind(this);
        Log.e("mPhone", getIntent().getStringExtra("mPhone"));
        mUserPhone = getIntent().getStringExtra("mPhone");
        mPhone.setText(mUserPhone);
        mProgressBar = new ProgressDialog(this);
        mProgressBar.setTitle("Loading");


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                SignInWithPhoneCredental(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e("EEEEEE", e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                VerId = s;
            }
        };

        SendVereficition();
        mCode.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence str) {
                mProgressBar.show();
                VerifyWithCode(String.valueOf(str));
            }
        });


    }

    private void logIn() {
            Intent mIntent = new Intent(this, MainActivity.class);
            startActivity(mIntent);
            finish();
    }

    private void SendVereficition() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mUserPhone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void SignInWithPhoneCredental(PhoneAuthCredential phoneAuthCredential) {
        mProgressBar.show();
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    Log.e("Sucesss", "Sucessssss");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    HashMap<String, Object> map = new HashMap();
                    assert user != null;
                    map.put("mName", user.getPhoneNumber());
                    map.put("mPhone", user.getPhoneNumber());
                    map.put("mImage", "");
                    map.put("mId" , user.getUid());
                    map.put("mStatus" , "0");
                    map.put("mTyping" , "");
                    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                    mDatabaseReference.updateChildren(map);
                    logIn();
                    mProgressBar.dismiss();
                }
            }

        });
    }

    void VerifyWithCode(String vr) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerId, vr);
        SignInWithPhoneCredental(credential);
    }


}