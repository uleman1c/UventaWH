package com.example.uventawh;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HttpAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;

    private final Context mContext;
    private List<RefDesc> mResults;

    private List<RefDesc> refDescs;

    public HttpClientSync httpClientSync;
    String method;

    private OnProcessResponseListener onProcessResponseListener;



    public HttpAutoCompleteAdapter(Context mContext, String method, OnProcessResponseListener onProcessResponseListener) {

        this.mContext = mContext;
        this.mResults = new ArrayList<>();
        this.refDescs = new ArrayList<>();
        this.httpClientSync = new HttpClientSync(mContext);
        this.method = method;
        this.onProcessResponseListener = onProcessResponseListener;
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public RefDesc getItem(int index) {
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
        RefDesc refDesc = getItem(position);
        ((TextView) convertView.findViewById(R.id.text1)).setText(refDesc.desc);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {

                    GetTransportByFilterTask getTransportByFilterTask = new GetTransportByFilterTask();
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

                    List<RefDesc> books = findResults(mContext, constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = books;
                    filterResults.count = books.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    mResults = (List<RefDesc>) results.values;
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

            getTransportByFilter(strings[0]);

            return null;
        }
    }

    /**
     * Returns a search result for the given book title.
     */
    private List<RefDesc> findResults(Context mContext, String filter) {

        return refDescs;
    }

    public interface OnProcessResponseListener {
        void OnProcessResponse(HttpClientSync httpClientSync, List<RefDesc> refDescs, JSONObject response);
    }

    private void getTransportByFilter(String filter){

        httpClientSync.addParam("filter", filter);

        httpClientSync.postForResult(method, new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                onProcessResponseListener.OnProcessResponse(httpClientSync, refDescs, response);


            }
        });


    }
}