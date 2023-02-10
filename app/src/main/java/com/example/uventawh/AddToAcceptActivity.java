package com.example.uventawh;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
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

import java.util.ArrayList;
import java.util.List;

public class AddToAcceptActivity extends AppCompatActivity {

    private String refAccept, nameAccept, numberAccept, dateAccept, descriptionAccept,
            refContractor, descriptionContractor,
            refSender, descriptionSender,
            refReceiver, descriptionReceiver;

    private TextView tvReceiverDescription, tvShtrihCodeCount, tvAdded, tvScanned;
    private Integer count = 1, scanned = 0, added = 0;
    private Button btnSave;
    private EditText etShtrihCode;
    private LinearLayout llMain;
    private ProgressBar progressBar;

    private List<String> shtrihCodes = new ArrayList<>();
    private Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_accept);

        Intent intent = getIntent();
        refAccept = intent.getStringExtra("ref");
        nameAccept = intent.getStringExtra("name");
        numberAccept = intent.getStringExtra("number");
        dateAccept = intent.getStringExtra("date");
        refContractor = intent.getStringExtra("refContractor");
        descriptionContractor = intent.getStringExtra("company");
        refSender = intent.getStringExtra("refSender");
        descriptionSender = intent.getStringExtra("sender");

        ((TextView) findViewById(R.id.tvHeader)).setText("Добавить места: " + nameAccept + " №" + numberAccept + " от " + dateAccept + " " + descriptionContractor);
        ((TextView) findViewById(R.id.tvSender)).setText(descriptionSender);

        llMain = findViewById(R.id.llMain);
        progressBar = findViewById(R.id.progressBar);

        tvReceiverDescription = findViewById(R.id.tvReceiverDescription);

        tvAdded = findViewById(R.id.tvAdded);
        tvScanned = findViewById(R.id.tvScanned);

        findViewById(R.id.ibChooseReceiver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent("com.example.uventawh.action.addresseslistactivity");
                intent.putExtra("ref", refContractor);
                intent.putExtra("description", descriptionContractor);
                intent.putExtra("header", "Выбор адреса получателя: " + descriptionContractor);
                intent.putExtra("descriptionContractor", descriptionContractor);

                startActivityForResult(intent, 2);


            }
        });

        tvShtrihCodeCount = (TextView) findViewById(R.id.tvShtrihCodeCount);

        ((Button) findViewById(R.id.btnDec)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (count > 1) {

                    count = count - 1;

                    tvShtrihCodeCount.setText(count.toString());

                }

            }
        });

        ((Button) findViewById(R.id.btnInc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count = count + 1;

                tvShtrihCodeCount.setText(count.toString());

            }
        });

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

//                                              scanShtrihCode(strCatName);

                                              return true;
                                          }
                                          return false;
                                      }
                                  }
        );




        findViewById(R.id.btnGenerate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                added = added + count;

                tvAdded.setText("Добавлено: " + String.valueOf(added));

                btnSave.setText("Сохранить: " + String.valueOf(added + shtrihCodes.size()));

                count = 1;

                tvShtrihCodeCount.setText(count.toString());
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendShtrihCodes();

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

    private void sendShtrihCodes() {
        final String[] str = new String[1];
        HttpClient client1 = new HttpClient(this);

        DB db = new DB(this);
        db.open();

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        try {
            requestParams.put("accept", refAccept);
            requestParams.put("receiver", refReceiver);
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

        if(requestCode == 2){

            if (resultCode == RESULT_OK) {

                refReceiver = data.getStringExtra("ref");
                descriptionReceiver = data.getStringExtra("description");

                tvReceiverDescription.setText(descriptionReceiver);

            }
        }

    }
}
