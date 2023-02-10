package com.example.uventawh;

import org.json.JSONObject;

public class ShipmentItem {

    String ref;
    String name;
    String number;
    String date;
    String company, refContractor;
    String sender, refSender;
    String deliverer, refDeliverer;
    String numDocument;
    int image;
    int quantity;
    int accepted;
    String status;

    Boolean selfDelivery;
    String schetNumber;

    public ShipmentItem(String ref, String name, String number, String date, String refContractor, String company, String refSender, String sender, String refDeliverer, String deliverer, String numDocument,
                        int image, Integer quantity, Integer accepted, String status, Boolean selfDelivery, String schetNumber){

        this.ref = ref;
        this.name = name;
        this.number = number;
        this.date = date;
        this.refContractor = refContractor;
        this.company = company;
        this.refSender = refSender;
        this.sender = sender;
        this.refDeliverer = refDeliverer;
        this.deliverer = deliverer;
        this.numDocument = numDocument;
        this.image = image;
        this.quantity = quantity;
        this.accepted = accepted;
        this.status = status;
        this.selfDelivery = selfDelivery;
        this.schetNumber = schetNumber;
    }

    public static ShipmentItem extractFromJSON(JSONObject deliveryOrderJSON, HttpClient client1) {
        String ref = client1.getStringFromJSON(deliveryOrderJSON, "Ref");
        String type = client1.getStringFromJSON(deliveryOrderJSON, "Type");
        String number = client1.getStringFromJSON(deliveryOrderJSON, "Number");
        String date = client1.getStringFromJSON(deliveryOrderJSON, "Date");
        String refContractor = client1.getStringFromJSON(deliveryOrderJSON, "ContractorRef");
        String company = client1.getStringFromJSON(deliveryOrderJSON, "Contractor");
        String refSender = client1.getStringFromJSON(deliveryOrderJSON, "SenderRef");
        String sender = client1.getStringFromJSON(deliveryOrderJSON, "Sender");
        String refDeliverer = client1.getStringFromJSON(deliveryOrderJSON, "DelivererRef");
        String deliverer = client1.getStringFromJSON(deliveryOrderJSON, "Deliverer");
        String numDocument = client1.getStringFromJSON(deliveryOrderJSON, "DocumentNumber");
        Integer quantity = client1.getIntegerFromJSON(deliveryOrderJSON, "Quantity");
        Integer accepted = client1.getIntegerFromJSON(deliveryOrderJSON, "Executed");
        String status = client1.getStringFromJSON(deliveryOrderJSON, "Status");
        Boolean selfDelivery = client1.getBooleanFromJSON(deliveryOrderJSON, "SelfDelivery");
        String schetNumber = client1.getStringFromJSON(deliveryOrderJSON, "SchetNumber");

        Integer intPicture = 0;
        if (type.equals("Отгрузка")) {
            intPicture = R.drawable.red_arrow;
        } else if (type.equals("Приемка")) {
            intPicture = R.drawable.green_arrow;
        } else if (type.equals("ГрупповаяПриемка")) {
            intPicture = R.drawable.green_2_arrows;
        }

        return new ShipmentItem(ref, type, number, client1.dateStrToDate(date), refContractor, company, refSender, sender, refDeliverer, deliverer, numDocument,
                intPicture, quantity, accepted, status, selfDelivery, schetNumber);
    }



}
