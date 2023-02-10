package com.example.uventawh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int
            REQUEST_CODE_ACCEPT = 1,
            REQUEST_CODE_SHIPMENT = 2,
            REQUEST_CODE_ADD_SKLAD = 3,
            REQUEST_CODE_SCAN_DOC = 4,
            REQUEST_CODE_TRANSPORT_LIST = 5,
            REQUEST_CODE_ROUTE_LIST = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.ibTransportArrival).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), TrasportListActivity.class);

                startActivityForResult(intent, REQUEST_CODE_TRANSPORT_LIST);


            }
        });

        findViewById(R.id.ibAccept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), AcceptActivity.class);

                startActivityForResult(intent, REQUEST_CODE_ACCEPT);


            }
        });

        findViewById(R.id.ibShipment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), RoutesListActivity.class);

                startActivityForResult(intent, REQUEST_CODE_SHIPMENT);


            }
        });

        findViewById(R.id.ibAddSklad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), AddSkladActivity.class);

                startActivityForResult(intent, REQUEST_CODE_ADD_SKLAD);


            }
        });

        findViewById(R.id.ibScanDoc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), ScanDocActivity.class);

                startActivityForResult(intent, REQUEST_CODE_SCAN_DOC);


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_ACCEPT) {

            }
            else if (requestCode == REQUEST_CODE_SHIPMENT) {

            }
            else if (requestCode == REQUEST_CODE_ADD_SKLAD) {

            }
            else if (requestCode == REQUEST_CODE_TRANSPORT_LIST) {

                Intent intent = new Intent(getBaseContext(), RoutesListActivity.class);

                intent.putExtra("refTransport", data.getStringExtra("refTransport"));
                intent.putExtra("descTransport", data.getStringExtra("descTransport"));
                intent.putExtra("refDriver", data.getStringExtra("refDriver"));
                intent.putExtra("descDriver", data.getStringExtra("descDriver"));

                startActivityForResult(intent, REQUEST_CODE_ROUTE_LIST);



            }
        }
    }

}
