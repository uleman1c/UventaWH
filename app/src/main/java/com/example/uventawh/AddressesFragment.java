package com.example.uventawh;

import android.app.Activity;
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
 * Use the {@link AddressesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddressesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddressesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddressesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddressesFragment newInstance(String param1, String param2) {
        AddressesFragment fragment = new AddressesFragment();
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

    Boolean refFromDialog = false;

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

    private Bundle bundle;

    private int back = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_addresses, container, false);

        DB db = new DB(getContext());
        String url = db.getRequestUserProg();

        bundle = getArguments();
        ref = bundle.getString("ref");
        refRoute = bundle.getString("refRoute");
        description = bundle.getString("description");
        refTransport = bundle.getString("refTransport");
        descTransport = bundle.getString("descTransport");
        refDriver = bundle.getString("refDriver");
        descDriver = bundle.getString("descDriver");
        back = bundle.getInt("back", 0);

        BoldStringBuilder builder = new BoldStringBuilder();

        builder.erase();
        builder.addBoldString("Контрагент: ");
        builder.addString(bundle.getString("description") + "\n");
        builder.addBoldString("Водитель: ");
        builder.addString(bundle.getString("descDriver") + "\n");
        builder.addBoldString("Транспорт: ");
        builder.addString(bundle.getString("descTransport") + "\n");
        builder.addBoldString("Номер накладной: " );
        builder.addString(bundle.getString("docNumber"));

        ((TextView)root.findViewById(R.id.tvHeader)).setText(builder.spannableStringBuilder, TextView.BufferType.SPANNABLE);

        actvSender = root.findViewById(R.id.actvSender);

        actvSender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!refFromDialog){

                    ref_sender = emptyRef;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

                refFromDialog = true;

                actvSender.setText(description_sender);

                refFromDialog = false;

                hideKeyboard();

            }
        });

        actvReceiver = root.findViewById(R.id.actvReceiver);

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

                hideKeyboard();

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

                Boolean validated = true;

                if(ref_sender.equals(emptyRef)){

                    validated = false;

                    Toast toast = Toast.makeText(getContext(), "Не заполнен отправитель", Toast.LENGTH_SHORT);
                    toast.show();


                }
                else if(ref_receiver.equals(emptyRef)){

                    validated = false;

                    Toast toast = Toast.makeText(getContext(), "Не заполнен получатель", Toast.LENGTH_SHORT);
                    toast.show();


                }

                if (validated){

                    addEmptyReceipt();

                }

            }

        });


        return root;
    }

    private void hideKeyboard() {

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        View v = ((Activity) getContext()).getCurrentFocus();
        if (v != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void addEmptyReceipt() {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("organization", ref);
        httpClient.addParam("sender", ref_sender);
        httpClient.addParam("receiver", ref_receiver);
        httpClient.addParam("route", refRoute);
        httpClient.addParam("numDocument", bundle.getString("docNumber"));

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
                    bundle.putString("number", bundle.getString("docNumber"));
//                    bundle.putString("date", taskItem.date);
                    bundle.putString("refContractor", ref);
//                    bundle.putString("company", taskItem.company);
//                    bundle.putLong("image", taskItem.image);
                    bundle.putString("refSender", ref_sender);
                    bundle.putString("sender", description_sender);
                    bundle.putString("refReceiver", ref_receiver);
                    bundle.putString("receiver", description_receiver);

                    if (back != 0){

                        bundle.putInt("back", back);
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack(back, false);
                    }


                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.scanFragment, bundle);
                }

            }
        });




    }

    private void selectAddress(String s, int i) {
        Intent intent = new Intent(getContext(), AddressesListActivity.class);
        intent.putExtra("ref", ref);
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

                refFromDialog = true;

                actvSender.setText(description_sender);

                refFromDialog = false;

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