package com.example.GringottsTool.Enteties;

public class QueueItem {
    private String name;
    private String tgId;
    private int sum;

    public QueueItem(String name, String tgId, int sum) {
        this.name = name;
        this.tgId = tgId;
        this.sum = sum;
    }

    public String getName() {
        return name;
    }

    public String getTgId() {
        return tgId;
    }

    public int getSum() {
        return sum;
    }
}
