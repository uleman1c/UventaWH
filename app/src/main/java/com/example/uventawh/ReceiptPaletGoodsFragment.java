package com.example.uventawh;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiptPaletGoodsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiptPaletGoodsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReceiptPaletGoodsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReceiptPaletGoodsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReceiptPaletGoodsFragment newInstance(String param1, String param2) {
        ReceiptPaletGoodsFragment fragment = new ReceiptPaletGoodsFragment();
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

    private String refRoute, refTransport, refDriver, descTransport, descDriver, number, date, ref, code, refContractor, descContractor;
    private ProgressBar progressBar;
    private DelayAutoCompleteTextView actvShtrihCode;
    private InputMethodManager imm;
    private boolean shtrihCodeKeyboard, inputSenderAddress, inputDelivererAddress, inputQuantity;

    private EditText etQuantity;
    private androidx.appcompat.app.AlertDialog quantityDialog = null;
    private Double quantity;

    private ScannedShtrihCodeAdapter adapterScanned;
    private List<ScannedShtrihCode> scanned = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_receipt_palet_goods, container, false);

        Bundle bundle = getArguments();

        ref = bundle.getString("ref");
        code = bundle.getString("code");
        refRoute = bundle.getString("refRoute");
        number = bundle.getString("number");
        date = bundle.getString("date");
        refContractor = bundle.getString("refContractor");
        descContractor = bundle.getString("descContractor");
        refTransport = bundle.getString("refTransport");
        refDriver = bundle.getString("refDriver");
        descTransport = bundle.getString("descTransport");
        descDriver = bundle.getString("descDriver");

        ((TextView) root.findViewById(R.id.tvHeader)).setText("Рейс №" + number
                + " от " + StrDateTime.strToDate(date) + ", "
                + descDriver + ", "
                + descTransport + ", " + descContractor);

        ((TextView) root.findViewById(R.id.tvPalet)).setText("Палет №" + code);

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        actvShtrihCode = root.findViewById(R.id.actvShtrihCode);

        actvShtrihCode.requestFocus();

        shtrihCodeKeyboard = false;

        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

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

                                                    if (inputQuantity){

                                                        inputQuantity(strCatName);
                                                    }
                                                    else {

                                                        setGoodToRoutePalet(strCatName, 1.0);
                                                    }

                                                    return true;
                                                }
                                                return false;
                                            }
                                        }
        );


        root.findViewById(R.id.ibKeyboard).setOnClickListener(new View.OnClickListener() {
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

        adapterScanned = new ScannedShtrihCodeAdapter(getContext(), scanned);
        ((RecyclerView) root.findViewById(R.id.list)).setAdapter(adapterScanned);

        adapterScanned.setOnStringClickListener(new ScannedShtrihCodeAdapter.OnStringClickListener(){
            @Override
            public void onStringClick(ScannedShtrihCode str, View itemView) {

//                askForAddService(str.shtrihCode);

            }
        });

        contractorSettings();

        return root;
    }

    private void contractorSettings() {

        final HttpClient httpClient = new HttpClient(getContext());
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

//                    getShtrihs();

                }
            }

        });

    }

    private void inputQuantity(final String strCatName) {

        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
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

        setGoodToRoutePalet(strCatName, quantity);
    }

    final Runnable setFocus2 = new Runnable() {
        public void run() {

            InputMethodManager imm = (InputMethodManager) quantityDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            etQuantity.requestFocus();

            imm.showSoftInput(etQuantity, 0, null);


        }
    };

    private void setGoodToRoutePalet(final String strCatName, final Double quantity) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refRoute", refRoute);
        httpClient.addParam("refPalet", ref);
        httpClient.addParam("refContractor", refContractor);
        httpClient.addParam("code", strCatName);
        httpClient.addParam("quantity", quantity);
        httpClient.postForResult("setGoodToRoutePalet", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if(httpClient.getBooleanFromJSON(response, "Success")) {

                    scanned.add(0, new ScannedShtrihCode(strCatName, StrDateTime.dateToStr(new Date()), false, quantity));

                    adapterScanned.notifyDataSetChanged();

                }

            }
        });


    }
}