package com.example.uventawh;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class ShipmentTestActivity extends AppCompatActivity {

    String ref, name, number, date, refContractor, company, refSender, sender, curShtrihCode, currentPhotoPath;
    Integer image;

    EditText editText;
    TextView error, filter;

    List<String> toScanStart = new ArrayList<>();
    List<String> toScan = new ArrayList<>();
    List<String> scanned = new ArrayList<>();
    DataStringAdapter adapterToScan, adapterScanned;

    ProgressBar progressBar;
    LinearLayout llMain;

    private Timer timer=new Timer();
    private final long DELAY = 1000; // milliseconds

    int cnt;
    final int max = 100;
    Handler h;

    Boolean manualInput = false, sendingInProgress = false;

    Uri outputFileUri;

    private static final int CAMERA_REQUEST = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shipment_test);

        h = new Handler();

        Intent intent = getIntent();
        ref = intent.getStringExtra("ref");
        name = intent.getStringExtra("name");
        number = intent.getStringExtra("number");
        date = intent.getStringExtra("date");
        refContractor = intent.getStringExtra("refContractor");
        company = intent.getStringExtra("company");
        refSender = intent.getStringExtra("refSender");
        sender = intent.getStringExtra("sender");
        image = (int) intent.getLongExtra("image", 0);

        progressBar = findViewById(R.id.progressBar);
        llMain = findViewById(R.id.llMain);

        ((TextView) findViewById(R.id.header)).setText(name + " №" + number + " от " + date + " " + company + ", " + sender);

        error = (TextView) findViewById(R.id.error);

        editText = (EditText) findViewById(R.id.editText);
        editText.setOnKeyListener(new View.OnKeyListener()
                                  {
                                      public boolean onKey(View v, int keyCode, KeyEvent event)
                                      {
                                          if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                                  (keyCode == KeyEvent.KEYCODE_ENTER))
                                          {
                                              manualInput = false;

                                              String strCatName = editText.getText().toString();

                                              editText.setText("");

                                              scanShtrihCode(strCatName);

//                                              filter.setText("");
//
//                                              setFilter("");

                                              return true;
                                          }
                                          return false;
                                      }
                                  }
        );

        Thread t = new Thread(new Runnable() {
            public void run() {
                h.post(setFocus);
            }
        });
        t.start();


        adapterToScan = new DataStringAdapter(this, toScan);

        adapterToScan.setOnStringClickListener(new DataStringAdapter.OnStringClickListener(){
            @Override
            public void onStringClick(String str, View itemView) {

//                if (manualInput) {
//
//                    scanShtrihCode(str);
//
//                    filter.setText("");
//
//                    setFilter("");
//
//                    manualInput = false;
//                }
            }
        });



        RecyclerView recyclerViewToScan = findViewById(R.id.toScan);
        recyclerViewToScan.setAdapter(adapterToScan);

        RecyclerView recyclerViewScanned = findViewById(R.id.scanned);
        adapterScanned = new DataStringAdapter(this, scanned);
        recyclerViewScanned.setAdapter(adapterScanned);

        adapterScanned.setOnStringClickListener(new DataStringAdapter.OnStringClickListener(){
            @Override
            public void onStringClick(String str, View itemView) {

//                askForRepeatCode(str);

            }
        });


        getShtrihs();




    }

    protected void getShtrihs(){

        String url = new DB(this).getRequestUserProg();

        httpShtrihs(url);

    }



    private void scanShtrihCode(String strCatName) {

        if (!sendingInProgress){

            Integer index = toScan.indexOf(strCatName);

            if (index < 0){
                error.setText("Не найден штрихкод " + strCatName);
            }
            else {
                error.setText("");

                toScan.remove(strCatName);

//                setFilter("");

                scanned.add(0, strCatName);
                adapterScanned.notifyDataSetChanged();

                setShtrihs(strCatName);
            }
        }
    }

    final Runnable setFocus = new Runnable() {
        public void run() {

            editText.requestFocus();

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            h.postDelayed(setFocus, 500);

        }
    };

    private void setFilter(String s){

        String curText = (String) filter.getText();

        if(s.equals("<")){

            if(curText.length() > 0){

                curText = curText.substring(0, curText.length()-1);
            }

        }
        else{
            curText = curText + s;
        }

        filter.setText(curText);
        toScan.clear();

        for(String str : toScanStart){

            if(str.contains(curText)){
                toScan.add(str);
            }

        }

        adapterToScan.notifyDataSetChanged();

        manualInput = curText.length() > 0;

    }

    protected void setShtrihs(String shtrihcode){

        String url = new DB(this).getRequestUserProg();

        httpSetShtrihs(url, shtrihcode);

    }

    protected void httpShtrihs(String url){

        final String[] str = new String[1];
        final HttpClient client1 = new HttpClient(this);

        sendingInProgress = true;

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        try {
            requestParams.put("refRoute", ref);
            requestParams.put("refContractor", refContractor);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "getRouteShtrihs");
            request.put("parameters", requestParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put(request);

        client1.post(this, url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

                llMain.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {

                llMain.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                sendingInProgress = false;

                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                JSONArray response = client1.getResponseJSONArray(responseBody);

                for (int i = 0; i < response.length(); i++) {

                    JSONObject response_item = client1.getItemJSONArray(response, i);

                    if (client1.getStringFromJSON(response_item, "Name").equals("getRouteShtrihs")) {

                        toScan.clear();
                        scanned.clear();

                        JSONArray routeShtrihs = client1.getJsonArrayFromJsonObject(response_item, "RouteShtrihs");

                        for (int j = 0; j < routeShtrihs.length(); j++) {

                            JSONObject accept_item = null;

                            try {
                                accept_item = (JSONObject) routeShtrihs.get(j);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Boolean tested = client1.getBooleanFromJSON(accept_item, "Tested");
                            String shtrih_code = client1.getStringFromJSON(accept_item, "ShtrihCode");

                            if (tested){

                                scanned.add(shtrih_code);
                            } else {

                                toScan.add(shtrih_code);
                            }


                        }

                        Integer scannedQ = scanned.size();
                        Integer toAcceptQ = toScan.size();

                        Integer procent = toAcceptQ == 0 ? 100 : scannedQ * 100 / toAcceptQ;

                        ((TextView) findViewById(R.id.scannedText)).setText(scannedQ.toString() + " из " + toAcceptQ.toString()
                                + ", " + procent.toString() + "%");

                        adapterToScan.notifyDataSetChanged();
                        adapterScanned.notifyDataSetChanged();


                    }
                }
            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.v("HTTPLOG", String.valueOf(statusCode));
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });

    }

    protected void httpSetShtrihs(String url, String shtrihcode){

        final String[] str = new String[1];
        final HttpClient client1 = new HttpClient(this);

        sendingInProgress = true;

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        try {
            requestParams.put("refRoute", ref);
            requestParams.put("refContractor", refContractor);
            requestParams.put("shtrihcode", shtrihcode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "setRouteShtrihCode");
            request.put("parameters", requestParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put(request);

        client1.post(this, url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

                llMain.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {

                llMain.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                sendingInProgress = false;

                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                JSONArray response = client1.getResponseJSONArray(responseBody);

                for (int i = 0; i < response.length(); i++) {

                    JSONObject response_item = client1.getItemJSONArray(response, i);

                    if (client1.getStringFromJSON(response_item, "Name").equals("setRouteShtrihCode")) {

                            if(client1.getBooleanFromJSON(response_item, "Success")){

                                getShtrihs();

                            }

                    }

                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.v("HTTPLOG", String.valueOf(statusCode));
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });

    }





}

