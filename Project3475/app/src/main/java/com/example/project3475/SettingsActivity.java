package com.example.project3475;


import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceFragment;

import android.support.v7.app.AppCompatActivity;



public class SettingsActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        PrefFragment mPrefsFragment = new PrefFragment();
        mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
        mFragmentTransaction.commit();
    }
}




