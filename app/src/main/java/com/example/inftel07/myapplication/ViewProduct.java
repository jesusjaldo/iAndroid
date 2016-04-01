package com.example.inftel07.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import model.Product;
import model.User;
import util.Util;

public class ViewProduct extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location mLastLocation;
    private Product product;

    private String urlProduct;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize
        urlProduct = getString(R.string.urlsaveProduct);

        String productString = getIntent().getStringExtra("product");
        Gson gson = new Gson();
        product = gson.fromJson(productString, Product.class);
        setTitle(product.getNameproduct());

        ImageView imageProduct = (ImageView) findViewById(R.id.imageProduct);
        Bitmap bitmap = Util.base64ToBitmap(product.getImage());
        imageProduct.setImageBitmap(bitmap);

        TextView priceProduct = (TextView) findViewById(R.id.priceProduct);
        priceProduct.setText(product.getPrice());

        TextView nameProduct = (TextView) findViewById(R.id.nameProduct);
        nameProduct.setText(product.getNameproduct());

        TextView descriptionProduct = (TextView) findViewById(R.id.descriptionProduct);
        descriptionProduct.setText(product.getDescription());

        //Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        User user = User.getInstance();
        if (user.getEmail().equals(product.getUsername().getUsername())) {
            getMenuInflater().inflate(R.menu.viewproductmenu, menu);
        } else {
            getMenuInflater().inflate(R.menu.viewproductmenuemail, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            FragmentManager fragmentManager = getFragmentManager();
            ConfirmDialog dialog = new ConfirmDialog();
            dialog.show(fragmentManager, "fragmentDialog");

            return true;
        }

        if (id == R.id.action_email) {
            Intent emailIntent = Util.sendMail(product, getString(R.string.interested));
            startActivity(Intent.createChooser(emailIntent, getString(R.string.emailClient)));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*mGoogleApiClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ViewProduct Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.inftel07.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);*/
    }

    @Override
    public void onStop() {
        super.onStop();
        /*
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ViewProduct Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.inftel07.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
        mGoogleApiClient.disconnect();*/
    }


    private class RemoveProductClass extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return removeProduct(urls[0]);


            } catch (IOException e) {
                Log.d("RemoveProductClass", "Unable to retrieve web page. URL may be invalid.");
                return null;
            }

        }

        @Override
        protected void onPostExecute(String json) {

        }
    }


    private String removeProduct(String myurl) throws IOException {


        URL obj = new URL(myurl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("DELETE");
        con.getResponseCode();
        con.disconnect();

        return "";
    }

    public class ConfirmDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setMessage(getString(R.string.questionDelete))
                    .setTitle(getString(R.string.confirmation))
                    .setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.i("Dialogos", "Confirmacion Aceptada.");
                            new RemoveProductClass().execute(urlProduct + product.getIdproduct());
                            NavUtils.navigateUpFromSameTask(getActivity());
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.i("Dialogos", "Confirmacion Cancelada.");
                            dialog.cancel();
                        }
                    });

            return builder.create();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Random r = new Random();
        double rangeLatitude = (r.nextDouble() * 2 - 1)/100;
        double rangeLongitude = (r.nextDouble() * 2 - 1)/100;

        LatLng latlng = new LatLng(Double.valueOf(product.getLatitude()) + rangeLatitude,
                Double.valueOf(product.getLongitude()) + rangeLongitude);

        CircleOptions circleOptions = new CircleOptions()
                .center(latlng)
                .radius(1500)// In meters
                .strokeColor(Color.parseColor("#009688"));

        // Get back the mutable Circle
        mMap.addCircle(circleOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));
    }
}
