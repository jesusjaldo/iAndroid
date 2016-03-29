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
import java.util.List;

import model.Product;
import model.User;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final long SPLASH_TIME_OUT = 2;
    private GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN = 1;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private SharedPreferences sharedPref;

    private String urlLogin = "http://192.168.183.43:8080/iChoppingWS/webresources/model.membership/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize sdk Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

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

        } else {

            setContentView(R.layout.activity_login);

            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                            //.requestProfile()
                    .build();

            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();



            //Set the listener button signIn
            findViewById(R.id.sign_in_button).setOnClickListener(this);

            //Facebook button
            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                @Override
                public void onSuccess(LoginResult loginResult) {


                    GraphRequest data_request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject json_object,
                                        GraphResponse response) {

                                    try {
                                        JSONObject jsonPicture = ((JSONObject) json_object.get("picture")).getJSONObject("data");
                                        User user = User.getInstance();
                                        user.setEmail(json_object.getString("id"));
                                        user.setPhoto(jsonPicture.getString("url"));
                                        user.setUsername(json_object.getString("name"));
                                        MainActivity.loginMode = MainActivity.FACEBOOK;
                                        new LoginUserClass().execute(urlLogin);
                                        goMainActivity(user, true);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            });
                    Bundle permission_param = new Bundle();
                    permission_param.putString("fields", "id,name,email,picture");
                    data_request.setParameters(permission_param);
                    data_request.executeAsync();

                }

                @Override
                public void onCancel() {
                    System.out.println("Login attempt canceled.");

                }

                @Override
                public void onError(FacebookException e) {
                    System.out.println("Login attempt failed.");

                }
            });
        }

    }




    @Override
    public void onConnectionFailed(ConnectionResult result){
        Log.i("MainActivity", "Conection Failed");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
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
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("MainAct", "handleSignInResult:" + result.isSuccess());

        MainActivity.loginMode = MainActivity.GOOGLE;

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            User user = User.getInstance();
            user.setEmail(acct.getEmail());
            if (acct.getPhotoUrl() == null) {
                user.setPhoto("https://cdn3.iconfinder.com/data/icons/ballicons-free/128/wooman.png");
            } else {
                user.setPhoto("http://" + acct.getPhotoUrl().getHost() + acct.getPhotoUrl().getPath());
            }

            user.setUsername(acct.getDisplayName());

            System.out.println("USER: " + user.getEmail());
            MainActivity.loginMode = MainActivity.GOOGLE;
            new LoginUserClass().execute(urlLogin);
            goMainActivity(user, true);

        } else {
            Log.d("MainAct", "NameSignInResult Error");
        }
    }


    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        Log.d("MainAct", "Sign OUT");
                    }
                });
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
                System.out.println("LoginUser " + urls[0]);
                return loginUser(urls[0]);


            } catch (IOException e) {
                System.out.println("Unable to retrieve web page. URL may be invalid.");
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

        // optional default is GET
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        System.out.println("URLGET: " + getURL);
        System.out.println("Response Code : " + responseCode);

        if (responseCode != 200) {

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

            System.out.println("JSONREQ-2: " + jsonObject);

            OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
            wr.write(jsonObject.toString());
            wr.flush();
            wr.close();

            // Starts the query
            conn.connect();

            System.out.println("RESPONSE: " + conn.getResponseMessage());
            int response2 = conn.getResponseCode();
            Log.d("DEBUGTAG", "The response is: " + response2);


        }

        con.disconnect();

        return "";
    }



}
