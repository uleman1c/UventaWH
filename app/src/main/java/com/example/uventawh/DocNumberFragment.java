package com.example.uventawh;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DocNumberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DocNumberFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DocNumberFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DocNumberFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DocNumberFragment newInstance(String param1, String param2) {
        DocNumberFragment fragment = new DocNumberFragment();
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
            ShtrihCode = "", refRoute, refContractor, descContractor;

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
    private Thread t;

    private static final int
            REQUEST_SELECT_CONTRACTOR = 5;

    private Boolean inputQuantity;

    Bundle bundle;

    private int back = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_doc_number, container, false);

        tvNumberDoc = root.findViewById(R.id.tvNumberDoc);
        progressBar = root.findViewById(R.id.progressBar);
        llMain = root.findViewById(R.id.llMain);

        BoldStringBuilder builder = new BoldStringBuilder();

        bundle = getArguments();

        refRoute = bundle.getString("refRoute");
        refContractor = bundle.getString("ref");
        descContractor = bundle.getString("description");
        inputQuantity = bundle.getBoolean("inputQuantity");
        back = bundle.getInt("back", 0);

        builder.erase();
        builder.addBoldString("Контрагент: ");
        builder.addString(bundle.getString("description") + "\n");
        builder.addBoldString("Водитель: ");
        builder.addString( bundle.getString("descDriver") + "\n");
        builder.addBoldString("Транспорт: ");
        builder.addString(bundle.getString("descTransport"));

        ((TextView)root.findViewById(R.id.tvHeader)).setText(builder.spannableStringBuilder, TextView.BufferType.SPANNABLE);

        root.findViewById(R.id.ibKeyboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                etShtrihCode.requestFocus();
//
//                if (shtrihCodeKeyboard){
//
//                    hideKeyboard();
//
//                } else {
//
//                    etShtrihCode.requestFocus();
//
//                    ((MainWareHouseActivity) getActivity()).imm.showSoftInput(etShtrihCode, 0, null);
//                }
//
//                shtrihCodeKeyboard = !shtrihCodeKeyboard;


            }
        });

        root.findViewById(R.id.btnGenerate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TakeScreenShot.Do(getContext(), root, v);

                ShtrihCode = StrDateTime.dateToStr(new Date());

                checkDouble(refContractor, ShtrihCode);


            }
        });

        root.findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TakeScreenShot.Do(getContext(), root, v);

                ShtrihCode = etShtrihCode.getText().toString();

                if (!ShtrihCode.isEmpty()) {

                    checkDouble(refContractor, ShtrihCode);
                }

            }
        });

        etShtrihCode = root.findViewById(R.id.etShtrihCode);
        etShtrihCode.setOnKeyListener(new View.OnKeyListener()
                                      {
                                          public boolean onKey(View v, int keyCode, KeyEvent event)
                                          {
                                              if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                                      (keyCode == KeyEvent.KEYCODE_ENTER))
                                              {
                                                  TakeScreenShot.Do(getContext(), root, v);

                                                  ShtrihCode = etShtrihCode.getText().toString();

                                                  etShtrihCode.setText("");

                                                  BoldStringBuilder builder = new BoldStringBuilder();

                                                  builder.erase();
                                                  builder.addBoldString("Номер накладной: " + "\n");
                                                  builder.addString("    " + ShtrihCode);

                                                  tvNumberDoc.setText(builder.spannableStringBuilder, TextView.BufferType.SPANNABLE);

                                                  shtrihCodeKeyboard = false;

                                                  hideKeyboard();

                                                  checkDouble(refContractor, ShtrihCode);

                                                  return true;
                                              }
                                              return false;
                                          }
                                      }
        );



        h = new Handler();



        t = new Thread(new Runnable() {
            public void run() {
                h.post(setFocus);
            }
        });
        t.start();


//        etShtrihCode.requestFocus();


        return root;
    }

    private void hideKeyboard() {

//        View v = ((Activity) getContext()).getCurrentFocus();
//        if (v != null) {
////            ((MainWareHouseActivity) getActivity()).imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//        }
    }

    private void checkDouble(final String refContractor, final String docNumber) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refContractor", refContractor);
        httpClient.addParam("docNumber", docNumber);

        httpClient.postForResult("getCheckDoubleDocNum", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
                llMain.setEnabled(visibility != View.VISIBLE);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")){

                    JSONArray doubles = httpClient.getJsonArrayFromJsonObject(response, "CheckDoubleDocNum");
                    if (doubles.length() == 0){

                        getContractorSettings(refContractor);

                    }
                    else {

                        JSONObject curDouble = httpClient.getItemJSONArray(doubles,0);

                        ref_receiver = httpClient.getStringFromJSON(curDouble, "RefReceiver");
                        description_receiver = httpClient.getStringFromJSON(curDouble, "DescReceiver");

                        askForAction(docNumber, httpClient.getStringFromJSON(curDouble, "Date"),
                                httpClient.getIntegerFromJSON(curDouble, "Plan"),
                                httpClient.getIntegerFromJSON(curDouble, "Fact"),
                                httpClient.getStringFromJSON(curDouble, "Ref"));

                    }

                }

            }
        });

    }

    private void getContractorSettings(String refContractor) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("ref", refContractor);

        httpClient.postForResult("getContractorSettings", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
                llMain.setEnabled(visibility != View.VISIBLE);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")) {

                    Boolean inputSenderAddress = httpClient.getBooleanFromJSON(response, "InputSenderAddress");
                    Boolean inputDelivererAddress = httpClient.getBooleanFromJSON(response, "InputDelivererAddress");

                    bundle.putString("docNumber", ShtrihCode);
                    bundle.putBoolean("inputQuantity", httpClient.getBooleanFromJSON(response, "InputQuantity"));
                    bundle.putBoolean("inputSenderAddress", inputSenderAddress);
                    bundle.putBoolean("inputDelivererAddress", inputDelivererAddress);

                    shtrihCodeKeyboard = false;

                    if (inputSenderAddress || inputDelivererAddress){

                        if (back != 0) {

                            bundle.putInt("back", back);

                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack(back, false);
                        }

                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.addressesFragment, bundle);

                    }
                    else {

                        startScan(ref, ShtrihCode, "");

                    }

                }
            }
        });

    }

    private void askForAction(final String docNumber, final String date, final Integer plan, final Integer fact, final String ref) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Выбрать");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_ask_repeat_doc_number, null);

        ((TextView)view.findViewById(R.id.tvHeader)).setText(docNumber + " уже есть." + "\n"
                + "Приемка от " + StrDateTime.strToDate(date) + " " + StrDateTime.strToTime(date));

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

                continueScan(ref, docNumber, date);

            }
        });

        view.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

                deleteAccepted(ref, docNumber, date);

            }
        });

        alertDialog.show();


    }

    private void continueScan(String ref, String docNumber, String date){

        Bundle bundle = new Bundle();

        bundle.putString("refRoute", refRoute);
        bundle.putString("ref", ref);
        bundle.putString("name", "Приемка");
        bundle.putString("number", docNumber);
        //                    bundle.putString("date", taskItem.date);
        bundle.putString("refContractor", refContractor);
        //                    bundle.putString("company", taskItem.company);
        //                    bundle.putLong("image", taskItem.image);
        bundle.putString("refSender", ref_sender);
        bundle.putString("sender", description_sender);
        bundle.putString("refReceiver", ref_receiver);
        bundle.putString("receiver", description_receiver);

        if (back != 0) {
            bundle.putInt("back", back);
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack(back, false);
        }

        shtrihCodeKeyboard = false;

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.scanFragment, bundle);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();



    }

    private void startScan(final String ref, final String docNumber, String date) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("organization", refContractor);
        httpClient.addParam("sender", ref_sender);
        httpClient.addParam("receiver", ref_receiver);
        httpClient.addParam("route", refRoute);
        httpClient.addParam("numDocument", docNumber);

        httpClient.postForResult("addEmptyReceipt", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")){

                    Bundle bundle = new Bundle();

                    bundle.putString("refRoute", refRoute);
                    bundle.putString("ref", httpClient.getStringFromJSON(response, "Ref"));
                    bundle.putString("name", "Приемка");
                    bundle.putString("number", docNumber);
            //                    bundle.putString("date", taskItem.date);
                    bundle.putString("refContractor", refContractor);
            //                    bundle.putString("company", taskItem.company);
            //                    bundle.putLong("image", taskItem.image);
                    bundle.putString("refSender", ref_sender);
                    bundle.putString("sender", description_sender);
                    bundle.putString("refReceiver", ref_receiver);
                    bundle.putString("receiver", description_receiver);

                    if (back != 0) {
                        bundle.putInt("back", back);
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack(back, false);
                    }

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.scanFragment, bundle);

                }

            }
        });



    }

    private void deleteAccepted(final String ref, final String docNumber, final String date) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refAccept", ref);

        httpClient.postForResult("deleteAccepted", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")){

                    shtrihCodeKeyboard = false;

                    startScan(ref, docNumber, date);
                }

            }
        });

    }

    final Runnable setFocus = new Runnable() {
        public void run() {

//            if (etShtrihCode.isFocused() && !shtrihCodeKeyboard) {
//
//                hideKeyboard();
//
//            }

            h.postDelayed(setFocus, 500);

        }
    };




}