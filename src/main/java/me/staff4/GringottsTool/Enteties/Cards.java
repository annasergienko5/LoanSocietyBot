package me.staff4.GringottsTool.Enteties;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class Cards {
    private String card;
    private String sum;
    private String name;
    private String numberPhone;
    private String city;
    private String bank;
    private String payWay;
    private String link;

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append("\n").append(name)
                .append("\nбанк: ").append(bank)
                .append(String.format("\nСБП: `%s`", numberPhone))
                .append(String.format("\nномер карты: `%s`", card))
                .append("\nсумма на карте: ").append(sum)
                .append("\nгород: ").append(city);
        if (link != null) {
            res.append("\nпополнить без комиссии можно по ссылке:\n")
                    .append(this.link);
        }
        return res.toString();
    }
}
