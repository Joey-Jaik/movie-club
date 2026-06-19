package com.movieclub.models;

public class User {
    
    private int id;
    private String username;
    private String pin;
    private int pickOrder;

    public User() {}

    public User(int id, String username, String pin, int pickOrder){
        this.id = id;
        this.username = username;
        this.pin = pin;
        this.pickOrder = pickOrder;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPin() { return pin; }
    public int getPickOrder() { return pickOrder; }

    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPin(String pin) {this.pin = pin; }
    public void setPickOrder(int pickOrder) { this.pickOrder = pickOrder; }
}
