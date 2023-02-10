package com.example.uventawh;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeliveryOrdersActivity extends AppCompatActivity {

    List<ShipmentItem> task_items = new ArrayList<>();

    DataDeliveryOrdersAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout linearLayout;

    String refRoute, refContractor, mode, refTransport, refDriver, descTransport, descDriver;

    private static final int
            REQUEST_TEST_DELIVERY_ORDER = 0,
            REQUEST_ADD_DELIVERY_ORDER = 1,
            REQUEST_CHOOSE_CONTRACTOR = 2,
            REQUEST_ADD_ACCEPT_FROM_TRANSPORT = 3,
            REQUEST_ADD_RECEIPT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlivery_orders);

        Intent intent = getIntent();

        refRoute = intent.getStringExtra("ref");
        refTransport = intent.getStringExtra("refTransport");
        refDriver = intent.getStringExtra("refDriver");
        descTransport = intent.getStringExtra("descTransport");
        descDriver = intent.getStringExtra("descDriver");

        linearLayout = findViewById(R.id.linearLayout);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);


        adapter = new DataDeliveryOrdersAdapter(this, task_items);
        adapter.setOnTaskItemClickListener(new DataDeliveryOrdersAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(ShipmentItem taskItem, View itemView) {

                editItem(taskItem);

            }
        });

        recyclerView = findViewById(R.id.list);
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

        findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                add();

            }
        });

        update();

    }

    private void editItem(final ShipmentItem taskItem) {

        final HttpClient httpClient = new HttpClient(this);
        httpClient.addParam("ref", taskItem.refContractor);

        httpClient.postForResult("getContractorSettings", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")) {

                    Boolean inputSenderAddress = httpClient.getBooleanFromJSON(response, "InputSenderAddress");
                    Boolean inputDelivererAddress = httpClient.getBooleanFromJSON(response, "InputDelivererAddress");
                    Boolean inputQuantity = httpClient.getBooleanFromJSON(response, "InputQuantity");

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
                        intent.putExtra("inputSenderAddress", inputSenderAddress);
                        intent.putExtra("inputDelivererAddress", inputDelivererAddress);
                        intent.putExtra("inputQuantity", inputQuantity);

                        startActivityForResult(intent, REQUEST_TEST_DELIVERY_ORDER);
                    } else if (taskItem.name.equals("Приемка")) {

                        Intent intent = new Intent(getBaseContext(), ScanActivity.class);

                        intent.putExtra("ref", taskItem.ref);
                        intent.putExtra("name", taskItem.name);
                        intent.putExtra("number", taskItem.number);
                        intent.putExtra("date", taskItem.date);
                        intent.putExtra("refContractor", taskItem.refContractor);
                        intent.putExtra("company", taskItem.company);
                        intent.putExtra("image", taskItem.image);
                        intent.putExtra("refSender", taskItem.refSender);
                        intent.putExtra("sender", taskItem.sender);
                        intent.putExtra("inputSenderAddress", inputSenderAddress);
                        intent.putExtra("inputDelivererAddress", inputDelivererAddress);
                        intent.putExtra("inputQuantity", inputQuantity);

                        startActivityForResult(intent, REQUEST_TEST_DELIVERY_ORDER);
                    }
                }
            }
        });



    }

    private void add() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Выбрать");

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_choose_receipt_delivery, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.ibReceipt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

                mode = "receipt";

                chooseContractor();


            }
        });

        view.findViewById(R.id.ibDelivery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

                mode = "delivery";

                chooseContractor();

            }
        });


        alertDialog.show();


   }

    private void chooseContractor() {

        if (mode.equals("receipt")){

            Intent intent = new Intent(this, AcceptListActivity.class);
            intent.putExtra("refTransport", refTransport);
            intent.putExtra("descTransport", descTransport);
            intent.putExtra("refDriver", refDriver);
            intent.putExtra("descDriver", descDriver);
            intent.putExtra("refRoute", refRoute);
            intent.putExtra("refFilter", getString(R.string.emptyRef));
            intent.putExtra("descFilter", "");

            startActivityForResult(intent, REQUEST_ADD_RECEIPT);

//            Intent intent = new Intent(this, AcceptPageOneActivity.class);
//
//            intent.putExtra("refTransport", refTransport);
//            intent.putExtra("descTransport", descTransport);
//            intent.putExtra("refDriver", refDriver);
//            intent.putExtra("descDriver", descDriver);
//            intent.putExtra("refRoute", refRoute);
//            intent.putExtra("refFilter", getString(R.string.emptyRef));
//            intent.putExtra("descFilter", "");
//
//            startActivityForResult(intent, REQUEST_ADD_ACCEPT_FROM_TRANSPORT);
        }
        else {

            Intent intent = new Intent(this, ContractorsListActivity.class);

            startActivityForResult(intent, REQUEST_CHOOSE_CONTRACTOR);

        }

    }

    private void addDeliveryOrder(String ref, String description) {

        Intent intent = new Intent(this, SelectDeliveryOrderActivity.class);
        intent.putExtra("ref", ref);
        intent.putExtra("description", description);

        startActivityForResult(intent, REQUEST_ADD_DELIVERY_ORDER);

    }

    protected void update(){

        final HttpClient httpClient = new HttpClient(this);
        httpClient.addParam("refRoute", refRoute);

        httpClient.postProc("getRouteDeliveryPoints", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getStringFromJSON(response, "Name").equals("getRouteDeliveryPoints")) {

                    task_items.clear();

                    JSONArray routesJSON = httpClient.getJsonArrayFromJsonObject(response, "RouteDeliveryPoints");

                    for (int j = 0; j < routesJSON.length(); j++) {

                        JSONObject routeJSON = httpClient.getItemJSONArray(routesJSON, j);

                        String dateShipment = httpClient.getStringFromJSON(routeJSON, "DateShipment");

                        ((TextView) findViewById(R.id.tvHeader)).setText(httpClient.getStringFromJSON(routeJSON, "Driver") + ", "
                                + httpClient.getStringFromJSON(routeJSON, "Transport") + ", "
                                + "Рейс №" + httpClient.getStringFromJSON(routeJSON, "Number")
                                + " от " + httpClient.dateStrToDate(dateShipment));

                        JSONArray deliveryOrdersJSON = httpClient.getJsonArrayFromJsonObject(routeJSON, "DeliveryPoints");

                        for (int k = 0; k < deliveryOrdersJSON.length(); k++) {

                            JSONObject deliveryOrderJSON = httpClient.getItemJSONArray(deliveryOrdersJSON, k);

                            task_items.add(ShipmentItem.extractFromJSON(deliveryOrderJSON, httpClient));
                        }
                    }


                    adapter.notifyDataSetChanged();
                }



            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_TEST_DELIVERY_ORDER
            || requestCode == REQUEST_ADD_ACCEPT_FROM_TRANSPORT){

            update();

        }

        if (resultCode == RESULT_OK){

            if(requestCode == REQUEST_CHOOSE_CONTRACTOR){

                if (mode.equals("delivery")){

                    addDeliveryOrder(data.getStringExtra("ref"), data.getStringExtra("description"));
                }
                else {


                }


            }
            else if(requestCode == REQUEST_ADD_DELIVERY_ORDER){

                setRouteToDeliveryOrder(data.getStringExtra("ref"));

            }
            else if(requestCode == REQUEST_ADD_RECEIPT){

                setRouteToReceipt(data.getStringExtra("ref"));

            }

        }

    }

    private void setRouteToDeliveryOrder(String refDeliveryOrder) {

        final HttpClient client1 = new HttpClient(this);

        String url = new DB(this).getRequestUserProg();

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        try {
            requestParams.put("refRoute", refRoute);
            requestParams.put("refDeliveryOrder", refDeliveryOrder);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "setRouteToDeliveryOrder");
            request.put("parameters", requestParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put(request);

        client1.post(this, url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

//                linearLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {

//                linearLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                JSONArray response = client1.getResponseJSONArray(responseBody);

                for (int i = 0; i < response.length(); i++){

                    JSONObject response_item = client1.getItemJSONArray(response, i);

                    if (client1.getStringFromJSON(response_item, "Name").equals("setRouteToDeliveryOrder")) {

                        if (client1.getBooleanFromJSON(response_item, "Success")){

                            update();

                        }

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

    private void setRouteToReceipt(String refReceipt) {

        final HttpClient httpClient = new HttpClient(this);
        httpClient.addParam("refRoute", refRoute);
        httpClient.addParam("refReceipt", refReceipt);

        httpClient.postForResult("setRouteToReceipt", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if(httpClient.getBooleanFromJSON(response, "Success")){

                            update();
                }

            }
        });


    }

}
