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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AcceptanceListFragment extends Fragment {

    private AcceptanceListViewModel mViewModel;

    public static AcceptanceListFragment newInstance() {
        return new AcceptanceListFragment();
    }

    private String emptyRef = "00000000-0000-0000-0000-000000000000";
    private String refRoute, ref, description, shtrihCode = "",
            ref_sender = emptyRef, description_sender = "",
            ref_receiver = emptyRef, description_receiver = "";

    List<AcceptanceItem> task_items = new ArrayList<>();

    DelayAutoCompleteTextView actvShtrihCode, actvSender, actvReceiver, actvNumber;

    DataAcceptancesAdapter adapter;
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

    private TsdSettings tsdSettings;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.acceptance_list_fragment, container, false);

        tsdSettings = new TsdSettings();

        Bundle intent = getArguments();
        refRoute = ""; //intent.getString("refRoute");
        ref = emptyRef; // intent.getString("ref");
        description = ""; // intent.getString("description");

        ((TextView) root.findViewById(R.id.tvHeader)).setText(description + ": Заказы на отгрузку");

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

        adapter = new DataAcceptancesAdapter(getContext(), task_items);
        adapter.setOnTaskItemClickListener(new DataAcceptancesAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(AcceptanceItem taskItem, View itemView) {

                if (taskItem.name.equals("Отгрузка")) {

//                    setRouteToShipment(taskItem.ref);

                    if(taskItem.status.equals("Новая")) {

                        askForAcceptanceToWork(taskItem);

                    }  else{

                        acceptanceProducts(taskItem);

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

        getSettings(new BundleMethodInterface() {
            @Override
            public void callMethod(Bundle arguments) {
                update("");
            }
        });




        return root;
    }

    private void getSettings(BundleMethodInterface bundleMethodInterface) {

        final HttpClient httpClient = new HttpClient(getContext());

        httpClient.postProc("getTsdSettings", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                tsdSettings.generateAcceptContainer = httpClient.getBooleanFromJSON(response, "GenerateAcceptContainer");

                bundleMethodInterface.callMethod(new Bundle());

            }
        });



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AcceptanceListViewModel.class);
        // TODO: Use the ViewModel
    }


    private void acceptanceProducts(AcceptanceItem acceptanceItem) {

        Bundle bundle = new Bundle();
        bundle.putBoolean("generateAcceptContainer", tsdSettings.generateAcceptContainer);
        bundle.putString("ref", acceptanceItem.ref);
        bundle.putString("number", acceptanceItem.number);
        bundle.putString("numDocument", acceptanceItem.numDocument);
        bundle.putString("date", acceptanceItem.date);
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.acceptanceProductsFragment, bundle);


//        final HttpClient httpClient = new HttpClient(getContext());
//        httpClient.addParam("ref", taskItem.ref);
//
//        httpClient.postProc("getShipmentOrderContainer", new HttpRequestInterface() {
//            @Override
//            public void setProgressVisibility(int visibility) {
//
//            }
//
//            @Override
//            public void processResponse(JSONObject response) {
//
//                String containerRef = httpClient.getStringFromJSON(response, "ContainerRef");
//                String containerName = httpClient.getStringFromJSON(response, "ContainerName");
//
//                if (containerRef.isEmpty()) {
//
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
//                    bundle.putString("status", taskItem.status);
//                    bundle.putInt("quantity", taskItem.quantity);
//                    bundle.putInt("accepted", taskItem.accepted);
//                    bundle.putString("numDocument", taskItem.numDocument);
//
//                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.newContainerFragment, bundle);
//                } else {
//
//
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
//                    bundle.putString("containerRef", containerRef);
//                    bundle.putString("containerName", containerName);
//                    bundle.putString("status", taskItem.status);
//                    bundle.putInt("quantity", taskItem.quantity);
//                    bundle.putInt("accepted", taskItem.accepted);
//                    bundle.putString("numDocument", taskItem.numDocument);
//
//                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.deliveryOrderTestFragment, bundle);
//                }
//            }
//        });
//
    }

    private void askForAcceptanceToWork(AcceptanceItem taskItem) {

        final HttpClient httpClient = new HttpClient(getContext());

        Bundle arguments = new Bundle();
        arguments.putString("ref", taskItem.ref);

        httpClient.showQuestionYesNoCancel(getActivity(), new BundleMethodInterface() {
            @Override
            public void callMethod(Bundle arguments) {

                setToWorkForAcceptance(taskItem);

            }
        }, arguments, "Ожидаемая приемка №" + taskItem.number + " в статусе Новая. Отправить в работу?", "Ожидаемая приемка №" + taskItem.number);

    }

    private void setToWorkForAcceptance(AcceptanceItem acceptanceItem) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("ref", acceptanceItem.ref);

        httpClient.postForResult("setToWorkForAcceptance", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                acceptanceProducts(acceptanceItem);

//                Bundle bundle = new Bundle();
//                bundle.putString("ref", ref);
//                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.acceptanceListFragment, bundle);
//                update(etFilter.getText().toString());

//                Boolean found = false;
//                for (int i = 0; i < task_items.size() && !found; i++) {
//
//                    ShipmentItem curTI = task_items.get(i);
//                    found = curTI.ref.equals(ref);
//
//                    if(found){
//                        deliveryOrderTest(curTI);
//                    }
//                }

            }
        });


    }

    private void setStatusCheckBoxes(View root) {

//        final CheckBox cbNotLoaded = root.findViewById(R.id.cbNotLoaded);
////        cbNotLoaded.setChecked(deliveryListViewModel.getNotLoaded().getValue());
//        mViewModel.getNotLoaded().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
//        mViewModel.getNew().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
//        mViewModel.getToPlan().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
//        mViewModel.getPlanedPartially().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
//        mViewModel.getPlaned().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
//        mViewModel.getToSelect().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
//        mViewModel.getSelecting().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
//        mViewModel.getSelected().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
//        mViewModel.getPacked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
//        mViewModel.getReadyToShip().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
//        mViewModel.getShiped().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
//        mViewModel.getCanceled().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
//        mViewModel.getBlocked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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

        httpClient.postProc("getAcceptanceByStringFilter", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                task_items.clear();

                JSONArray deliveryOrdersJSON = httpClient.getJsonArrayFromJsonObject(response, "AcceptanceByStringFilter");

                for (int k = 0; k < deliveryOrdersJSON.length(); k++) {
                    JSONObject deliveryOrderJSON = httpClient.getItemJSONArray(deliveryOrdersJSON, k);

                    task_items.add(AcceptanceItem.extractFromJSON(deliveryOrderJSON, httpClient));
                }

                adapter.notifyDataSetChanged();

            }
        });





    }

    protected void update2(){

        final HttpClient httpClient = new HttpClient(getContext());
        String curStatus = (mViewModel.getNotLoaded().getValue() ? "cbNotLoaded," : "")
                + (mViewModel.getNew().getValue() ? "cbNew," : "")
                + (mViewModel.getToExecute().getValue() ? "cbToExecute," : "")
                + (mViewModel.getInWork().getValue() ? "cbInWork," : "")
                + (mViewModel.getAccepted().getValue() ? "cbAccepted," : "")
                + (mViewModel.getControlled().getValue() ? "cbControlled," : "")
                + (mViewModel.getMarked().getValue() ? "cbMarked," : "")
                + (mViewModel.getSelected().getValue() ? "cbSelected," : "")
                + (mViewModel.getSelected().getValue() ? "cbSelected," : "")
                + (mViewModel.getFinished().getValue() ? "cbFinished," : "")
                + (mViewModel.getCanceled().getValue() ? "cbCanceled," : "")
                + (mViewModel.getBlocked().getValue() ? "cbBlocked," : "");

        httpClient.addParam("refContractor", ref);
        httpClient.addParam("numberDocument", shtrihCode);
        httpClient.addParam("ref_sender", ref_sender);
        httpClient.addParam("ref_receiver", ref_receiver);
        httpClient.addParam("status", curStatus);

        httpClient.postProc("getAcceptancesByFilter", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                task_items.clear();

                JSONArray deliveryOrdersJSON = httpClient.getJsonArrayFromJsonObject(response, "AcceptancesByFilter");

                for (int k = 0; k < deliveryOrdersJSON.length(); k++) {

                    JSONObject deliveryOrderJSON = httpClient.getItemJSONArray(deliveryOrdersJSON, k);

                    task_items.add(AcceptanceItem.extractFromJSON(deliveryOrderJSON, httpClient));
                }

                adapter.notifyDataSetChanged();


            }
        });

//        JSONObject request = new JSONObject();
//        try {
//            request.put("request", "");
//            request.put("parameters", requestParams);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        params.put(request);
//
//        client1.post(getContext(), url, params, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onStart() {
//
//                progressBar.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onFinish() {
//
//                progressBar.setVisibility(View.GONE);
//
//                super.onFinish();
//            }
//
//            @Override
//            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
//
//                JSONArray response = client1.getResponseJSONArray(responseBody);
//
//                for (int i = 0; i < response.length(); i++){
//
//                    JSONObject response_item = client1.getItemJSONArray(response, i);
//
//                    if (client1.getStringFromJSON(response_item, "Name").equals("getDeliveryByFilter")) {
//
//                        task_items.clear();
//
//                        JSONArray deliveryOrdersJSON = client1.getJsonArrayFromJsonObject(response_item, "DeliveryByFilter");
//
//                        for (int k = 0; k < deliveryOrdersJSON.length(); k++) {
//
//                            JSONObject deliveryOrderJSON = client1.getItemJSONArray(deliveryOrdersJSON, k);
//
//                            task_items.add(ShipmentItem.extractFromJSON(deliveryOrderJSON, client1));
//                        }
//
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
//
//                client1.showMessageOnFailure(statusCode, headers, responseBody, error);
//
//            }
//
//            @Override
//            public void onRetry(int retryNo) {
//                // called when request is retried
//            }
//        });






    }






}