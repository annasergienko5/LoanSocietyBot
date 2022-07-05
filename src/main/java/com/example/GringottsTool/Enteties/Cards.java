package com.example.GringottsTool.Enteties;

public class Cards {
    private String card;
    private double sum;

    public Cards(String card, double sum) {
        this.card = card;
        this.sum = sum;
    }

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append(this.card).append(" ").append(this.sum);
        return res.toString();
    }
}
