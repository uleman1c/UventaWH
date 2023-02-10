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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class DataRoutesAdapter extends RecyclerView.Adapter<DataRoutesAdapter.StringViewHolder> {

    private LayoutInflater inflater;
    private List<Route> routes;
    private String filter;

    private OnStringClickListener onStringClickListener;

    class StringViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDateShipment, tvTimeShipment, tvDriver, tvTransport, tvContractors, tvQuantityShipment, tvQuantityReceipt;
        private ConstraintLayout clToShipment, clToReceipt;
        private LinearLayout llMain;

        public StringViewHolder(final View itemView) {
            super(itemView);

            tvDateShipment = itemView.findViewById(R.id.tvDateShipment);
            tvTimeShipment = itemView.findViewById(R.id.tvTimeShipment);
            tvDriver = itemView.findViewById(R.id.tvDriver);
            tvTransport = itemView.findViewById(R.id.tvTransport);
            llMain = itemView.findViewById(R.id.llMain);
            tvContractors = itemView.findViewById(R.id.tvContractors);
            tvQuantityShipment = itemView.findViewById(R.id.tvQuantityShipment);
            tvQuantityReceipt = itemView.findViewById(R.id.tvQuantityReceipt);
            clToShipment = itemView.findViewById(R.id.clToShipment);
            clToReceipt = itemView.findViewById(R.id.clToReceipt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Route str = routes.get(getLayoutPosition());
                    onStringClickListener.onStringClick(str, itemView);
                }
            });
        }

    }

    DataRoutesAdapter(Context context, List<Route> routes) {
        this.routes = routes;
        this.inflater = LayoutInflater.from(context);
        this.filter = "";
    }
    @Override
    public StringViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.routes_list_item, parent, false);
        return new StringViewHolder(view);
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

    @Override
    public void onBindViewHolder(@NonNull StringViewHolder holder, int position) {

        Route str = routes.get(position);

        holder.tvDateShipment.setText(str.dateShipment.isEmpty() ? "" : StrDateTime.strToDate(str.dateShipment));
        holder.tvTimeShipment.setText(str.dateShipment.isEmpty() ? "" : StrDateTime.strToTime(str.dateShipment));

        holder.tvDriver.setText(getMarked(str.driver));
        holder.tvTransport.setText((getMarked(str.transport)));

        String contractorsText = "";
        Integer toShipment = 0;
        Integer toReceipt = 0;

        for (ContractorRoute contractorRoute:str.contractorRoutes) {

            contractorsText = contractorsText + (contractorsText.equals("") ? "" : ", ") + contractorRoute.contractor.description;

            toShipment = toShipment + contractorRoute.toShipment;
            toReceipt = toReceipt + contractorRoute.toReceipt;

        }
        holder.tvContractors.setText(getMarked(contractorsText));

        holder.tvQuantityShipment.setText(toShipment.toString() + " мест");
        holder.tvQuantityReceipt.setText(toReceipt.toString() + " мест");

        if (toShipment == 0){
            holder.clToShipment.setVisibility(View.GONE);
        }

        if (toReceipt == 0){
            holder.clToReceipt.setVisibility(View.GONE);
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
        return routes.size();
    }

    public interface OnStringClickListener {
        void onStringClick(Route taskItem, View itemView);
    }

    public void setOnStringClickListener(OnStringClickListener onStringClickListener) {
        this.onStringClickListener = onStringClickListener;
    }

}
