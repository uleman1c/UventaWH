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

public class DataInputStartAdapter extends RecyclerView.Adapter<DataInputStartAdapter.TaskItemViewHolder> {

    private LayoutInflater inflater;
    private List<InputStartItem> phones;
    private OnTaskItemClickListener onTaskItemClickListener;
    private String filter;

    class TaskItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView nameView;
        private TextView tv1, tv2, tv3;
        private TextView tvPersent, tvStatus;
        private LinearLayout llMain;

        public TaskItemViewHolder(final View itemView) {
            super(itemView);

            tv1 = itemView.findViewById(R.id.tv1);
            tv2 = itemView.findViewById(R.id.tv2);
            tv3 = itemView.findViewById(R.id.tv3);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputStartItem taskItem = phones.get(getLayoutPosition());
                    onTaskItemClickListener.onTaskItemClick(taskItem, itemView);
                }
            });
        }

    }




    DataInputStartAdapter(Context context, List<InputStartItem> phones) {
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
        void onTaskItemClick(InputStartItem taskItem, View itemView);
    }

    public void setOnTaskItemClickListener(OnTaskItemClickListener onTaskItemClickListener) {
        this.onTaskItemClickListener = onTaskItemClickListener;
    }

    @Override
    public TaskItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_input_start_item, parent, false);
        return new TaskItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskItemViewHolder holder, int position) {
        InputStartItem phone = phones.get(position);
        //holder.imageView.setImageResource(phone.image);
        holder.tv1.setText(getMarked("Ввод остатков от " + phone.date));
        holder.tv2.setText(getMarked("Контейнер: " + phone.container));
        holder.tv3.setText(getMarked(phone.product + ", " + phone.quantity + " шт"));

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