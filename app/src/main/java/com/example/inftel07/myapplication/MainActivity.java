package com.example.inftel07.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import adapter.ListViewAdapter;
import model.Product;
import model.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private DrawerLayout drawer;
    private User user;
    private boolean search;
    private List<Product> listProduct;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private final int ACCESS_FINE_LOCATION = 0;

    private String urlProductCoordinate;
    private String urlProductUserName;
    private String urlProductQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize variables
        urlProductCoordinate = getString(R.string.urlProductCoordinate);
        urlProductUserName = getString(R.string.urlProductUserName);
        urlProductQuery = getString(R.string.urlProductQuery);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        //Get the user
        user = User.getInstance();
        printUserInformation(header);

        //floatingbutton
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newProductPressed();

            }
        });

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Auth.GOOGLE_SIGN_IN_API)
                    .build();
        }

        search = false;
        handleIntent(getIntent());

    }

       @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("MainActivity", query);
            search = true;
            getProducts(urlProductQuery + query);
        }
    }

    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void printList () {

        ListView productList = (ListView) findViewById(R.id.list);
        TextView textEmpty = (TextView) findViewById(R.id.empty_list);

        if (listProduct.size() != 0) {
            productList.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.INVISIBLE);
            ListViewAdapter adapter = new ListViewAdapter(this, listProduct);
            productList.setAdapter(adapter);

        } else {
            productList.setVisibility(View.INVISIBLE);
            textEmpty.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
        Log.d("MainActivity", "onConnectionFailed");
    }


    public void onConnected(Bundle connectionHint) {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) { // Check Permissions Now

            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION);

        } else {
            // permission has been granted, continue as usual
            mLastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (!search){
                getProducts(urlProductCoordinate + mLastLocation.getLongitude() + "/" + mLastLocation.getLatitude());
            }

        }

    }

    public void getProducts (String url) {

        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadProductClass().execute(url);
        } else {
            Log.d("MainActivity","No network connection available.");
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("MainActivity","onConnectionSuspended");

    }

    private class DownloadProductClass extends AsyncTask<String, Void, List<Product>> {

        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected List<Product> doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadProducts(urls[0]);

            } catch (IOException e) {
                Log.d("MainActivity","doInBackground exception");
                return null;
            }


        }

        @Override
        protected void onPostExecute(List<Product> json) {
            printList();
            Dialog.cancel();
        }

        @Override
        protected void onPreExecute()
        {
            Dialog.setMessage(getString(R.string.loading));
            Dialog.show();
        }
    }


    private List<Product> downloadProducts(String myurl) throws IOException {

        URL obj = new URL(myurl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
        con.disconnect();

        JSONArray json = null;
        try {
            json = new JSONArray(response.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        listProduct = gson.fromJson(String.valueOf(json), new TypeToken<List<Product>>(){}.getType());

        return listProduct;
    }



    public void newProductPressed () {
        Intent createProductIntent = new Intent(this, pruebagit.class);
        startActivity(createProductIntent);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Configure the search info and add any event listeners
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_products) {
            getProducts(urlProductUserName+user.getEmail());

        } else if (id == R.id.products_area) {
            getProducts(urlProductCoordinate + mLastLocation.getLongitude() + "/" + mLastLocation.getLatitude());

        } else if (id == R.id.logout) {

            Auth.GoogleSignInApi.signOut(mGoogleApiClient);

            user = null;

            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putString("email", "");
            editor.putString("username", "");
            editor.putString("photo", "");
            editor.commit();

            Intent logoutIntent = new Intent(this, Login.class);
            startActivity(logoutIntent);
            finish();

        } else if (id == R.id.new_product) {
            newProductPressed();
        }

        setTitle(item.getTitle());
        drawer.closeDrawers();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {


        switch (requestCode) {
            case ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission has been granted, continue as usual
                    mLastLocation =
                            LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                    ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        new DownloadProductClass().execute(urlProductCoordinate + mLastLocation.getLongitude() + "/" + mLastLocation.getLatitude());
                    } else {
                        Log.d("MainActivity", "No network connection available.");
                    }

                } else {
                    Toast.makeText(this, getString(R.string.permissions), Toast.LENGTH_LONG).show();
                }
                return;
            }
        }

    }


    public void printUserInformation (View header) {

        TextView textEmail = (TextView) header.findViewById(R.id.textViewEmail);
        textEmail.setText(user.getEmail());

        TextView textUserName = (TextView) header.findViewById(R.id.textViewUsername);
        textUserName.setText(user.getUsername());

        ImageView userImage = (ImageView) header.findViewById(R.id.imageViewUser);
        if(!user.getPhoto().equals("")){
            Picasso.with(this).load(user.getPhoto()).into(userImage);
        }

    }

}

