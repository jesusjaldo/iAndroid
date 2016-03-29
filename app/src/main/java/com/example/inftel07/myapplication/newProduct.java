package com.example.inftel07.myapplication;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import model.Product;
import model.User;
import util.Util;

public class NewProduct extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    ImageButton photo;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Product product;
    private User user;
    private Bitmap imageBitmap;
    private ImageView imageProduct;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    int ACCESS_FINE_LOCATION = 0;
    private String saveProduct = "http://192.168.183.43:8080/iChoppingWS/webresources/model.product";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize
        product = new Product();
        user = User.getInstance();

        //Photo
        photo = (ImageButton) findViewById(R.id.photo);
        photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        imageProduct = (ImageView) findViewById(R.id.imageViewPhoto);
        if (savedInstanceState!=null){
            Log.d("Image","savedInstance != null");
            Bitmap imageBM = savedInstanceState.getParcelable("BitmapImage");
            if (imageBM!=null){
                Log.d("Image", "imaggeInstance != null");
                imageProduct.setImageBitmap(imageBM);
                //photo.setVisibility(View.GONE);
                product.setImage(Util.bitmapToBase64(imageBM));
            }
        }


        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        Log.d("Image", "OnSaveInstance");

        savedInstanceState.putParcelable("BitmapImage", imageBitmap);
        super.onSaveInstanceState(savedInstanceState);


    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){

        imageBitmap = savedInstanceState.getParcelable("BitmapImage");
        imageProduct.setImageBitmap(imageBitmap);
        //photo.setVisibility(View.GONE);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.newproductmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {

            TextInputEditText textNameProduct = (TextInputEditText) findViewById(R.id.input_nameProduct);
            product.setNameproduct(textNameProduct.getText().toString());

            TextInputEditText textDescriptionProduct = (TextInputEditText) findViewById(R.id.input_descriptionProduct);
            product.setDescription(textDescriptionProduct.getText().toString());

            TextInputEditText textPriceProduct = (TextInputEditText) findViewById(R.id.input_price);
            product.setPrice(textPriceProduct.getText().toString() + " €");

            if (textNameProduct.getText().toString().equals("") | textDescriptionProduct.getText().toString().equals("") |
                    textPriceProduct.getText().toString().equals("")){
                Toast.makeText(getBaseContext(), "Debes rellenar todos los campos", Toast.LENGTH_LONG).show();
            }
            else {

                product.setUsername(user);

                product.setLatitude(Double.toString(mLastLocation.getLatitude()));
                product.setLongitude(Double.toString(mLastLocation.getLongitude()));

                System.out.println("PRODUCT" + product);

                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new UploadProductClass().execute(saveProduct);
                } else {
                    System.out.println("No network connection available.");
                }

                NavUtils.navigateUpFromSameTask(this);
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class UploadProductClass extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {

                return uploadProduct(urls[0]);
            } catch (IOException e) {
                System.out.println("Unable to retrieve web page. URL may be invalid.");
                return null;
            }


        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String json) {
            // Convert String to json object
            Toast.makeText(getBaseContext(), " Nuevo Producto Guardado", Toast.LENGTH_LONG).show();
        }
    }

    private String uploadProduct(String myurl) throws IOException {



        URL url = new URL(saveProduct);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        //String textPost = (new Gson()).toJson(product);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nameproduct", product.getNameproduct());
            jsonObject.put("description", product.getDescription());
            jsonObject.put("price", product.getPrice());
            jsonObject.put("longitude", product.getLongitude());
            jsonObject.put("latitude", product.getLatitude());
            JSONObject username = new JSONObject();
            username.put("username", user.getEmail());
            jsonObject.put("username", username);
            jsonObject.put("image", product.getImage());
            jsonObject.put("idproduct", 1);

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
        int response = conn.getResponseCode();
        Log.d("DEBUGTAG", "The response is: " + response);

        return "";

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            product.setImage(Util.bitmapToBase64(imageBitmap));
            imageProduct.setImageBitmap(imageBitmap);
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently

        // ...
    }

    public void onConnected(Bundle connectionHint) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            final int REQUEST_LOCATION = 2;

            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION);

        } else {
            // permission has been granted, continue as usual
            mLastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            System.out.println("Latitude: " +    mLastLocation.getLatitude());

            //Map
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        }



    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LatLng latlng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        /*CircleOptions circleOptions = new CircleOptions()
                .center(latlng)
                .radius(1000)// In meters
                .strokeColor(Color.parseColor("#009688"));*/

        // Get back the mutable Circle

        mMap.addMarker(new MarkerOptions().position(latlng).title("¡Aquí estás!"));
        //Circle circle = mMap.addCircle(circleOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));
    }

}
