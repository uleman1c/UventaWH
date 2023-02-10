package com.example.uventawh;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DocumentNumberAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;

    private final Context mContext;
    private List<String> mResults;

    private List<String> refDescs;

    private HttpClientSync client1;
    String url, refContractor;

    public DocumentNumberAutoCompleteAdapter(Context mContext, String url, HttpClientSync client1, String refContractor) {

        this.mContext = mContext;
        this.mResults = new ArrayList<>();
        this.refDescs = new ArrayList<>();
        this.client1 = client1;
        this.url = url;
        this.refContractor = refContractor;
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public String getItem(int index) {
        return mResults.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.simple_dropdown_item_1line, parent, false);
        }
        String refDesc = getItem(position);
        ((TextView) convertView.findViewById(R.id.text1)).setText(refDesc);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {

                    DocumentNumberAutoCompleteAdapter.GetTransportByFilterTask getTransportByFilterTask = new DocumentNumberAutoCompleteAdapter.GetTransportByFilterTask();
                    getTransportByFilterTask.execute(constraint.toString());

                    try {
                        getTransportByFilterTask.get(7, TimeUnit.SECONDS);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }

                    List<String> books = findResults(mContext, constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = books;
                    filterResults.count = books.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    mResults = (List<String>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};

        return filter;
    }

    private class GetTransportByFilterTask extends AsyncTask<String, Integer, Integer>{

        @Override
        protected Integer doInBackground(String... strings) {

            getTransportByFilter(mContext, strings[0]);

            return null;
        }
    }

    /**
     * Returns a search result for the given book title.
     */
    private List<String> findResults(Context mContext, String filter) {

        return refDescs;
    }

    private void getTransportByFilter(Context mContext, String filter){

        JSONArray params = new JSONArray();

        JSONObject requestParams = new JSONObject();

        try {
            requestParams.put("filter", filter);
            requestParams.put("organization", refContractor);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();
        try {
            request.put("request", "getDocumentNumberByFilter");
            request.put("parameters", requestParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put(request);

        client1.post(mContext, url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

//                linearLayout.setVisibility(View.GONE);
//                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {

//                linearLayout.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.GONE);

                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                JSONArray response = client1.getResponseJSONArray(responseBody);

                for (int i = 0; i < response.length(); i++){

                    JSONObject response_item = client1.getItemJSONArray(response, i);

                    if (client1.getStringFromJSON(response_item, "Name").equals("getDocumentNumberByFilter")) {

                        refDescs.clear();

                        JSONArray tasksJSON = new JSONArray();
                        try {
                            tasksJSON = (JSONArray) response_item.get("DocumentNumberByFilter");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int j = 0; j < tasksJSON.length(); j++) {

                            JSONObject task_item = client1.getItemJSONArray(tasksJSON, j);

                            String descTransport = client1.getStringFromJSON(task_item, "DocumentNumber");

//                            Integer intPicture = 0;
//                            if (type.equals("Приемка")) {
//                                intPicture = R.drawable.green_arrow;
//                            } else if (type.equals("ГрупповаяПриемка")) {
//                                intPicture = R.drawable.green_2_arrows;
//                            }



                            refDescs.add(descTransport);

                        }

                    }
                }


            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                Boolean debug = true;

                if (debug){

//                    Toast toast = Toast.makeText(TrasportListActivity.this, "StatusCode" + String.valueOf(statusCode), Toast.LENGTH_SHORT);
//                    toast.show();
                }

                Log.v("HTTPLOG", String.valueOf(statusCode));
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

//            @Override
//            public boolean getUseSynchronousMode() {
//                return false;
//            }
//
//            @Override
//            public void setUseSynchronousMode(boolean useSynchronousMode) {
//
//            }
        });



    }
}
