package com.example.uventawh;

import android.content.Context;
import android.content.Intent;
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

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class AddSkladActivity extends AppCompatActivity {

    private static final int
            REQUEST_CODE_CHOOSE_CONTAINER = 1,
            REQUEST_CODE_CHOOSE_CELL = 2;

    private String containerRef, containerDescription;

    private TextView tvContainerDescription, tvCellDescription;

    private LinearLayout llMain;
    private ProgressBar progressBar;
    private EditText etShtrihCode;

    private Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sklad);

        llMain = findViewById(R.id.llMain);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        tvContainerDescription = findViewById(R.id.tvContainerDescription);
        tvCellDescription = findViewById(R.id.tvCellDescription);

        etShtrihCode = findViewById(R.id.etShtrihCode);
        etShtrihCode.setOnKeyListener(new View.OnKeyListener()
                                      {
                                          public boolean onKey(View v, int keyCode, KeyEvent event)
                                          {
                                              if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                                      (keyCode == KeyEvent.KEYCODE_ENTER))
                                              {
                                                  String strShtrihCode = etShtrihCode.getText().toString();

                                                  etShtrihCode.setText("");

                                                  tvCellDescription.setText(strShtrihCode);

                                                  return true;
                                              }
                                              return false;
                                          }
                                      }
        );

        findViewById(R.id.ibSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addToSklad();

            }
        });

        findViewById(R.id.ibChooseContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent("com.example.uventawh.action.containerslistactivity");

                startActivityForResult(intent, REQUEST_CODE_CHOOSE_CONTAINER);


            }
        });

        findViewById(R.id.ibChooseCell).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent();
//
//                setResult(RESULT_OK, intent);
//
//                finish();

            }
        });

        h = new Handler();

        Thread t = new Thread(new Runnable() {
            public void run() {
                h.post(setFocus);
            }
        });
        t.start();


    }

    private void addToSklad() {

        final String[] str = new String[1];
        HttpClient client1 = new HttpClient(this);

        DB db = new DB(this);
        db.open();

        String url = "request/" + db.getConstant("user_id") + "/" + db.getConstant("prog_id");

        db.close();

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        try {
            requestParams.put("containerRef", containerRef);
            requestParams.put("cell", tvCellDescription.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "setAddToSklad");
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

                    if (getStringFromJSON(accept_item, "Name").equals("setAddToSklad")){

                        JSONArray contractorsJSON = new JSONArray();
                        try {
                            contractorsJSON = (JSONArray) accept_item.get("Addresses");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent();

                        setResult(RESULT_OK, intent);

                        finish();

                    }

                }



            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
//                if (debug){Toast toast = Toast.makeText(ChooserActivity.this, "FAIL. FBToken: " + FireBaseToken + "StatusCode" + String.valueOf(statusCode), Toast.LENGTH_SHORT);
//                    toast.show();}
                llMain.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

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

    final Runnable setFocus = new Runnable() {
        public void run() {

            etShtrihCode.requestFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etShtrihCode.getWindowToken(), 0);

            h.postDelayed(setFocus, 500);

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE_CONTAINER) {

                containerRef = data.getStringExtra("ref");
                containerDescription = data.getStringExtra("description");

                tvContainerDescription.setText(containerDescription);

            }
            else if (requestCode == REQUEST_CODE_CHOOSE_CELL) {

            }
        }
    }



}
