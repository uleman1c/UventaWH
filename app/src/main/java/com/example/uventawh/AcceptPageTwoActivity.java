package com.example.uventawh;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AcceptPageTwoActivity extends AppCompatActivity {

    private String emptyRef = "00000000-0000-0000-0000-000000000000";

    private String ref, description,
            ref_sender = emptyRef, description_sender = "<выберите>",
            ref_receiver = emptyRef, description_receiver = "<выберите>",
            refTransport, descTransport, refDriver, descDriver, numDocument, refRoute;

    private LinearLayout llMain;
    private ProgressBar progressBar;

    private Button btnSave;
    private Integer count = 1, scanned = 0, added = 0;
    private TextView tvShtrihCodeCount, tvSenderDescription, tvReceiverDescription, tvAdded, tvScanned;
    private EditText etShtrihCode;

    private Boolean dateIsSet = false;

    private List<String> shtrihCodes = new ArrayList<>();
    private Handler h;

    private String[] cargos = {"Товар", "Переброска", "Оборудование"};

    TextView tvSendDate, tvCargoDescription;
    Calendar dateAndTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_page_two);

        llMain = findViewById(R.id.llMain);

        tvSendDate = findViewById(R.id.tvSendDate);
        tvCargoDescription = findViewById(R.id.tvCargoDescription);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        Intent intent = getIntent();
        ref = intent.getStringExtra("ref");
        description = intent.getStringExtra("description");
        ref_sender = intent.getStringExtra("ref_sender");
        description_sender = intent.getStringExtra("description_sender");
        ref_receiver = intent.getStringExtra("ref_receiver");
        description_receiver = intent.getStringExtra("description_receiver");

        refRoute = intent.getStringExtra("refRoute");
        refTransport = intent.getStringExtra("refTransport");
        descTransport = intent.getStringExtra("descTransport");
        refDriver = intent.getStringExtra("refDriver");
        descDriver = intent.getStringExtra("descDriver");
        numDocument = intent.getStringExtra("numDocument");

        ((TextView)findViewById(R.id.tvContractor)).setText(description);
        ((TextView)findViewById(R.id.tvTransport)).setText(descTransport);
        ((TextView)findViewById(R.id.tvDriver)).setText(descDriver);
        ((TextView)findViewById(R.id.tvNumberDocument)).setText(numDocument);

        dateAndTime.setTimeInMillis(intent.getLongExtra("sendDate", 0));
        tvCargoDescription.setText(intent.getStringExtra("cargo"));

        dateIsSet = intent.getBooleanExtra("dateIsSet", false);

        if (dateIsSet){

            tvSendDate.setText(new SimpleDateFormat("dd MMMM yyyy").format(dateAndTime.getTime()));
        }

        tvShtrihCodeCount = findViewById(R.id.tvShtrihCodeCount);

        tvSenderDescription = findViewById(R.id.tvSenderDescription);
        tvSenderDescription.setText(description_sender);

        tvReceiverDescription = findViewById(R.id.tvReceiverDescription);
        tvReceiverDescription.setText(description_receiver);

        btnSave = findViewById(R.id.btnSave);

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

                                                  if (shtrihCodes.indexOf(strShtrihCode) == -1) {

                                                      shtrihCodes.add(strShtrihCode);

                                                      tvScanned.setText("Сканировано: " + String.valueOf(shtrihCodes.size()));

                                                      btnSave.setText("Сохранить: " + String.valueOf(added + shtrihCodes.size()));

                                                  }
                                                  else {

                                                      askForRepeatCode(strShtrihCode);

                                                  }
//                                              scanShtrihCode(strCatName);

                                                  return true;
                                              }
                                              return false;
                                          }
                                      }
        );

        tvAdded = findViewById(R.id.tvAdded);
        tvScanned = findViewById(R.id.tvScanned);

        ((TextView) findViewById(R.id.tvHeader)).setText("Приемка: " + description);

        findViewById(R.id.btnDec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (count > 1) {

                    count = count - 1;

                    tvShtrihCodeCount.setText(count.toString());

                }

            }
        });

        findViewById(R.id.btnInc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count = count + 1;

                tvShtrihCodeCount.setText(count.toString());

            }
        });

        findViewById(R.id.btnGenerate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                added = added + count;

                tvAdded.setText("Генерировано: " + String.valueOf(added));

                btnSave.setText("Сохранить: " + String.valueOf(added + shtrihCodes.size()));

                count = 1;

                tvShtrihCodeCount.setText(count.toString());

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendGroupAccept(ref, ref_sender, ref_receiver, count);

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

    final Runnable setFocus = new Runnable() {
        public void run() {

            etShtrihCode.requestFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etShtrihCode.getWindowToken(), 0);

            h.postDelayed(setFocus, 500);

        }
    };

    private void sendGroupAccept(String ref, String ref_sender, String ref_receiver, Integer count){

        HttpClient httpClient = new HttpClient(this);
        httpClient.addParam("organization", ref);
        httpClient.addParam("sender", ref_sender == null ? emptyRef : ref_sender);
        httpClient.addParam("receiver", ref_receiver == null ? emptyRef : ref_receiver);
        httpClient.addParam("count", added);
        httpClient.addParam("cargo", tvCargoDescription.getText().toString());
        httpClient.addParam("sendDate", new SimpleDateFormat("yyyyMMdd").format(dateAndTime.getTime()) + "000000");
        httpClient.addParam("dateIsSet", dateIsSet);
        httpClient.addParam("shtrihCodes", new JSONArray(shtrihCodes));
        httpClient.addParam("refTransport", refTransport);
        httpClient.addParam("refDriver", refDriver);
        httpClient.addParam("route", refRoute);
        httpClient.addParam("numDocument", numDocument);

        httpClient.postProc("setAcceptFromRoute", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

                llMain.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {



                Intent intent = new Intent();

                setResult(RESULT_OK, intent);

                finish();

            }
        });

//        JSONObject request = new JSONObject();
//        try {
//            request.put("request", "setAccept");
//            request.put("parameters", requestParams);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        params.put(request);
//
//        client1.post(this, url, params, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onStart() {
//
//                llMain.setVisibility(View.GONE);
//                progressBar.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onFinish() {
//
//                llMain.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.GONE);
//
//                super.onFinish();
//            }
//
//            @Override
//            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
//
//                str[0] = null;
//                try {
//                    str[0] = new String(responseBody, "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//
//                JSONObject readerArray = null;
//                try {
//                    readerArray = new JSONObject(str[0]);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                JSONArray accept = new JSONArray();
//                try {
//                    accept = (JSONArray) readerArray.get("Response");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                for (int i = 0; i < accept.length(); i++){
//
//                    JSONObject accept_item = null;
//
//                    try {
//                        accept_item = (JSONObject) accept.get(i);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    if (getStringFromJSON(accept_item, "Name").equals("setAccept")){
//
//                        JSONArray contractorsJSON = new JSONArray();
//                        try {
//                            contractorsJSON = (JSONArray) accept_item.get("Addresses");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        Intent intent = new Intent();
//
//                        setResult(RESULT_OK, intent);
//
//                        finish();
//
//                    }
//
//                }
//
//
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
////                if (debug){Toast toast = Toast.makeText(ChooserActivity.this, "FAIL. FBToken: " + FireBaseToken + "StatusCode" + String.valueOf(statusCode), Toast.LENGTH_SHORT);
////                    toast.show();}
//                Log.v("HTTPLOG", String.valueOf(statusCode));
//            }
//
//            @Override
//            public void onRetry(int retryNo) {
//                // called when request is retried
//            }
//        });
//





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

    private void askForRepeatCode(String strShtrihCode) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AcceptPageTwoActivity.this);
        alertDialogBuilder.setTitle(strShtrihCode + " уже есть. Выбрать");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = AcceptPageTwoActivity.this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_ask_repeat_shtrihcode, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnGenerate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

                added = added + count;

                tvAdded.setText("Генерировано: " + String.valueOf(added));

                btnSave.setText("Сохранить: " + String.valueOf(added + shtrihCodes.size()));

                count = 1;

                tvShtrihCodeCount.setText(count.toString());

            }
        });

        view.findViewById(R.id.btnIgnore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

            }
        });

        alertDialog.show();


    }


    private void sendShtrihCodes() {
        final String[] str = new String[1];
        HttpClient client1 = new HttpClient(this);

        DB db = new DB(this);
        db.open();

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        try {
//            requestParams.put("accept", refAccept);
//            requestParams.put("receiver", refReceiver);
            requestParams.put("count", added);
            requestParams.put("shtrihCodes", new JSONArray(shtrihCodes));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "updateAccept");
            request.put("parameters", requestParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put(request);

        client1.post(this, "request/" + db.getConstant("user_id") + "/" + db.getConstant("prog_id"), params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

                progressBar.setVisibility(View.VISIBLE);
                llMain.setVisibility(View.GONE);
            }

            @Override
            public void onFinish() {

                progressBar.setVisibility(View.GONE);
                llMain.setVisibility(View.VISIBLE);

                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                Intent intent = new Intent();

                setResult(RESULT_OK, intent);

                finish();


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            if (resultCode == RESULT_OK)  {

                ref_sender = data.getStringExtra("ref");
                description_sender = data.getStringExtra("description");

                tvSenderDescription.setText(description_sender);

            }
        }
        else if(requestCode == 2){

            if (resultCode == RESULT_OK) {

                ref_receiver = data.getStringExtra("ref");
                description_receiver = data.getStringExtra("description");

                tvReceiverDescription.setText(description_receiver);

            }
        }


    }
}
