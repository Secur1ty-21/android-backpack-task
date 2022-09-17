package com.example.backpackpicker.model;

import java.io.Serializable;

public class Backpack implements Serializable {
    public final float capacityWeight;
    public final float capacityVolume;
    public float weightItems;
    public float volumeItems;
    public String items = "";

    public Backpack(final float capacityWeight, final float capacityVolume) {
        this.capacityWeight = capacityWeight;
        this.capacityVolume = capacityVolume;
    }
}