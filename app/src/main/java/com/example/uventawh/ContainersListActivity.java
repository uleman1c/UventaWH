package com.example.uventawh;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ContainersListActivity extends AppCompatActivity {

    private DataContainersAdapter adapter;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;

    private List<Container> containers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_containers_list);

        linearLayout = findViewById(R.id.linearLayout);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        adapter = new DataContainersAdapter(this, containers);

        adapter.setOnStringClickListener(new DataContainersAdapter.OnStringClickListener(){
            @Override
            public void onStringClick(Container str) {

                Intent intent = new Intent();

                intent.putExtra("ref", str.ref);
                intent.putExtra("description", str.description);

                setResult(RESULT_OK, intent);

                finish();


            }
        });



        RecyclerView recyclerViewToScan = findViewById(R.id.rcvList);
        recyclerViewToScan.setAdapter(adapter);

        update();


    }

    protected void update(){

        final String[] str = new String[1];
        HttpClient client1 = new HttpClient(this);

        DB db = new DB(this);
        db.open();

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        try {
            requestParams.put("cell", "Priemka");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "getContainers");
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

                    if (getStringFromJSON(accept_item, "Name").equals("getContainers")){

                        JSONArray contractorsJSON = new JSONArray();
                        try {
                            contractorsJSON = (JSONArray) accept_item.get("Containers");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        containers.clear();

                        for (int j = 0; j < contractorsJSON.length(); j++) {

                            JSONObject contractorsJSON_item = null;

                            try {
                                contractorsJSON_item = (JSONObject) contractorsJSON.get(j);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String ref = getStringFromJSON(contractorsJSON_item, "Ref");
                            String description = getStringFromJSON(contractorsJSON_item, "Description");
                            String senderRef = getStringFromJSON(contractorsJSON_item, "SenderRef");
                            String senderDescription = getStringFromJSON(contractorsJSON_item, "SenderDescription");
                            String receiverRef = getStringFromJSON(contractorsJSON_item, "ReceiverRef");
                            String receiverDescription = getStringFromJSON(contractorsJSON_item, "ReceiverDescription");

                            JSONArray goodsJSON = new JSONArray();
                            try {
                                goodsJSON = (JSONArray) contractorsJSON_item.get("Goods");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            ArrayList<Good> goods = new ArrayList<Good>();

                            for (int k = 0; k < goodsJSON.length(); k++) {

                                JSONObject goodsJSON_item = null;

                                try {
                                    goodsJSON_item = (JSONObject) goodsJSON.get(k);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String goodRef = getStringFromJSON(contractorsJSON_item, "Ref");
                                String goodDescription = getStringFromJSON(contractorsJSON_item, "Description");

                                goods.add(new Good(goodRef, goodDescription));

                            }
                                containers.add(new Container(ref, description, senderRef, senderDescription, receiverRef, receiverDescription, goods));
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
