package com.example.uventawh;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A fragment representing a list of Items.
 */
public class RefillTasksListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RefillTasksListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RefillTasksListFragment newInstance(int columnCount) {
        RefillTasksListFragment fragment = new RefillTasksListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    private InputMethodManager imm;

    DelayAutoCompleteTextView actvShtrihCode;

    private SoundPlayer soundPlayer;

    ProgressTextView progressTextView;

    Handler h;

    private ArrayList<RefillTask> refillTasks = new ArrayList<>();
    RecyclerView rvList;

    DataRefillTasksAdapter dataRefillTasksAdapter;

    ProgressBar progressBar;

    private boolean shtrihCodeKeyboard = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_refill_tasks_list_list, container, false);

        progressBar = root.findViewById(R.id.progressBar);

        progressTextView = root.findViewById(R.id.scannedText);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        soundPlayer = new SoundPlayer(getContext(), R.raw.hrn05);
        getActivity().setVolumeControlStream(soundPlayer.streamType);

        h = new Handler();

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

        dataRefillTasksAdapter = new DataRefillTasksAdapter(getContext(), refillTasks);
        dataRefillTasksAdapter.setOnTaskItemClickListener(new DataRefillTasksAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(RefillTask taskItem, Integer pos, View itemView) {

                if (taskItem.level <= 2 && (taskItem.status.equals("К выполнению") || taskItem.status.equals("В работе"))) {

                    HttpClient httpClient = new HttpClient(getContext());

                    Bundle bundle = new Bundle();
                    bundle.putInt("pos", pos);
                    httpClient.showQuestionYesNoCancel(getActivity(), new BundleMethodInterface() {
                        @Override
                        public void callMethod(Bundle arguments) {

                            onTaskItemFound(taskItem, pos);


                        }
                    }, bundle, "Ввести вручную ?", "Ввод вручную");


                }

            }
        });

        rvList = root.findViewById(R.id.list);
        rvList.setAdapter(dataRefillTasksAdapter);

        getShtrihs();



        return root;
    }

    protected void getShtrihs(){

        refillTasks.clear();

        getTasks();

    }

    protected void getTasks() {

        final HttpClient httpClient = new HttpClient(getContext());

        httpClient.postProc("getRefillTasks", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                JSONArray routeShtrihs = httpClient.getJsonArrayFromJsonObject(response, "RefillTasks");

                Integer iAdded = 0;

                Integer toScan = 0;
                Integer toAccept = 0;
                Integer scanned = 0;

                for (int j = 0; j < routeShtrihs.length(); j++) {

                    JSONObject accept_item = httpClient.getItemJSONArray(routeShtrihs, j);

                    RefillTask cdot = RefillTask.RefillTaskFromJSON(accept_item, httpClient);

                    toAccept += cdot.quantity;
                    toScan += cdot.status.equals("Выполнена") ? 0 : cdot.quantity - cdot.scanned;
                    scanned += cdot.status.equals("Выполнена") ? cdot.quantity : 0;

                    refillTasks.add(cdot);

                }

                Integer procent = scanned * 100 / toAccept;

//                progressTextView.setText(scanned.toString() + " из " + toAccept.toString() + ", " + procent.toString() + "%");
//                progressTextView.setMax(toAccept);
//                progressTextView.setProgress(scanned);

                dataRefillTasksAdapter.notifyDataSetChanged();

//                if (toScan == 0 && (status.equals("В отбор") || status.equals("В отборе"))){
//
//                    inputQuantitySetOrderScanned(shipmentOrder, httpClient);
//                }

            }
        });

    }



    private void scanShtrihCode(String strCatName) {

        Boolean foundInProcess = false;
        RefillTask curTask = null;

        for (int i = 0; i < refillTasks.size() && !foundInProcess; i++) {

            curTask = refillTasks.get(i);

            foundInProcess = curTask.status.equals("В работе") && curTask.level == 2;
        }

        if (foundInProcess){

            setPutContainer(curTask.ref, strCatName);

        } else {

            Boolean found = false;

            int i;
            for (i = 0; i < refillTasks.size() && !found; i++) {

                curTask = refillTasks.get(i);

                found = curTask.level == 0
                        && (curTask.status.equals("К выполнению") || curTask.status.equals("В работе")) && curTask.cell_shtrih_code.equals(strCatName)
                        || curTask.level == 1 && curTask.container_shtrih_code.equals(strCatName)
                        || curTask.level == 2 && curTask.shtrih_codes.indexOf(strCatName) >= 0;

            }

            if (found) {

                onTaskItemFound(curTask, i - 1);

            } else {

                soundPlayer.play();

            }

//        setShtrihCode(strCatName);

        }
    }

    private void onTaskItemFound(RefillTask taskItem, Integer pos) {

        if (taskItem.level == 0) {

            addLevel(taskItem, pos);

        } else if (taskItem.level == 1){

            if (taskItem.type.contains("Контейнер")) {

                if (taskItem.status.equals("К выполнению")) {
                    setTakeContainer(taskItem.ref);
                } else if (taskItem.status.equals("В работе")) {

                    addLevel(taskItem, pos);

                }


            } else {

                addLevel(taskItem, pos);
            }

        } else if (taskItem.level == 2){

            taskItem.scanned = taskItem.scanned + 1;

//            if (taskItem.serialNumberExist) {
//
//                inputSerialNumber(taskItem);
//
//            } else {
//
//                setTaskExecuted(taskItem);
//
//            }

        }


        dataRefillTasksAdapter.notifyDataSetChanged();
    }

    private void setTakeContainer(String ref) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refTask", ref);

        httpClient.postProc("setTakeContainer", new HttpRequestInterface() {
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

    private void setPutContainer(String ref, String cell_shtrihCode) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refTask", ref);
        httpClient.addParam("cell_shtrihCode", cell_shtrihCode);

        httpClient.postProc("setPutContainer", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")) {

                    getShtrihs();

                }
                else {
                    Toast.makeText(getContext(), httpClient.getStringFromJSON(response, "Message"), Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    private void addLevel(RefillTask taskItem, Integer pos) {
        Integer curPos = pos;

        if (taskItem.level == 0) {

            Collections.rotate(refillTasks, refillTasks.size() - pos);

            curPos = 0;
        }

        taskItem.childExist = true;

        taskItem.level = taskItem.level + 1;

        rvList.getLayoutManager().scrollToPosition(curPos);
    }


}