package com.example.uventawh;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class ScannedShtrihCodeAdapter extends RecyclerView.Adapter<ScannedShtrihCodeAdapter.StringViewHolder> {

    private LayoutInflater inflater;
    private List<ScannedShtrihCode> scannedShtrihCodes;

    private OnStringClickListener onStringClickListener;

    class StringViewHolder extends RecyclerView.ViewHolder {

        private TextView valueView, tvDate, tvTime;
        private ImageView ivImage;
        private LinearLayout llMain;

        public StringViewHolder(final View itemView) {
            super(itemView);

            valueView = itemView.findViewById(R.id.value);
            llMain = itemView.findViewById(R.id.llMain);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivImage = itemView.findViewById(R.id.ivImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ScannedShtrihCode str = scannedShtrihCodes.get(getLayoutPosition());
                    onStringClickListener.onStringClick(str, itemView);
                }
            });
        }

    }

    ScannedShtrihCodeAdapter(Context context, List<ScannedShtrihCode> strings) {
        this.scannedShtrihCodes = strings;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public StringViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.scanned_shtrihcode_list_item, parent, false);
        return new StringViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StringViewHolder holder, int position) {
        ScannedShtrihCode str = scannedShtrihCodes.get(position);
        holder.valueView.setText(str.shtrihCode + (str.quantity != null ? ", " + String.valueOf(str.quantity) : ""));
        holder.tvDate.setText(str.getDateStr());
        holder.tvTime.setText(str.getTimeStr());

        if (str.added){

            holder.ivImage.setImageResource(R.drawable.add);
        } else
        {
            holder.ivImage.setImageResource(R.drawable.scanned48);

        }

        if (position % 2 == 0) {
            holder.llMain.setBackgroundColor(Color.parseColor("#F0F0F0"));

        }
        else{
            holder.llMain.setBackgroundColor(Color.parseColor("#FFFFFF"));

        }



    }
    @Override
    public int getItemCount() {
        return scannedShtrihCodes.size();
    }

    public interface OnStringClickListener {
        void onStringClick(ScannedShtrihCode taskItem, View itemView);
    }

    public void setOnStringClickListener(OnStringClickListener onStringClickListener) {
        this.onStringClickListener = onStringClickListener;
    }

}
