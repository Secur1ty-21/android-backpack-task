package com.example.backpackpicker.model;

public class Item {
    public final String name;
    public final int weight;
    public final int volume;

    public Item(String name, int weight, int volume) {
        this.name = name;
        this.weight = weight;
        this.volume = volume;
    }
}