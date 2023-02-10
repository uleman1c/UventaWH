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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiptPageOneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiptPageOneFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReceiptPageOneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReceiptPageOneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReceiptPageOneFragment newInstance(String param1, String param2) {
        ReceiptPageOneFragment fragment = new ReceiptPageOneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_receipt_page_one, container, false);

        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        h = new Handler();



        Thread t = new Thread(new Runnable() {
            public void run() {
                h.post(setFocus);
            }
        });
        t.start();

        DB db = new DB(getContext());
        String url = db.getRequestUserProg();

        Bundle bundle = getArguments();

        ref = bundle.getString("refFilter");
        refRoute = bundle.getString("refRoute");
        description = bundle.getString("descFilter");
        refTransport = bundle.getString("refTransport");
        descTransport = bundle.getString("descTransport");
        refDriver = bundle.getString("refDriver");
        descDriver = bundle.getString("descDriver");

        tvNumberDoc = root.findViewById(R.id.tvNumberDoc);

        actvSender = root.findViewById(R.id.actvSender);
        actvSender.setThreshold(3);

        aucaAddress = new AddressAutoCompleteAdapter(getContext(), url, new HttpClientSync(getContext()), ref, "getAddressesByFilter");

        actvSender.setAdapter(aucaAddress);
        actvSender.setLoadingIndicator((ProgressBar) root.findViewById(R.id.progress_bar));

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

        actvReceiver = root.findViewById(R.id.actvReceiver);
        actvReceiver.setThreshold(3);

        actvReceiver.setAdapter(aucaAddress);
        actvReceiver.setLoadingIndicator((ProgressBar) root.findViewById(R.id.progress_bar2));

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

        root.findViewById(R.id.ibKeyboard).setOnClickListener(new View.OnClickListener() {
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

        ((TextView) root.findViewById(R.id.tvTransport)).setText(descTransport);
        ((TextView) root.findViewById(R.id.tvDriver)).setText(descDriver);
        tvContractor = root.findViewById(R.id.tvContractor);
        tvContractor.setText(description);

        llAddresses = root.findViewById(R.id.llAddresses);

        if (ref.equals(emptyRef)){
            llAddresses.setVisibility(View.GONE);
        }

        llMain = root.findViewById(R.id.llMain);

        tvSendDate = root.findViewById(R.id.tvSendDate);
        tvCargoDescription = root.findViewById(R.id.tvCargoDescription);

        root.findViewById(R.id.ibChooseCargo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

        root.findViewById(R.id.ibChooseSendDate).setOnClickListener(new View.OnClickListener() {
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

                new DatePickerDialog(getContext(), d,
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

        ((TextView) root.findViewById(R.id.tvHeader)).setText("Приемка: " + description);

        root.findViewById(R.id.ibChooseContractor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), ContractorsListActivity.class);

                startActivityForResult(intent, REQUEST_SELECT_CONTRACTOR);

            }
        });

        root.findViewById(R.id.ibChooseSender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectAddress("Выбор адреса отправителя: ", 1);

            }
        });

        root.findViewById(R.id.ibChooseReceiver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectAddress("Выбор адреса получателя: ", 2);

            }
        });

        root.findViewById(R.id.fabOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean validated = true;

                if(actvSender.getText().length() < 4){

                    validated = false;

                    Toast toast = Toast.makeText(getContext(), "Не заполнен отправитель", Toast.LENGTH_SHORT);
                    toast.show();


                }
                else if(actvReceiver.getText().length() < 4){

                    validated = false;

                    Toast toast = Toast.makeText(getContext(), "Не заполнен получатель", Toast.LENGTH_SHORT);
                    toast.show();


                }
                else if(ShtrihCode.isEmpty() && etShtrihCode.getText().toString().isEmpty()){

                    validated = false;

                    Toast toast = Toast.makeText(getContext(), "Не заполнен номер накладной", Toast.LENGTH_SHORT);
                    toast.show();


                }

                if (validated){

                    Intent intent = new Intent(getContext(), AcceptPageTwoActivity.class);
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

//                    finish();
                }

            }

        });

        etShtrihCode = root.findViewById(R.id.etShtrihCode);
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






        return root;
    }

    private void selectAddress(String s, int i) {
        Intent intent = new Intent(getContext(), AddressesListActivity.class);
        intent.putExtra("ref", ref);
        intent.putExtra("description", description);
        intent.putExtra("header", s + description);

        startActivityForResult(intent, i);
    }

    final Runnable setFocus = new Runnable() {
        public void run() {

            if (etShtrihCode.isFocused() && !shtrihCodeKeyboard) {

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etShtrihCode.getWindowToken(), 0);

            }

            h.postDelayed(setFocus, 500);

        }
    };



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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