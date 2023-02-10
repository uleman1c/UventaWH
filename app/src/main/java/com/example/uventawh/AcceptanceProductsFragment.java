package com.example.uventawh;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AcceptanceProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AcceptanceProductsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AcceptanceProductsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AcceptanceProductsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AcceptanceProductsFragment newInstance(String param1, String param2) {
        AcceptanceProductsFragment fragment = new AcceptanceProductsFragment();
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

//        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//
//
//
//            }
//        });



    }

    private InputMethodManager imm;

    private SoundPlayer soundPlayer;

    ProgressTextView progressTextView;

    ProgressBar progressBar;

    Handler h;

    EditText etQuantity;

    String ref, number, numDocument, date;

    DataAcceptanceProductsAdapter dataAcceptanceProductsAdapter;
    RecyclerView rvList;

    List<AcceptanceProduct> acceptanceProducts = new ArrayList<>();

    DelayAutoCompleteTextView actvShtrihCode;

    TextView error, filter, tvPartyDate, tvPartyDateExpired, tvPartyDateProduced;

    private boolean shtrihCodeKeyboard = false;

    androidx.appcompat.app.AlertDialog quantityDialog = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentActivity activity = getActivity();

        ((MainWareHouseActivity)activity).setOnBackPressedListener(new BaseBackPressedListener(activity));

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_acceptance_products, container, false);

        progressTextView = root.findViewById(R.id.scannedText);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        soundPlayer = new SoundPlayer(getContext(), R.raw.hrn05);
        getActivity().setVolumeControlStream(soundPlayer.streamType);

        h = new Handler();

        Bundle bundle = getArguments();

        ref = bundle.getString("ref");
        number  = bundle.getString("number");
        numDocument = bundle.getString("numDocument");
        date = bundle.getString("date");

        ((TextView) root.findViewById(R.id.header)).setText(numDocument + " №" + number + " от " + date);

        error = root.findViewById(R.id.error);

        progressBar = root.findViewById(R.id.progressBar);

        dataAcceptanceProductsAdapter = new DataAcceptanceProductsAdapter(getContext(), acceptanceProducts);
        dataAcceptanceProductsAdapter.setOnTaskItemClickListener(new DataAcceptanceProductsAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(AcceptanceProduct taskItem, Integer pos, View itemView) {

                if (!taskItem.isContainer) {

                    HttpClient httpClient = new HttpClient(getContext());

                    Bundle bundle = new Bundle();
                    bundle.putInt("pos", pos);
                    httpClient.showQuestionYesNoCancel(getActivity(), new BundleMethodInterface() {
                        @Override
                        public void callMethod(Bundle arguments) {

                            scanShtrihCode(taskItem.shtrih_codes.get(0).toString());

                        }
                    }, bundle, "Ввести вручную ?", "Ввод вручную");

                } else {

                    askToCloseContainer(taskItem, pos);

                }

            }
        });

        dataAcceptanceProductsAdapter.setOnTaskItemLongClickListener(new DataAcceptanceProductsAdapter.OnTaskItemLongClickListener() {
            @Override
            public void onTaskItemLongClick(AcceptanceProduct taskItem, Integer pos, View itemView) {


//                if (taskItem.level == 2 && taskItem.status.equals("К выполнению")) {
//
//                    HttpClient httpClient = new HttpClient(getContext());
//
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("pos", pos);
//                    httpClient.showInputQuantity(taskItem.quantity - taskItem.scanned, getActivity(), new BundleMethodInterface() {
//                        @Override
//                        public void callMethod(Bundle arguments) {
//
//                            onTaskItemFound(taskItem, pos, arguments.getInt("quantity"));
//
//
//                        }
//                    }, bundle, "Ввести вручную", "Ввод количества вручную");
//
//
//                }
//
            }
        });

        rvList = root.findViewById(R.id.rvTasks);
        rvList.setAdapter(dataAcceptanceProductsAdapter);

        actvShtrihCode = root.findViewById(R.id.actvShtrihCode);

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

        update();

        return root;
    }

    private void askToCloseContainer(AcceptanceProduct taskItem, Integer pos) {
        HttpClient httpClient = new HttpClient(getContext());

        Bundle bundle = new Bundle();
        bundle.putInt("pos", pos);
        httpClient.showQuestionYesNoCancel(getActivity(), new BundleMethodInterface() {
            @Override
            public void callMethod(Bundle arguments) {

                finishContainer(taskItem, pos, 1);

            }
        }, bundle, "Завершить контейнер ?", "Ввод вручную");
    }

    private void finishContainer(AcceptanceProduct taskItem, Integer pos, int i) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("ref", ref);
        httpClient.addParam("container", taskItem.container);
        httpClient.addParam("shtrih_code", taskItem.shtrih_codes.get(0).toString());
        httpClient.addParam("quantity", taskItem.quantity);
        httpClient.addParam("serialNumbers", taskItem.serialNumbers.toString().replace('[', ' ').replace(']', ' '));
        httpClient.addParam("partyNumber", taskItem.party_number);
        httpClient.addParam("partyDate", taskItem.party_date);
        httpClient.addParam("partyDateExpired", taskItem.party_date_expired);
        httpClient.addParam("partyDateProduced", taskItem.party_date_produced);

        httpClient.postProc("setCloseContainer", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                ((MainWareHouseActivity) getActivity()).containerPresent = false;

                update();

//                if (scanned > 0 && toScan == 0 && (status.equals("В отбор") || status.equals("Новый") || status.equals("В отборе"))){
//
//                    inputQuantitySetOrderScanned(shipmentOrder, httpClient);
//                }
//
            }
        });

    }

    private void inputParty(Boolean party_number_exist, Boolean party_date_exist, Boolean party_date_expired_exist, Boolean party_date_produced_exist, AcceptanceProduct taskItem, int j) {

        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Ввод партии");

        LayoutInflater inflater = getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_input_party, null);

        ((TextView) view.findViewById(R.id.tvCode)).setText("Партия номенклатуры");

        ((LinearLayout) view.findViewById(R.id.llPartyNumber)).setVisibility(party_number_exist ? View.VISIBLE : View.GONE);
        ((LinearLayout) view.findViewById(R.id.llPartyDate)).setVisibility(party_date_exist ? View.VISIBLE : View.GONE);
        ((LinearLayout) view.findViewById(R.id.llPartyDateExpired)).setVisibility(party_date_expired_exist ? View.VISIBLE : View.GONE);
        ((LinearLayout) view.findViewById(R.id.llPartyDateProduced)).setVisibility(party_date_produced_exist ? View.VISIBLE : View.GONE);

        tvPartyDate = view.findViewById(R.id.tvPartyDate);
        tvPartyDateExpired = view.findViewById(R.id.tvPartyDateExpired);
        tvPartyDateProduced = view.findViewById(R.id.tvPartyDateProduced);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setIcon(R.drawable.sklad96);

        view.findViewById(R.id.ibChoosePartyDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                doChooseDate(tvPartyDate);

            }
        });

        quantityDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String serialNumber = ((TextView) view.findViewById(R.id.tvPartyDate)).getText().toString();

                Date curDate = null;
                try {
                    curDate = new SimpleDateFormat("dd MMMM yyyy").parse(serialNumber);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (curDate != null) {

                    acceptanceProducts.get(0).party_date = new SimpleDateFormat("yyyyMMdd").format(curDate);
                    quantityDialog.cancel();

                    doScanShtrihCode(taskItem, j);

                }

            }
        });

        quantityDialog.show();

//        final Handler h2 = new Handler();
//        h2.postDelayed(setFocus3, 500);

    }

    private void doChooseDate(TextView tvPartyDate) {

        Calendar dateAndTime = Calendar.getInstance();

        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                dateAndTime.set(year, month, day);

                tvPartyDate.setText(new SimpleDateFormat("dd MMMM yyyy").format(dateAndTime.getTime()));


            }
        },
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
        ;
    }

    private void inputSerialNumber(AcceptanceProduct taskItem, int j) {

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

                        taskItem.serialNumbers.add(serialNumber);
                        quantityDialog.cancel();

                        doScanShtrihCode(taskItem, j);

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

                    acceptanceProducts.get(0).serialNumbers.add(serialNumber);
                    quantityDialog.cancel();

                    doScanShtrihCode(taskItem, j);

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

                doScanShtrihCode(taskItem, j);

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

    private void scanShtrihCode(String strCatName) {

        Boolean foundContainer = false;
        Boolean found = false;
        AcceptanceProduct curContainer = null;
        AcceptanceProduct curTask = null;

        int i;
        for (i = 0; i < acceptanceProducts.size() && !foundContainer; i++) {

            curContainer = acceptanceProducts.get(i);

            foundContainer = curContainer.isContainer; // && curContainer.container.equals(strCatName);

        }

        int j;
        for (j = 0; j < acceptanceProducts.size() && !found; j++) {

            curTask = acceptanceProducts.get(j);

            found = !curTask.isContainer && curTask.shtrih_codes.indexOf(strCatName) >= 0;

        }

        if(!foundContainer && !found){

            ((MainWareHouseActivity) getActivity()).containerPresent = true;

            curContainer = new AcceptanceProduct("", "", strCatName, "", "", 0, false, new ArrayList<>(), false, false, false, false);
            curContainer.isContainer = true;

            acceptanceProducts.add(0, curContainer);

            error.setVisibility(View.GONE);

        } else if(foundContainer && found) {



            curContainer.product = curTask.product;
            curContainer.product_part = curTask.product_part;
            curContainer.product_status = curTask.product_status;
            curContainer.serial_number_exist = curTask.serial_number_exist;
            curContainer.shtrih_codes = curTask.shtrih_codes;
            curContainer.party_number_exist = curTask.party_number_exist;
            curContainer.party_date_exist = curTask.party_date_exist;
            curContainer.party_date_expired_exist = curTask.party_date_expired_exist;
            curContainer.party_date_produced_exist = curTask.party_date_produced_exist;
            curContainer.quantity += 1;

            error.setVisibility(View.GONE);

            if ((curTask.party_number_exist && curTask.party_number == null)
                    || (curTask.party_date_exist && curTask.party_date == null)
                    || (curTask.party_date_expired_exist && curTask.party_date_expired == null)
                    || (curTask.party_date_produced_exist && curTask.party_date_produced == null)) {

                inputParty(curTask.party_number_exist, curTask.party_date_exist, curTask.party_date_expired_exist, curTask.party_date_produced_exist, curTask, j);

            } else if (curTask.serial_number_exist) {

                inputSerialNumber(curTask, j);

            } else {

                doScanShtrihCode(curTask, j);

            }

        } else if (!foundContainer && found){

            error.setText("Не найден контейнер");
            error.setVisibility(View.VISIBLE);

            soundPlayer.play();

        } else if (foundContainer && !found){

            error.setText("Не найден штрихкод " + strCatName);
            error.setVisibility(View.VISIBLE);

            soundPlayer.play();

        }

        dataAcceptanceProductsAdapter.notifyDataSetChanged();

//        if (found){
//
//            onTaskItemFound(curTask, i - 1, 1);
//
////            ArrayList<Good> curGoods = containers.get(i-1).goods;
////
////            tvContainer.setText("Контейнер: " + strCatName);
////
////            toScanContainer.clear();
////
////            for (int j = 0; j < curGoods.size(); j++) {
////
////                toScanContainer.add(curGoods.get(j).description + ", " + curGoods.get(j).quantity.toString());
////
////            }
////
////            adapterToScanContainer.notifyDataSetChanged();
////
////            rcContainers.setAdapter(adapterToScanContainer);
////
////            btnContainers.setVisibility(View.VISIBLE);
////
//        }
//        else {
////
////            if (!sendingInProgress) {
////
////                Integer index = toScan.indexOf(strCatName);
////
////                if (index < 0) {
////
//            error.setText("Не найден штрихкод " + strCatName);
//
//            soundPlayer.play();
//
////                    Boolean present = false;
////                    for (ScannedShtrihCode scannedShtrihCode : scanned) {
////                        present = scannedShtrihCode.shtrihCode.equals(strCatName);
////                        if (present) break;
////                    }
////
////                    if (present) {
////
////                        askForRepeatCode(strCatName);
////
////                    } else {
////
////                        askForNotFoundCode(strCatName);
////
////                    }
////
////
////                } else {
////                    error.setText("");
////
////                    toScan.remove(strCatName);
////
////                    scanned.add(0, new ScannedShtrihCode(strCatName, "", false));
////                    adapterScanned.notifyDataSetChanged();
////
////                    setShtrihs(strCatName);
////                }
////            }
//        }
//
//        setShtrihCode(strCatName);


    }

    private void doScanShtrihCode(AcceptanceProduct curTask, int j) {

        curTask.quantity -= 1;

        if (curTask.quantity == 0){


            acceptanceProducts.remove(j - 1);

            if (acceptanceProducts.size() == 1){

                askToCloseContainer(acceptanceProducts.get(0), 0);

            }

        }
    }

    private void update() {

        acceptanceProducts.clear();

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("ref", ref);

        httpClient.postProc("getAcceptanceProducts", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                JSONArray routeShtrihs = httpClient.getJsonArrayFromJsonObject(response, "AcceptanceProducts");

//                Integer iAdded = 0;

                Integer toScan = 0;
                Integer toAccept = 0;
                Integer scanned = 0;

                for (int j = 0; j < routeShtrihs.length(); j++) {

                    JSONObject accept_item = httpClient.getItemJSONArray(routeShtrihs, j);

                    AcceptanceProduct cdot = AcceptanceProduct.AcceptanceProductFromJSON(accept_item, httpClient);

                    toAccept += cdot.quantity;
//                    toScan += cdot.status.equals("Выполнена") ? 0 : cdot.quantity - cdot.scanned;
//                    scanned += cdot.status.equals("Выполнена") ? cdot.quantity : 0;

                    acceptanceProducts.add(cdot);

                }

                Integer procent = toAccept == 0 ? 0 : scanned * 100 / toAccept;

                progressTextView.setText(scanned.toString() + " из " + toAccept.toString() + ", " + procent.toString() + "%");
                progressTextView.setMax(toAccept);
                progressTextView.setProgress(scanned);

                dataAcceptanceProductsAdapter.notifyDataSetChanged();

                if (acceptanceProducts.size() == 0){

                    Bundle bundle = new Bundle();
                    httpClient.showQuestionYesNoCancel(getActivity(), new BundleMethodInterface() {
                        @Override
                        public void callMethod(Bundle arguments) {

                            CloseAcceptance();

                        }
                    }, bundle, "Завершить приемку ?", "Завершение");


                }

//                if (scanned > 0 && toScan == 0 && (status.equals("В отбор") || status.equals("Новый") || status.equals("В отборе"))){
//
//                    inputQuantitySetOrderScanned(shipmentOrder, httpClient);
//                }
//
            }
        });



    }

    private void CloseAcceptance(){

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("ref", ref);

        httpClient.postProc("setCloseAcceptance", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();

            }
        });


    }



}