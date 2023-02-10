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
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

public class AcceptFromRouteActivity extends AppCompatActivity {

    private String emptyRef = "00000000-0000-0000-0000-000000000000";

    private String ref, description, ref_route = emptyRef,
            ref_sender = emptyRef, description_sender = "<выберите>",
            ref_receiver = emptyRef, description_receiver = "<выберите>";

    private ProgressBar progressBar;

    private Button btnSave;
    private Integer count = 1, scanned = 0, added = 0;
    private TextView tvShtrihCodeCount, tvSenderDescription, tvReceiverDescription, tvAdded, tvScanned;
    private EditText etShtrihCode;
    private LinearLayout llMain;

    private List<String> shtrihCodes = new ArrayList<>(), shtrihCodesBefore = new ArrayList<>();
    private Handler h;

    private String[] cargos = {"Переброска", "Оборудование"};

    TextView tvSendDate, tvCargoDescription;
    Calendar dateAndTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_from_route);

        llMain = findViewById(R.id.llMain);

        tvSendDate = findViewById(R.id.tvSendDate);
        tvCargoDescription = findViewById(R.id.tvCargoDescription);

        findViewById(R.id.ibChooseCargo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AcceptFromRouteActivity.this);
                builder.setIcon(R.drawable.sklad96);

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

        findViewById(R.id.ibChooseSendDate).setOnClickListener(new View.OnClickListener() {
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

                new DatePickerDialog(AcceptFromRouteActivity.this, d,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH))
                        .show();;

            }
        });

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        Intent intent = getIntent();
        ref_route = intent.getStringExtra("ref");
        ref = intent.getStringExtra("contractorRef");
        String dateShipment = intent.getStringExtra("dateShipment");
        String driver = intent.getStringExtra("driver");
        String transport = intent.getStringExtra("transport");
        String contractorDescription = intent.getStringExtra("contractorDescription");

        description = contractorDescription + ", из рейса от " + dateShipment.substring(6, 8) + "." + dateShipment.substring(4, 6) + "." + dateShipment.substring(0, 4) + ", " + driver + ", " + transport;

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

                                                  if (shtrihCodes.indexOf(strShtrihCode) == -1 && shtrihCodesBefore.indexOf(strShtrihCode) == -1) {

                                                      shtrihCodes.add(strShtrihCode);

                                                      tvScanned.setText("Сканировано: " + String.valueOf(shtrihCodes.size()) + " (" + String.valueOf(shtrihCodesBefore.size()) + ")");

                                                      btnSave.setText("Сохранить: " + String.valueOf(added + shtrihCodes.size()));

                                                  } else {

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

        ((TextView) findViewById(R.id.tvHeader)).setText("Приемка из рейса: " + description);

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

        findViewById(R.id.ibChooseSender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseSender();

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

    private void askForRepeatCode(String strShtrihCode) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AcceptFromRouteActivity.this);
        alertDialogBuilder.setTitle(strShtrihCode + " уже есть. Выбрать");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = AcceptFromRouteActivity.this.getLayoutInflater();

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

    private void chooseSender() {

        Intent intent = new Intent("com.example.uventawh.action.addresseslistactivity");
        intent.putExtra("ref", ref);
        intent.putExtra("description", description);
        intent.putExtra("ref_route", ref_route);
        intent.putExtra("header", "Выбор адреса отправителя: " + description);

        startActivityForResult(intent, 1);

    }

    private void getAcceptFromRouteCodes() {

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
            requestParams.put("sender", ref_sender);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "getAcceptFromRouteCodes");
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

                    if (getStringFromJSON(accept_item, "Name").equals("getAcceptFromRouteCodes")){

                        JSONArray contractorsJSON = new JSONArray();
                        try {
                            contractorsJSON = (JSONArray) accept_item.get("Shtrihcodes");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        shtrihCodesBefore.clear();
                        for (int j = 0; j < contractorsJSON.length(); j++) {

                            JSONObject contractorsJSON_item = null;

                            try {
                                contractorsJSON_item = (JSONObject) contractorsJSON.get(j);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String shtrihCode = getStringFromJSON(contractorsJSON_item, "Shtrihcode");

                            shtrihCodesBefore.add(shtrihCode);
                        }

                        tvScanned.setText("Сканировано: " + String.valueOf(shtrihCodes.size()) + " (" + String.valueOf(shtrihCodesBefore.size()) + ")");

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
            requestParams.put("route", ref_route);
            requestParams.put("cargo", tvCargoDescription.getText());
            requestParams.put("sendDate", new SimpleDateFormat("yyyyMMdd").format(dateAndTime.getTime()) + "000000");
            requestParams.put("shtrihCodes", new JSONArray(shtrihCodes));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "setAcceptFromRoute");
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

                    if (getStringFromJSON(accept_item, "Name").equals("setAcceptFromRoute")){

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
