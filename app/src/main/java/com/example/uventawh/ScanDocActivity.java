package com.example.uventawh;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ScanDocActivity extends AppCompatActivity {

    private TextView tvCellDescription;

    private EditText etShtrihCode;

    private Handler h;

    Calendar dateAndTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_doc);

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

                          inputDate(strShtrihCode);

                          return true;
                      }

                      return false;
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

    private void inputDate(final String strShtrihCode) {

        DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateAndTime.set(Calendar.YEAR, year);
                dateAndTime.set(Calendar.MONTH, monthOfYear);
                dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SendMessage(strShtrihCode, new SimpleDateFormat("yyyyMMdd").format(dateAndTime.getTime()) + "000000");

            }
        };

        new DatePickerDialog(ScanDocActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();;


    }

    private void SendMessage(String shtrihCode, String date) {

        final String[] str = new String[1];
        HttpClientAcc client1 = new HttpClientAcc(this);

        DB db = new DB(this);
        db.open();

        String url = "request/" + db.getConstant("user_id") + "/" + db.getConstant("prog_id");

        db.close();

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        JSONObject message = new JSONObject();

        try {
            message.put("date", date);
            message.put("shtrihCode", shtrihCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            requestParams.put("message", message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "setMessageFromMobileDevice");
            request.put("parameters", requestParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put(request);

        client1.post(this, url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

//                llMain.setVisibility(View.GONE);
//                progressBar.setVisibility(View.VISIBLE);
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

                    if (getStringFromJSON(accept_item, "Name").equals("MessageFromMobileDevice")){

                        JSONArray contractorsJSON = new JSONArray();
                        try {
                            contractorsJSON = (JSONArray) accept_item.get("Addresses");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        Intent intent = new Intent();
//
//                        setResult(RESULT_OK, intent);
//
//                        finish();

                    }

                }



            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
//                if (debug){Toast toast = Toast.makeText(ChooserActivity.this, "FAIL. FBToken: " + FireBaseToken + "StatusCode" + String.valueOf(statusCode), Toast.LENGTH_SHORT);
//                    toast.show();}
//                llMain.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.GONE);

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


}
