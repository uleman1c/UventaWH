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

public class DataDeliveryOrdersAdapter extends RecyclerView.Adapter<DataDeliveryOrdersAdapter.TaskItemViewHolder> {

    private LayoutInflater inflater;
    private List<ShipmentItem> phones;
    private OnTaskItemClickListener onTaskItemClickListener;
    private String filter;

    class TaskItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView nameView;
        private TextView companyView, tvFrom, tvTo, tvQuantity;
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
            tvStatus = itemView.findViewById(R.id.tvStatus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShipmentItem taskItem = phones.get(getLayoutPosition());
                    onTaskItemClickListener.onTaskItemClick(taskItem, itemView);
                }
            });
        }

    }




    DataDeliveryOrdersAdapter(Context context, List<ShipmentItem> phones) {
        this.phones = phones;
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

    public interface OnTaskItemClickListener {
        void onTaskItemClick(ShipmentItem taskItem, View itemView);
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
        //holder.imageView.setImageResource(phone.image);
        holder.companyView.setText(getMarked((phone.selfDelivery ? "Самовывоз, " : "")
                + (phone.schetNumber.isEmpty() ? "" : "(" + phone.schetNumber + "), ")
                + phone.numDocument + (phone.numDocument.isEmpty() ? "" : ", ") + "№" + phone.number + " от " + phone.date));
        holder.tvStatus.setText(phone.status);

        if (phone.status.equals("Отобран")){
            holder.tvStatus.setBackgroundColor(Color.parseColor("#3CB371"));
        }else if (phone.status.equals("В отборе") || phone.status.equals("В отбор")){
            holder.tvStatus.setBackgroundColor(Color.parseColor("#98FB98"));
        }else if (phone.status.equals("К планированию")){
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FAFAD2"));
        }else if (phone.status.equals("Спланировано частично")){
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FFDED2"));
        }else if (phone.status.equals("Готов к отгрузке")){
            holder.tvStatus.setBackgroundColor(Color.parseColor("#D8BFD8"));
        } else {
            holder.tvStatus.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        if (phone.sender.isEmpty() && phone.company.isEmpty()){

           holder.tvFrom.setVisibility(View.GONE);

        }
        else {
            holder.tvFrom.setText(getMarked("от " + (phone.sender.isEmpty() ? phone.company : phone.sender)));
        }

        if (phone.deliverer.isEmpty()){

            holder.tvTo.setVisibility(View.GONE);
        }
        else {
            holder.tvTo.setText(getMarked("до " + phone.deliverer));
        }

        Integer padeDigit = phone.quantity % 100 < 10 ? phone.quantity % 100 : (phone.quantity % 100 > 19 ? phone.quantity % 10 : 9);

//        holder.nameView.setText(phone.name + " №" + phone.number + " от " + phone.date);

        holder.tvQuantity.setText(phone.quantity == 0 ? "" : phone.quantity + " мест" + (padeDigit == 1 ? "о" : (padeDigit > 1 && padeDigit < 5 ? "а" : "")));

        holder.tvPersent.setText(phone.quantity == 0 ? "" : Math.round(phone.accepted * 100 / phone.quantity) + "%");

//        if (position % 2 == 0) {
//            holder.llMain.setBackgroundColor(Color.parseColor("#F0F0F0"));
//
//        }
//        else{
//            holder.llMain.setBackgroundColor(Color.parseColor("#FFFFFF"));
//
//        }
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