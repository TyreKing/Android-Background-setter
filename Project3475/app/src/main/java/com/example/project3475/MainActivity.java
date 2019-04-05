package com.example.project3475;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ParseJSON";
    MainActivity  mainActivity = this;

    public static final int MAX_LINES = 15;
    private static final int SPACES_TO_INDENT_FOR_EACH_LEVEL_OF_NESTING = 2;
    int statusCode;
    private static final int INITIAL_POS =0;
    JSONArray jsonArray;
    private TextView names;
    int numberentries = -1;
    int currententry = -1;
    Toolbar myToolbar;
    ArrayAdapter<String> myAdapter;
    ArrayList<String> setupStrings = new ArrayList<String>();
    ArrayList<String> files = new ArrayList<String>();
    static Spinner mySpinner;
    WebImageView_KP mv;
    String urlMain ="http://www.pcs.cnu.edu/~kperkins/pets/pets.json";
    private static final String MYURL = "http://www.pcs.cnu.edu/~kperkins/pets/pets.json";
    private static final String MYURL2 = "http://www.pcs.cnu.edu/~kperkins/pets/";
    private static final String MYURL3 = "http://www.tetonsoftware.com/pets/";
    private SharedPreferences.OnSharedPreferenceChangeListener listener = null;
    private SharedPreferences myPreference;
    ConnectivityCheck myCheck = new ConnectivityCheck(this);
    ImageView pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (myCheck.isNetworkReachable()) {
            DownloadTask process = new DownloadTask(this);
            process.execute(urlMain);
        }
        else{
            setContentView(R.layout.no_network);
       }
        setup();

    }




    public void showPreferenceActivity(View view) {
        // start the pref activity with the embedded pref fragment

        Intent myIntent = new Intent(this, SettingsActivity.class);
        startActivity(myIntent);
        setPreferenceChangeListener(view);
    }

    public void setPreferenceChangeListener(View view) {
        //if not created yet then do so
        if (listener == null) {
            listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                    switch(key){
                        case "info":

                            ArrayList<String> names = new ArrayList<String>();
                            Toast.makeText(getApplication(), "Handling which data to be loaded", Toast.LENGTH_SHORT).show();

                            myPreference.getString(key,"");
                            myPreference.registerOnSharedPreferenceChangeListener(listener);
                            urlMain = myPreference.getString(key,"");

                            myAdapter.notifyDataSetChanged();
                            try {
                                if(myCheck.isNetworkReachable()) {
                                    DownloadTask process = new DownloadTask(mainActivity);
                                    process.execute(urlMain);
                                }
                                else{
                                    setContentView(R.layout.no_network);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            Toast.makeText(MainActivity.this, "Yikes, handle this unknown key", Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
        myPreference.registerOnSharedPreferenceChangeListener(listener);
    }
    public void setup(){
        myPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        pref = (ImageView) findViewById(R.id.pref);
        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        mv = (WebImageView_KP) findViewById(R.id.kp);

        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        mySpinner = (Spinner) findViewById(R.id.p3_spinner);
        names = (TextView) findViewById(R.id.names);
        mySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (myCheck.isNetworkReachable()) {
                    if (urlMain.equals(MYURL)) {
                        mv.setImageUrl(MYURL2 + files.get(pos));
                    } else if (urlMain.equals(MYURL3)) {
                        mv.setImageUrl(MYURL3 + files.get(pos));
                    }
                } else {
                    setContentView(R.layout.no_network);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        setupStrings.clear();
        //spinnerSetup();
    }

    public void spinnerSetup() {
        try{
            for(int i = 0; i<jsonArray.length();++i) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                setupStrings.add(jsonObject.getString("name"));
                files.add(jsonObject.getString("file"));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        myAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_spinner_item,setupStrings);
        mySpinner.setAdapter(myAdapter);

    }
    public void processJSON(String string) {
        try {
            JSONObject jsonobject = new JSONObject(string);

            //*********************************
            //makes JSON indented, easier to read
//            Log.d(TAG,jsonobject.toString(SPACES_TO_INDENT_FOR_EACH_LEVEL_OF_NESTING));
//            tvRaw.setText(jsonobject.toString(SPACES_TO_INDENT_FOR_EACH_LEVEL_OF_NESTING));

            // you must know what the data format is, a bit brittle
            jsonArray = jsonobject.getJSONArray("pets");

            // how many entries
            numberentries = jsonArray.length();
            Log.i(TAG, "Number of entries " + numberentries);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }



}
