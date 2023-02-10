package com.example.uventawh;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ShipmentActivity extends AppCompatActivity {

    Boolean debug = false;
    Boolean authorized = false;
    String user_id, filterRef, filterDescription = "";

    List<ShipmentItem> task_items = new ArrayList<>();

    DataShipmentAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    TextView tvFilter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shipment);


        filterRef  = this.getString(R.string.emptyRef);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        tvFilter = (TextView) findViewById(R.id.tvFilter);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        findViewById(R.id.btnClearFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                filterRef = getString(R.string.emptyRef);
                filterDescription = "выберите контрагента";

                tvFilter.setText(filterDescription);

                update();

            }
        });

        findViewById(R.id.btnChoose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent("com.example.uventawh.action.contractorsactivity");

                startActivityForResult(intent, 5);

            }
        });

        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                add();

            }
        });



        adapter = new DataShipmentAdapter(this, task_items);
        adapter.setOnTaskItemClickListener(new DataShipmentAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(ShipmentItem taskItem) {

                if (taskItem.name.equals("Отгрузка")) {

                    Intent intent = new Intent(ShipmentActivity.this, DeliveryOrdersActivity.class);

                    intent.putExtra("ref", taskItem.ref);
                    intent.putExtra("name", taskItem.name);
                    intent.putExtra("number", taskItem.number);
                    intent.putExtra("date", taskItem.date);
                    intent.putExtra("refContractor", taskItem.refContractor);
                    intent.putExtra("company", taskItem.company);
                    intent.putExtra("image", taskItem.image);
                    intent.putExtra("refSender", taskItem.refSender);
                    intent.putExtra("sender", taskItem.sender);

                    startActivityForResult(intent, 2);
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.list);
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


        update();

    }

    private void addAccept() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShipmentActivity.this);
        alertDialogBuilder.setTitle("Добавить приемку");

        LayoutInflater inflater = ShipmentActivity.this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_choose_accept, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnAddFromRoute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

                addFromRoute();

            }
        });

        view.findViewById(R.id.btnAddFree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

                add();

            }
        });


        alertDialog.show();






    }

    private void addFromRoute() {

        Intent intent = new Intent("com.example.uventawh.action.routesactivity");

        startActivityForResult(intent, 6);
    }

    protected void update(){

        final String[] str = new String[1];
        HttpClient client1 = new HttpClient(this);

        String url = new DB(this).getRequestUserProg();

        DB db = new DB(this);
        db.open();

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        try {
            requestParams.put("contractor", filterRef);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "getShipments");
            request.put("parameters", requestParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put(request);

        client1.post(this, url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

//                linearLayout.setVisibility(View.GONE);
//                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {

//                linearLayout.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.GONE);

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

                JSONArray response = new JSONArray();
                try {
                    response = (JSONArray) readerArray.get("Response");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < response.length(); i++){

                    JSONObject response_item = null;

                    try {
                        response_item = (JSONObject) response.get(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (getStringFromJSON(response_item, "Name").equals("getShipments")) {

                        task_items.clear();

                        JSONArray tasksJSON = new JSONArray();
                        try {
                            tasksJSON = (JSONArray) response_item.get("Shipments");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int j = 0; j < tasksJSON.length(); j++) {

                            JSONObject task_item = null;

                            try {
                                task_item = (JSONObject) tasksJSON.get(j);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            task_items.add(ShipmentItem.extractFromJSON(task_item, client1));

                        }


                        adapter.notifyDataSetChanged();
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

    protected void add(){

        if (filterRef.equals(this.getString(R.string.emptyRef))) {

            Intent intent = new Intent("com.example.uventawh.action.contractorsactivity");

            startActivityForResult(intent, 3);
        }
        else {

            addAcceptForContractor(filterRef, filterDescription);

        }
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

    private Integer getIntegerFromJSON(JSONObject accept_item, String field_name) {
        Integer date = 0;
        try {
            date = accept_item.getInt(field_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2){

            update();

        }
        else if(requestCode == 3){

            if (resultCode == RESULT_OK) {

                String ref = data.getStringExtra("ref");
                String description = data.getStringExtra("description");

                addAcceptForContractor(ref, description);

            }
        }
        else if(requestCode == 4){

            if (resultCode == RESULT_OK) {

                update();

            }
        }

        else if(requestCode == 5){

            if (resultCode == RESULT_OK) {

                filterRef = data.getStringExtra("ref");
                filterDescription = data.getStringExtra("description");

                tvFilter.setText(filterDescription);

                update();

            }
        }

        else if(requestCode == 6){

            if (resultCode == RESULT_OK) {

                String routeRef = data.getStringExtra("ref");
                String dateShipment = data.getStringExtra("dateShipment");
                String driver = data.getStringExtra("driver");
                String transport = data.getStringExtra("transport");
                String contractorRef = data.getStringExtra("contractorRef");
                String contractorDescription = data.getStringExtra("contractorDescription");

                Intent intent = new Intent("com.example.uventawh.action.acceptfromrouteactivity");
                intent.putExtra("ref", routeRef);
                intent.putExtra("dateShipment", dateShipment);
                intent.putExtra("driver", driver);
                intent.putExtra("transport", transport);
                intent.putExtra("contractorRef", contractorRef);
                intent.putExtra("contractorDescription", contractorDescription);

                startActivityForResult(intent, 7);

            }
        }



    }

    private void addAcceptForContractor(String ref, String description) {

        Intent intent = new Intent("com.example.uventawh.action.acceptpageoneactivity");
        intent.putExtra("ref", ref);
        intent.putExtra("description", description);

        startActivityForResult(intent, 4);

    }



}
