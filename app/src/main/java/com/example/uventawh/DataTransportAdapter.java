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

public class DataTransportAdapter extends RecyclerView.Adapter<DataTransportAdapter.ItemViewHolder> {

        private LayoutInflater inflater;
        private List<TransportItem> items;
        private DataTransportAdapter.OnItemClickListener onItemClickListener;

        class ItemViewHolder extends RecyclerView.ViewHolder {

            private ImageView imageView;
            private TextView tvTransport, tvDriver, tvDocument, tvQuantity, tvPersent;
            private LinearLayout llMain;

            public ItemViewHolder(View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.image);
                tvTransport = itemView.findViewById(R.id.tvTransport);
                tvDriver = itemView.findViewById(R.id.tvDriver);
                tvDocument = itemView.findViewById(R.id.tvDocument);
                llMain = itemView.findViewById(R.id.llMain);
                tvQuantity = itemView.findViewById(R.id.tvQuantity);
                tvPersent = itemView.findViewById(R.id.tvPersent);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TransportItem taskItem = items.get(getLayoutPosition());
                        onItemClickListener.onItemClick(taskItem);
                    }
                });
            }

        }

        DataTransportAdapter(Context context, List<TransportItem> items) {
            this.items = items;
            this.inflater = LayoutInflater.from(context);
        }

        public interface OnItemClickListener {
            void onItemClick(TransportItem taskItem);
        }

        public void setOnItemClickListener(DataTransportAdapter.OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public DataTransportAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.transport_list_item, parent, false);
            return new DataTransportAdapter.ItemViewHolder(view);
        }

    @Override
        public void onBindViewHolder(@NonNull DataTransportAdapter.ItemViewHolder holder, int position) {
            TransportItem item = items.get(position);
//            holder.imageView.setImageResource(item.image);

        holder.tvTransport.setText(StrDateTime.descTransportToString(item.descTransport));
        holder.tvDriver.setText(item.descDriver);
        holder.tvDocument.setText(StrDateTime.strToDate(item.date) + " " + StrDateTime.strToTime(item.date));

//            Integer padeDigit = phone.quantity % 100 < 10 ? phone.quantity % 100 : (phone.quantity % 100 > 19 ? phone.quantity % 10 : 9);
//
//            holder.tvQuantity.setText(phone.quantity == 0 ? "" : phone.quantity + " мест" + (padeDigit == 1 ? "о" : (padeDigit > 1 && padeDigit < 5 ? "а" : "")));
//
//            holder.tvPersent.setText(phone.quantity == 0 ? "" : Math.round(phone.accepted * 100 / phone.quantity) + "%");

        holder.tvQuantity.setText("");
        holder.tvPersent.setText("");

            if (position % 2 == 0) {
                holder.llMain.setBackgroundColor(Color.parseColor("#F0F0F0"));

            }
            else{
                holder.llMain.setBackgroundColor(Color.parseColor("#FFFFFF"));

            }
        }

        @Override
        public int getItemCount() {
            return items.size();
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