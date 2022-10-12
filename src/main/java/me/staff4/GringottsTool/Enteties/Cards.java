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
        res.append("\n").append(name)
                .append("\nгород: ").append(city)
                .append("\nбанк: ").append(bank)
                .append("\nсумма на карте: ").append(sum);
        if (numberPhone.equals("unknown")) {
            res.append(String.format("\nСБП: %s", numberPhone));
        } else {
            res.append(String.format("\nСБП: `%s`", numberPhone));
        }
        if (card.equals("unknown")) {
            res.append(String.format("\nномер карты: %s", card));
        } else {
            res.append(String.format("\nномер карты: `%s`", card));
        }
        if (meta != null) {
            for (Object key : meta.keySet()) {
                res.append("\n").append(key).append(": ").append(meta.get(key));
            }
        }
        return res.toString();
    }
}
