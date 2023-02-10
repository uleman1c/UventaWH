package com.example.uventawh;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Created by Михаил on 01.09.2017.
 */


public class InputPinCode extends Activity {

    private TextView pinCode, tvMessage;
    private Handler mHandler = new Handler();

    /**
     * прячем программную клавиатуру
     */
    protected void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(pinCode.getWindowToken(), 0);
        }
    }

    /**
     * показываем программную клавиатуру
     */
    protected void showInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(pinCode, 0);
        }
    }

    private Runnable mShowInputMethodTask = new Runnable() {
        public void run() {
            showInputMethod();
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // если окно в фокусе, то ждем еще немного и показываем клавиатуру
            mHandler.postDelayed(mShowInputMethodTask, 0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void verifyUserIdByPinCode(final String pinCodeStr) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {

        final String[] str = new String[1];
        HttpClient client1 = new HttpClient(this);
//        JSONObject jsonParams = new JSONObject();
//        jsonParams.put("FBToken", FireBaseToken);
//        jsonParams.put("SurnameName", SurnameName);
//        jsonParams.put("PhoneNumber", PhoneNumber);
//        jsonParams.put("FriendPromoCode", FriendPromoCode);
//        jsonParams.put("Email", email);
//        jsonParams.put("BDay", BDay);
//        jsonParams.put("sex", sex);
//        jsonParams.put("children", children);
//        StringEntity entity = new StringEntity(jsonParams.toString(),"UTF-8");

        String serverAnswer = "";

        client1.get(this, "auth/" + pinCodeStr, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

                hideInputMethod();

                RelativeLayout rlProgress = (RelativeLayout) findViewById(R.id.rlProgress);
                rlProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                RelativeLayout rlProgress = (RelativeLayout) findViewById(R.id.rlProgress);
                rlProgress.setVisibility(View.GONE);

                showInputMethod();

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

                String user_id = null;
                try {
                    user_id = readerArray.get("User").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(user_id.isEmpty()) {

                    TextView tvMessage = (TextView) findViewById(R.id.tvMessage);
                    tvMessage.setText("Неверный пароль");

                    pinCode.setText("");

                }

                else
                {

                    hideInputMethod();

                    Intent intent = new Intent();

                    intent.putExtra("user_id", user_id);

                    setResult(RESULT_OK, intent);

                    finish();

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_pin_code);

        String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.READ_PHONE_STATE",
                "android.permission.SYSTEM_ALERT_WINDOW",
                "android.permission.CAMERA"};

        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }

        pinCode = (TextView) findViewById(R.id.pinCode);
        tvMessage = (TextView) findViewById(R.id.tvMessage);

        Intent intent = getIntent();
        tvMessage.setText(intent.getStringExtra("message"));
        Boolean debug = intent.getBooleanExtra("debug", true);

//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        ((EditText) findViewById(R.id.pinCode)).setOnKeyListener(new View.OnKeyListener()
                                  {
                                      @RequiresApi(api = Build.VERSION_CODES.O)
                                      public boolean onKey(View v, int keyCode, KeyEvent event)
                                      {
              if(event.getAction() == KeyEvent.ACTION_DOWN &&
                      (keyCode == KeyEvent.KEYCODE_ENTER))
              {

                  doverifyUserIdByPinCode();


                  return true;
              }
              return false;
          }
      }
        );

        Button btnOk = (Button) findViewById(R.id.buttonOK);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                doverifyUserIdByPinCode();

            }
        });

        Button btnSettings = (Button) findViewById(R.id.buttonSettings);

        btnSettings.setVisibility(debug ? View.VISIBLE : View.GONE);

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideInputMethod();

                Intent intent = new Intent("com.example.uventawh.action.settingsactivity");

                intent.putExtra("settings", 1);

                startActivityForResult(intent, 1);

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void doverifyUserIdByPinCode(){

        String pinCodeText = pinCode.getText().toString();

        if(!pinCodeText.isEmpty()) {

            tvMessage.setText("");

            try {
                verifyUserIdByPinCode(pinCodeText);
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            if (resultCode == RESULT_CANCELED) {

            } else {
            }
        }



    }

    @Override
    public void onBackPressed() {

        hideInputMethod();

        Intent intent = new Intent();

        intent.putExtra("user_id", "");

        setResult(RESULT_CANCELED, intent);

        finish();

    }

}
