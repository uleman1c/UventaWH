package com.example.uventawh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StringAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;

    private final Context mContext;
    private List<String> mResults;

    private List<String> strings;

    private HttpClientSync client1;
    String url, refContractor;

    public StringAutoCompleteAdapter(Context mContext, List<String> list) {

        this.mContext = mContext;
        this.mResults = new ArrayList<>();
        this.strings = list;
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

    /**
     * Returns a search result for the given book title.
     */
    private List<String> findResults(Context mContext, String filter) {

        List<String> fr = new ArrayList<>();

        for (String curStr: strings){

            if (curStr.contains(filter)){
                fr.add(curStr);
            }

        }

        return fr;

    }

}
