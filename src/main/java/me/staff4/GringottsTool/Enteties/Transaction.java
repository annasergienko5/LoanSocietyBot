package me.staff4.GringottsTool.Enteties;
import me.staff4.GringottsTool.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public final class Transaction {
    private final String date;
    private final float value;
    public String getString(final boolean isHtmlParseModeOn) {
        String template;
        if (isHtmlParseModeOn) {
            template = Constants.TRANSACTION;
        } else {
            template = Constants.TRANSACTION_PARSEMODE_OFF;
        }
        return String.format(template, date, value)
                .replace(',', ' ');
    }
}
