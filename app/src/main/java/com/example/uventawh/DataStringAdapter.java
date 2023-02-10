package com.example.uventawh;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class DataStringAdapter extends RecyclerView.Adapter<DataStringAdapter.StringViewHolder> {

    private LayoutInflater inflater;
    private List<String> strings;

    private OnStringClickListener onStringClickListener;

    class StringViewHolder extends RecyclerView.ViewHolder {

        private TextView valueView;
        private LinearLayout llMain;

        public StringViewHolder(final View itemView) {
            super(itemView);

            valueView = itemView.findViewById(R.id.value);
            llMain = itemView.findViewById(R.id.llMain);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = strings.get(getLayoutPosition());
                    onStringClickListener.onStringClick(str, itemView);
                }
            });
        }

    }

    DataStringAdapter(Context context, List<String> strings) {
        this.strings = strings;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public StringViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.string_list_item, parent, false);
        return new StringViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StringViewHolder holder, int position) {
        String str = strings.get(position);
        holder.valueView.setText(str);

        if (position % 2 == 0) {
            holder.llMain.setBackgroundColor(Color.parseColor("#F0F0F0"));

        }
        else{
            holder.llMain.setBackgroundColor(Color.parseColor("#FFFFFF"));

        }



    }
    @Override
    public int getItemCount() {
        return strings.size();
    }

    public interface OnStringClickListener {
        void onStringClick(String taskItem, View itemView);
    }

    public void setOnStringClickListener(OnStringClickListener onStringClickListener) {
        this.onStringClickListener = onStringClickListener;
    }

}
