package com.example.backpackpicker.model;

public class Item {
    public final String name;
    public final float weight;
    public final float volume;

    public Item(String name, float weight, float volume) {
        this.name = name;
        this.weight = weight;
        this.volume = volume;
    }
}