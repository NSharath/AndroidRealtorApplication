package com.example.sharathn.newnavi;

/**
 * Created by SHARATH N on 5/15/2016.
 */

import android.widget.EditText;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class AddUpdatePlaceActivity extends AppCompatActivity {

    boolean isUpdate = false;
    int placeId;
    String placeJsonString;
    JSONObject placeJsonObject;

    Spinner propertyTypeSpinner;
    LayoutInflater layoutInflater;
    EditText placeNameEditText, streetEditText, cityEditText, stateEditText, zipEditText, priceEditText, roomsEditText, bathroomsEditText, areaEditText, phoneEditText, emailEditText, descriptionEditText;
    String placeName, street, city, state, zip, propertyType, phone, email, description;
    String rooms, bathrooms, area, price;
    String addorupdateUrl;
    String debugTag = "AddUpdatePlaceActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_place);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        //get the intent
        Intent intent = getIntent();

        //get the data associated with intent
        isUpdate = intent.getBooleanExtra("update", false);

        if (isUpdate) {
            placeId = intent.getIntExtra("placeid", -1);

            if (placeId == -1) {
                throw new RuntimeException("Please pass placeID");
            } else {
                getSupportActionBar().setTitle("Update Place");
                placeJsonString = intent.getStringExtra("result");
            }
        } else {
            getSupportActionBar().setTitle("Add Place");
        }


        //set spinner configuration (propertyTypeSpinner)

        //Get property type spinner and set adapter
        String[] spinnerItems = new String[3];
        spinnerItems[0] = "Condo";
        spinnerItems[1] = "Apartment";
        spinnerItems[2] = "Villa";

        propertyTypeSpinner = (Spinner) findViewById(R.id.propertyTypeSpinner);
        propertyTypeSpinner.setPrompt("Condo");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_item_custom, spinnerItems);

        propertyTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView selectedItem = (TextView) view;
                propertyType = selectedItem.getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        propertyTypeSpinner.setAdapter(adapter);


        //get other UI elements
        placeNameEditText = (EditText) findViewById(R.id.placeNameEditText);
        streetEditText = (EditText) findViewById(R.id.streetEditText);
        cityEditText = (EditText) findViewById(R.id.cityEditText);
        stateEditText = (EditText) findViewById(R.id.stateEditText);
        zipEditText = (EditText) findViewById(R.id.zipEditText);
        priceEditText = (EditText) findViewById(R.id.priceEditText);
        roomsEditText = (EditText) findViewById(R.id.roomsEditText);
        bathroomsEditText = (EditText) findViewById(R.id.bathroomsEditText);
        areaEditText = (EditText) findViewById(R.id.areaEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);


        //if update, populate edit text with fetched values values

        if (isUpdate) {
            try {
                placeJsonObject = new JSONObject(placeJsonString);

                street = placeJsonObject.getJSONObject("place").getJSONObject("address").getString("street-level");
                city = placeJsonObject.getJSONObject("place").getJSONObject("address").getString("city-name");
                state = placeJsonObject.getJSONObject("place").getJSONObject("address").getString("state");
                zip = placeJsonObject.getJSONObject("place").getJSONObject("address").getString("zip-code");
                placeName = placeJsonObject.getJSONObject("place").getString("name");
                rooms = placeJsonObject.getJSONObject("place").getString("rooms");
                bathrooms = placeJsonObject.getJSONObject("place").getString("bathrooms");
                area = placeJsonObject.getJSONObject("place").getString("area");
                price = placeJsonObject.getJSONObject("place").getString("price");
                phone = placeJsonObject.getJSONObject("place").getString("phone");
                email = placeJsonObject.getJSONObject("place").getString("email");
                description = placeJsonObject.getJSONObject("place").getString("description");
                //propertyType = placeJsonObject.getJSONObject("place").getString("name");
                JSONArray picUrlArray = placeJsonObject.getJSONObject("place").getJSONArray("imageurllist");


                //after getting all the values, update the UI
                //first update all edit texts and spinner, then pictures

                streetEditText.setText(street);
                cityEditText.setText(city);
                stateEditText.setText(state);
                zipEditText.setText(zip);
                placeNameEditText.setText(placeName);
                roomsEditText.setText(String.valueOf(rooms));
                bathroomsEditText.setText(String.valueOf(bathrooms));
                areaEditText.setText(String.valueOf(area));
                priceEditText.setText(String.valueOf(price));
                phoneEditText.setText(phone);
                emailEditText.setText(email);
                descriptionEditText.setText(description);
                propertyTypeSpinner.setPrompt(propertyType);


            } catch (JSONException e) {
                Log.e(debugTag, "Json string parse error");
                e.printStackTrace();
            }

        }
    }

    /*
        Method to store values from UI to variables.
     */
    private void storeValues() {

        //store values
        placeName = placeNameEditText.getText().toString();
        if (placeNameEditText.getText().toString().length() == 0)
            placeNameEditText.setError(" Name is required");

        street = streetEditText.getText().toString();
        if (streetEditText.getText().toString().length() == 0)
            streetEditText.setError(" Street is required");

        city = cityEditText.getText().toString();
        if (cityEditText.getText().toString().length() == 0)
            cityEditText.setError(" City is required");

        state = stateEditText.getText().toString();
        if (stateEditText.getText().toString().length() == 0)
            stateEditText.setError(" State is required");

        zip = zipEditText.getText().toString();
        if (zipEditText.getText().toString().length() == 0)
            zipEditText.setError(" Zip code is required");

        price = (priceEditText.getText().toString());
        if (priceEditText.getText().toString().length() == 0)
            priceEditText.setError(" price is required");

        rooms = (roomsEditText.getText().toString());
        if (roomsEditText.getText().toString().length()==0)
            roomsEditText.setError(" rooms is required");

        bathrooms = (bathroomsEditText.getText().toString());
        if (bathroomsEditText.getText().toString().length()==0)
            bathroomsEditText.setError(" bathroom is required");

        area = (areaEditText.getText().toString());
        if (areaEditText.getText().toString().length()==0)
            areaEditText.setError(" area is required");

        phone = phoneEditText.getText().toString();
        if (phoneEditText.getText().toString().length() == 0)
            phoneEditText.setError(" Phone is required");

        email = emailEditText.getText().toString();
        if (emailEditText.getText().toString().length() == 0)
            emailEditText.setError(" email is required");

        description = descriptionEditText.getText().toString();
        if (descriptionEditText.getText().toString().length() == 0)
            descriptionEditText.setError(" description is required");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_update_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.task_done:
                // will add place or update place
                System.out.println("inside update");
                processRequest();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void processRequest() {
        System.out.println("inside process request");
        // set latest test values, for spinner and pictureList, they will
        //already have latest values

        if (isUpdate) {
            addorupdateUrl = "http://52.39.4.163:3000/api/landlord/updatePlace/" + placeId;
            //    if ((placeNameEditText.getText().toString().equals("")) || ((streetEditText.getText().toString().equals(""))) || ((cityEditText.getText().toString().equals(""))) || ((stateEditText.getText().toString().equals(""))) || ((zipEditText.getText().toString().equals(""))) || ((priceEditText.getText().toString().equals(""))) || ((roomsEditText.getText().toString().equals(""))) || ((bathroomsEditText.getText().toString().equals(""))) || ((phoneEditText.getText().toString().equals(""))) || ((emailEditText.getText().toString().equals(""))) || ((descriptionEditText.getText().toString().equals("")))) {
            //fetch latest values from different edit texts and update
            storeValues();

            UpdatePlaceAsyncTask updatePlaceAsyncTask = new UpdatePlaceAsyncTask();
            updatePlaceAsyncTask.execute();
        } else {
            System.out.println("inside post");

            addorupdateUrl = "http://52.39.4.163:3000/api/landlord/addPlace";
            if ((placeNameEditText.getText().toString().equals("")) || ((streetEditText.getText().toString().equals(""))) || ((cityEditText.getText().toString().equals(""))) || ((stateEditText.getText().toString().equals(""))) || ((zipEditText.getText().toString().equals(""))) || ((priceEditText.getText().toString()).equals(null)) || ((priceEditText.getText().toString()).equals(null)) || ((priceEditText.getText().toString()).equals(null)) ||((priceEditText.getText().toString()).equals(null)) || ((phoneEditText.getText().toString().equals("")))|| ((emailEditText.getText().toString().equals(""))) || ((descriptionEditText.getText().toString().equals("")))) {
                //fetch latest values from different edit texts and update
                System.out.println("inside IF statement of post");
                storeValues();
                System.out.println("store values complete inside if");
            }
            //fetch latest values from different edit texts and update
            else {
                System.out.println("inside ASYNC TASK");
                AddPlaceAsyncTask addPlaceAsyncTask = new AddPlaceAsyncTask();
                addPlaceAsyncTask.execute();
            }
        }

    }


    /*
        Async task for adding a place
     */

    private class AddPlaceAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {


            Log.i(debugTag, "Adding a place");


            try {

                JSONObject jsonObject = new JSONObject();
                JSONObject place = new JSONObject();
                JSONObject address = new JSONObject();
                JSONArray imageUrlArray = new JSONArray();

                address.accumulate("street", street);
                address.accumulate("city", city);
                address.accumulate("state", state);
                address.accumulate("zip", zip);
                place.accumulate("address", address);

                place.accumulate("rooms", rooms);
                place.accumulate("bathrooms", bathrooms);
                place.accumulate("area", area);
                place.accumulate("price", price);
                place.accumulate("phone", phone);
                place.accumulate("name", placeName);
                place.accumulate("email", email);
                place.accumulate("description", description);
                place.accumulate("propertytype", propertyType);


                place.accumulate("imageurllist", imageUrlArray);
                jsonObject.accumulate("place", place);
                String email = Utility.getValueFromSP(AddUpdatePlaceActivity.this, "email");
//                String email = "sharu300@gmail.com";
                Log.i(debugTag, "EMail : " + email);


                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();

                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(addorupdateUrl);

                StringEntity se = new StringEntity(jsonObject.toString());

                httpPost.setEntity(se);
                httpPost.setHeader("Content-type", "application/json");
                httpPost.setHeader("token", email);

                HttpResponse httpResponse = httpclient.execute(httpPost);
                InputStream inputStream = httpResponse.getEntity().getContent();
                JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();

                    if (name.equals("result")) {
                        String result = reader.nextString();

                        if (result.equals("true")) {
                            //go to the next activity
                            Intent intent = new Intent(AddUpdatePlaceActivity.this, LandlordSearch.class);
                            startActivity(intent);
                        } else {

                            Log.e(debugTag, "result" + result);

                            // unable to add place
                            Log.e(debugTag, "Unable to add a place");
                        }
                    } else {
                        Log.e(debugTag, "Something wrong in Add Async task execute method");
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(debugTag, "Error while creating Json");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.e(debugTag, "Error while creating string entity from json");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(debugTag, "IO Exception");
            }

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    /*
        Async Task to update place
     */
    private class UpdatePlaceAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {

                JSONObject jsonObject = new JSONObject();
                JSONObject place = new JSONObject();
                JSONObject address = new JSONObject();
                JSONArray imageUrlArray = new JSONArray();

                address.accumulate("street", street);
                address.accumulate("city", city);
                address.accumulate("state", state);
                address.accumulate("zip", zip);
                place.accumulate("address", address);

                place.accumulate("rooms", rooms);
                place.accumulate("bathrooms", bathrooms);
                place.accumulate("area", area);
                place.accumulate("price", price);
                place.accumulate("phone", phone);
                place.accumulate("name", placeName);
                place.accumulate("email", email);
                place.accumulate("description", description);
                place.accumulate("propertytype", propertyType);


                place.accumulate("imageurllist", imageUrlArray);
                jsonObject.accumulate("place", place);
                String email = Utility.getValueFromSP(AddUpdatePlaceActivity.this, "email");
                //String email = "sharu300@gmail.com";


                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();

                // 2. make PUT request to the given URL

                HttpPut httpPut = new HttpPut(addorupdateUrl);

                StringEntity se = new StringEntity(jsonObject.toString());

                httpPut.setEntity(se);
                httpPut.setHeader("Content-type", "application/json");
                httpPut.setHeader("token", email);

                HttpResponse httpResponse = httpclient.execute(httpPut);
                InputStream inputStream = httpResponse.getEntity().getContent();
                JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();

                    if (name.equals("result")) {
                        String result = reader.nextString();

                        if (result.equals("true")) {
                            //go to the next activity
                            Intent intent = new Intent(AddUpdatePlaceActivity.this, LandlordSearch.class);
                            startActivity(intent);
                        } else {
                            // unable to add place
                            Log.e(debugTag, "Unable to update a place");
                        }
                    } else {
                        Log.e(debugTag, "Something wrong in Update Async task execute method");
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(debugTag, "Error while creating Json");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.e(debugTag, "Error while creating string entity from json");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(debugTag, "IO Exception");
            }

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


}



