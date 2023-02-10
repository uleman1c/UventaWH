package com.example.uventawh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlacementMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlacementMenuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlacementMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlacementMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlacementMenuFragment newInstance(String param1, String param2) {
        PlacementMenuFragment fragment = new PlacementMenuFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_placement_menu, container, false);

        root.findViewById(R.id.btnPlacement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_placement);

            }
        });

        root.findViewById(R.id.btnPlacementContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("mode", "placementContainer");

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.placeToCellFragment, bundle);

            }
        });

        root.findViewById(R.id.btnWhatPlaced).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.whatPlacedFragment);

            }
        });

        root.findViewById(R.id.btnWherePlaced).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.wherePlacedFragment);

            }
        });

        root.findViewById(R.id.btnGetPlaced).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.getPlacedFragment);

            }
        });

        return root;
    }
}