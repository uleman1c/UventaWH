package com.example.uventawh;

public class TsdSettings {

    public boolean generateAcceptContainer;



    public TsdSettings(boolean generateAcceptContainer) {
        this.generateAcceptContainer = generateAcceptContainer;
    }

    public TsdSettings() {

        new TsdSettings(false);

    }
}
