package com.example.uventawh;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DataAcceptanceProductsAdapter extends RecyclerView.Adapter<DataAcceptanceProductsAdapter.TaskItemViewHolder> {

    private LayoutInflater inflater;
    private List<AcceptanceProduct> deliveryOrderTasks;
    private OnTaskItemClickListener onTaskItemClickListener;
    private OnTaskItemLongClickListener onTaskItemLongClickListener;
    private String filter;

    class TaskItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView nameView;
        private TextView companyView, tvFrom, tvTo, tvQuantity, tvProduct, tvShtrihcode;
        private TextView tvPersent, tvStatus;
        private LinearLayout llMain;

        public TaskItemViewHolder(final View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);
            nameView = itemView.findViewById(R.id.name);
            companyView = itemView.findViewById(R.id.company);
            tvPersent = itemView.findViewById(R.id.tvPersent);
            llMain = itemView.findViewById(R.id.llMain);
            tvFrom = itemView.findViewById(R.id.tvFrom);
            tvTo = itemView.findViewById(R.id.tvTo);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvProduct = itemView.findViewById(R.id.tvProduct);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvShtrihcode = itemView.findViewById(R.id.tvShtrihcode);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AcceptanceProduct taskItem = deliveryOrderTasks.get(getLayoutPosition());
                    onTaskItemClickListener.onTaskItemClick(taskItem, getLayoutPosition(), itemView);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AcceptanceProduct taskItem = deliveryOrderTasks.get(getLayoutPosition());
                    onTaskItemLongClickListener.onTaskItemLongClick(taskItem, getLayoutPosition(), itemView);

                    return false;
                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {

        return deliveryOrderTasks.get(position).isContainer ? 1 : 0;
    }

    DataAcceptanceProductsAdapter(Context context, List<AcceptanceProduct> phones) {
        this.deliveryOrderTasks = phones;
        this.inflater = LayoutInflater.from(context);
        this.filter = "";
    }

    public void setFilter(String filter){
        this.filter = filter;
    }

    private Spannable getMarked(String notMarked){

        Spannable text = new SpannableString(notMarked);

        if (!filter.isEmpty()){

            Integer start = notMarked.toLowerCase().indexOf(filter.toLowerCase());

            if (start >= 0){

                text.setSpan(new ForegroundColorSpan(Color.RED), start, start + filter.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }

        return text;

    }

    public interface OnTaskItemLongClickListener {
        void onTaskItemLongClick(AcceptanceProduct taskItem, Integer pos, View itemView);
    }

    public interface OnTaskItemClickListener {
        void onTaskItemClick(AcceptanceProduct taskItem, Integer pos, View itemView);
    }

    public void setOnTaskItemClickListener(OnTaskItemClickListener onTaskItemClickListener) {
        this.onTaskItemClickListener = onTaskItemClickListener;
    }

    public void setOnTaskItemLongClickListener(OnTaskItemLongClickListener onTaskItemLongClickListener) {
        this.onTaskItemLongClickListener = onTaskItemLongClickListener;
    }

    @Override
    public TaskItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(viewType == 0 ? R.layout.acceptance_product_list_item : R.layout.acceptance_product_in_container_list_item, parent, false);
        return new TaskItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskItemViewHolder holder, int position) {
        AcceptanceProduct phone = deliveryOrderTasks.get(position);
//        holder.imageView.setImageResource(phone.image);
        if(holder.getItemViewType() == 0) {
            holder.tvTo.setText(phone.product);

            holder.tvQuantity.setText(phone.quantity.toString() + " шт");

            String scs = "";
            for (int i = 0; i < phone.shtrih_codes.size(); i++) {
                scs = scs + (scs.isEmpty() ? "" : ", ") + phone.shtrih_codes.get(i);
            }

            holder.tvShtrihcode.setText(scs);

        } else if(holder.getItemViewType() == 1) {
            holder.tvFrom.setText("Контейнер: " + phone.container);
            holder.tvTo.setText(phone.product);

            holder.tvQuantity.setText(phone.quantity.toString() + " шт");

            String scs = "";
            for (int i = 0; i < phone.shtrih_codes.size(); i++) {
                scs = scs + (scs.isEmpty() ? "" : ", ") + phone.shtrih_codes.get(i);
            }

            holder.tvShtrihcode.setText(scs);
        }


        Integer padeDigit = phone.quantity % 100 < 10 ? phone.quantity % 100 : (phone.quantity % 100 > 19 ? phone.quantity % 10 : 9);

//        holder.nameView.setText(phone.name + " №" + phone.number + " от " + phone.date);

//        holder.tvQuantity.setText(phone.quantity == 0 ? "" : phone.quantity + " мест" + (padeDigit == 1 ? "о" : (padeDigit > 1 && padeDigit < 5 ? "а" : "")));

//        holder.tvPersent.setText(phone.quantity == 0 ? "" : Math.round(phone.accepted * 100 / phone.quantity) + "%");

        if (phone.isContainer){
                                                //            phone.serialNumberExist ? "#D81B60" :
            holder.llMain.setBackgroundColor(Color.parseColor("#00FF00"));
        }
//        else {
//            if (position % 2 == 0) {
//                holder.llMain.setBackgroundColor(Color.parseColor("#F0F0F0"));
//
//            } else {
//                holder.llMain.setBackgroundColor(Color.parseColor("#FFFFFF"));
//
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return deliveryOrderTasks.size();
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