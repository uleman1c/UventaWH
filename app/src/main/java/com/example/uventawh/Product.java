package com.example.uventawh;

import java.util.ArrayList;

public class Product {

    public String ref, description, senderRef, senderDescription, receiverRef, receiverDescription;
    public ArrayList<Good> goods;

    Product(String ref, String description, String senderRef, String senderDescription, String receiverRef, String receiverDescription, ArrayList<Good> goods){

        this.ref = ref;
        this.description = description;
        this.senderRef = senderRef;
        this.senderDescription = senderDescription;
        this.receiverRef = receiverRef;
        this.receiverDescription = receiverDescription;
        this.goods = new ArrayList<>();

        this.goods.addAll(goods);

    }
}
