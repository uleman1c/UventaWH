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

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.TaskItemViewHolder> {

    private LayoutInflater inflater;
    private List<TaskItem> phones;
    private OnTaskItemClickListener onTaskItemClickListener;

    class TaskItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView nameView;
        private TextView companyView;
        private TextView tvPersent;
        private LinearLayout llMain;

        public TaskItemViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);
            nameView = itemView.findViewById(R.id.name);
            companyView = itemView.findViewById(R.id.company);
            tvPersent = itemView.findViewById(R.id.tvPersent);
            llMain = itemView.findViewById(R.id.llMain);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TaskItem taskItem = phones.get(getLayoutPosition());
                    onTaskItemClickListener.onTaskItemClick(taskItem);
                }
            });
        }

    }




    DataAdapter(Context context, List<TaskItem> phones) {
        this.phones = phones;
        this.inflater = LayoutInflater.from(context);
    }

    public interface OnTaskItemClickListener {
        void onTaskItemClick(TaskItem taskItem);
    }

    public void setOnTaskItemClickListener(OnTaskItemClickListener onTaskItemClickListener) {
        this.onTaskItemClickListener = onTaskItemClickListener;
    }

    @Override
    public TaskItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new TaskItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskItemViewHolder holder, int position) {
        TaskItem phone = phones.get(position);
        holder.imageView.setImageResource(phone.image);
        holder.companyView.setText(phone.company + (phone.sender.isEmpty() ? "" : ", ") + phone.sender);

        Integer padeDigit = phone.quantity < 10 ? phone.quantity : (phone.quantity > 19 ? phone.quantity % 10 : 9);

        holder.nameView.setText((phone.documentNumber.isEmpty() ? phone.name + " №" + phone.number : phone.documentNumber)
                + " от " + new StrDateTime().strToDate(phone.date)
                + (phone.quantity == 0 ? "" : ", " + phone.quantity + " мест" + (padeDigit == 1 ? "о" : (padeDigit > 1 && padeDigit < 5 ? "а" : "")))
                + (phone.driverFio.isEmpty() ? "" : ", водитель: " + phone.driverFio)
                + (phone.transport.isEmpty() ? "" : ", транспорт: " + phone.transport));

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