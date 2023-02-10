package com.example.uventawh;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeliveryListFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeliveryListFragment2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DeliveryListFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeliveryListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeliveryListFragment2 newInstance(String param1, String param2) {
        DeliveryListFragment2 fragment = new DeliveryListFragment2();
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
    private String refRoute, ref, description, shtrihCode = "",
            ref_sender = emptyRef, description_sender = "",
            ref_receiver = emptyRef, description_receiver = "";

    List<ShipmentItem> task_items = new ArrayList<>();

    DelayAutoCompleteTextView actvShtrihCode, actvSender, actvReceiver;

    DataDeliveryOrdersAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout linearLayout;

    private boolean shtrihCodeKeyboard = false;

    private InputMethodManager imm;

    private static final int
            REQUEST_SELECT_SENDER_ADDRESS = 0,
            REQUEST_SELECT_RECEIVER_ADDRESS = 1;

    private Handler h;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_delivery_list, container, false);



        Bundle intent = getArguments();
        refRoute = ""; //intent.getString("refRoute");
        ref = ""; // intent.getString("ref");
        description = ""; // intent.getString("description");

        ((TextView) root.findViewById(R.id.tvHeader)).setText(description + ": Заказы на отгрузку");

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        h = new Handler();

        Thread t = new Thread(new Runnable() {
            public void run() {
                h.post(setFocus);
            }
        });
        t.start();

//        setTabHost(root);

        adapter = new DataDeliveryOrdersAdapter(getContext(), task_items);
        adapter.setOnTaskItemClickListener(new DataDeliveryOrdersAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(ShipmentItem taskItem, View itemView) {

                if (taskItem.name.equals("Отгрузка")) {

                    setRouteToShipment(taskItem.ref);

//                    Bundle bundle = new Bundle();
//                    bundle.putString("ref", taskItem.ref);
//                    bundle.putString("name", taskItem.name);
//                    bundle.putString("number", taskItem.number);
//                    bundle.putString("date", taskItem.date);
//                    bundle.putString("refContractor", taskItem.refContractor);
//                    bundle.putString("company", taskItem.company);
//                    bundle.putInt("image", taskItem.image);
//                    bundle.putString("refSender", taskItem.refSender);
//                    bundle.putString("sender", taskItem.sender);
//
//                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.deliveryOrderTestFragment, bundle);


                }
            }
        });

        recyclerView = root.findViewById(R.id.rvList);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState==RecyclerView.SCROLL_STATE_IDLE){

                    if (!recyclerView.canScrollVertically(1)) {

                        update();

                    }
                    else if (!recyclerView.canScrollVertically(-1)) {

                        update();

                    }
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        actvShtrihCode = root.findViewById(R.id.actvShtrihCode);
        actvShtrihCode.setThreshold(3);

        actvShtrihCode.setAdapter(new DocumentNumberAutoCompleteAdapter(getContext(), new DB(getContext()).getRequestUserProg(), new HttpClientSync(getContext()), ref));
        actvShtrihCode.setLoadingIndicator((ProgressBar) root.findViewById(R.id.progress_bar0));

        actvShtrihCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                shtrihCode = (String) adapterView.getItemAtPosition(position);

                imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                update();

//                refTransport = refDesc.ref;
//                descTransport = refDesc.desc;
//
//                actvShtrihCode.setText(descTransport);
            }
        });

        actvShtrihCode.requestFocus();
        imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

        actvShtrihCode.setOnKeyListener(new View.OnKeyListener()
                                        {
                                            public boolean onKey(View v, int keyCode, KeyEvent event)
                                            {
                                                if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                                        (keyCode == KeyEvent.KEYCODE_ENTER))
                                                {
                                                    shtrihCode = actvShtrihCode.getText().toString();

                                                    actvShtrihCode.setText("");

                                                    //                                                  tvNumberDoc.setText("Номер накладной: " + ShtrihCode);

                                                    shtrihCodeKeyboard = false;

                                                    imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                                                    update();

                                                    return true;
                                                }
                                                return false;
                                            }
                                        }
        );

        AddressAutoCompleteAdapter aucaAddress = new AddressAutoCompleteAdapter(getContext(), new DB(getContext()).getRequestUserProg(), new HttpClientSync(getContext()), ref, "getAddressesByFilter");

        actvSender = root.findViewById(R.id.actvSender);
        actvSender.setThreshold(3);

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

                update();

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

                update();

            }
        });


        root.findViewById(R.id.ibChooseSender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectAddress("Выбор адреса отправителя: ", REQUEST_SELECT_SENDER_ADDRESS);

            }
        });

        root.findViewById(R.id.ibChooseReceiver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectAddress("Выбор адреса получателя: ", REQUEST_SELECT_RECEIVER_ADDRESS);

            }
        });



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



        update();

        return root;




    }

    private void setRouteToShipment(String refReceipt) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refRoute", refRoute);
        httpClient.addParam("refDeliveryOrder", refReceipt);

        httpClient.postForResult("setRouteToDeliveryOrder", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                //if(httpClient.getBooleanFromJSON(response, "Success")){

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack(R.id.editRouteFragment, false);

                //}

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


    protected void update(){

        final HttpClient client1 = new HttpClient(getContext());

        String url = new DB(getContext()).getRequestUserProg();

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        try {
            requestParams.put("refContractor", ref);
            requestParams.put("numberDocument", shtrihCode);
            requestParams.put("ref_sender", ref_sender);
            requestParams.put("ref_receiver", ref_receiver);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "getContractorDeliveryPoints");
            request.put("parameters", requestParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put(request);

        client1.post(getContext(), url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {

                progressBar.setVisibility(View.GONE);

                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                JSONArray response = client1.getResponseJSONArray(responseBody);

                for (int i = 0; i < response.length(); i++){

                    JSONObject response_item = client1.getItemJSONArray(response, i);

                    if (client1.getStringFromJSON(response_item, "Name").equals("getContractorDeliveryPoints")) {

                        task_items.clear();

                        JSONArray deliveryOrdersJSON = client1.getJsonArrayFromJsonObject(response_item, "ContractorDeliveryPoints");

                        for (int k = 0; k < deliveryOrdersJSON.length(); k++) {

                            JSONObject deliveryOrderJSON = client1.getItemJSONArray(deliveryOrdersJSON, k);

                            task_items.add(ShipmentItem.extractFromJSON(deliveryOrderJSON, client1));
                        }

                        adapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                client1.showMessageOnFailure(statusCode, headers, responseBody, error);

            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });






    }


    private void setTabHost(View root) {

        TabHost tabHost = root.findViewById(R.id.thSearch);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");

        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("Номер накладной");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("Отправитель");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator("Получатель");
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)  {

            if (requestCode == REQUEST_SELECT_SENDER_ADDRESS) {

                ref_sender = data.getStringExtra("ref");
                description_sender = data.getStringExtra("description");

                actvSender.setText(description_sender);

                update();

            }
            else if(requestCode == REQUEST_SELECT_RECEIVER_ADDRESS){

                ref_receiver = data.getStringExtra("ref");
                description_receiver = data.getStringExtra("description");

                actvReceiver.setText(description_receiver);

                update();

            }

        }



    }

    final Runnable setFocus = new Runnable() {
        public void run() {

            if (actvShtrihCode.isFocused() && !shtrihCodeKeyboard) {

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

            }

            h.postDelayed(setFocus, 500);

        }
    };




}