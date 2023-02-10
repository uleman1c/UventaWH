package com.example.uventawh;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DataArrayAdapter<T> extends RecyclerView.Adapter<DataArrayAdapter<T>.StringViewHolder> {

    private LayoutInflater inflater;
    private List<T> routes;
    private String fieldFoto;

    private String[] fields;

    private int[] ids, fotos;

    private int item, idLlMain, idIvFoto = 0;

    private OnStringClickListener onStringClickListener;

    class StringViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llMain;
        private ImageView ivFoto;
        private List<TextView> textViews;

        public StringViewHolder(View itemView) {
            super(itemView);

            llMain = itemView.findViewById(idLlMain);

            if (idIvFoto != 0){
                ivFoto = itemView.findViewById(idIvFoto);
            }

            textViews = new ArrayList<>();

            for (int i = 0; i < ids.length; i++) {
                textViews.add((TextView) itemView.findViewById(ids[i]));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onStringClickListener!=null){

                        Adapterable str = (Adapterable) routes.get(getLayoutPosition());
                        onStringClickListener.onStringClick(str);
                    }

                }
            });
        }

    }

    public DataArrayAdapter(Context context, ArrayList<T> cursor, String[] fields, int[] ids, int item, int idLlMain) {

        this.idLlMain = idLlMain;
        this.item = item;
        this.ids = ids;
        this.fields = fields;
        this.routes = cursor;

        this.inflater = LayoutInflater.from(context);
    }

    public DataArrayAdapter(Context context, ArrayList<T> cursor, String[] fields, int[] ids, int item, int idLlMain, int idIvFoto, String fieldFoto) {

        this.idIvFoto = idIvFoto;
        this.fieldFoto = fieldFoto;
        this.idLlMain = idLlMain;
        this.item = item;
        this.ids = ids;
        this.fields = fields;
        this.routes = cursor;

        this.inflater = LayoutInflater.from(context);
    }

    public DataArrayAdapter(Context context, ArrayList<T> cursor, String[] fields, int[] ids, int item, int idLlMain, int idIvFoto, String fieldFoto, int[] fotos) {

        this.idIvFoto = idIvFoto;
        this.fieldFoto = fieldFoto;
        this.idLlMain = idLlMain;
        this.item = item;
        this.ids = ids;
        this.fields = fields;
        this.routes = cursor;
        this.fotos = fotos;

        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public StringViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(item, parent, false);

        return new StringViewHolder(view);
    }

        @Override
    public void onBindViewHolder(@NonNull StringViewHolder holder, int position) {

        T str = routes.get(position);

        if (idIvFoto != 0){

            if (fotos == null){
                holder.ivFoto.setImageURI(Uri.parse(((Adapterable) str).getField(fieldFoto)));

            }
            else {
                holder.ivFoto.setImageResource(fotos[Integer.valueOf(((Adapterable) str).getField(fieldFoto))]);

            }

        }


        for (int i = 0; i < fields.length; i++) {

            holder.textViews.get(i).setText(((Adapterable) str).getField(fields[i]));

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
        void onStringClick(Adapterable taskItem);
    }

    public void setOnStringClickListener(OnStringClickListener onStringClickListener) {
        this.onStringClickListener = onStringClickListener;
    }

}
