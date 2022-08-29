package com.example.GringottsTool.Enteties;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Cards {
    private String card;
    private double sum;
    private String name;
    private String city;
    private String bank;
    private String payWay;
    private String link;

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append("\nНомер карты: ").append(this.card)
                .append("\nсумма на карте: ").append(this.sum)
                .append("\nгород: ").append(this.city)
                .append("\nкарта: ").append(this.bank);
        if (payWay != null){
            res.append(" ").append(this.payWay);
        }
        if (link != null){
            res.append("\nПополнить без комиссии можно по ссылке:\n").append(this.link);
        }
        return res.toString();
    }
}
