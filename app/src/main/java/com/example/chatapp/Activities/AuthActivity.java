package com.example.chatapp.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.chatapp.Adapters.fragmentPagerAdapter;
import com.example.chatapp.Fragments.LogInFragment;
import com.example.chatapp.Fragments.SignUpFragment;
import com.example.chatapp.R;

import com.google.android.material.tabs.TabLayout;

public class AuthActivity extends AppCompatActivity {
    TabLayout mTabLayout;
    ViewPager mViewPager;
    fragmentPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        instView();
    }

    private void instView() {
        mTabLayout = findViewById(R.id.tab);
        mViewPager = findViewById(R.id.viewpager);
        mPagerAdapter = new fragmentPagerAdapter(this.getSupportFragmentManager());
        mPagerAdapter.showFragments(new LogInFragment(),"Log In");
        mPagerAdapter.showFragments(new SignUpFragment() , "Sign Up");


        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
