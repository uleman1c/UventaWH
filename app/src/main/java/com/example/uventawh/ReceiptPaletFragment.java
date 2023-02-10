package com.example.uventawh;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiptPaletFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiptPaletFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReceiptPaletFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReceiptPaletFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReceiptPaletFragment newInstance(String param1, String param2) {
        ReceiptPaletFragment fragment = new ReceiptPaletFragment();
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

    private String refRoute, refTransport, refDriver, descTransport, descDriver, number, date, refContractor, descContractor;
    private ProgressBar progressBar;
    private DelayAutoCompleteTextView actvShtrihCode;
    private InputMethodManager imm;
    private boolean shtrihCodeKeyboard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_receipt_palet, container, false);

        Bundle bundle = getArguments();

        refRoute = bundle.getString("ref");
        number = bundle.getString("number");
        date = bundle.getString("date");
        refContractor = bundle.getString("refContractor");
        descContractor = bundle.getString("descContractor");
        refTransport = bundle.getString("refTransport");
        refDriver = bundle.getString("refDriver");
        descTransport = bundle.getString("descTransport");
        descDriver = bundle.getString("descDriver");

        ((TextView) root.findViewById(R.id.tvHeader)).setText("Рейс №" + number
                + " от " + StrDateTime.strToDate(date) + ", "
                + descDriver + ", "
                + descTransport + ", " + descContractor);

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        actvShtrihCode = root.findViewById(R.id.actvShtrihCode);

        actvShtrihCode.requestFocus();

        shtrihCodeKeyboard = false;
        
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

        actvShtrihCode.setOnKeyListener(new View.OnKeyListener()
                                        {
                                            public boolean onKey(View v, int keyCode, KeyEvent event)
                                            {
                                                if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                                        (keyCode == KeyEvent.KEYCODE_ENTER))
                                                {

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

                if (shtrihCodeKeyboard){

                    imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                } else {

                    imm.showSoftInput(actvShtrihCode, 0, null);
                }

                shtrihCodeKeyboard = !shtrihCodeKeyboard;


            }
        });

        return root;
    }

    private void scanShtrihCode(final String strCatName) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("code", strCatName);
        httpClient.postForResult("getContainer", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {
                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                if(httpClient.getBooleanFromJSON(response, "Success")){

                    Bundle bundle = new Bundle();
                    bundle.putString("refRoute", refRoute);
                    bundle.putString("number", number);
                    bundle.putString("date", date);
                    bundle.putString("refContractor", refContractor);
                    bundle.putString("descContractor", descContractor);
                    bundle.putString("refTransport", refTransport);
                    bundle.putString("refDriver", refDriver);
                    bundle.putString("descTransport", descTransport);
                    bundle.putString("descDriver", descDriver);
                    bundle.putString("ref", httpClient.getStringFromJSON(response, "Ref"));
                    bundle.putString("code", strCatName);

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.receiptPaletGoodsFragment, bundle);



                };


            }
        });

    }
}