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

public class DataShipmentAdapter extends RecyclerView.Adapter<DataShipmentAdapter.TaskItemViewHolder> {

    private LayoutInflater inflater;
    private List<ShipmentItem> phones;
    private OnTaskItemClickListener onTaskItemClickListener;

    class TaskItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView nameView;
        private TextView companyView, tvFrom, tvTo, tvQuantity;
        private TextView tvPersent;
        private LinearLayout llMain;

        public TaskItemViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);
            nameView = itemView.findViewById(R.id.name);
            companyView = itemView.findViewById(R.id.company);
            tvPersent = itemView.findViewById(R.id.tvPersent);
            llMain = itemView.findViewById(R.id.llMain);
            tvFrom = itemView.findViewById(R.id.tvFrom);
            tvTo = itemView.findViewById(R.id.tvTo);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShipmentItem taskItem = phones.get(getLayoutPosition());
                    onTaskItemClickListener.onTaskItemClick(taskItem);
                }
            });
        }

    }




    DataShipmentAdapter(Context context, List<ShipmentItem> phones) {
        this.phones = phones;
        this.inflater = LayoutInflater.from(context);
    }

    public interface OnTaskItemClickListener {
        void onTaskItemClick(ShipmentItem taskItem);
    }

    public void setOnTaskItemClickListener(OnTaskItemClickListener onTaskItemClickListener) {
        this.onTaskItemClickListener = onTaskItemClickListener;
    }

    @Override
    public TaskItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_shipment_item, parent, false);
        return new TaskItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskItemViewHolder holder, int position) {
        ShipmentItem phone = phones.get(position);
        holder.imageView.setImageResource(phone.image);
        holder.companyView.setText(phone.company + " " + phone.name + " №" + phone.number + " от " + phone.date);

        if (phone.sender.isEmpty()){

            holder.tvFrom.setVisibility(View.GONE);
        }
        else {
            holder.tvFrom.setText("от " + phone.sender);
        }

        if (phone.deliverer.isEmpty()){

            holder.tvTo.setVisibility(View.GONE);
        }
        else {
            holder.tvTo.setText("до " + phone.deliverer);
        }

        Integer padeDigit = phone.quantity % 100 < 10 ? phone.quantity % 100 : (phone.quantity % 100 > 19 ? phone.quantity % 10 : 9);

//        holder.nameView.setText(phone.name + " №" + phone.number + " от " + phone.date);

        holder.tvQuantity.setText(phone.quantity == 0 ? "" : phone.quantity + " мест" + (padeDigit == 1 ? "о" : (padeDigit > 1 && padeDigit < 5 ? "а" : "")));

        holder.tvPersent.setText(phone.quantity == 0 ? "" : Math.round(phone.accepted * 100 / phone.quantity) + "%");

        if (position % 2 == 0) {
            holder.llMain.setBackgroundColor(Color.parseColor("#F0F0F0"));

        }
        else{
            holder.llMain.setBackgroundColor(Color.parseColor("#FFFFFF"));

        }
    }

    @Override
    public int getItemCount() {
        return phones.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView nameView, companyView;
        ViewHolder(View view){
            super(view);
            imageView = (ImageView)view.findViewById(R.id.image);
            nameView = (TextView) view.findViewById(R.id.name);
            companyView = (TextView) view.findViewById(R.id.company);
        }
    }
}