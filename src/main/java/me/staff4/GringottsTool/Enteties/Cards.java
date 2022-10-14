package me.staff4.GringottsTool.Enteties;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public final class Cards {
    private String card;
    private String sum;
    private String name;
    private String numberPhone;
    private String city;
    private String bank;
    private Map<?, ?> meta;

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append("\n*").append(name).append("*");
        if (!bank.equals("")) {
            res.append("\n").append(bank);
        }
        if (!card.equals("")) {
            res.append(String.format("\n`%s`", card));
        }
        if (!numberPhone.equals("")) {
            res.append(String.format("\n`%s`", numberPhone));
        }
        if (!sum.equals("")) {
            res.append(String.format("\nсумма на карте: `%,.2f₽`", Float.parseFloat(sum.replaceAll(",", "."))));
        }
        if (!city.equals("")) {
            res.append("\n").append(city);
        }
        if (meta != null) {
            for (Object key : meta.keySet()) {
                res.append("\n").append(key).append(": ").append(meta.get(key));
            }
        }
        return res.toString();
    }
}
