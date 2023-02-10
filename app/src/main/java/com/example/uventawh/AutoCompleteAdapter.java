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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;

    private final Context mContext;
    private List<RefDesc> mResults;

    private List<RefDesc> refDescs;

    private HttpClientSync client1;
    private String methodName;

    public AutoCompleteAdapter(Context mContext, String methodName) {

        this.mContext = mContext;
        this.mResults = new ArrayList<>();
        this.refDescs = new ArrayList<>();
        this.client1 = new HttpClientSync(mContext);
        this.methodName = methodName;
    }

    public void addParam(String name, String value){
        client1.addParam(name, value);
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

                    AutoCompleteAdapter.GetTransportByFilterTask getTransportByFilterTask = new AutoCompleteAdapter.GetTransportByFilterTask();
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

    private void getTransportByFilter(String filter){

        client1.addParam("filter", filter);

        client1.postForResult(methodName, new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

            }

            @Override
            public void processResponse(JSONObject response) {

                refDescs.clear();

                JSONArray tasksJSON = client1.getJsonArrayFromJsonObject(response, methodName.substring(3));

                for (int j = 0; j < tasksJSON.length(); j++) {

                    JSONObject task_item = client1.getItemJSONArray(tasksJSON, j);

                    String refTransport = client1.getStringFromJSON(task_item, "RefAddress");
                    String descTransport = client1.getStringFromJSON(task_item, "DescAddress");

                    refDescs.add(new RefDesc(refTransport, descTransport));

                }


            }
        });

    }
}
