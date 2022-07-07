package com.example.GringottsTool.Enteties;

public class Cards {
    private String card;
    private double sum;
    private String name;
    private String city;
    private String bank;
    private String payWay;
    private String link;

    public Cards(String card, double sum, String name, String city, String bank, String payWay, String link) {
        this.card = card;
        this.sum = sum;
        this.name = name;
        this.city = city;
        this.bank = bank;
        this.payWay = payWay;
        this.link = link;
    }

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
