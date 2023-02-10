package com.example.uventawh;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditRouteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditRouteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditRouteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditRouteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditRouteFragment newInstance(String param1, String param2) {
        EditRouteFragment fragment = new EditRouteFragment();
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

    List<ShipmentItem> task_items = new ArrayList<>(), task_items_for_show = new ArrayList<>();

    Boolean presentAccept, presentShipment, isReceipt;

    private EditText etFilter;

    private InputMethodManager imm;

    DataDeliveryOrdersAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout linearLayout;

    String refRoute, refContractor, mode, refTransport, refDriver, descTransport, descDriver, number, dateShipment;

    private static final int
            REQUEST_TEST_DELIVERY_ORDER = 0,
            REQUEST_ADD_DELIVERY_ORDER = 1,
            REQUEST_CHOOSE_CONTRACTOR = 2,
            REQUEST_ADD_ACCEPT_FROM_TRANSPORT = 3,
            REQUEST_ADD_RECEIPT = 4;

    View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_edit_route, container, false);

        Bundle bundle = getArguments();

        refRoute = bundle.getString("ref");
        refTransport = bundle.getString("refTransport");
        refDriver = bundle.getString("refDriver");
        descTransport = bundle.getString("descTransport");
        descDriver = bundle.getString("descDriver");
        isReceipt = bundle.getBoolean("isReceipt");

        linearLayout = root.findViewById(R.id.linearLayout);

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);


        adapter = new DataDeliveryOrdersAdapter(getContext(), task_items_for_show);
        adapter.setOnTaskItemClickListener(new DataDeliveryOrdersAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(ShipmentItem taskItem, View itemView) {

                TakeScreenShot.Do(getContext(), root, itemView);

                if (taskItem.name.equals("Отгрузка")) {

                    Bundle bundle = new Bundle();

                    bundle.putString("ref", taskItem.ref);
                    bundle.putString("name", taskItem.name);
                    bundle.putString("number", taskItem.number);
                    bundle.putString("date", taskItem.date);
                    bundle.putString("refContractor", taskItem.refContractor);
                    bundle.putString("company", taskItem.company);
                    bundle.putLong("image", taskItem.image);
                    bundle.putString("refSender", taskItem.refSender);
                    bundle.putString("sender", taskItem.sender);

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.deliveryOrderTestFragment, bundle);


                }
                else if (taskItem.name.equals("Приемка")) {

                    scanReceipt(taskItem);
                }

            }
        });

        recyclerView = root.findViewById(R.id.list);
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

        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        root.findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                if (isReceipt == true) {
                    mode = "receipt";

                    chooseContractor();

            }
                else if (isReceipt == false) {
                    mode = "delivery";

                    chooseContractor();


                }
                else {
                        add();
                    }
            }
        });

        root.findViewById(R.id.btnCloseRoute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                close();

            }
        });

        root.findViewById(R.id.btnMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                chooseAction();

            }
        });

        etFilter = root.findViewById(R.id.etFilter);
        etFilter.setText("");

        etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                setFilter(etFilter.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        etFilter.setOnKeyListener(new View.OnKeyListener()
                                  {
                                      public boolean onKey(View v, int keyCode, KeyEvent event)
                                      {
                                          if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                                  (keyCode == KeyEvent.KEYCODE_ENTER))
                                          {
                                              imm.hideSoftInputFromWindow(etFilter.getWindowToken(), 0);

                                              setFilter(etFilter.getText().toString());

                                              return true;
                                          }
                                          return false;
                                      }
                                  }
        );

        root.findViewById(R.id.btnClearFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFilter.setText("");
                setFilter("");
            }
        });



        update();


        return root;
    }

    private void setFilter(String value) {

        task_items_for_show.clear();
        for (ShipmentItem shipmentItem:task_items){

            if (value.isEmpty() || shipmentItem.numDocument.toLowerCase().contains(value.toLowerCase())
                    || shipmentItem.sender.toLowerCase().contains(value.toLowerCase())
                    || shipmentItem.deliverer.toLowerCase().contains(value.toLowerCase())
            ){
                task_items_for_show.add(shipmentItem);
            }

        }

        adapter.setFilter(value);
        adapter.notifyDataSetChanged();


    }

    private void chooseAction() {

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Выберите действие");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = this.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_route_menu, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        ((TextView) view.findViewById(R.id.tvQuestion)).setText("Рейс №" + number
                + " от " + StrDateTime.strToDate(dateShipment) + ", "
                + descDriver + ", "
                + descTransport);

        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        if(!presentShipment) {

            view.findViewById(R.id.btnShipAddress).setVisibility(View.GONE);

        }

        if(!presentAccept) {

            view.findViewById(R.id.btnReceiptRoute).setVisibility(View.GONE);
            view.findViewById(R.id.btnReceiptPalet).setVisibility(View.GONE);

        }

        view.findViewById(R.id.btnShipAddress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

                choseAdddressForShip();


            }
        });

        view.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

                close();


            }
        });

        view.findViewById(R.id.btnReceiptPalet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

                chooseRouteContractor("palet");


            }
        });

        view.findViewById(R.id.btnReceiptRoute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

                chooseRouteContractor("route");


            }
        });

        alertDialog.show();

    }

    private void choseAdddressForShip() {

        final ArrayList<String> address_refs = new ArrayList<>();
        final ArrayList<String> address_desc = new ArrayList<>();

        for(ShipmentItem shipmentItem:task_items){

            if(shipmentItem.name.equals("Отгрузка") && address_refs.indexOf(shipmentItem.refDeliverer) == -1){

                address_refs.add(shipmentItem.refDeliverer);
                address_desc.add(shipmentItem.deliverer);

            }


        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());

        final ArrayList<Dialog> phoneDialog = new ArrayList<>();

        builder.setTitle("Выбор адреса")
        .setItems(address_desc.toArray(new String[address_desc.size()]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                TakeScreenShot.Do(getContext(), root, (View) phoneDialog.get(0).getCurrentFocus());

                String refDeliverer = address_refs.get(which);

                ArrayList<String> shipmentOrders = new ArrayList<>();

                ShipmentItem shipmentItemStart = null;
                for(ShipmentItem shipmentItem:task_items){

                    if(shipmentItem.name.equals("Отгрузка") && shipmentItem.refDeliverer.equals(refDeliverer)){

                        if (shipmentItemStart == null){
                            shipmentItemStart = shipmentItem;
                        }

                        shipmentOrders.add(shipmentItem.ref);

                    }

                }

                Bundle bundle = new Bundle();

                bundle.putStringArrayList("shipmentOrders", shipmentOrders);

//                bundle.putString("ref", taskItem.ref);
                bundle.putString("name", shipmentItemStart.name);
//                bundle.putString("number", taskItem.number);
//                bundle.putString("date", taskItem.date);
                bundle.putString("refContractor", shipmentItemStart.refContractor);
                bundle.putString("company", shipmentItemStart.company);
                bundle.putLong("image", shipmentItemStart.image);
//                bundle.putString("refSender", taskItem.refSender);
                bundle.putString("sender", shipmentItemStart.deliverer);

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.deliveryOrderTestFragment, bundle);




            }
        });

        phoneDialog.add(builder.create());

        phoneDialog.get(0).show();



    }

    private void chooseRouteContractor(final String mode) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refRoute", refRoute);
        httpClient.postForResult("getRouteContractors", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if(httpClient.getBooleanFromJSON(response, "Success")) {

                    final JSONArray contractorsJSON = httpClient.getJsonArrayFromJsonObject(response, "RouteContractors");

                    if (contractorsJSON.length() == 1){

                        JSONObject contractorJSON = httpClient.getItemJSONArray(contractorsJSON, 0);
                        finishCooseContractor(mode, httpClient.getStringFromJSON(contractorJSON, "Ref"), httpClient.getStringFromJSON(contractorJSON, "Description"));

                    }
                    else {

                        ArrayList<String> contractor_items = new ArrayList<>();

                        for (int i = 0; i < contractorsJSON.length(); i++) {

                            contractor_items.add(httpClient.getStringFromJSON(httpClient.getItemJSONArray(contractorsJSON, i), "Description"));
                        }

                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                        builder.setIcon(R.drawable.sklad96);

                        builder.setTitle("Выбор контрагента")
                                .setItems(contractor_items.toArray(new String[contractor_items.size()]), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        TakeScreenShot.Do(getContext(), root, (View) dialog);

                                        JSONObject contractorJSON = httpClient.getItemJSONArray(contractorsJSON, which);
                                        finishCooseContractor(mode, httpClient.getStringFromJSON(contractorJSON, "Ref"),
                                                httpClient.getStringFromJSON(contractorJSON, "Description"));

                                    }
                                });

                        Dialog phoneDialog = builder.create();

                        phoneDialog.show();

                    }

                }
            }
        });
    }

    private void finishCooseContractor(String mode, String refContractor, String descriptionContractor) {

        Bundle bundle = new Bundle();
        bundle.putString("ref", refRoute);
        bundle.putString("number", number);
        bundle.putString("date", StrDateTime.strToDate(dateShipment));
        bundle.putString("refContractor", refContractor);
        bundle.putString("descContractor", descriptionContractor);
        bundle.putString("company", descriptionContractor);
        bundle.putString("refTransport", refTransport);
        bundle.putString("descTransport", descTransport);
        bundle.putString("refDriver", refDriver);
        bundle.putString("descDriver", descDriver);
        bundle.putString("mode", mode);

        if (mode.equals("palet")){

            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.receiptPaletFragment, bundle);

        }
        else if (mode.equals("route")) {

            bundle.putString("name", "Рейс");

            ArrayList<String> receipts = new ArrayList<>();

            for (int i = 0; i < task_items.size(); i++) {

                ShipmentItem curItem = task_items.get(i);
                if (curItem.name.equals("Приемка")){

                    receipts.add(curItem.ref);
                }

            }

            bundle.putStringArrayList("receipts", receipts);
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.scanFragment, bundle);

        }

    }

    private void close() {

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Закрытие рейса");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = this.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_yes_no_cancel, null);

        ((TextView) view.findViewById(R.id.tvQuestion)).setText("Закрыть рейс " + descDriver + ", " + descTransport + " ?");

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                closeRoute();

                alertDialog.cancel();


            }
        });

        view.findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

            }
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), view, v);

                alertDialog.cancel();

            }
        });

        alertDialog.show();



    }

    private void closeRoute() {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("ref", refRoute);

        httpClient.postForResult("closeRoute", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                if(httpClient.getBooleanFromJSON(response, "Success")){

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack(R.id.routesListFragment, false);

                };

            }
        });

    }

    private void scanReceipt(ShipmentItem taskItem) {

        Bundle bundle = new Bundle();

        bundle.putString("refRoute", refRoute);
        bundle.putString("ref", taskItem.ref);
        bundle.putString("name", taskItem.name);
        bundle.putString("number", taskItem.number);
        bundle.putString("date", taskItem.date);
        bundle.putString("refContractor", taskItem.refContractor);
        bundle.putString("company", taskItem.company);
        bundle.putLong("image", taskItem.image);
        bundle.putString("refSender", taskItem.refSender);
        bundle.putString("sender", taskItem.sender);
        bundle.putString("refReceiver", taskItem.refDeliverer);
        bundle.putString("receiver", taskItem.deliverer);

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.scanFragment, bundle);

    }

    private void add() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Выбрать");

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_choose_receipt_delivery, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.ibReceipt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

                mode = "receipt";

                chooseContractor();


            }
        });

        view.findViewById(R.id.ibDelivery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

                mode = "delivery";

                chooseContractor();

            }
        });


        alertDialog.show();


    }

    private void chooseContractor() {

        Bundle bundle = new Bundle();
        bundle.putString("refTransport", refTransport);
        bundle.putString("descTransport", descTransport);
        bundle.putString("refDriver", refDriver);
        bundle.putString("descDriver", descDriver);
        bundle.putString("refRoute", refRoute);
        bundle.putString("refFilter", getString(R.string.emptyRef));
        bundle.putString("descFilter", "");

        if (mode.equals("receipt")){

            bundle.putString("header", "Приемка");
//            bundle.putInt("next", R.id.docInputFragment);
            bundle.putInt("next", R.id.docNumberFragment);
            bundle.putInt("back", R.id.editRouteFragment);

            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.contractorsListFragment, bundle);

        }
        else {

            bundle.putString("header", "Отгрузка");
            bundle.putInt("next", R.id.deliveryListFragment);
            bundle.putInt("back", R.id.editRouteFragment);

            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.contractorsListFragment, bundle);

//            Intent intent = new Intent(getContext(), ContractorsListActivity.class);
//
//            startActivityForResult(intent, REQUEST_CHOOSE_CONTRACTOR);

        }

    }

    private void addDeliveryOrder(String ref, String description) {

        Intent intent = new Intent(getContext(), SelectDeliveryOrderActivity.class);
        intent.putExtra("ref", ref);
        intent.putExtra("description", description);

        startActivityForResult(intent, REQUEST_ADD_DELIVERY_ORDER);

    }

    protected void update(){

        presentAccept = false;
        presentShipment = false;

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refRoute", refRoute);

        httpClient.postProc("getRouteDeliveryPoints", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getStringFromJSON(response, "Name").equals("getRouteDeliveryPoints")) {

                    task_items.clear();

                    JSONArray routesJSON = httpClient.getJsonArrayFromJsonObject(response, "RouteDeliveryPoints");

                    for (int j = 0; j < routesJSON.length(); j++) {

                        JSONObject routeJSON = httpClient.getItemJSONArray(routesJSON, j);

                        dateShipment = httpClient.getStringFromJSON(routeJSON, "DateShipment");

                        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

                        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);

                        MenuItem checkedItem = navigationView.getCheckedItem();

                        actionBar.setTitle(checkedItem.getTitle() + " - Рейс");

                        descDriver = httpClient.getStringFromJSON(routeJSON, "Driver");
                        descTransport = httpClient.getStringFromJSON(routeJSON, "Transport");
                        number = httpClient.getStringFromJSON(routeJSON, "Number");

                        ((TextView)root.findViewById(R.id.tvHeader)).setText("Рейс №" + httpClient.getStringFromJSON(routeJSON, "Number")
                                + " от " + httpClient.dateStrToDate(dateShipment) + ", "
                                + descDriver + ", "
                                + descTransport);


                        JSONArray deliveryOrdersJSON = httpClient.getJsonArrayFromJsonObject(routeJSON, "DeliveryPoints");

                        for (int k = 0; k < deliveryOrdersJSON.length(); k++) {

                            JSONObject deliveryOrderJSON = httpClient.getItemJSONArray(deliveryOrdersJSON, k);

                            task_items.add(ShipmentItem.extractFromJSON(deliveryOrderJSON, httpClient));
                        }
                    }


                    setFilter(etFilter.getText().toString());

                }



            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_TEST_DELIVERY_ORDER
                || requestCode == REQUEST_ADD_ACCEPT_FROM_TRANSPORT){

            update();

        }

        if (resultCode == RESULT_OK){

            if(requestCode == REQUEST_CHOOSE_CONTRACTOR){

                if (mode.equals("delivery")){

                    addDeliveryOrder(data.getStringExtra("ref"), data.getStringExtra("description"));
                }
                else {


                }


            }
            else if(requestCode == REQUEST_ADD_DELIVERY_ORDER){

                setRouteToDeliveryOrder(data.getStringExtra("ref"));

            }
            else if(requestCode == REQUEST_ADD_RECEIPT){

                setRouteToReceipt(data.getStringExtra("ref"));

            }

        }

    }

    private void setRouteToDeliveryOrder(String refDeliveryOrder) {

        final HttpClient client1 = new HttpClient(getContext());

        String url = new DB(getContext()).getRequestUserProg();

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        try {
            requestParams.put("refRoute", refRoute);
            requestParams.put("refDeliveryOrder", refDeliveryOrder);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "setRouteToDeliveryOrder");
            request.put("parameters", requestParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put(request);

        client1.post(getContext(), url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

//                linearLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {

//                linearLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                JSONArray response = client1.getResponseJSONArray(responseBody);

                for (int i = 0; i < response.length(); i++){

                    JSONObject response_item = client1.getItemJSONArray(response, i);

                    if (client1.getStringFromJSON(response_item, "Name").equals("setRouteToDeliveryOrder")) {

                        if (client1.getBooleanFromJSON(response_item, "Success")){

                            update();

                        }

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

    private void setRouteToReceipt(String refReceipt) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refRoute", refRoute);
        httpClient.addParam("refReceipt", refReceipt);

        httpClient.postForResult("setRouteToReceipt", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if(httpClient.getBooleanFromJSON(response, "Success")){

                    update();
                }

            }
        });


    }


}