package com.example.uventawh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetNameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetNameFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SetNameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetNameFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetNameFragment newInstance(String param1, String param2) {
        SetNameFragment fragment = new SetNameFragment();
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

    Bundle bundle;
    DelayAutoCompleteTextView actvName;
    AddressAutoCompleteAdapter aucaAddress;

    String refContractor, shtrihCode, name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_set_name, container, false);

        DB db = new DB(getContext());
        String url = db.getRequestUserProg();

        bundle = getArguments();
        refContractor = bundle.getString("refContractor");
        shtrihCode = bundle.getString("shtrihCode");

        name = "";

//        description = bundle.getString("description");
//        refTransport = bundle.getString("refTransport");
//        descTransport = bundle.getString("descTransport");
//        refDriver = bundle.getString("refDriver");
//        descDriver = bundle.getString("descDriver");
//        back = bundle.getInt("back", 0);

        BoldStringBuilder builder = new BoldStringBuilder();

//        builder.erase();
//        builder.addBoldString("Контрагент: ");
//        builder.addString(bundle.getString("description") + "\n");
//        builder.addBoldString("Водитель: ");
//        builder.addString(bundle.getString("descDriver") + "\n");
//        builder.addBoldString("Транспорт: ");
//        builder.addString(bundle.getString("descTransport") + "\n");
//        builder.addBoldString("Номер накладной: " );
//        builder.addString(bundle.getString("docNumber"));
//
//        ((TextView)root.findViewById(R.id.tvName)).setText(builder.spannableStringBuilder, TextView.BufferType.SPANNABLE);

        actvName = root.findViewById(R.id.actvName);

        actvName.setThreshold(3);

        aucaAddress = new AddressAutoCompleteAdapter(getContext(), url, new HttpClientSync(getContext()), refContractor, "getNamesByFilter");

        actvName.setAdapter(aucaAddress);
        actvName.setLoadingIndicator((ProgressBar) root.findViewById(R.id.progress_bar));

        actvName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RefDesc refDesc = (RefDesc) adapterView.getItemAtPosition(position);

                name = refDesc.desc;
                actvName.setText(name);

//                ref_sender = refDesc.ref;
//                description_sender = refDesc.desc;
//
//                refFromDialog = true;
//
//                actvSender.setText(description_sender);
//
//                refFromDialog = false;
//
//                hideKeyboard();

            }
        });


        root.findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakeScreenShot.Do(getContext(), root, v);

                Boolean validated = true;

                if(name.isEmpty()){

                    validated = false;

                    Toast toast = Toast.makeText(getContext(), "Не заполнено наименование", Toast.LENGTH_SHORT);
                    toast.show();


                }

                if (validated){

                    setNameToGood();

                }

            }

        });




        return root;
    }

    private void setNameToGood() {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("organization", refContractor);
        httpClient.addParam("shtrihCode", shtrihCode);
        httpClient.addParam("name", name);

        httpClient.postForResult("setNameToGood", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                if (httpClient.getBooleanFromJSON(response, "Success")){

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();

                }

            }
        });




    }





}