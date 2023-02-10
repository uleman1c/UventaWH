package com.example.uventawh;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
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

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeliveryOrderTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeliveryOrderTestFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DeliveryOrderTestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeliveryOrderTestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeliveryOrderTestFragment newInstance(String param1, String param2) {
        DeliveryOrderTestFragment fragment = new DeliveryOrderTestFragment();
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

    private class ReceiptRefCode{

        public String receiptRef, code;

        public ReceiptRefCode(String receiptRef, String code) {
            this.receiptRef = receiptRef;
            this.code = code;
        }
    }

    String ref, name, number, date, refContractor, company, refSender, sender, curShtrihCode, currentPhotoPath,
            numDocument, status;
    Integer image;

    EditText editText;
    TextView error, filter;

    List<ReceiptRefCode> receiptRefCodes = new ArrayList<>();

    ArrayList<String> shipmentOrders = new ArrayList<>();

    List<String> toScanStart = new ArrayList<>();
    List<String> toScan = new ArrayList<>();
    List<String> toScanContainer = new ArrayList<>();
    List<String> serialNumbers = new ArrayList<>();
    List<ScannedShtrihCode> scanned = new ArrayList<>();
    DataStringAdapter adapterToScan, adapterToScanContainer;
    DataContainersAdapter adapterContainers;
    ScannedShtrihCodeAdapter adapterScanned;

    DataDeliveryOrdersTasksAdapter dataDeliveryOrdersTasksAdapter;
    private ArrayList<DeliveryOrderTask> deliveryOrderTasks = new ArrayList<>();
    RecyclerView rvTasks;

    ProgressBar progressBar;
    LinearLayout llMain;

    private Timer timer = new Timer();
    private final long DELAY = 1000; // milliseconds

    int cnt;
    final int max = 100;
    Handler h;

    Boolean manualInput = false, sendingInProgress = false;

    Uri outputFileUri;

    private static final int CAMERA_REQUEST = 20;

    private boolean shtrihCodeKeyboard = false;

    private InputMethodManager imm;

    DelayAutoCompleteTextView actvShtrihCode;

    private SoundPlayer soundPlayer;

    ProgressTextView progressTextView;

    Boolean inputSenderAddress, inputDelivererAddress, inputQuantity;

    androidx.appcompat.app.AlertDialog quantityDialog = null;

    EditText etQuantity;

    Double quantity = null;

    private ArrayList<Container> containers = new ArrayList<>();
    private RecyclerView rcContainers;
    private TextView tvContainer;
    private LinearLayout llContainers;
    private Button btnContainers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_delivery_order_test, container, false);

//        ((TextView) root.findViewById(R.id.tvVersion)).setText(getResources().getString(R.string.version));

        progressTextView = root.findViewById(R.id.scannedText);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        soundPlayer = new SoundPlayer(getContext(), R.raw.hrn05);
        getActivity().setVolumeControlStream(soundPlayer.streamType);

        h = new Handler();

        Bundle bundle = getArguments();

        ref = bundle.getString("ref");
        name = bundle.getString("name");
        status = bundle.getString("status");
        number = bundle.getString("number");
        date = bundle.getString("date");
        refContractor = bundle.getString("refContractor");
        company = bundle.getString("company");
        refSender = bundle.getString("refSender");
        sender = bundle.getString("sender");
        numDocument = bundle.getString("numDocument");
        image = (int) bundle.getLong("image", 0);

        shipmentOrders = bundle.getStringArrayList("shipmentOrders");

        if (shipmentOrders == null){

            shipmentOrders = new ArrayList<>();
            shipmentOrders.add(ref);

        }

        inputSenderAddress = bundle.getBoolean("inputSenderAddress", false);
        inputDelivererAddress = bundle.getBoolean("inputDelivererAddress", false);
        inputQuantity = bundle.getBoolean("inputQuantity", false);

        progressBar = root.findViewById(R.id.progressBar);
        llMain = root.findViewById(R.id.llMain);
        tvContainer = root.findViewById(R.id.tvContainer);
//        llContainers = root.findViewById(R.id.llContainers);
//        btnContainers = root.findViewById(R.id.btnContainers);

        actvShtrihCode = root.findViewById(R.id.actvShtrihCode);
        actvShtrihCode.setThreshold(3);

        actvShtrihCode.setAdapter(new StringAutoCompleteAdapter(getContext(), toScan));
        actvShtrihCode.setLoadingIndicator((ProgressBar) root.findViewById(R.id.progress_bar0));

        actvShtrihCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                String strCatName = (String) adapterView.getItemAtPosition(position);

                actvShtrihCode.setText("");

                shtrihCodeKeyboard = false;

                imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                scanShtrihCode(strCatName);

            }
        });

        actvShtrihCode.requestFocus();
        imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

        actvShtrihCode.setOnKeyListener(new View.OnKeyListener() {
                                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                                                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

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


        ((TextView) root.findViewById(R.id.header)).setText(numDocument + " №" + number + " от " + date + " " + company + ", " + sender);

        error = (TextView) root.findViewById(R.id.error);

        Thread t = new Thread(new Runnable() {
            public void run() {
                h.post(setFocus);
            }
        });
        t.start();


        adapterToScan = new DataStringAdapter(getContext(), toScan);

        adapterToScan.setOnStringClickListener(new DataStringAdapter.OnStringClickListener() {
            @Override
            public void onStringClick(String str, View itemView) {

//                if (manualInput) {
//
//                    scanShtrihCode(str);
//
//                    filter.setText("");
//
//                    setFilter("");
//
//                    manualInput = false;
//                }
            }
        });

        adapterContainers = new DataContainersAdapter(getContext(), containers, R.layout.container_w_list_item);

        adapterContainers.setOnStringClickListener(new DataContainersAdapter.OnStringClickListener() {
                                                       @Override
                                                       public void onStringClick(Container taskItem) {

                                                           scanShtrihCode(taskItem.description);

                                                       }
                                                   });

        adapterToScanContainer = new DataStringAdapter(getContext(), toScanContainer);

        adapterToScanContainer.setOnStringClickListener(new DataStringAdapter.OnStringClickListener() {
            @Override
            public void onStringClick(String taskItem, View itemView) {

            }
        });

        dataDeliveryOrdersTasksAdapter = new DataDeliveryOrdersTasksAdapter(getContext(), deliveryOrderTasks);
        dataDeliveryOrdersTasksAdapter.setOnTaskItemClickListener(new DataDeliveryOrdersTasksAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(DeliveryOrderTask taskItem, Integer pos, View itemView) {

                if (taskItem.level <= 2 && taskItem.status.equals("К выполнению")) {

                    HttpClient httpClient = new HttpClient(getContext());

                    Bundle bundle = new Bundle();
                    bundle.putInt("pos", pos);
                    httpClient.showQuestionYesNoCancel(getActivity(), new BundleMethodInterface() {
                        @Override
                        public void callMethod(Bundle arguments) {

                            Integer quantity = 1;

                            if (taskItem.level == 2) {

//                                Boolean foundShtrih = false;
//                                for (int j = 0; j < taskItem.shtrihPacks.size() && !foundShtrih; j++) {
//
//                                    ShtrihPack csp = taskItem.shtrihPacks.get(j);
//
//                                    foundShtrih = csp.shtrih.equals(strCatName);
//                                    quantity = csp.quantity == 0 ? (csp.range == 0 ? 1 : csp.range) : csp.quantity;
//                                }

                            }

                            onTaskItemFound(taskItem, pos, quantity);


                        }
                    }, bundle, "Ввести вручную ?", "Ввод вручную");


                }

            }
        });

        dataDeliveryOrdersTasksAdapter.setOnTaskItemLongClickListener(new DataDeliveryOrdersTasksAdapter.OnTaskItemLongClickListener() {
            @Override
            public void onTaskItemLongClick(DeliveryOrderTask taskItem, Integer pos, View itemView) {


                if (taskItem.level == 2 && taskItem.status.equals("К выполнению")) {

                    HttpClient httpClient = new HttpClient(getContext());

                    Bundle bundle = new Bundle();
                    bundle.putInt("pos", pos);
                    httpClient.showInputQuantity(taskItem.quantity - taskItem.scanned, getActivity(), new BundleMethodInterface() {
                        @Override
                        public void callMethod(Bundle arguments) {

                            onTaskItemFound(taskItem, pos, arguments.getInt("quantity"));


                        }
                    }, bundle, "Ввести вручную", "Ввод количества вручную");


                }

            }
        });


        rvTasks = root.findViewById(R.id.rvTasks);
        rvTasks.setAdapter(dataDeliveryOrdersTasksAdapter);

//        rcContainers = root.findViewById(R.id.containers);
//        rcContainers.setAdapter(adapterContainers);

//        RecyclerView recyclerViewToScan = root.findViewById(R.id.toScan);
//        recyclerViewToScan.setAdapter(adapterToScan);
//
//        RecyclerView recyclerViewScanned = root.findViewById(R.id.scanned);
//        adapterScanned = new ScannedShtrihCodeAdapter(getContext(), scanned);
//        recyclerViewScanned.setAdapter(adapterScanned);
//
//        adapterScanned.setOnStringClickListener(new ScannedShtrihCodeAdapter.OnStringClickListener() {
//            @Override
//            public void onStringClick(ScannedShtrihCode str, View itemView) {
//
////                askForRepeatCode(str);
//
//            }
//        });

        root.findViewById(R.id.ibKeyboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actvShtrihCode.requestFocus();

                if (shtrihCodeKeyboard) {

                    imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                } else {

                    imm.showSoftInput(actvShtrihCode, 0, null);
                }

                shtrihCodeKeyboard = !shtrihCodeKeyboard;


            }
        });

//        btnContainers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                tvContainer.setText("Контейнер:");
//                btnContainers.setVisibility(View.GONE);
//                rcContainers.setAdapter(adapterContainers);
//
//            }
//        });



        getShtrihs();

        return root;

    }

    private void onTaskItemFound(DeliveryOrderTask taskItem, Integer pos, Integer quantity) {

        if (taskItem.level < 2){

//            if (!taskItem.childExist) {

                Integer curPos = pos;

                if (taskItem.level == 0) {

                    Collections.rotate(deliveryOrderTasks, deliveryOrderTasks.size() - pos);

                    curPos = 0;
                }

                taskItem.childExist = true;

//                DeliveryOrderTask cell = new DeliveryOrderTask(taskItem.ref, taskItem.status, taskItem.product, taskItem.shtrih_code, taskItem.product_status,
//                        taskItem.product_part, taskItem.container, taskItem.container_shtrih_code, taskItem.cell, taskItem.cell_shtrih_code, taskItem.quantity);
                taskItem.level = taskItem.level + 1;

//                deliveryOrderTasks.add(curPos + 1, cell);

                rvTasks.getLayoutManager().scrollToPosition(curPos);

                serialNumbers.clear();

//            }

        } else

        if (taskItem.level == 2){

            taskItem.scanned = taskItem.scanned + quantity;

            if (taskItem.serialNumberExist) {

                inputSerialNumber(taskItem);

            } else {

                setTaskExecuted(taskItem);

            }

        }


        dataDeliveryOrdersTasksAdapter.notifyDataSetChanged();
    }

    private void setTaskExecuted(DeliveryOrderTask taskItem) {

        if (taskItem.quantity == taskItem.scanned){

            final HttpClient httpClient = new HttpClient(getContext());
            httpClient.addParam("refTask", taskItem.ref);
            httpClient.addParam("serialNumbers", serialNumbers.toString().replace('[', ' ').replace(']', ' '));

            httpClient.postProc("setSelectTask", new HttpRequestInterface() {
                @Override
                public void setProgressVisibility(int visibility) {
                    progressBar.setVisibility(visibility);
                }

                @Override
                public void processResponse(JSONObject response) {

                    if (httpClient.getBooleanFromJSON(response, "Success")) {

                        getShtrihs();

                    }
                }
            });




        }
    }


    private void openCell(String ref, String cell, String cell_shtrih_code) {

        Bundle bundle = new Bundle();
        bundle.putString("shipmentOrder", ref);
        bundle.putString("cell", cell);
        bundle.putString("cell_shtrih_code", cell_shtrih_code);

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.cellFragment, bundle);

    }

    protected void getShtrihs(){

        deliveryOrderTasks.clear();

        getTasks(ref);

//        receiptRefCodes.clear();
//
//        containers.clear();
//
//        toScan.clear();
//        scanned.clear();
//
//        if (shipmentOrders != null && shipmentOrders.size() > 0){
//
//            getReceiptShtrihs(shipmentOrders.get(0));
//
//        }
//        else {
//
//            getReceiptShtrihs(ref);
//
//        }
    }

    protected void getReceiptShtrihs(final String shipmentOrder) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refDeliveryOrder", shipmentOrder);

        httpClient.postProc("getDeliveryOrderShtrihs", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                JSONArray routeShtrihs = httpClient.getJsonArrayFromJsonObject(response, "DeliveryOrderShtrihs");

                Integer iAdded = 0;

                for (int j = 0; j < routeShtrihs.length(); j++) {

                    JSONObject accept_item = httpClient.getItemJSONArray(routeShtrihs, j);

                    Boolean tested = httpClient.getBooleanFromJSON(accept_item, "Tested");
                    Boolean added = httpClient.getBooleanFromJSON(accept_item, "Added");
                    String date = httpClient.getStringFromJSON(accept_item, "Date");
                    String shtrih_code = httpClient.getStringFromJSON(accept_item, "ShtrihCode");
                    Double quantity = httpClient.getDoubleFromJSON(accept_item, "Quantity");

                    JSONArray containers_json = httpClient.getJsonArrayFromJsonObject(accept_item, "Containers");

                    for (int i = 0; i < containers_json.length(); i++) {

                        JSONObject container_item = httpClient.getItemJSONArray(containers_json, i);

                        String containerRef = httpClient.getStringFromJSON(container_item, "ContainerRef");
                        String goodRef = httpClient.getStringFromJSON(container_item, "GoodRef");
                        String goodDesc = httpClient.getStringFromJSON(container_item, "GoodDesc");
                        Double containerQuantity = httpClient.getDoubleFromJSON(container_item, "Quantity");

                        Boolean bAdded = false;
                        for (int k = 0; k < containers.size(); k++) {

                            Container container = containers.get(k);

                            if (containerRef.equals(container.ref)){

                                container.goods.add(new Good(goodRef, goodDesc, containerQuantity));

                                bAdded = true;

                            }

                        }

                        if (!bAdded){

                            String containerDesc = httpClient.getStringFromJSON(container_item, "ContainerDesc");

                            int addCount = 0;
                            Boolean found = false;
                            for (int k = 0; k < containers.size() && !found; k++) {

                                if (containerDesc.compareTo(containers.get(k).description) < 0){
                                    addCount = k;
                                    found = true;
                                }

                            }

                            ArrayList<Good> aGoods = new ArrayList<>();

                            aGoods.add(new Good(goodRef, goodDesc, containerQuantity));
                            containers.add(found ? addCount : containers.size(), new Container(containerRef, containerDesc,
                                    "", "", "", "", aGoods));

                        }

                    }

                    if (tested) {

                        scanned.add(new ScannedShtrihCode(shtrih_code, date, added, quantity));
                    } else {

                        toScan.add(shtrih_code);

                        receiptRefCodes.add(new ReceiptRefCode(shipmentOrder, shtrih_code));

                    }

                    iAdded = iAdded + (added ? 1 : 0);

                }

                Integer scannedQ = scanned.size();
                Integer toAcceptQ = toScan.size() + scannedQ;

                Integer procent = toAcceptQ == 0 ? 100 : scannedQ * 100 / toAcceptQ;

                progressTextView.setText(scannedQ.toString() + " из " + toAcceptQ.toString() + ", " + procent.toString() + "%");
                progressTextView.setMax(toAcceptQ);
                progressTextView.setProgress(scannedQ);

                adapterToScan.notifyDataSetChanged();
                adapterScanned.notifyDataSetChanged();

                adapterContainers.notifyDataSetChanged();

                llContainers.setVisibility(containers.size() == 0 ? View.GONE :View.VISIBLE);

                if (shipmentOrders != null && shipmentOrders.size() > 0){

                    Integer index = shipmentOrders.indexOf(shipmentOrder);
                    if (index + 1 < shipmentOrders.size()) {

                        getReceiptShtrihs(shipmentOrders.get(index + 1));

                    }

                }



            }
        });

    }

    protected void getTasks(final String shipmentOrder) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refDeliveryOrder", shipmentOrder);

        httpClient.postProc("getDeliveryOrderTasks", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                JSONArray routeShtrihs = httpClient.getJsonArrayFromJsonObject(response, "DeliveryOrderTasks");

                Integer iAdded = 0;

                Integer toScan = 0;
                Integer toAccept = 0;
                Integer scanned = 0;

                for (int j = 0; j < routeShtrihs.length(); j++) {

                    JSONObject accept_item = httpClient.getItemJSONArray(routeShtrihs, j);

                    DeliveryOrderTask cdot = DeliveryOrderTask.DeliveryOrderTaskFromJSON(accept_item, httpClient);

                    toAccept += cdot.quantity;
                    toScan += cdot.status.equals("Выполнена") ? 0 : cdot.quantity - cdot.scanned;
                    scanned += cdot.status.equals("Выполнена") ? cdot.quantity : 0;

                    deliveryOrderTasks.add(cdot);

                }

                Integer procent = toAccept == 0 ? 0 : scanned * 100 / toAccept;

                progressTextView.setText(scanned.toString() + " из " + toAccept.toString() + ", " + procent.toString() + "%");
                progressTextView.setMax(toAccept);
                progressTextView.setProgress(scanned);

                dataDeliveryOrdersTasksAdapter.notifyDataSetChanged();

                if (scanned > 0 && toScan == 0 && (status.equals("В отбор") || status.equals("Новый") || status.equals("В отборе"))){

                    inputQuantitySetOrderScanned(shipmentOrder, httpClient);
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

    final Runnable setFocus2 = new Runnable() {
        public void run() {

            InputMethodManager imm = (InputMethodManager) quantityDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            etQuantity.requestFocus();

            imm.showSoftInput(etQuantity, 0, null);


        }
    };

    private void inputSerialNumber1(DeliveryOrderTask taskItem, HttpClient httpClient) {

        Bundle bundle = new Bundle();
        bundle.putString("ref", taskItem.ref);
        httpClient.showQuestionYesNoCancel(getActivity(), new BundleMethodInterface() {
            @Override
            public void callMethod(Bundle arguments) {

                inputQuantityDialog(arguments.getString("ref"));


            }
        }, bundle, "Все отсканировано. Закрыть ?", "Закрытие");
    }

    private void inputSerialNumber(DeliveryOrderTask taskItem) {

        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Ввод серийного номера");

        LayoutInflater inflater = getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_input_serial_number, null);

        ((TextView) view.findViewById(R.id.tvCode)).setText("Серийный номер");

        Button btnQuantity = view.findViewById(R.id.btnQuantity);

        etQuantity = view.findViewById(R.id.etQuantity);

        etQuantity.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    String serialNumber = ((TextView) view.findViewById(R.id.etQuantity)).getText().toString();

                    if (!serialNumber.isEmpty()) {

                        serialNumbers.add(serialNumber);
                        quantityDialog.cancel();

                        setTaskExecuted(taskItem);

//                    final HttpClient httpClient = new HttpClient(getContext());
//                    httpClient.addParam("refTask", taskItem.ref);
//                    httpClient.addParam("serialNumber", serialNumber);
//
//                    httpClient.postProc("setSerialNumber", new HttpRequestInterface() {
//                        @Override
//                        public void setProgressVisibility(int visibility) {
//
//                        }
//
//                        @Override
//                        public void processResponse(JSONObject response) {
//
//                            quantityDialog.cancel();
//
//                            getShtrihs();
//
//                        }
//                    });

                    }




                    return true;
                }
                return false;
            }
        });

//        if (quantity == null) {

        btnQuantity.setVisibility(View.GONE);
//        } else {
//
//            btnQuantity.setVisibility(View.VISIBLE);
//            btnQuantity.setText("<<  " + quantity.toString());
//        }

        btnQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                etQuantity.setText(quantity.toString());
//
//                CloseScan(view, strCatName);





            }
        });

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        quantityDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String serialNumber = ((TextView) view.findViewById(R.id.etQuantity)).getText().toString();

                if (!serialNumber.isEmpty()) {

                    serialNumbers.add(serialNumber);
                    quantityDialog.cancel();

                    setTaskExecuted(taskItem);

//                    final HttpClient httpClient = new HttpClient(getContext());
//                    httpClient.addParam("refTask", taskItem.ref);
//                    httpClient.addParam("serialNumber", serialNumber);
//
//                    httpClient.postProc("setSerialNumber", new HttpRequestInterface() {
//                        @Override
//                        public void setProgressVisibility(int visibility) {
//
//                        }
//
//                        @Override
//                        public void processResponse(JSONObject response) {
//
//                            quantityDialog.cancel();
//
//                            getShtrihs();
//
//                        }
//                    });

                }


            }
        });

        view.findViewById(R.id.btnNoSerial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                quantityDialog.cancel();

                setTaskExecuted(taskItem);

            }
        });

        quantityDialog.show();

        final Handler h2 = new Handler();
        h2.postDelayed(setFocus3, 500);

    }

    private void inputQuantitySetOrderScanned(String shipmentOrder, HttpClient httpClient) {

        Bundle bundle = new Bundle();
        bundle.putString("ref", shipmentOrder);
        httpClient.showQuestionYesNoCancel(getActivity(), new BundleMethodInterface() {
            @Override
            public void callMethod(Bundle arguments) {

                inputQuantityDialog(arguments.getString("ref"));


            }
        }, bundle, "Все отсканировано. Закрыть ?", "Закрытие");
    }

    private void scanShtrihCode(String strCatName) {

        Boolean found = false;
        DeliveryOrderTask curTask = null;
        Integer quantity = 1;

        int i;
        for (i = 0; i < deliveryOrderTasks.size() && !found; i++) {

            curTask = deliveryOrderTasks.get(i);

            Boolean foundShtrih = false;
            for (int j = 0; j < curTask.shtrihPacks.size() && !foundShtrih; j++) {

                ShtrihPack csp = curTask.shtrihPacks.get(j);

                foundShtrih = csp.shtrih.equals(strCatName);
                quantity = csp.quantity == 0 ? (csp.range == 0 ? 1 : csp.range) : csp.quantity;
            }

            found = curTask.level == 0 && curTask.status.equals("К выполнению") && curTask.cell_shtrih_code.equals(strCatName)
                || curTask.level == 1 && curTask.container_shtrih_code.equals(strCatName)
                    || curTask.level == 2 && (foundShtrih || curTask.shtrih_codes.indexOf(strCatName) >= 0);

        }

        if (found){

            onTaskItemFound(curTask, i - 1, quantity);

//            ArrayList<Good> curGoods = containers.get(i-1).goods;
//
//            tvContainer.setText("Контейнер: " + strCatName);
//
//            toScanContainer.clear();
//
//            for (int j = 0; j < curGoods.size(); j++) {
//
//                toScanContainer.add(curGoods.get(j).description + ", " + curGoods.get(j).quantity.toString());
//
//            }
//
//            adapterToScanContainer.notifyDataSetChanged();
//
//            rcContainers.setAdapter(adapterToScanContainer);
//
//            btnContainers.setVisibility(View.VISIBLE);
//
        }
        else {
//
//            if (!sendingInProgress) {
//
//                Integer index = toScan.indexOf(strCatName);
//
//                if (index < 0) {
//
                    error.setText("Не найден штрихкод " + strCatName);

                    soundPlayer.play();

//                    Boolean present = false;
//                    for (ScannedShtrihCode scannedShtrihCode : scanned) {
//                        present = scannedShtrihCode.shtrihCode.equals(strCatName);
//                        if (present) break;
//                    }
//
//                    if (present) {
//
//                        askForRepeatCode(strCatName);
//
//                    } else {
//
//                        askForNotFoundCode(strCatName);
//
//                    }
//
//
//                } else {
//                    error.setText("");
//
//                    toScan.remove(strCatName);
//
//                    scanned.add(0, new ScannedShtrihCode(strCatName, "", false));
//                    adapterScanned.notifyDataSetChanged();
//
//                    setShtrihs(strCatName);
//                }
//            }
        }

        setShtrihCode(strCatName);


    }

    protected void setShtrihCode(final String shtrihcode) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("shtrihCode", shtrihcode);

        httpClient.postProc("setShtrihCode", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")) {

//                    if (inputQuantity && httpClient.getBooleanFromJSON(response, "IsNew")) {
//
//                        inputQuantity(shtrihcode);
//
//                    } else {
//                        getShtrihs();
//                    }

                }
            }
        });

    }

    private void askForNotFoundCode(final String strShtrihCode) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(strShtrihCode + " не найден. Выбрать");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_ask_not_found_shtrihcode, null);

        if (shipmentOrders.size() > 1){

            view.findViewById(R.id.btnAdd).setVisibility(View.GONE);
            view.findViewById(R.id.btnAddP).setVisibility(View.GONE);

        }

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setShtrihs(strShtrihCode);

                alertDialog.cancel();


            }
        });

        view.findViewById(R.id.btnIgnore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

            }
        });

        alertDialog.show();

    }

    private void askForRepeatCode(final String strShtrihCode) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(strShtrihCode + " уже есть. Выбрать");

        alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_ask_repeat_shtrihcode, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setShtrihs(strShtrihCode);

                alertDialog.cancel();


            }
        });

        view.findViewById(R.id.btnIgnore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

            }
        });

        alertDialog.show();


    }


    final Runnable setFocus = new Runnable() {
        public void run() {

            if (actvShtrihCode.isFocused() && !shtrihCodeKeyboard) {

                imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

            }

            h.postDelayed(setFocus, 500);

        }
    };

    protected void setShtrihs(final String shtrihcode) {

        String shipmentOrder = null;
        for (ReceiptRefCode receiptRefCode:receiptRefCodes) {

            if (receiptRefCode.code.equals(shtrihcode)){

                shipmentOrder = receiptRefCode.receiptRef;

            }

        }

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refDeliveryOrder", shipmentOrder == null ? receiptRefCodes.get(0).receiptRef : shipmentOrder);
        httpClient.addParam("shtrihcode", shtrihcode);

        httpClient.postProc("setDeliveryOrderShtrihCode", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")) {

                    if (inputQuantity && httpClient.getBooleanFromJSON(response, "IsNew")) {

                        inputQuantity(shtrihcode);

                    } else {
                        getShtrihs();
                    }

                }
            }
        });

    }

    private void inputQuantityDialog(final String ref) {

        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Ввод количества");

        LayoutInflater inflater = getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_input_quantity, null);

        ((TextView) view.findViewById(R.id.tvCode)).setText("Количество коробок");

        Button btnQuantity = view.findViewById(R.id.btnQuantity);

        etQuantity = view.findViewById(R.id.etQuantity);

//        if (quantity == null) {

            btnQuantity.setVisibility(View.GONE);
//        } else {
//
//            btnQuantity.setVisibility(View.VISIBLE);
//            btnQuantity.setText("<<  " + quantity.toString());
//        }

        btnQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                etQuantity.setText(quantity.toString());
//
//                CloseScan(view, strCatName);





            }
        });

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        quantityDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                CloseScan(view, strCatName);

                final HttpClient httpClient = new HttpClient(getContext());
                httpClient.addParam("ref", ref);
                httpClient.addParam("quantity", Integer.valueOf(((TextView) view.findViewById(R.id.etQuantity)).getText().toString()));

                httpClient.postProc("setDeliveryOrderScanned", new HttpRequestInterface() {
                    @Override
                    public void setProgressVisibility(int visibility) {

                    }

                    @Override
                    public void processResponse(JSONObject response) {

                        quantityDialog.cancel();

                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();

                    }
                });




            }
        });

        quantityDialog.show();

        final Handler h2 = new Handler();
        h2.postDelayed(setFocus3, 500);

    }

    final Runnable setFocus3 = new Runnable() {
        public void run() {

            InputMethodManager imm = (InputMethodManager) quantityDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            etQuantity.requestFocus();

            imm.showSoftInput(etQuantity, 0, null);


        }
    };

    private void CloseScan(View view, String strCatName) {
        quantityDialog.cancel();

        quantity = Double.valueOf(((TextView) view.findViewById(R.id.etQuantity)).getText().toString());

        scanShtrihCode(strCatName, quantity);
    }

    private void scanShtrihCode(String strCatName, Double quantity) {

        if (!sendingInProgress) {

            scanned.add(0, new ScannedShtrihCode(strCatName, "", false, quantity));
            adapterScanned.notifyDataSetChanged();

            setQuantityToShtrih(strCatName, quantity);

        }
    }

    private void setQuantityToShtrih(String shtrihcode, Double quantity) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("contractorRef", refContractor);
        httpClient.addParam("shtrihCode", shtrihcode);
        httpClient.addParam("quantity", quantity);

        httpClient.postForResult("setQuantityToShtrih", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")) {

                    getShtrihs();

                }
                ;

            }
        });


    }

}