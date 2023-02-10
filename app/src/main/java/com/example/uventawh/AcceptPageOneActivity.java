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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AcceptPageOneActivity extends AppCompatActivity {

    private String emptyRef = "00000000-0000-0000-0000-000000000000";

    private String ref = emptyRef, description ="",
            refTransport = emptyRef, descTransport = "",
            refDriver = emptyRef, descDriver = "",
            ref_sender = emptyRef, description_sender = "",
            ref_receiver = emptyRef, description_receiver = "",
            ShtrihCode = "", refRoute;

    private ProgressBar progressBar;

    private Button btnNext;
    private TextView tvSenderDescription, tvReceiverDescription, tvContractor, tvNumberDoc;
    private LinearLayout llMain, llAddresses;

    private List<String> shtrihCodes = new ArrayList<>();

    private String[] cargos = {"Товар", "Переброска", "Оборудование"};

    private Boolean dateIsSet = false, shtrihCodeKeyboard = false;

    DelayAutoCompleteTextView actvSender, actvReceiver;
    AddressAutoCompleteAdapter aucaAddress;
    TextView tvSendDate, tvCargoDescription;
    Calendar dateAndTime = Calendar.getInstance();

    EditText etShtrihCode;

    private Handler h;

    private static final int
            REQUEST_SELECT_CONTRACTOR = 5;

    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_page_one);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        h = new Handler();



        Thread t = new Thread(new Runnable() {
            public void run() {
                h.post(setFocus);
            }
        });
        t.start();

        DB db = new DB(this);
        String url = db.getRequestUserProg();

        Intent intent = getIntent();
        ref = intent.getStringExtra("refFilter");
        refRoute = intent.getStringExtra("refRoute");
        description = intent.getStringExtra("descFilter");
        refTransport = intent.getStringExtra("refTransport");
        descTransport = intent.getStringExtra("descTransport");
        refDriver = intent.getStringExtra("refDriver");
        descDriver = intent.getStringExtra("descDriver");

        tvNumberDoc = findViewById(R.id.tvNumberDoc);

        actvSender = findViewById(R.id.actvSender);
        actvSender.setThreshold(3);

        aucaAddress = new AddressAutoCompleteAdapter(this, url, new HttpClientSync(this), ref, "getAddressesByFilter");

        actvSender.setAdapter(aucaAddress);
        actvSender.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar));

        actvSender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RefDesc refDesc = (RefDesc) adapterView.getItemAtPosition(position);

                ref_sender = refDesc.ref;
                description_sender = refDesc.desc;

                actvSender.setText(description_sender);

                imm.hideSoftInputFromWindow(actvSender.getWindowToken(), 0);

            }
        });

        actvReceiver = findViewById(R.id.actvReceiver);
        actvReceiver.setThreshold(3);

        actvReceiver.setAdapter(aucaAddress);
        actvReceiver.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar2));

        actvReceiver.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RefDesc refDesc = (RefDesc) adapterView.getItemAtPosition(position);

                ref_receiver = refDesc.ref;
                description_receiver = refDesc.desc;

                actvReceiver.setText(description_receiver);

                imm.hideSoftInputFromWindow(actvReceiver.getWindowToken(), 0);

            }
        });

        findViewById(R.id.ibKeyboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etShtrihCode.requestFocus();

                if (shtrihCodeKeyboard){

                    imm.hideSoftInputFromWindow(etShtrihCode.getWindowToken(), 0);

                } else {

                    imm.showSoftInput(etShtrihCode, 0, null);
                }

                shtrihCodeKeyboard = !shtrihCodeKeyboard;


            }
        });

        ((TextView) findViewById(R.id.tvTransport)).setText(descTransport);
        ((TextView) findViewById(R.id.tvDriver)).setText(descDriver);
        tvContractor = findViewById(R.id.tvContractor);
        tvContractor.setText(description);

        llAddresses = findViewById(R.id.llAddresses);

        if (ref.equals(emptyRef)){
            llAddresses.setVisibility(View.GONE);
        }

        llMain = findViewById(R.id.llMain);

        tvSendDate = findViewById(R.id.tvSendDate);
        tvCargoDescription = findViewById(R.id.tvCargoDescription);

        findViewById(R.id.ibChooseCargo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AcceptPageOneActivity.this);
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

                        dateIsSet = true;

                        tvSendDate.setText(new SimpleDateFormat("dd MMMM yyyy").format(dateAndTime.getTime()));
                    }
                };

                new DatePickerDialog(AcceptPageOneActivity.this, d,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH))
                        .show();;

            }
        });

//        tvSenderDescription = findViewById(R.id.tvSenderDescription);
//        tvSenderDescription.setText(description_sender);
//
//        tvReceiverDescription = findViewById(R.id.tvReceiverDescription);
//        tvReceiverDescription.setText(description_receiver);

        ((TextView) findViewById(R.id.tvHeader)).setText("Приемка: " + description);

        findViewById(R.id.ibChooseContractor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AcceptPageOneActivity.this, ContractorsListActivity.class);

                startActivityForResult(intent, REQUEST_SELECT_CONTRACTOR);

            }
        });

        findViewById(R.id.ibChooseSender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectAddress("Выбор адреса отправителя: ", 1);

            }
        });

        findViewById(R.id.ibChooseReceiver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectAddress("Выбор адреса получателя: ", 2);

            }
        });

        findViewById(R.id.fabOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean validated = true;

                if(actvSender.getText().length() < 4){

                    validated = false;

                    Toast toast = Toast.makeText(AcceptPageOneActivity.this, "Не заполнен отправитель", Toast.LENGTH_SHORT);
                    toast.show();


                }
                else if(actvReceiver.getText().length() < 4){

                    validated = false;

                    Toast toast = Toast.makeText(AcceptPageOneActivity.this, "Не заполнен получатель", Toast.LENGTH_SHORT);
                    toast.show();


                }
                else if(ShtrihCode.isEmpty() && etShtrihCode.getText().toString().isEmpty()){

                    validated = false;

                    Toast toast = Toast.makeText(AcceptPageOneActivity.this, "Не заполнен номер накладной", Toast.LENGTH_SHORT);
                    toast.show();


                }

                if (validated){

                    Intent intent = new Intent(AcceptPageOneActivity.this, AcceptPageTwoActivity.class);
                    intent.putExtra("ref", ref);
                    intent.putExtra("description", description);

                    intent.putExtra("refRoute", refRoute);
                    intent.putExtra("refTransport", refTransport);
                    intent.putExtra("descTransport", descTransport);
                    intent.putExtra("refDriver", refDriver);
                    intent.putExtra("descDriver", descDriver);
                    intent.putExtra("numDocument", ShtrihCode.isEmpty() ? etShtrihCode.getText().toString() : ShtrihCode);

                    intent.putExtra("ref_sender", ref_sender);
                    intent.putExtra("description_sender", description_sender);
                    intent.putExtra("ref_receiver", ref_receiver);
                    intent.putExtra("description_receiver", description_receiver);
                    intent.putExtra("cargo", tvCargoDescription.getText());
                    intent.putExtra("sendDate", dateAndTime.getTimeInMillis());
                    intent.putExtra("dateIsSet", dateIsSet);

                    startActivity(intent);

                    finish();
                }

            }

        });

        etShtrihCode = findViewById(R.id.etShtrihCode);
        etShtrihCode.requestFocus();
        etShtrihCode.setOnKeyListener(new View.OnKeyListener()
                                      {
                                          public boolean onKey(View v, int keyCode, KeyEvent event)
                                          {
                                              if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                                      (keyCode == KeyEvent.KEYCODE_ENTER))
                                              {
                                                  ShtrihCode = etShtrihCode.getText().toString();

                                                  etShtrihCode.setText("");

                                                  tvNumberDoc.setText("Номер накладной: " + ShtrihCode);

                                                  shtrihCodeKeyboard = false;

                                                  imm.hideSoftInputFromWindow(etShtrihCode.getWindowToken(), 0);

                                                  return true;
                                              }
                                              return false;
                                          }
                                      }
        );




    }

    private void selectAddress(String s, int i) {
        Intent intent = new Intent(this, AddressesListActivity.class);
        intent.putExtra("ref", ref);
        intent.putExtra("description", description);
        intent.putExtra("header", s + description);

        startActivityForResult(intent, i);
    }

    final Runnable setFocus = new Runnable() {
        public void run() {

            if (etShtrihCode.isFocused() && !shtrihCodeKeyboard) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etShtrihCode.getWindowToken(), 0);

            }

            h.postDelayed(setFocus, 500);

        }
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)  {

            if (requestCode == 1) {

                ref_sender = data.getStringExtra("ref");
                description_sender = data.getStringExtra("description");

                actvSender.setText(description_sender);

            }
            else if(requestCode == 2){

                ref_receiver = data.getStringExtra("ref");
                description_receiver = data.getStringExtra("description");

                actvReceiver.setText(description_receiver);

            }
            else if(requestCode == REQUEST_SELECT_CONTRACTOR){

                ref = data.getStringExtra("ref");
                description = data.getStringExtra("description");

                tvContractor.setText(description);

                llAddresses.setVisibility(View.VISIBLE);

                aucaAddress.refContractor = ref;


            }
        }



    }


}
