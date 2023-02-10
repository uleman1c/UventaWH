package com.example.uventawh;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddressFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddressFragment newInstance(String param1, String param2) {
        AddressFragment fragment = new AddressFragment();
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

    Bundle bundle;

    private String refContractor, refAccept, shtrihCode;
    private Boolean isPalet, added;
    private Double boxQuantity;
    private Integer back;

    Boolean refFromDialog = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_address, container, false);

        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        progressBar = root.findViewById(R.id.progressBar);

        DB db = new DB(getContext());
        String url = db.getRequestUserProg();

        bundle = getArguments();

        refContractor = bundle.getString("refContractor");
        refAccept = bundle.getString("refAccept");
        shtrihCode = bundle.getString("shtrihCode");
        isPalet = bundle.getBoolean("isPalet");
        added = bundle.getBoolean("added");
        boxQuantity = bundle.getDouble("boxQuantity");
        back = bundle.getInt("back");

        ref_receiver = bundle.getString("refReceiver");
        description_receiver = bundle.getString("receiver");

        if (ref_receiver == null){

            db.open();
            ref_receiver = db.getConstant("ref_receiver" + refAccept);
            if (ref_receiver != null) {
                description_receiver = db.getConstant("description_receiver" + refAccept);
            }
        }

        Button btnOldAddress = root.findViewById(R.id.btnOldAddress);
        btnOldAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                actvReceiver.setText(description_receiver);

                doNext();

            }
        });

        if (ref_receiver != null){

            btnOldAddress.setText(description_receiver);

        }
        else {
            btnOldAddress.setVisibility(View.GONE);
        }


        BoldStringBuilder builder = new BoldStringBuilder();

        builder.erase();
        builder.addBoldString("Штрихкод: ");
        builder.addString(shtrihCode);

        ((TextView)root.findViewById(R.id.tvHeader)).setText(builder.spannableStringBuilder, TextView.BufferType.SPANNABLE);

        actvSender = root.findViewById(R.id.actvSender);
        actvSender.setThreshold(3);

        aucaAddress = new AddressAutoCompleteAdapter(getContext(), url, new HttpClientSync(getContext()), refContractor, "getAddressesByFilter");

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

                refFromDialog = true;

                actvReceiver.setText(description_receiver);

                refFromDialog = false;

                imm.hideSoftInputFromWindow(actvReceiver.getWindowToken(), 0);

            }
        });

        actvReceiver.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!refFromDialog){

                    ref_receiver = emptyRef;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        llAddresses = root.findViewById(R.id.llAddresses);

//        if (ref.equals(emptyRef)){
//            llAddresses.setVisibility(View.GONE);
//        }

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

        root.findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                doNext();

            }

        });

        root.findViewById(R.id.btnCarantine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                final HttpClient httpClient = new HttpClient(getContext());

                Bundle arguments = new Bundle();
                arguments.putString("refAccept", refAccept);
                arguments.putString("shtrihCode", shtrihCode);
                arguments.putBoolean("isPalet", isPalet);
                arguments.putDouble("boxQuantity", boxQuantity);
                arguments.putString("refReceiver", emptyRef);

                httpClient.showQuestionYesNoCancel(getActivity(), new BundleMethodInterface() {
                    @Override
                    public void callMethod(Bundle arguments) {

                        addEmptyReceipt(arguments.getString("refAccept"),
                                arguments.getString("shtrihCode"),
                                arguments.getBoolean("isPalet"),
                                arguments.getDouble("boxQuantity"),
                                arguments.getString("refReceiver"));

                    }
                }, arguments, "Отправить в карантин?", shtrihCode);

            }

        });


        return root;
    }

    private void doNext() {
        Boolean validated = true;

//                if(actvSender.getText().length() < 4){
//
//                    validated = false;
//
//                    Toast toast = Toast.makeText(getContext(), "Не заполнен отправитель", Toast.LENGTH_SHORT);
//                    toast.show();
//
//
//                }
//                else

        if(ref_receiver.equals(emptyRef)){

        validated = false;

        Toast toast = Toast.makeText(getContext(), "Не заполнен получатель", Toast.LENGTH_SHORT);
        toast.show();


    }

        if (validated){


            addEmptyReceipt(refAccept, shtrihCode, isPalet, boxQuantity, ref_receiver);

        }
    }

    private void addEmptyReceipt(String refAccept, String shtrihCode, Boolean isPalet, Double boxQuantity, String ref_receiver) {

        DB db = new DB(getContext());
        db.open();
        db.updateConstant("ref_receiver" + refAccept, ref_receiver);
        db.updateConstant("description_receiver" + refAccept, description_receiver);

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refAccept", refAccept);
        httpClient.addParam("shtrihCode", shtrihCode);
        httpClient.addParam("isPalet", isPalet);
        httpClient.addParam("boxQuantity", boxQuantity);
        httpClient.addParam("refReceiver", ref_receiver);

        httpClient.postProc("setReceiptShtrihAddress", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")) {

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack(back, false);

//                    scanned.add(0, new ScannedShtrihCode(shtrihCode, StrDateTime.dateToStr(new Date()), added, quantity, isPalet));
//                    adapterScanned.notifyDataSetChanged();
//
//                    updatePercent();
//
//                    if (inputQuantity && httpClient.getBooleanFromJSON(response, "IsNew")) {
//
//                        setQuantityToShtrih(shtrihCode, quantity);
//
//                    } else {
//
//                        if (update_scan) {
//
//                            getShtrihs();
//                        }
//
//                    }

                }

            }
        });




    }

    private void selectAddress(String s, int i) {
        Intent intent = new Intent(getContext(), AddressesListActivity.class);
        intent.putExtra("ref", refContractor);
        intent.putExtra("description", description);
        intent.putExtra("header", s + description);

        startActivityForResult(intent, i);
    }


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

                refFromDialog = true;

                actvReceiver.setText(description_receiver);

                refFromDialog = false;

            }
        }



    }



}