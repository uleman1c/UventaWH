package com.example.uventawh;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CodeExchangePageTwoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CodeExchangePageTwoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CodeExchangePageTwoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CodeExchangePageTwoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CodeExchangePageTwoFragment newInstance(String param1, String param2) {
        CodeExchangePageTwoFragment fragment = new CodeExchangePageTwoFragment();
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

    DelayAutoCompleteTextView actvShtrihCode;

    private InputMethodManager imm;

    private boolean shtrihCodeKeyboard = false, inputSenderAddress, inputDelivererAddress, inputQuantity;

    TextView tvContractor, tvSource, tvDestination;
    ProgressBar progressBar;

    String contractor, source = "", destination = "", sourceRef, destinationRef;

    AutoCompleteAdapter aucaAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_code_exchange_page_two, container, false);

        Bundle bundle = getArguments();
        contractor = bundle.getString("ref");

        progressBar = root.findViewById(R.id.progressBar);

        actvShtrihCode = root.findViewById(R.id.actvShtrihCode);
        actvShtrihCode.setThreshold(3);

        aucaAddress = new AutoCompleteAdapter(getContext(), "getContractorGoods");
        aucaAddress.addParam("contractor", contractor);

        actvShtrihCode.setAdapter(aucaAddress);
        actvShtrihCode.setLoadingIndicator((ProgressBar) root.findViewById(R.id.progress_bar0));

        actvShtrihCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                RefDesc strCatName = (RefDesc) adapterView.getItemAtPosition(position);

                actvShtrihCode.setText("");

                shtrihCodeKeyboard = false;

                imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                scanShtrihCodeRef(strCatName);

            }
        });

        actvShtrihCode.requestFocus();

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

                                                    scanShtrihCode(strCatName);

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

        tvContractor = root.findViewById(R.id.tvContractor);
        tvSource = root.findViewById(R.id.tvSource);
        tvDestination = root.findViewById(R.id.tvDestination);

        tvContractor.setText("Контрагент: " + bundle.getString("description"));

        root.findViewById(R.id.btnExchange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (source.isEmpty()){

                    Toast.makeText(getContext(), "Не заполнен источник", Toast.LENGTH_SHORT).show();

                }
                else if (destination.isEmpty()){

                    Toast.makeText(getContext(), "Не заполнен приемник", Toast.LENGTH_SHORT).show();

                }
                else {

                    exchangeCode();

                }
            }
        });

        return root;
    }

    private void scanShtrihCodeRef(RefDesc strCatName) {
        if (!destination.isEmpty()){
            source = "";
            destination = "";
        }

        if (source.isEmpty()){
            source = strCatName.desc;
            sourceRef = strCatName.ref;

        }
        else {
            destination = strCatName.desc;
            destinationRef = strCatName.ref;
        }

        tvSource.setText("Штрихкод: " + source);
        tvDestination.setText("Заменить на: " + destination);
    }

    private void exchangeCode() {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("contractor", contractor);
        httpClient.addParam("source", sourceRef);
        httpClient.addParam("destination", destinationRef);

        httpClient.postForResult("exchangeCode", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

                progressBar.setVisibility(visibility);

            }

            @Override
            public void processResponse(JSONObject response) {

                source = "";
                destination = "";
                tvSource.setText("Штрихкод: " + source);
                tvDestination.setText("Заменить на: " + destination);

                httpClient.toastShow("Отправлено задание на замену штрихкода");
//        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();

            }
        });

    }

    private void scanShtrihCode(final String strCatName) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("contractor", contractor);
        httpClient.addParam("filter", strCatName);

        httpClient.postForResult("getContractorGoods", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                JSONArray tasksJSON = httpClient.getJsonArrayFromJsonObject(response, "ContractorGoods");

                if (tasksJSON.length() == 0){

                    httpClient.toastShow("Штрихкод не найден");

                }
                else if (tasksJSON.length() > 1){

                    httpClient.toastShow("Штрихкодов более одного");

                }
                else{

                    String ref = httpClient.getStringFromJSON(httpClient.getItemJSONArray(tasksJSON, 0), "RefAddress");

                    scanShtrihCodeRef(new RefDesc(ref, strCatName));

                }

            }
        });

    }
}