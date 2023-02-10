package com.example.uventawh;

public class TaskItem {

    String ref;
    String name;
    String number;
    String date;
    String company, refContractor;
    String sender, refSender;
    String driverFio, driverDocument, transport, documentNumber;
    int image;
    int quantity;
    int accepted;

    public TaskItem(String ref, String name, String number, String date, String refContractor, String company, String refSender, String sender, int image, Integer quantity, Integer accepted,
        String driverFio, String driverDocument, String transport, String documentNumber){

        this.ref = ref;
        this.name = name;
        this.number = number;
        this.date = date;
        this.refContractor = refContractor;
        this.company = company;
        this.refSender = refSender;
        this.sender = sender;
        this.image = image;
        this.quantity = quantity;
        this.accepted = accepted;
        this.driverFio = driverFio;
        this.driverDocument = driverDocument;
        this.transport = transport;
        this.documentNumber = documentNumber;
    }

}
