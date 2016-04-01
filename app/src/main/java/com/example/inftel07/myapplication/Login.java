package com.example.inftel07.myapplication;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import model.Product;
import model.User;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    //Login Google
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    private int RC_SIGN_IN = 1;
    //SharedPreference for login
    private SharedPreferences sharedPref;
    //URL restful Login
    private String urlLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize variables
        urlLogin = getString(R.string.urlLogin);

        //Load content sharedPreference
        sharedPref =  getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String email = sharedPref.getString("email", "");
        String username = sharedPref.getString("username", "");
        String photo = sharedPref.getString("photo", "");
        if (!email.equals("")){
            User user = User.getInstance();
            user.setEmail(email);
            user.setPhoto(photo);
            user.setUsername(username);
            goMainActivity(user, false);

        } else { //Login

            setContentView(R.layout.activity_login);

            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            //Set the listener button signIn
            findViewById(R.id.sign_in_button).setOnClickListener(this);

        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result){
        Log.d("Login", "Conection Failed");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {

        Log.d("Login", "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            User user = User.getInstance();
            user.setEmail(acct.getEmail());
            if (acct.getPhotoUrl() == null) {
                user.setPhoto("");
            } else {
                user.setPhoto("http://" + acct.getPhotoUrl().getHost() + acct.getPhotoUrl().getPath());
            }

            user.setUsername(acct.getDisplayName());
            new LoginUserClass().execute(urlLogin);
            goMainActivity(user, true);

        } else {
            Log.d("Login", "NameSignInResult Error");
        }
    }


    private void goMainActivity(User user, boolean newSession){

        if (newSession) {
            sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putString("email", user.getEmail());
            editor.putString("username", user.getUsername());
            editor.putString("photo", user.getPhoto());
            editor.commit();

        }

        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
        finish();

    }


    private class LoginUserClass extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return loginUser(urls[0]);

            } catch (IOException e) {
                Log.d("Login", "Unable to retrieve web page. URL may be invalid.");
                return null;
            }
        }

        @Override
        protected void onPostExecute(String json) {

        }
    }


    private String loginUser(String myurl) throws IOException {
        User user = User.getInstance();
        String getURL = myurl + user.getEmail();

        URL obj = new URL(getURL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();

        if (responseCode == 204) {

            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", user.getEmail());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
            wr.write(jsonObject.toString());
            wr.flush();
            wr.close();

            // Starts the query
            conn.connect();

            int response2 = conn.getResponseCode();
            Log.d("Login", "The response is: " + response2);
        }

        con.disconnect();

        return "";
    }



}
