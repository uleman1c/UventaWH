package com.example.uventawh;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectDeliveryOrderActivity extends AppCompatActivity {

    private String emptyRef = "00000000-0000-0000-0000-000000000000";
    private String ref, description, shtrihCode = "",
            ref_sender = emptyRef, description_sender = "",
            ref_receiver = emptyRef, description_receiver = "";

    List<ShipmentItem> task_items = new ArrayList<>();

    DelayAutoCompleteTextView actvShtrihCode, actvSender, actvReceiver;

    DataDeliveryOrdersAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout linearLayout;

    private boolean shtrihCodeKeyboard = false;

    private InputMethodManager imm;

    private static final int
            REQUEST_SELECT_SENDER_ADDRESS = 0,
            REQUEST_SELECT_RECEIVER_ADDRESS = 1;

    private Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_delivery_order);

        Intent intent = getIntent();
        ref = intent.getStringExtra("ref");
        description = intent.getStringExtra("description");

        ((TextView) findViewById(R.id.tvHeader)).setText(description + ": Заказы на отгрузку");

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        h = new Handler();

        Thread t = new Thread(new Runnable() {
            public void run() {
                h.post(setFocus);
            }
        });
        t.start();

        setTabHost();

        adapter = new DataDeliveryOrdersAdapter(this, task_items);
        adapter.setOnTaskItemClickListener(new DataDeliveryOrdersAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(ShipmentItem taskItem, View itemView) {

                if (taskItem.name.equals("Отгрузка")) {

                    Intent intent = new Intent(getBaseContext(), DeliveryOrderTestActivity.class);

                    intent.putExtra("ref", taskItem.ref);
                    intent.putExtra("name", taskItem.name);
                    intent.putExtra("number", taskItem.number);
                    intent.putExtra("date", taskItem.date);
                    intent.putExtra("refContractor", taskItem.refContractor);
                    intent.putExtra("company", taskItem.company);
                    intent.putExtra("image", taskItem.image);
                    intent.putExtra("refSender", taskItem.refSender);
                    intent.putExtra("sender", taskItem.sender);

                    setResult(RESULT_OK, intent);

                    finish();

                }
            }
        });

        recyclerView = findViewById(R.id.rvList);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState==RecyclerView.SCROLL_STATE_IDLE){

                    if (!recyclerView.canScrollVertically(1)) {

                        update();

                    }
                    else if (!recyclerView.canScrollVertically(-1)) {

                        update();

                    }
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        actvShtrihCode = findViewById(R.id.actvShtrihCode);
        actvShtrihCode.setThreshold(3);

        actvShtrihCode.setAdapter(new DocumentNumberAutoCompleteAdapter(getBaseContext(), new DB(this).getRequestUserProg(), new HttpClientSync(this), ref));
        actvShtrihCode.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar0));

        actvShtrihCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                shtrihCode = (String) adapterView.getItemAtPosition(position);

                imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                update();

//                refTransport = refDesc.ref;
//                descTransport = refDesc.desc;
//
//                actvShtrihCode.setText(descTransport);
            }
        });

        actvShtrihCode.requestFocus();
        imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

        actvShtrihCode.setOnKeyListener(new View.OnKeyListener()
              {
                  public boolean onKey(View v, int keyCode, KeyEvent event)
                  {
                      if(event.getAction() == KeyEvent.ACTION_DOWN &&
                              (keyCode == KeyEvent.KEYCODE_ENTER))
                      {
                          shtrihCode = actvShtrihCode.getText().toString();

                          actvShtrihCode.setText("");

        //                                                  tvNumberDoc.setText("Номер накладной: " + ShtrihCode);

                          shtrihCodeKeyboard = false;

                          imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                          update();

                          return true;
                      }
                      return false;
                  }
              }
        );

        AddressAutoCompleteAdapter aucaAddress = new AddressAutoCompleteAdapter(this, new DB(this).getRequestUserProg(), new HttpClientSync(this), ref, "getAddressesByFilter");

        actvSender = findViewById(R.id.actvSender);
        actvSender.setThreshold(3);

        actvSender.setAdapter(aucaAddress);
        actvSender.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar));

        actvSender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RefDesc refDesc = (RefDesc) adapterView.getItemAtPosition(position);

                ref_sender = refDesc.ref;
                description_sender = refDesc.desc;

                actvSender.setText(description_sender);

                imm.hideSoftInputFromWindow(actvSender.getWindowToken(), 0);

                update();

            }
        });

        actvReceiver = findViewById(R.id.actvReceiver);
        actvReceiver.setThreshold(3);

        actvReceiver.setAdapter(aucaAddress);
        actvReceiver.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar2));

        actvReceiver.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RefDesc refDesc = (RefDesc) adapterView.getItemAtPosition(position);

                ref_receiver = refDesc.ref;
                description_receiver = refDesc.desc;

                actvReceiver.setText(description_receiver);

                imm.hideSoftInputFromWindow(actvReceiver.getWindowToken(), 0);

                update();

            }
        });


        findViewById(R.id.ibChooseSender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectAddress("Выбор адреса отправителя: ", REQUEST_SELECT_SENDER_ADDRESS);

            }
        });

        findViewById(R.id.ibChooseReceiver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectAddress("Выбор адреса получателя: ", REQUEST_SELECT_RECEIVER_ADDRESS);

            }
        });



        findViewById(R.id.ibKeyboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actvShtrihCode.requestFocus();

                if (shtrihCodeKeyboard){

                    imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                } else {

                    imm.showSoftInput(actvShtrihCode, 0, null);
                }

                shtrihCodeKeyboard = !shtrihCodeKeyboard;


            }
        });



        update();

    }

    private void selectAddress(String s, int i) {
        Intent intent = new Intent(this, AddressesListActivity.class);
        intent.putExtra("ref", ref);
        intent.putExtra("description", description);
        intent.putExtra("header", s + description);

        startActivityForResult(intent, i);
    }


    protected void update(){

        final HttpClient client1 = new HttpClient(this);

        String url = new DB(this).getRequestUserProg();

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        try {
            requestParams.put("refContractor", ref);
            requestParams.put("numberDocument", shtrihCode);
            requestParams.put("ref_sender", ref_sender);
            requestParams.put("ref_receiver", ref_receiver);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "getContractorDeliveryPoints");
            request.put("parameters", requestParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put(request);

        client1.post(this, url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {

                progressBar.setVisibility(View.GONE);

                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                JSONArray response = client1.getResponseJSONArray(responseBody);

                for (int i = 0; i < response.length(); i++){

                    JSONObject response_item = client1.getItemJSONArray(response, i);

                    if (client1.getStringFromJSON(response_item, "Name").equals("getContractorDeliveryPoints")) {

                        task_items.clear();

                        JSONArray deliveryOrdersJSON = client1.getJsonArrayFromJsonObject(response_item, "ContractorDeliveryPoints");

                        for (int k = 0; k < deliveryOrdersJSON.length(); k++) {

                            JSONObject deliveryOrderJSON = client1.getItemJSONArray(deliveryOrdersJSON, k);

                            task_items.add(ShipmentItem.extractFromJSON(deliveryOrderJSON, client1));
                        }

                        adapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                client1.showMessageOnFailure(statusCode, headers, responseBody, error);

            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });






    }


    private void setTabHost() {

        TabHost tabHost = findViewById(R.id.thSearch);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");

        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("Номер накладной");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("Отправитель");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator("Получатель");
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)  {

            if (requestCode == REQUEST_SELECT_SENDER_ADDRESS) {

                ref_sender = data.getStringExtra("ref");
                description_sender = data.getStringExtra("description");

                actvSender.setText(description_sender);

                update();

            }
            else if(requestCode == REQUEST_SELECT_RECEIVER_ADDRESS){

                ref_receiver = data.getStringExtra("ref");
                description_receiver = data.getStringExtra("description");

                actvReceiver.setText(description_receiver);

                update();

            }

        }



    }

    final Runnable setFocus = new Runnable() {
        public void run() {

            if (actvShtrihCode.isFocused() && !shtrihCodeKeyboard) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

            }

            h.postDelayed(setFocus, 500);

        }
    };



}
