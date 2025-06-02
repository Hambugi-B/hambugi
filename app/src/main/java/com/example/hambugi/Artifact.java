package com.example.hambugi;

public class Artifact {
    private String id;
    private String name;
    private int price;
    private String description;
    private boolean purchased;

    public Artifact(String id, String name, int price, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.purchased = false;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public String getDescription() { return description; }
    public boolean isPurchased() { return purchased; }
    public void setPurchased(boolean purchased) { this.purchased = purchased; }
}
