package com.example.uventawh.ui.home;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.uventawh.R;
import com.example.uventawh.RoutesListActivity;
import com.example.uventawh.TakeScreenShot;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private static final int
            REQUEST_CODE_ACCEPT = 1,
            REQUEST_CODE_SHIPMENT = 2,
            REQUEST_CODE_ADD_SKLAD = 3,
            REQUEST_CODE_SCAN_DOC = 4,
            REQUEST_CODE_TRANSPORT_LIST = 5,
            REQUEST_CODE_ROUTE_LIST = 6;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                TakeScreenShot.Do(getContext(), root, event.getX(), event.getY());

                return false;
            }
        });

        setButtons(root);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {



            }
        });
        return root;
    }

    private void setButtons(final View root) {
        root.findViewById(R.id.ibTransportArrival).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_truck_arrival);

            }
        });

        root.findViewById(R.id.ibAccept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                chooseContractor("receipt");

//                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.routesListFragment);
            }
        });

        root.findViewById(R.id.ibShipment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

//                chooseContractor("shipment");

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.deliveryListFragment);
            }
        });

        root.findViewById(R.id.btnTake).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

//                chooseContractor("shipment");

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.deliveryListFragment);
            }
        });

        root.findViewById(R.id.btnInputStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

//                chooseContractor("shipment");

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.inputStartFragment);
            }
        });

        root.findViewById(R.id.btnInvent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

//                chooseContractor("shipment");

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.inventarizationFragment);
            }
        });

        root.findViewById(R.id.btnAccept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

//                chooseContractor("shipment");

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.acceptanceListFragment);
            }
        });

        root.findViewById(R.id.btnRefill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

//                chooseContractor("shipment");

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.refillTasksListFragment);
            }
        });

        root.findViewById(R.id.btnLeftovers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.leftoversFragment);
            }
        });

        root.findViewById(R.id.ibScanRouteList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_scanRouteListFragment);
            }
        });

//        root.findViewById(R.id.ibShipment).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(getContext(), RoutesListActivity.class);
//
//                startActivityForResult(intent, REQUEST_CODE_SHIPMENT);
//
//
//            }
//        });

        root.findViewById(R.id.ibAddSklad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_placement_menu);

            }
        });

//        root.findViewById(R.id.ibScanDoc).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(getContext(), ScanDocActivity.class);
//
//                startActivityForResult(intent, REQUEST_CODE_SCAN_DOC);
//
//
//            }
//        });

        root.findViewById(R.id.ibCodeExchange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_code_exchange);

            }
        });

        root.findViewById(R.id.ibInvent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_invent);

            }
        });

        root.findViewById(R.id.ibInvent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_invent);

            }
        });

        root.findViewById(R.id.llMenu1).setVisibility(View.GONE);
    }

    private void chooseContractor(String mode) {

        Bundle bundle = new Bundle();
//        bundle.putString("refTransport", refTransport);
//        bundle.putString("descTransport", descTransport);
//        bundle.putString("refDriver", refDriver);
//        bundle.putString("descDriver", descDriver);
//        bundle.putString("refRoute", refRoute);
//        bundle.putString("refFilter", getString(R.string.emptyRef));
//        bundle.putString("descFilter", "");

        if (mode.equals("receipt")){

            bundle.putString("header", "Приемка");
            bundle.putBoolean("isReceipt", true);
            bundle.putInt("next", R.id.routesExtendedListFragment);

            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.contractorsListFragment, bundle);

        }
        else {

            bundle.putString("header", "Отгрузка");
            bundle.putBoolean("isReceipt", false);
            bundle.putInt("next", R.id.routesExtendedListFragment);

            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.contractorsListFragment, bundle);

//            Intent intent = new Intent(getContext(), ContractorsListActivity.class);
//
//            startActivityForResult(intent, REQUEST_CHOOSE_CONTRACTOR);

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CODE_ACCEPT) {

            }
            else if (requestCode == REQUEST_CODE_SHIPMENT) {

            }
            else if (requestCode == REQUEST_CODE_ADD_SKLAD) {

            }
            else if (requestCode == REQUEST_CODE_TRANSPORT_LIST) {

                Intent intent = new Intent(getContext(), RoutesListActivity.class);

                intent.putExtra("refTransport", data.getStringExtra("refTransport"));
                intent.putExtra("descTransport", data.getStringExtra("descTransport"));
                intent.putExtra("refDriver", data.getStringExtra("refDriver"));
                intent.putExtra("descDriver", data.getStringExtra("descDriver"));

                startActivityForResult(intent, REQUEST_CODE_ROUTE_LIST);

            }
        }
    }



}
