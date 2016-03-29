/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.inftel07.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import adapter.ListViewAdapter;
import model.Product;
import model.User;

public class HeadlinesFragment extends ListFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    OnHeadlineSelectedListener mCallback;
    List<Product> listProduct;
    ListViewAdapter adapter;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    int ACCESS_FINE_LOCATION = 0;





    private String urlProductCoordinate = "http://192.168.183.43:8080/iChoppingWS/webresources/model.product/getProductByCoordinate/";

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnHeadlineSelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onArticleSelected(int position);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        /*

        ListView productList= (ListView) getActivity().findViewById(R.id.list);

        System.out.println("Priemro");

        Product p = new Product("nameproduct", "price", "description", "image", "longitude" , "latitude", User.getInstance(), 2);
        List<Product> prueba = new ArrayList<>();
        prueba.add(p);

        ListViewAdapter adapter = new ListViewAdapter(getActivity().getBaseContext(),prueba);

        System.out.println("Psegund");

        productList.setAdapter(adapter);*/


    }


    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
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


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            final int REQUEST_LOCATION = 2;

            ActivityCompat.requestPermissions(
                    getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION);
            System.out.println("espro?");

        } else {
            // permission has been granted, continue as usual
            mLastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                System.out.println("yuju " + mLastLocation.getLatitude() + " " + mLastLocation.getLongitude());
                //Añadir longitud latitud
                new DownloadProductClass().execute(urlProductCoordinate + mLastLocation.getLongitude() + "/" + mLastLocation.getLatitude());
                System.out.println("onCreate tras execute");
            } else {
                System.out.println("No network connection available.");
            }
        }


    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        System.out.println("lospermosps");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private class DownloadProductClass extends AsyncTask<String, Void, List<Product>> {
        @Override
        protected List<Product> doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {

                System.out.println("downloadProducts");
                return downloadProducts(urls[0]);


            } catch (IOException e) {
                System.out.println("Unable to retrieve web page. URL may be invalid.");
                return null;
            }


        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(List<Product> json) {
            // Convert String to json object

            // Create an array adapter for the list view, using the Ipsum headlines array;

            //setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.listfragment, R.id.nameProductList, Ipsum.Articles));
            //setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.listfragment, R.id.priceProductList, Ipsum.Headlines));



            System.out.println("Cero");
            ListView productList= (ListView) getActivity().findViewById(R.id.list);

            System.out.println("Priemro");

            ListViewAdapter adapter = new ListViewAdapter(getActivity(),listProduct);
            System.out.println("Psegund");
            productList.setAdapter(adapter);

            //adapter.notifyDataSetChanged();
            System.out.println("Ptercerd");

        }
    }


    private List<Product> downloadProducts(String myurl) throws IOException {

            URL obj = new URL(myurl);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            con.disconnect();

            System.out.println("RESPUESTAAA: " + response.toString());

        JSONArray json = null;
        try {
            json = new JSONArray(response.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //System.out.println("aleho: " + json);

        Gson gson = new Gson();
        listProduct = gson.fromJson(String.valueOf(json), new TypeToken<List<Product>>(){}.getType());

        //System.out.println("erprimero: " + listProduct.get(0).toString());

        return listProduct;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // When in two-pane layout, set the listview to highlight the selected list item
//        // (We do this during onStart because at the point the listview is available.)
//        if (getFragmentManager().findFragmentById(R.id.article) != null) {
//            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        }
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//
//        // This makes sure that the container activity has implemented
//        // the callback interface. If not, it throws an exception.
//        try {
//            mCallback = (OnHeadlineSelectedListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnHeadlineSelectedListener");
//        }
//    }
//
//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        // Notify the parent activity of selected item
//        mCallback.onArticleSelected(position);
//
//        // Set the item as checked to be highlighted when in two-pane layout
//        getListView().setItemChecked(position, true);
//    }


}