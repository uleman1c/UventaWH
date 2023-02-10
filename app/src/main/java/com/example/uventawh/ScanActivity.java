package com.example.uventawh;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class ScanActivity extends AppCompatActivity {

    String ref, name, number, date, refContractor, company, refSender, sender, curShtrihCode, currentPhotoPath;
    Integer image;

    EditText editText;

    List<String> toScanStart = new ArrayList<>();
    List<String> toScan = new ArrayList<>();
    List<ScannedShtrihCode> scanned = new ArrayList<>();

    ScannedShtrihCodeAdapter adapterScanned;
    DataStringAdapter adapterToScan;

    ProgressBar progressBar;
    LinearLayout llMain;

    ProgressTextView progressTextView;

    private Button btnClose;

    private Timer timer=new Timer();
    private final long DELAY = 1000; // milliseconds

    int cnt;
    final int max = 100;
    Handler h;

    Boolean manualInput = false, sendingInProgress = false;

    Uri outputFileUri;

    private static final int CAMERA_REQUEST = 20;

    private SoundPlayer soundPlayer;

    DelayAutoCompleteTextView actvShtrihCode;

    private InputMethodManager imm;

    private boolean shtrihCodeKeyboard = false, inputSenderAddress, inputDelivererAddress, inputQuantity;

    EditText etQuantity;

    androidx.appcompat.app.AlertDialog quantityDialog = null;

    Double quantity;

    Bundle bundle;

    TextView tvQuantity;

    GetFoto getFoto;
    String pathFoto;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan);


        progressTextView = findViewById(R.id.scannedText);

        actvShtrihCode = findViewById(R.id.actvShtrihCode);
        actvShtrihCode.setThreshold(3);

        actvShtrihCode.setAdapter(new StringAutoCompleteAdapter(getBaseContext(), toScan));
        actvShtrihCode.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar0));

        actvShtrihCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                String strCatName = (String) adapterView.getItemAtPosition(position);

                actvShtrihCode.setText("");

                shtrihCodeKeyboard = false;

                imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                scanShtrihCode(strCatName);

            }
        });

        actvShtrihCode.requestFocus();

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

        actvShtrihCode.setOnKeyListener(new View.OnKeyListener()
                                        {
                                            public boolean onKey(View v, int keyCode, KeyEvent event)
                                            {
                                                if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                                        (keyCode == KeyEvent.KEYCODE_ENTER))
                                                {

                                                    String strCatName = actvShtrihCode.getText().toString();

                                                    actvShtrihCode.setText("");

                                                    shtrihCodeKeyboard = false;

                                                    imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                                                    scanShtrihCode(strCatName);

                                                    return true;
                                                }
                                                return false;
                                            }
                                        }
        );


        findViewById(R.id.ibKeyboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actvShtrihCode.requestFocus();

                if (shtrihCodeKeyboard){

                    imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                } else {

                    imm.showSoftInput(actvShtrihCode, 0, null);
                }

                shtrihCodeKeyboard = !shtrihCodeKeyboard;


            }
        });



        soundPlayer = new SoundPlayer(this, R.raw.hrn05);
        this.setVolumeControlStream(soundPlayer.streamType);

        h = new Handler();

        Intent intent = getIntent();
        ref = intent.getStringExtra("ref");
        name = intent.getStringExtra("name");
        number = intent.getStringExtra("number");
        date = intent.getStringExtra("date");
        refContractor = intent.getStringExtra("refContractor");
        company = intent.getStringExtra("company");
        refSender = intent.getStringExtra("refSender");
        sender = intent.getStringExtra("sender");
        image = (int) intent.getLongExtra("image", 0);

        progressBar = findViewById(R.id.progressBar);
        llMain = findViewById(R.id.llMain);

        ((TextView) findViewById(R.id.header)).setText(name + " №" + number + " от " + date + " " + company + ", " + sender);

        editText = (EditText) findViewById(R.id.editText);
        editText.setOnKeyListener(new View.OnKeyListener()
          {
              public boolean onKey(View v, int keyCode, KeyEvent event)
              {
                  if(event.getAction() == KeyEvent.ACTION_DOWN &&
                          (keyCode == KeyEvent.KEYCODE_ENTER))
                  {
                      manualInput = false;

                      String strCatName = editText.getText().toString();

                      editText.setText("");

                      scanShtrihCode(strCatName);

                      adapterToScan.notifyDataSetChanged();

                      return true;
                  }
                  return false;
              }
          }
        );

        Thread t = new Thread(new Runnable() {
            public void run() {
                h.post(setFocus);
            }
        });
        t.start();

        ((Button) findViewById(R.id.btnOcr)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent("com.example.uventawh.action.startocrcapture");

                startActivityForResult(intent, 2);


            }
        });

        ((ImageButton) findViewById(R.id.btnAdd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent("com.example.uventawh.action.add_to_accept_activity");

                intent.putExtra("ref", ref);
                intent.putExtra("name", name);
                intent.putExtra("number", number);
                intent.putExtra("date", date);
                intent.putExtra("refContractor", refContractor);
                intent.putExtra("company", company);
                intent.putExtra("refSender", refSender);
                intent.putExtra("sender", sender);

                startActivityForResult(intent, 3);


            }
        });

//        ((Button) findViewById(R.id.btn0)).setOnClickListener(btnOCL);
//        ((Button) findViewById(R.id.btn1)).setOnClickListener(btnOCL);
//        ((Button) findViewById(R.id.btn2)).setOnClickListener(btnOCL);
//        ((Button) findViewById(R.id.btn3)).setOnClickListener(btnOCL);
//        ((Button) findViewById(R.id.btn4)).setOnClickListener(btnOCL);
//        ((Button) findViewById(R.id.btn5)).setOnClickListener(btnOCL);
//        ((Button) findViewById(R.id.btn6)).setOnClickListener(btnOCL);
//        ((Button) findViewById(R.id.btn7)).setOnClickListener(btnOCL);
//        ((Button) findViewById(R.id.btn8)).setOnClickListener(btnOCL);
//        ((Button) findViewById(R.id.btn9)).setOnClickListener(btnOCL);
//        ((Button) findViewById(R.id.btnBS)).setOnClickListener(btnOCL);

        adapterToScan = new DataStringAdapter(this, toScan);

        adapterToScan.setOnStringClickListener(new DataStringAdapter.OnStringClickListener(){
            @Override
            public void onStringClick(String str, View itemView) {

                if (manualInput) {

                    scanShtrihCode(str);

                    adapterToScan.notifyDataSetChanged();

                    manualInput = false;
                }
            }
        });



        RecyclerView recyclerViewToScan = (RecyclerView) findViewById(R.id.toScan);
        recyclerViewToScan.setAdapter(adapterToScan);

        RecyclerView recyclerViewScanned = (RecyclerView) findViewById(R.id.scanned);

        adapterScanned = new ScannedShtrihCodeAdapter(this, scanned);
        recyclerViewScanned.setAdapter(adapterScanned);

        adapterScanned.setOnStringClickListener(new ScannedShtrihCodeAdapter.OnStringClickListener(){
            @Override
            public void onStringClick(ScannedShtrihCode str, View itemView) {

                askForAddService(str.shtrihCode);

            }
        });

        recyclerViewScanned.setAdapter(adapterScanned);

        btnClose = findViewById(R.id.btnClose);
        btnClose.setVisibility(View.GONE);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                close();

            }
        });

        contractorSettings();

    }

    private void contractorSettings() {

        final HttpClient httpClient = new HttpClient(this);
        httpClient.addParam("ref", refContractor);

        httpClient.postForResult("getContractorSettings", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")) {

                    inputSenderAddress = httpClient.getBooleanFromJSON(response, "InputSenderAddress");
                    inputDelivererAddress = httpClient.getBooleanFromJSON(response, "InputDelivererAddress");
                    inputQuantity = httpClient.getBooleanFromJSON(response, "InputQuantity");

                    getShtrihs();

                }
            }

        });

    }


    private void close() {


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Закрытие приемки");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_yes_no_cancel, null);

        ((TextView) view.findViewById(R.id.tvQuestion)).setText("Закрыть приемку \"" + name + " №" + number + " от " + date + " " + company + ", " + sender +"\" ?");

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            closeReceipt();

                alertDialog.cancel();


            }
        });

        view.findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

            }
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

            }
        });

        alertDialog.show();



    }

    private void closeReceipt() {

        final HttpClient httpClient = new HttpClient(this);
        httpClient.addParam("ref", ref);

        httpClient.postForResult("closeReceipt", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                if(httpClient.getBooleanFromJSON(response, "Success")){

                    Intent intent = new Intent();

                    setResult(RESULT_OK, intent);

                    finish();

                };

            }
        });

    }


    private void askForRepeatCode(final String strShtrihCode) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(strShtrihCode + " уже есть. Выбрать");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_ask_repeat_shtrihcode, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnGenerate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                setShtrihs("");

                alertDialog.cancel();


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

    private void askForAddService(final String strShtrihCode) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ScanActivity.this);
        alertDialogBuilder.setTitle(strShtrihCode + " доп.услуги:");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = ScanActivity.this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_ask_add_service, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnAddKoeff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

                askForAddKoeff(strShtrihCode);

            }
        });

        view.findViewById(R.id.btnRepack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

                sendAddService(strShtrihCode, "repack", 1);

            }
        });

        view.findViewById(R.id.btnFilm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

                sendAddService(strShtrihCode, "film", 1);

            }
        });

        view.findViewById(R.id.btnFoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

                getFoto(strShtrihCode);

            }
        });

        view.findViewById(R.id.btnContains).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

                getContains(strShtrihCode);

            }
        });

        alertDialog.show();


    }

    private void getContains(String strShtrihCode) {

        Intent intent = new Intent(getBaseContext(), ContainsActivity.class);

        intent.putExtra("refAccept", ref);
        intent.putExtra("refContractor", refContractor);
        intent.putExtra("shtrihCode", strShtrihCode);

        startActivityForResult(intent, 3);

    }

    private void getFoto(String strShtrihCode) {

        curShtrihCode = strShtrihCode;

        getFoto = new GetFoto(this);

        if (getFoto.intent != null){

            pathFoto = getFoto.uri.toString();

            startActivityForResult(getFoto.intent, CAMERA_REQUEST);

        }

}


    private void askForAddKoeff(final String strShtrihCode) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ScanActivity.this);
        alertDialogBuilder.setTitle("Выбрать коэффициент");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        String[] items = {"Коэффициент 2.000", "Коэффициент 3.000", "Коэффициент 4.000", "Коэффициент 5.000", "Коэффициент 6.000", "Коэффициент 7.000", "Коэффициент 8.000", "Коэффициент 9.000"};
        alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                sendAddService(strShtrihCode, "addKoeff", which + 2);

            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();


    }

    private void sendAddService(String strShtrihCode, String service, Integer koeff){

        final String[] str = new String[1];
        final HttpClient client1 = new HttpClient(this);

        DB db = new DB(this);
        db.open();

        String url = "request/" + db.getConstant("user_id") + "/" + db.getConstant("prog_id");

        db.close();

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        try {
            requestParams.put("ref", ref);
            requestParams.put("shtrihCode", strShtrihCode);
            requestParams.put("service", service);
            requestParams.put("koeff", koeff);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "setAddService");
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

                    if (client1.getStringFromJSON(accept_item, "Name").equals("setAddService")){

                        JSONArray contractorsJSON = new JSONArray();
                        try {
                            contractorsJSON = (JSONArray) accept_item.get("Addresses");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 3) {

                    getShtrihs();

                }

            else if (requestCode == CAMERA_REQUEST) {

                sendFotoInDoc(ref, curShtrihCode, getFoto.file);

            }

    }

}

    private void sendFotoInDoc(String ref, String strShtrihCode, final File file){

        final HttpClient httpClient = new HttpClient(this);

        httpClient.addParam("type", "doc");
        httpClient.addParam("name", "усПриемка");
        httpClient.addParam("ref", ref);
        httpClient.addParam("typeInDoc", strShtrihCode);

        httpClient.postForResult("getIncomingDocByParameters", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                sendFotoInDocRef(file, httpClient.getStringFromJSON(response, "Ref"), "files/doc/ДокументВходящийДО/");

            }
        });


    }

    private void sendFotoInDocRef(File file, String refInDoc, String pathInDoc){

        final HttpClient httpClient = new HttpClient(this);
        httpClient.addFile(file);

        httpClient.postBinaryForResult(pathInDoc + refInDoc, "setExamFoto", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
//                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")) {


                }
            }
        });

    }


    private void scanShtrihCode(String strCatName) {

        if (!sendingInProgress){

            Integer index = toScan.indexOf(strCatName);

            if (index < 0){

                soundPlayer.play();

                Boolean present = false;
                for (ScannedShtrihCode scannedShtrihCode:scanned){
                    present = scannedShtrihCode.shtrihCode.equals(strCatName);
                    if (present) break;
                }

                if (present){

                    askForRepeatCode(strCatName);

                } else {

                    askForNotFoundCode(strCatName);

                }


            }
            else {

                toScan.remove(strCatName);

                scanned.add(0, new ScannedShtrihCode(strCatName, "", false));
                adapterScanned.notifyDataSetChanged();

                setShtrihs(strCatName, 1.0);
            }
        }
    }

    private void askForNotFoundCode(final String strShtrihCode) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(strShtrihCode + " не найден. Выбрать");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_ask_not_found_shtrihcode, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (inputQuantity){
                    inputQuantity(strShtrihCode);
                }
                else {
                    setShtrihs(strShtrihCode, 1.0);
                }

                alertDialog.cancel();


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

    final Runnable setFocus = new Runnable() {
        public void run() {

            if (actvShtrihCode.isFocused() && !shtrihCodeKeyboard) {

                imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

            }

            h.postDelayed(setFocus, 500);


        }
    };

    protected void getShtrihs(){

        final HttpClient httpClient = new HttpClient(this);
        httpClient.addParam("refAccept", ref);

        httpClient.postProc("getAcceptShtrihs", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                JSONArray accept = httpClient.getJsonArrayFromJsonObject(response, "AcceptShtrihs");

                toScan.clear();
                scanned.clear();

                for (int i = 0; i < accept.length(); i++){

                    JSONObject accept_item = httpClient.getItemJSONArray(accept, i);

                    Boolean tested = httpClient.getBooleanFromJSON(accept_item, "Tested");
                    Boolean added = httpClient.getBooleanFromJSON(accept_item, "Added");
                    String date = httpClient.getStringFromJSON(accept_item, "Date");
                    String shtrih_code = httpClient.getStringFromJSON(accept_item, "ShtrihCode");
                    Double quantity = inputQuantity ? httpClient.getDoubleFromJSON(accept_item, "Quantity") : null;

                    if (tested){

                        scanned.add(new ScannedShtrihCode(shtrih_code, date, added, quantity));
                    } else {

                        toScan.add(shtrih_code);
                    }


                }

                adapterScanned.notifyDataSetChanged();
                adapterToScan.notifyDataSetChanged();

                Integer scannedQ = scanned.size();
                Integer toAcceptQ = toScan.size() + scannedQ;

                Integer procent = toAcceptQ == 0 ? 100 : scannedQ * 100 / toAcceptQ;

                if (toScan.size() == 0){
                    btnClose.setVisibility(View.VISIBLE);
                }

                progressTextView.setText(scannedQ.toString() + " из " + toAcceptQ.toString() + ", " + procent.toString() + "%");
                progressTextView.setMax(toAcceptQ);
                progressTextView.setProgress(scannedQ);

            }
        });

    }

    private void inputQuantity(final String strCatName) {

        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Ввод количества");

        LayoutInflater inflater = getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_input_quantity, null);

        ((TextView) view.findViewById(R.id.tvCode)).setText(strCatName);

        Button btnQuantity = view.findViewById(R.id.btnQuantity);

        etQuantity = view.findViewById(R.id.etQuantity);

        if(quantity == null){

            btnQuantity.setVisibility(View.GONE);
        }
        else {

            btnQuantity.setVisibility(View.VISIBLE);
            btnQuantity.setText("<<  " + quantity.toString());
        }

        btnQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etQuantity.setText(quantity.toString());

                CloseScan(view, strCatName);

            }
        });

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        quantityDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CloseScan(view, strCatName);

            }
        });

        quantityDialog.show();

        final Handler h2 = new Handler();
        h2.postDelayed(setFocus2, 500);

    }

    private void CloseScan(View view, String strCatName) {
        quantityDialog.cancel();

        quantity = Double.valueOf(((TextView) view.findViewById(R.id.etQuantity)).getText().toString());

        setShtrihs(strCatName, quantity);
    }

    final Runnable setFocus2 = new Runnable() {
        public void run() {

            InputMethodManager imm = (InputMethodManager) quantityDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            etQuantity.requestFocus();

            imm.showSoftInput(etQuantity, 0, null);


        }
    };

    private void scanShtrihCode1(String strCatName, Double quantity) {

        if (!sendingInProgress){

            scanned.add(0, new ScannedShtrihCode(strCatName, "", false, quantity));
            adapterScanned.notifyDataSetChanged();

            tvQuantity.setText("Количество: " + String.valueOf(scanned.size()));

            setShtrihs(strCatName, quantity);

        }
    }




    protected void setShtrihs(final String shtrihcode, final Double quantity){

        final HttpClient httpClient = new HttpClient(this);
        httpClient.addParam("refAccept", ref);
        httpClient.addParam("shtrihCode", shtrihcode);
        httpClient.addParam("isPalet", false);
        httpClient.addParam("boxQuantity", 0);

        httpClient.postProc("setAcceptShtrih", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")){

                    if (inputQuantity && httpClient.getBooleanFromJSON(response, "IsNew")){

                        setQuantityToShtrih(shtrihcode, quantity);

                    }
                    else {
                        getShtrihs();
                    }

                };

            }
        });

    }

    private void scanShtrihCode(String strCatName, Double quantity) {

        if (!sendingInProgress){

            scanned.add(0, new ScannedShtrihCode(strCatName, "", false, quantity));
            adapterScanned.notifyDataSetChanged();

            setQuantityToShtrih(strCatName, quantity);

        }
    }

    private void setQuantityToShtrih(String shtrihcode, Double quantity) {

        final HttpClient httpClient = new HttpClient(this);
        httpClient.addParam("contractorRef", refContractor);
        httpClient.addParam("shtrihCode", shtrihcode);
        httpClient.addParam("quantity", quantity);

        httpClient.postForResult("setQuantityToShtrih", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")){

                    getShtrihs();

                };

            }
        });



    }

}
