package com.example.uventawh;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GroupAcceptActivity extends AppCompatActivity {

    private String emptyRef = "00000000-0000-0000-0000-000000000000";

    private String ref, description,
            ref_sender = emptyRef, description_sender = "<выберите>",
            ref_receiver = emptyRef, description_receiver = "<выберите>";

    private ProgressBar progressBar;

    private Button btnSave;
    private Integer count = 1, scanned = 0, added = 0;
    private TextView tvShtrihCodeCount, tvSenderDescription, tvReceiverDescription, tvAdded, tvScanned;
    private EditText etShtrihCode;
    private LinearLayout llMain;

    private List<String> shtrihCodes = new ArrayList<>();
    private Handler h;

    private String[] cargos = {"Товар", "Переброска", "Оборудование"};

    TextView tvSendDate, tvCargoDescription;
    Calendar dateAndTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_accept);

        llMain = (LinearLayout) findViewById(R.id.llMain);

        tvSendDate = findViewById(R.id.tvSendDate);
        tvCargoDescription = findViewById(R.id.tvCargoDescription);

        ((ImageButton) findViewById(R.id.ibChooseCargo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupAcceptActivity.this);
                builder.setTitle("Выбор наименования груза")
                        .setItems(cargos, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                tvCargoDescription.setText(cargos[which]);

                            }
                        });
                Dialog phoneDialog = builder.create();

                phoneDialog.show();


            }
        });

        ((ImageButton) findViewById(R.id.ibChooseSendDate)).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateAndTime.set(Calendar.YEAR, year);
                        dateAndTime.set(Calendar.MONTH, monthOfYear);
                        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        tvSendDate.setText(new SimpleDateFormat("dd MMMM yyyy").format(dateAndTime.getTime()));
                    }
                };

                new DatePickerDialog(GroupAcceptActivity.this, d,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH))
                        .show();;

            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        Intent intent = getIntent();
        ref = intent.getStringExtra("ref");
        description = intent.getStringExtra("description");

        tvShtrihCodeCount = (TextView) findViewById(R.id.tvShtrihCodeCount);

        tvSenderDescription = (TextView) findViewById(R.id.tvSenderDescription);
        tvSenderDescription.setText(description_sender);

        tvReceiverDescription = (TextView) findViewById(R.id.tvReceiverDescription);
        tvReceiverDescription.setText(description_receiver);

        btnSave = findViewById(R.id.btnSave);

        etShtrihCode = (EditText) findViewById(R.id.etShtrihCode);
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

        tvAdded = findViewById(R.id.tvAdded);
        tvScanned = findViewById(R.id.tvScanned);

        ((TextView) findViewById(R.id.tvHeader)).setText("Приемка: " + description);

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

        findViewById(R.id.ibChooseSender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent("com.example.uventawh.action.addresseslistactivity");
                intent.putExtra("ref", ref);
                intent.putExtra("description", description);
                intent.putExtra("header", "Выбор адреса отправителя: " + description);

                startActivityForResult(intent, 1);


            }
        });

        findViewById(R.id.ibChooseReceiver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent("com.example.uventawh.action.addresseslistactivity");
                intent.putExtra("ref", ref);
                intent.putExtra("description", description);
                intent.putExtra("header", "Выбор адреса получателя: " + description);

                startActivityForResult(intent, 2);


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

        final String[] str = new String[1];
        HttpClient client1 = new HttpClient(this);

        DB db = new DB(this);
        db.open();

        String url = "request/" + db.getConstant("user_id") + "/" + db.getConstant("prog_id");

        db.close();

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        try {
            requestParams.put("organization", ref);
            requestParams.put("sender", ref_sender);
            requestParams.put("receiver", ref_receiver);
            requestParams.put("count", added);
            requestParams.put("cargo", tvCargoDescription.getText());
            requestParams.put("sendDate", new SimpleDateFormat("yyyyMMdd").format(dateAndTime.getTime()) + "000000");
            requestParams.put("shtrihCodes", new JSONArray(shtrihCodes));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "setGroupAccept");
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

                    if (getStringFromJSON(accept_item, "Name").equals("setGroupAccept")){

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
