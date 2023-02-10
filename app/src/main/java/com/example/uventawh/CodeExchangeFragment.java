package com.example.uventawh;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CodeExchangeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CodeExchangeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CodeExchangeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CodeExchangeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CodeExchangeFragment newInstance(String param1, String param2) {
        CodeExchangeFragment fragment = new CodeExchangeFragment();
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

    Handler h;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_code_exchange, container, false);

//        root.findViewById(R.id.btnChooseContractor).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                chooseContractor();
//
//            }
//        });

        h = new Handler();

        Thread t = new Thread(new Runnable() {
            public void run() {

                h.post(new Runnable() {
                    @Override
                    public void run() {

                        chooseContractor();

                    }
                });
            }
        });
        t.start();

        return root;
    }

    private void chooseContractor() {
        Bundle bundle = new Bundle();
        bundle.putInt("next", R.id.codeExchangePageTwoFragment);

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.contractorsListFragment, bundle);
    }


}