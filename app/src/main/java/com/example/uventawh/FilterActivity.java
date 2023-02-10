package com.example.uventawh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {

    private class FilterItem{

        public String name, description, value;
        public int id;
        public ImageButton ibClear, ibSelect;
        public TextView tvValue;

        FilterItem(String name, String description, String value){

            this.name = name;
            this.description = description;
            this.value = value;

        }

    }

    private JSONArray filter = new JSONArray();

    private ArrayList<FilterItem> filterItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        View.OnLongClickListener oclClear = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                for (FilterItem filterItem: filterItems){

                    if (v.getId() == filterItem.tvValue.getId()){

                        filterItem.value = "";
                        filterItem.tvValue.setText("");

                    }

                }

                return true;
            }

        };

        View.OnClickListener oclSelect = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (FilterItem filterItem: filterItems){

                    if (v.getId() == filterItem.tvValue.getId()){

                        if (filterItem.name.equals("driver")){

                            Intent intent = new Intent("com.example.uventawh.action.driversactivity");

                            startActivityForResult(intent, filterItems.indexOf(filterItem));

                        }

                    }

                }



            }
        };

        findViewById(R.id.fabSet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONArray params = new JSONArray();

                JSONObject request = new JSONObject();
                try {
                    request.put("name", "driver");
                    request.put("description", "Водитель");
                    request.put("value", filterItems.get(0).value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                params.put(request);

                Intent intent = new Intent();

                intent.putExtra("filter", params.toString());

                setResult(RESULT_OK, intent);

                finish();



            }
        });

        findViewById(R.id.fabClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONArray params = new JSONArray();

                JSONObject request = new JSONObject();
                try {
                    request.put("name", "driver");
                    request.put("description", "Водитель");
                    request.put("value", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                params.put(request);

                Intent intent = new Intent();

                intent.putExtra("filter", params.toString());

                setResult(RESULT_OK, intent);

                finish();



            }
        });

        Intent intent = getIntent();

        try {
            filter = new JSONArray(intent.getStringExtra("filter"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < filter.length(); i++){

            JSONObject filterItem = null;
            try {
                filterItem = (JSONObject) filter.get(i);

                filterItems.add(new FilterItem(filterItem.getString("name"),
                        filterItem.getString("description"),
                        filterItem.getString("value")));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (i == 0){

                FilterItem curFilterItem = filterItems.get(i);
                curFilterItem.tvValue = findViewById(R.id.tvValue1);

                ((TextView) findViewById(R.id.tvDescription1)).setText(curFilterItem.description);
                curFilterItem.tvValue.setText(curFilterItem.value);
                curFilterItem.tvValue.setOnLongClickListener(oclClear);
                curFilterItem.tvValue.setOnClickListener(oclSelect);

            } else {

//                ((TextView) findViewById(R.id.tvDescription1)).

            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            FilterItem curFilterItem = filterItems.get(requestCode);

            curFilterItem.value = data.getStringExtra("description");

            curFilterItem.tvValue.setText(curFilterItem.value);

        }
    }
}
