package com.example.uventawh;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
 * Use the {@link AcceptFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AcceptFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AcceptFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AcceptFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AcceptFragment newInstance(String param1, String param2) {
        AcceptFragment fragment = new AcceptFragment();
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

    DelayAutoCompleteTextView actvShtrihCode, actvSender, actvReceiver, actvNumber;

    DataDeliveryOrdersAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    ConstraintLayout ccFilter;
    Button btnFilter;
    Boolean isFilter;

    private boolean shtrihCodeKeyboard = false;

    private InputMethodManager imm;

    private static final int
            REQUEST_SELECT_SENDER_ADDRESS = 0,
            REQUEST_SELECT_RECEIVER_ADDRESS = 1;

    private Handler h;

    private EditText etFilter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_accept, container, false);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        setStatusCheckBoxes(root);

        etFilter = root.findViewById(R.id.etFilter);
        etFilter.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    String strCatName = etFilter.getText().toString();

//                    etFilter.setText("");

                    imm.hideSoftInputFromWindow(etFilter.getWindowToken(), 0);

                    update(strCatName);

                    return true;
                }

                return false;
            }
        });

        root.findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                etFilter.setText("");
                update(etFilter.getText().toString());

            }
        });


        ccFilter = root.findViewById(R.id.ccFilter);
        ccFilter.setVisibility(View.GONE);
        isFilter = false;

        btnFilter = root.findViewById(R.id.btnFilter);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isFilter = !isFilter;

                btnFilter.setText(isFilter ? "Список" : "Фильтр");

                ccFilter.setVisibility(isFilter ? View.VISIBLE : View.GONE);

                if (!isFilter)
                    update2();
            }
        });

        btnFilter.setVisibility(View.GONE);

//        setTabHost(root);

        adapter = new DataDeliveryOrdersAdapter(getContext(), task_items);
        adapter.setOnTaskItemClickListener(new DataDeliveryOrdersAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(ShipmentItem taskItem, View itemView) {

                if (taskItem.name.equals("Отгрузка")) {

//                    setRouteToShipment(taskItem.ref);

                    if(taskItem.status.equals("Новый")) {

                        askForOrderToPlan(taskItem);

                    }  else{

                        deliveryOrderTest(taskItem);

                    }
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

                        update(etFilter.getText().toString());

                    }
                    else if (!recyclerView.canScrollVertically(-1)) {

                        update(etFilter.getText().toString());

                    }
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        AddressAutoCompleteAdapter aucaAddress = new AddressAutoCompleteAdapter(getContext(),
                new DB(getContext()).getRequestUserProg(), new HttpClientSync(getContext()), ref, "getContractorsByFilter");

        actvSender = root.findViewById(R.id.actvSender);
        actvSender.setThreshold(3);

        actvSender.setAdapter(aucaAddress);
        actvSender.setLoadingIndicator((ProgressBar) root.findViewById(R.id.progress_bar));

        actvSender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RefDesc refDesc = (RefDesc) adapterView.getItemAtPosition(position);

                ref = refDesc.ref;
                description_sender = refDesc.desc;

                actvSender.setText(description_sender);

                imm.hideSoftInputFromWindow(actvSender.getWindowToken(), 0);

                update2();

            }
        });

        root.findViewById(R.id.btnClearContractor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ref = emptyRef;
                description_sender = "";

                actvSender.setText(description_sender);

            }
        });


        AddressAutoCompleteAdapter aucaNumber = new AddressAutoCompleteAdapter(getContext(),
                new DB(getContext()).getRequestUserProg(), new HttpClientSync(getContext()), ref, "getDeliveryNumbersByFilter");

        actvNumber = root.findViewById(R.id.actvNumber);
        actvNumber.setThreshold(3);

        actvNumber.setAdapter(aucaNumber);
        actvNumber.setLoadingIndicator((ProgressBar) root.findViewById(R.id.progress_bar));

        actvNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RefDesc refDesc = (RefDesc) adapterView.getItemAtPosition(position);

                shtrihCode = refDesc.desc;

                actvNumber.setText(shtrihCode);

                imm.hideSoftInputFromWindow(actvSender.getWindowToken(), 0);

                update2();

            }
        });

        root.findViewById(R.id.btnClearNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shtrihCode = "";

                actvNumber.setText(shtrihCode);

            }
        });

        update("");



        return root;
    }

    private void deliveryOrderTest(ShipmentItem taskItem) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("ref", taskItem.ref);

        httpClient.postProc("getShipmentOrderContainer", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                String containerRef = httpClient.getStringFromJSON(response, "ContainerRef");
                String containerName = httpClient.getStringFromJSON(response, "ContainerName");

                if (containerRef.isEmpty()) {

                    Bundle bundle = new Bundle();
                    bundle.putString("ref", taskItem.ref);
                    bundle.putString("name", taskItem.name);
                    bundle.putString("number", taskItem.number);
                    bundle.putString("date", taskItem.date);
                    bundle.putString("refContractor", taskItem.refContractor);
                    bundle.putString("company", taskItem.company);
                    bundle.putInt("image", taskItem.image);
                    bundle.putString("refSender", taskItem.refSender);
                    bundle.putString("sender", taskItem.sender);
                    bundle.putString("status", taskItem.status);
                    bundle.putInt("quantity", taskItem.quantity);
                    bundle.putInt("accepted", taskItem.accepted);
                    bundle.putString("numDocument", taskItem.numDocument);

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.newContainerFragment, bundle);
                } else {


                    Bundle bundle = new Bundle();
                    bundle.putString("ref", taskItem.ref);
                    bundle.putString("name", taskItem.name);
                    bundle.putString("number", taskItem.number);
                    bundle.putString("date", taskItem.date);
                    bundle.putString("refContractor", taskItem.refContractor);
                    bundle.putString("company", taskItem.company);
                    bundle.putInt("image", taskItem.image);
                    bundle.putString("refSender", taskItem.refSender);
                    bundle.putString("sender", taskItem.sender);
                    bundle.putString("containerRef", containerRef);
                    bundle.putString("containerName", containerName);
                    bundle.putString("status", taskItem.status);
                    bundle.putInt("quantity", taskItem.quantity);
                    bundle.putInt("accepted", taskItem.accepted);
                    bundle.putString("numDocument", taskItem.numDocument);

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.deliveryOrderTestFragment, bundle);
                }
            }
        });

    }

    private void askForOrderToPlan(ShipmentItem taskItem) {

        final HttpClient httpClient = new HttpClient(getContext());

        Bundle arguments = new Bundle();
        arguments.putString("ref", taskItem.ref);

        httpClient.showQuestionYesNoCancel(getActivity(), new BundleMethodInterface() {
            @Override
            public void callMethod(Bundle arguments) {

                setPlanToDeliveryOrder(arguments.getString("ref"));

            }
        }, arguments, "Заказ №" + taskItem.number + " в статусе Новый. Запланировать?", "Заказ №" + taskItem.number);

    }

    private void setPlanToDeliveryOrder(String ref) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("ref", ref);

        httpClient.postForResult("setPlanToDeliveryOrder", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                Boolean found = false;
                for (int i = 0; i < task_items.size() && !found; i++) {

                    ShipmentItem curTI = task_items.get(i);
                    found = curTI.ref.equals(ref);

                    if(found){
                        deliveryOrderTest(curTI);
                    }
                }

            }
        });


    }

    private void setStatusCheckBoxes(View root) {

//        final CheckBox cbNotLoaded = root.findViewById(R.id.cbNotLoaded);
////        cbNotLoaded.setChecked(deliveryListViewModel.getNotLoaded().getValue());
//        deliveryListViewModel.getNotLoaded().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                cbNotLoaded.setChecked(aBoolean);
//            }
//        });
//        cbNotLoaded.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                deliveryListViewModel.setNotLoaded(b);
//            }
//        });
//
//        final CheckBox cbNew = root.findViewById(R.id.cbNew);
//        deliveryListViewModel.getNew().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                cbNew.setChecked(aBoolean);
//            }
//        });
//        cbNew.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                deliveryListViewModel.setNew(b);
//            }
//        });
//
//        final CheckBox cbToPlan = root.findViewById(R.id.cbToPlan);
//        deliveryListViewModel.getToPlan().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                cbToPlan.setChecked(aBoolean);
//            }
//        });
//        cbToPlan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                deliveryListViewModel.setToPlan(b);
//            }
//        });
//
//        final CheckBox cbPlanedPartially = root.findViewById(R.id.cbPlanedPartially);
//        deliveryListViewModel.getPlanedPartially().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                cbPlanedPartially.setChecked(aBoolean);
//            }
//        });
//        cbPlanedPartially.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                deliveryListViewModel.setPlanedPartially(b);
//            }
//        });
//
//        final CheckBox cbPlaned = root.findViewById(R.id.cbPlaned);
//        deliveryListViewModel.getPlaned().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                cbPlaned.setChecked(aBoolean);
//            }
//        });
//        cbPlaned.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                deliveryListViewModel.setPlaned(b);
//            }
//        });
//
//        final CheckBox cbToSelect = root.findViewById(R.id.cbToSelect);
//        deliveryListViewModel.getToSelect().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                cbToSelect.setChecked(aBoolean);
//            }
//        });
//        cbToSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                deliveryListViewModel.setToSelect(b);
//            }
//        });
//
//        final CheckBox cbSelecting = root.findViewById(R.id.cbSelecting);
//        deliveryListViewModel.getSelecting().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                cbSelecting.setChecked(aBoolean);
//            }
//        });
//        cbSelecting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                deliveryListViewModel.setSelecting(b);
//            }
//        });
//
//        final CheckBox cbSelected = root.findViewById(R.id.cbSelected);
//        deliveryListViewModel.getSelected().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                cbSelected.setChecked(aBoolean);
//            }
//        });
//        cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                deliveryListViewModel.setSelected(b);
//            }
//        });
//
//        final CheckBox cbPacked = root.findViewById(R.id.cbPacked);
//        deliveryListViewModel.getPacked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                cbPacked.setChecked(aBoolean);
//            }
//        });
//        cbPacked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                deliveryListViewModel.setPacked(b);
//            }
//        });
//
//        final CheckBox cbReadyToShip = root.findViewById(R.id.cbReadyToShip);
//        deliveryListViewModel.getReadyToShip().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                cbReadyToShip.setChecked(aBoolean);
//            }
//        });
//        cbReadyToShip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                deliveryListViewModel.setReadyToShip(b);
//            }
//        });
//
//        final CheckBox cbShiped = root.findViewById(R.id.cbShiped);
//        deliveryListViewModel.getShiped().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                cbShiped.setChecked(aBoolean);
//            }
//        });
//        cbShiped.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                deliveryListViewModel.setShiped(b);
//            }
//        });
//
//        final CheckBox cbCanceled = root.findViewById(R.id.cbCanceled);
//        deliveryListViewModel.getCanceled().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                cbCanceled.setChecked(aBoolean);
//            }
//        });
//        cbCanceled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                deliveryListViewModel.setCanceled(b);
//            }
//        });
//
//        final CheckBox cbBlocked = root.findViewById(R.id.cbBlocked);
//        deliveryListViewModel.getBlocked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                cbBlocked.setChecked(aBoolean);
//            }
//        });
//        cbBlocked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                deliveryListViewModel.setBlocked(b);
//            }
//        });

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


    protected void update(String sFilter){

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("filter", sFilter);

        httpClient.postProc("getDeliveryByStringFilter", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                task_items.clear();

                JSONArray deliveryOrdersJSON = httpClient.getJsonArrayFromJsonObject(response, "DeliveryByStringFilter");

                for (int k = 0; k < deliveryOrdersJSON.length(); k++) {

                    JSONObject deliveryOrderJSON = httpClient.getItemJSONArray(deliveryOrdersJSON, k);

                    task_items.add(ShipmentItem.extractFromJSON(deliveryOrderJSON, httpClient));
                }

                adapter.notifyDataSetChanged();

            }
        });





    }

    protected void update2(){

        final HttpClient client1 = new HttpClient(getContext());

        String url = new DB(getContext()).getRequestUserProg();

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        String curStatus = "";
//        (deliveryListViewModel.getNotLoaded().getValue() ? "cbNotLoaded," : "")
//                + (deliveryListViewModel.getNew().getValue() ? "cbNew," : "")
//                + (deliveryListViewModel.getToPlan().getValue() ? "cbToPlan," : "")
//                + (deliveryListViewModel.getPlanedPartially().getValue() ? "cbPlanedPartially," : "")
//                + (deliveryListViewModel.getPlaned().getValue() ? "cbPlaned," : "")
//                + (deliveryListViewModel.getToSelect().getValue() ? "cbToSelect," : "")
//                + (deliveryListViewModel.getSelecting().getValue() ? "cbSelecting," : "")
//                + (deliveryListViewModel.getSelected().getValue() ? "cbSelected," : "")
//                + (deliveryListViewModel.getPacked().getValue() ? "cbPacked," : "")
//                + (deliveryListViewModel.getReadyToShip().getValue() ? "cbReadyToShip," : "")
//                + (deliveryListViewModel.getShiped().getValue() ? "cbReadyToShip," : "")
//                + (deliveryListViewModel.getCanceled().getValue() ? "cbCanceled," : "")
//                + (deliveryListViewModel.getBlocked().getValue() ? "cbBlocked," : "");

        try {
            requestParams.put("refContractor", ref);
            requestParams.put("numberDocument", shtrihCode);
            requestParams.put("ref_sender", ref_sender);
            requestParams.put("ref_receiver", ref_receiver);
            requestParams.put("status", curStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "getDeliveryByFilter");
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

                    if (client1.getStringFromJSON(response_item, "Name").equals("getDeliveryByFilter")) {

                        task_items.clear();

                        JSONArray deliveryOrdersJSON = client1.getJsonArrayFromJsonObject(response_item, "DeliveryByFilter");

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




}