package com.example.uventawh;

public class ContractorRoute {

    public Contractor contractor;
    public Integer toShipment, toReceipt;

    public ContractorRoute(Contractor contractor, Integer toShipment, Integer toReceipt) {
        this.contractor = contractor;
        this.toShipment = toShipment;
        this.toReceipt = toReceipt;
    }
}
