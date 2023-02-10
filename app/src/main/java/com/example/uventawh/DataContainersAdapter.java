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

class DataContainersAdapter extends RecyclerView.Adapter<DataContainersAdapter.StringViewHolder> {

    private LayoutInflater inflater;
    private List<Container> containers;
    private int resource;

    private OnStringClickListener onStringClickListener;

    class StringViewHolder extends RecyclerView.ViewHolder {

        private TextView valueView, tvQuantity, tvAddresses;
        private LinearLayout llMain;

        public StringViewHolder(View itemView) {
            super(itemView);

            valueView = itemView.findViewById(R.id.value);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvAddresses = itemView.findViewById(R.id.tvAddresses);
            llMain = itemView.findViewById(R.id.llMain);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Container str = containers.get(getLayoutPosition());
                    onStringClickListener.onStringClick(str);
                }
            });
        }

    }

    DataContainersAdapter(Context context, List<Container> containers) {
        this.containers = containers;
        this.inflater = LayoutInflater.from(context);
        this.resource = R.layout.container_list_item;
    }

    DataContainersAdapter(Context context, List<Container> containers, int resource) {
        this.containers = containers;
        this.inflater = LayoutInflater.from(context);
        this.resource = resource;
    }
    @Override
    public StringViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(resource, parent, false);
        return new StringViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StringViewHolder holder, int position) {
        Container str = containers.get(position);
        holder.valueView.setText(str.description);
        holder.tvQuantity.setText(String.valueOf(str.goods.size()));
        holder.tvAddresses.setText("Из " + str.senderDescription + " в " + str.receiverDescription);

        if (position % 2 == 0) {
            holder.llMain.setBackgroundColor(Color.parseColor("#F0F0F0"));

        }
        else{
            holder.llMain.setBackgroundColor(Color.parseColor("#FFFFFF"));

        }
    }
    @Override
    public int getItemCount() {
        return containers.size();
    }

    public interface OnStringClickListener {
        void onStringClick(Container taskItem);
    }

    public void setOnStringClickListener(OnStringClickListener onStringClickListener) {
        this.onStringClickListener = onStringClickListener;
    }

}
