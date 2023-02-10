package com.example.uventawh;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanRouteListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanRouteListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScanRouteListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScanRouteListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanRouteListFragment newInstance(String param1, String param2) {
        ScanRouteListFragment fragment = new ScanRouteListFragment();
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

    private boolean shtrihCodeKeyboard = false, inputSenderAddress, inputDelivererAddress, inputQuantity;

    TextView tvNumberRouteList;
    ProgressBar progressBar;

    String contractor, numberRouteList = "", numberRouteListRef = "";

    AutoCompleteAdapter aucaAddress;

    private boolean isReceipt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_scan_route_list, container, false);

        Bundle bundle = getArguments();

        if (bundle != null){
            isReceipt = bundle.getBoolean("isReceipt");
        }

        progressBar = root.findViewById(R.id.progressBar);

        actvShtrihCode = root.findViewById(R.id.actvShtrihCode);
        actvShtrihCode.setThreshold(3);

        aucaAddress = new AutoCompleteAdapter(getContext(), "getRouteByNumber");

        actvShtrihCode.setAdapter(aucaAddress);
        actvShtrihCode.setLoadingIndicator((ProgressBar) root.findViewById(R.id.progress_bar0));

        actvShtrihCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                TakeScreenShot.Do(getContext(), root, view);

                RefDesc strCatName = (RefDesc) adapterView.getItemAtPosition(position);

                actvShtrihCode.setText("");

                shtrihCodeKeyboard = false;

//                ((MainWareHouseActivity)getActivity()).imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                scanShtrihCodeRef(strCatName);

            }
        });

//        actvShtrihCode.requestFocus();


//        ((MainWareHouseActivity)getActivity()).imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

        actvShtrihCode.setOnKeyListener(new View.OnKeyListener()
                                        {
                                            public boolean onKey(View v, int keyCode, KeyEvent event)
                                            {
                                                if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                                        (keyCode == KeyEvent.KEYCODE_ENTER))
                                                {

                                                    TakeScreenShot.Do(getContext(), root, v);

                                                    String strCatName = actvShtrihCode.getText().toString();

                                                    actvShtrihCode.setText("");

                                                    shtrihCodeKeyboard = false;

//                                                    ((MainWareHouseActivity)getActivity()).imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

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

//                actvShtrihCode.requestFocus();
//
//                if (shtrihCodeKeyboard){
//
////                    ((MainWareHouseActivity)getActivity()).imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);
//
//                } else {
//
////                    ((MainWareHouseActivity)getActivity()).imm.showSoftInput(actvShtrihCode, 0, null);
//                }
//
//                shtrihCodeKeyboard = !shtrihCodeKeyboard;


            }
        });

        tvNumberRouteList = root.findViewById(R.id.tvNumberRouteList);

        root.findViewById(R.id.btnChoose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                if (numberRouteList.isEmpty()){

                    Toast.makeText(getContext(), "Не заполнен номер", Toast.LENGTH_SHORT).show();

                }
                else {

                    exchangeCode();

                }
            }
        });

        actvShtrihCode.requestFocus();

        return root;
    }

    private void scanShtrihCodeRef(RefDesc strCatName) {

        numberRouteList = strCatName.desc;
        numberRouteListRef = strCatName.ref;

        tvNumberRouteList.setText("№: " + numberRouteList);

        exchangeCode();

    }

    private void exchangeCode() {

        Bundle bundle = new Bundle();
        bundle.putString("ref", numberRouteListRef);
        bundle.putBoolean("isReceipt", isReceipt);
//        bundle.putString("refTransport", refTransport);
//        bundle.putString("refDriver", refDriver);
//        bundle.putString("descTransport", descTransport);
//        bundle.putString("descDriver", descDriver);

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.editRouteFragment, bundle);


    }

    private void scanShtrihCode(final String strCatName) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("filter", strCatName);

        httpClient.postForResult("getRouteByNumber", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                JSONArray tasksJSON = httpClient.getJsonArrayFromJsonObject(response, "RouteByNumber");

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