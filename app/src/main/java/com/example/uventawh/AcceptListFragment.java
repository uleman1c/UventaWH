package com.example.uventawh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AcceptListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AcceptListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AcceptListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AcceptListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AcceptListFragment newInstance(String param1, String param2) {
        AcceptListFragment fragment = new AcceptListFragment();
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

    List<TaskItem> task_items = new ArrayList<>();

    DataAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    String refRoute, refContractor, mode, refTransport, refDriver, descTransport, descDriver, descContractor;
    Boolean inputQuantity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_accept_list, container, false);

        Bundle bundle = getArguments();
        refContractor = bundle.getString("ref");
        refTransport = bundle.getString("refTransport");
        descTransport = bundle.getString("descTransport");
        refDriver = bundle.getString("refDriver");
        descDriver = bundle.getString("descDriver");
        refRoute = bundle.getString("refRoute");

        descContractor = bundle.getString("description");
        inputQuantity = bundle.getBoolean("inputQuantity");

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        root.findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addAccept();

            }
        });

        adapter = new DataAdapter(getContext(), task_items);
        adapter.setOnTaskItemClickListener(new DataAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(TaskItem taskItem) {

                if (taskItem.name.equals("Приемка")) {

                    setRouteToReceipt(taskItem.ref);


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

        update();



        return root;
    }

    private void addAccept() {

        add();

    }

    protected void update(){

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refContractor", refContractor);

        httpClient.postForResult("getReceiptsWithoutRoutes", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                task_items.clear();

                JSONArray tasksJSON = httpClient.getJsonArrayFromJsonObject(response, "ReceiptsWithoutRoutes");

                for (int j = 0; j < tasksJSON.length(); j++) {

                    JSONObject task_item = httpClient.getItemJSONArray(tasksJSON, j);

                    String ref = httpClient.getStringFromJSON(task_item, "Ref");
                    String type = httpClient.getStringFromJSON(task_item, "Type");
                    String number = httpClient.getStringFromJSON(task_item, "Number");
                    String date = httpClient.getStringFromJSON(task_item, "Date");
                    String refContractor = httpClient.getStringFromJSON(task_item, "ContractorRef");
                    String company = httpClient.getStringFromJSON(task_item, "Contractor");
                    String refSender = httpClient.getStringFromJSON(task_item, "SenderRef");
                    String sender = httpClient.getStringFromJSON(task_item, "Sender");
                    Integer quantity = httpClient.getIntegerFromJSON(task_item, "Quantity");
                    Integer accepted = httpClient.getIntegerFromJSON(task_item, "Accepted");
                    String driverFio = httpClient.getStringFromJSON(task_item, "DriverFIO");
                    String driverDocument = httpClient.getStringFromJSON(task_item, "DriverDocument");
                    String transport = httpClient.getStringFromJSON(task_item, "Transport");
                    String documentNumber = httpClient.getStringFromJSON(task_item, "DocumentNumber");

                    Integer intPicture = 0;
                    if (type.equals("Приемка")) {
                        intPicture = R.drawable.green_arrow;
                    } else if (type.equals("ГрупповаяПриемка")) {
                        intPicture = R.drawable.green_2_arrows;
                    }
                    task_items.add(new TaskItem(ref, type, number, date, refContractor, company, refSender, sender, intPicture, quantity, accepted, driverFio, driverDocument, transport, documentNumber));

                }

                adapter.notifyDataSetChanged();

                if (task_items.size() == 0){
                    addAccept();
                }

            }
        });

    }

    protected void add(){

        Bundle bundle = new Bundle();
        bundle.putString("refTransport", refTransport);
        bundle.putString("descTransport", descTransport);
        bundle.putString("refDriver", refDriver);
        bundle.putString("descDriver", descDriver);
        bundle.putString("refRoute", refRoute);
        bundle.putString("refFilter", getString(R.string.emptyRef));
        bundle.putString("descFilter", "");
        bundle.putBoolean("inputQuantity", inputQuantity);
        bundle.putString("description", descContractor);
        bundle.putString("ref", refContractor);

//        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.docInputFragment, bundle);
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.docNumberFragment, bundle);
//        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.receiptPageOneFragment, bundle);

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

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack(R.id.editRouteFragment, false);

                }

            }
        });


    }



}