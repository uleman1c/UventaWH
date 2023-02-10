package com.example.uventawh;

import java.util.ArrayList;

public class Route {

    public String ref, dateShipment, driver, transport;
    public ArrayList<ContractorRoute> contractorRoutes = new ArrayList<>();

    Route(String ref, String dateShipment, String driver, String transport, ArrayList<ContractorRoute> contractors){

        this.ref = ref;
        this.dateShipment = dateShipment;
        this.driver = driver;
        this.transport = transport;
        this.contractorRoutes.addAll(contractors);

    }

}
