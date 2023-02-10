package com.example.uventawh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CellFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CellFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CellFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CellFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CellFragment newInstance(String param1, String param2) {
        CellFragment fragment = new CellFragment();
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

    private TextView tvHeader;
    private String shipmentOrder, cell, cell_shtrih_code;
    private ProgressBar progressBar;
    private RecyclerView rvContainers;


    private List<DeliveryOrderContainer> deliveryOrderContainers = new ArrayList<>();
    private DataDeliveryOrdersContainersAdapter dataDeliveryOrdersContainersAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_cell, container, false);

        Bundle bundle = getArguments();
        shipmentOrder = bundle.getString("shipmentOrder");
        cell = bundle.getString("cell");
        cell_shtrih_code = bundle.getString("cell_shtrih_code");
        tvHeader = root.findViewById(R.id.tvHeader);
        tvHeader.setText(cell);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("Ячейка: " + cell);

        progressBar = root.findViewById(R.id.progressBar);

        dataDeliveryOrdersContainersAdapter = new DataDeliveryOrdersContainersAdapter(getContext(), deliveryOrderContainers);
        dataDeliveryOrdersContainersAdapter.setOnTaskItemClickListener(new DataDeliveryOrdersContainersAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(DeliveryOrderContainer taskItem, View itemView) {
                
                openContainer(shipmentOrder, cell_shtrih_code, taskItem.container_shtrih_code);
                
            }
        });
        rvContainers = root.findViewById(R.id.rvContainers);
        rvContainers.setAdapter(dataDeliveryOrdersContainersAdapter);

        update();

        return root;
    }

    private void openContainer(String shipmentOrder, String cell_shtrih_code, String container_shtrih_code) {

        Bundle bundle = new Bundle();
        bundle.putString("shipmentOrder", shipmentOrder);
        bundle.putString("cell_shtrih_code", cell_shtrih_code);
        bundle.putString("container_shtrih_code", container_shtrih_code);

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.containerFragment, bundle);

    }

    private void update() {

        deliveryOrderContainers.clear();

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("refDeliveryOrder", shipmentOrder);
        httpClient.addParam("cell_shtrih_code", cell_shtrih_code);

        httpClient.postProc("getDeliveryOrderCell", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                JSONArray routeShtrihs = httpClient.getJsonArrayFromJsonObject(response, "DeliveryOrderCell");

                for (int j = 0; j < routeShtrihs.length(); j++) {

                    JSONObject accept_item = httpClient.getItemJSONArray(routeShtrihs, j);

                    deliveryOrderContainers.add(DeliveryOrderContainer.DeliveryOrderContainerFromJSON(accept_item, httpClient));

                }

                dataDeliveryOrdersContainersAdapter.notifyDataSetChanged();

//                Integer scannedQ = scanned.size();
//                Integer toAcceptQ = toScan.size() + scannedQ;
//
//                Integer procent = toAcceptQ == 0 ? 100 : scannedQ * 100 / toAcceptQ;
//
//                progressTextView.setText(scannedQ.toString() + " из " + toAcceptQ.toString() + ", " + procent.toString() + "%");
//                progressTextView.setMax(toAcceptQ);
//                progressTextView.setProgress(scannedQ);
//
//                adapterToScan.notifyDataSetChanged();
//                adapterScanned.notifyDataSetChanged();
//
//                adapterContainers.notifyDataSetChanged();
//
//                llContainers.setVisibility(containers.size() == 0 ? View.GONE :View.VISIBLE);
//
//                if (shipmentOrders != null && shipmentOrders.size() > 0){
//
//                    Integer index = shipmentOrders.indexOf(shipmentOrder);
//                    if (index + 1 < shipmentOrders.size()) {
//
//                        getReceiptShtrihs(shipmentOrders.get(index + 1));
//
//                    }
//
//                }



            }
        });



    }
}