package com.example.uventawh;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AddressesListActivity extends AppCompatActivity {

    private DataContractorAdapter adapter;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private String ref, description, ref_route;
    CheckBox cbRouteOnly;

    private List<Contractor> contractors = new ArrayList<>(), contractorsToShow = new ArrayList<>();
    private ArrayList<Address> addresses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses_list);

        Intent intent = getIntent();
        ref = intent.getStringExtra("ref");
        description = intent.getStringExtra("description");
        ref_route = intent.getStringExtra("ref_route");

        ((TextView) findViewById(R.id.tvHeader)).setText(intent.getStringExtra("header"));

        linearLayout = findViewById(R.id.linearLayout);
        cbRouteOnly = findViewById(R.id.cbRouteOnly);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        adapter = new DataContractorAdapter(this, contractorsToShow);

        adapter.setOnStringClickListener(new DataContractorAdapter.OnStringClickListener(){
            @Override
            public void onStringClick(Contractor str, View itemView) {

                Intent intent = new Intent();

                intent.putExtra("ref", str.ref);
                intent.putExtra("description", str.description);

                setResult(RESULT_OK, intent);

                finish();


            }
        });

        cbRouteOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                setFilter();

            }
        });

        RecyclerView recyclerViewToScan = findViewById(R.id.rcvList);
        recyclerViewToScan.setAdapter(adapter);

        if (ref_route == null){

            cbRouteOnly.setVisibility(View.GONE);

            update();

        } else {

            cbRouteOnly.setChecked(true);

            getAddressOfRoute();

        }

    }

    private void setFilter() {

        contractorsToShow.clear();

            if (cbRouteOnly.isChecked()) {

                for (Address address : addresses)
                    contractorsToShow.add(new Contractor(address.ref, address.description));

            } else {
                for (Contractor contractor : contractors)
                    contractorsToShow.add(contractor);
            }


        adapter.notifyDataSetChanged();

    }

    private void getAddressOfRoute() {

        final String[] str = new String[1];
        HttpClient client1 = new HttpClient(this);

        DB db = new DB(this);
        db.open();

        String url = "request/" + db.getConstant("user_id") + "/" + db.getConstant("prog_id");

        db.close();

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        try {
            requestParams.put("route", ref_route);
            requestParams.put("contractor", ref);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "getRouteSenders");
            request.put("parameters", requestParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put(request);

        client1.post(this, url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

                linearLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {

                linearLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                str[0] = null;
                try {
                    str[0] = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                JSONObject readerArray = null;
                try {
                    readerArray = new JSONObject(str[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray accept = new JSONArray();
                try {
                    accept = (JSONArray) readerArray.get("Response");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < accept.length(); i++){

                    JSONObject accept_item = null;

                    try {
                        accept_item = (JSONObject) accept.get(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (getStringFromJSON(accept_item, "Name").equals("getRouteSenders")){

                        JSONArray contractorsJSON = new JSONArray();
                        try {
                            contractorsJSON = (JSONArray) accept_item.get("RouteSenders");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int j = 0; j < contractorsJSON.length(); j++) {

                            JSONObject contractorsJSON_item = null;

                            try {
                                contractorsJSON_item = (JSONObject) contractorsJSON.get(j);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String ref = getStringFromJSON(contractorsJSON_item, "Ref");
                            String description = getStringFromJSON(contractorsJSON_item, "Description");

                            addresses.add(new Address(ref, description));
                        }

                        update();

                    }

                }



            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
//                if (debug){Toast toast = Toast.makeText(ChooserActivity.this, "FAIL. FBToken: " + FireBaseToken + "StatusCode" + String.valueOf(statusCode), Toast.LENGTH_SHORT);
//                    toast.show();}
                Log.v("HTTPLOG", String.valueOf(statusCode));
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });

    }



    protected void update(){

        final String[] str = new String[1];
        HttpClient client1 = new HttpClient(this);

        DB db = new DB(this);
        db.open();

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        try {
            requestParams.put("organization", ref);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "getAddresses");
            request.put("parameters", requestParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put(request);

        client1.post(this, "request/" + db.getConstant("user_id") + "/" + db.getConstant("prog_id"), params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

                linearLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {

                linearLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                str[0] = null;
                try {
                    str[0] = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                JSONObject readerArray = null;
                try {
                    readerArray = new JSONObject(str[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray accept = new JSONArray();
                try {
                    accept = (JSONArray) readerArray.get("Response");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < accept.length(); i++){

                    JSONObject accept_item = null;

                    try {
                        accept_item = (JSONObject) accept.get(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (getStringFromJSON(accept_item, "Name").equals("getAddresses")){

                        JSONArray contractorsJSON = new JSONArray();
                        try {
                            contractorsJSON = (JSONArray) accept_item.get("Addresses");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        contractors.clear();

                        for (int j = 0; j < contractorsJSON.length(); j++) {

                            JSONObject contractorsJSON_item = null;

                            try {
                                contractorsJSON_item = (JSONObject) contractorsJSON.get(j);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String ref = getStringFromJSON(contractorsJSON_item, "Ref");
                            String description = getStringFromJSON(contractorsJSON_item, "Description");

                            contractors.add(new Contractor(ref, description));
                        }

                        setFilter();


                    }

                }



            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
//                if (debug){Toast toast = Toast.makeText(ChooserActivity.this, "FAIL. FBToken: " + FireBaseToken + "StatusCode" + String.valueOf(statusCode), Toast.LENGTH_SHORT);
//                    toast.show();}
                Log.v("HTTPLOG", String.valueOf(statusCode));
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });






    }

    private String getStringFromJSON(JSONObject accept_item, String field_name) {
        String date = "";
        try {
            date = accept_item.getString(field_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return date;
    }



}
