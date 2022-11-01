package me.staff4.GringottsTool.Templates;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class TemplateEngine {
    @Autowired
    private static Configuration config;
    private static Logger log = LogManager.getLogger();

    private static String fillTemplate(final String fileName, final Map<String, Object> dataMap) {
        Writer writer;
        TemplateEngine templateEngine = new TemplateEngine();
        InputStream inputStream = templateEngine.getClass().getClassLoader().
                getResourceAsStream("templates/" + fileName);
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            BufferedReader reader = new BufferedReader(inputStreamReader);
            Template template = new Template(fileName, reader, config);
            writer = new StringWriter();
            template.process(dataMap, writer);
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
            return "template not found: " + fileName;
        }
        return writer.toString();
    }

    public static String errorInSomeFunction(final String command, final String chatId, final String userTgId) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("command", command);
        dataMap.put("chatID", chatId);
        dataMap.put("userTgId", userTgId);
        return fillTemplate("errorInSomeFunction", dataMap);
    }

    public static String invalidDataException(final String sheetRange, final String columnA1Notation, final String name,
                                             final String cellValue, final String expectedValue) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("sheetRange", sheetRange);
        dataMap.put("columnA1Notation", columnA1Notation);
        dataMap.put("name", name);
        dataMap.put("cellValue", cellValue);
        dataMap.put("expectedValue", expectedValue);
        return fillTemplate("invalidDataException", dataMap);
    }

    public static String fastMessageToAdmins(final String userTgId, final float sum) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userTgId", userTgId);
        dataMap.put("sum", sum);
        return fillTemplate("fastMessageToAdmins", dataMap);
    }

    public static String pollNotification(final String chatId, final String messageId) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("chatId", chatId);
        dataMap.put("messageId", messageId);
        return fillTemplate("pollNotification", dataMap);
    }

    public static String pollQuestion(final String name, final float sum) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", name);
        dataMap.put("sum", sum);
        return fillTemplate("pollQuestion", dataMap);
    }

    public static String aboutCreditHistoryMessage(final String name, final String creditHistory) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", name);
        dataMap.put("creditHistory", creditHistory);
        return fillTemplate("aboutCreditHistoryMessage", dataMap);
    }

    public static String aboutCreditHistoryMessageParsemodeOff(final String name, final String creditHistory) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", name);
        dataMap.put("creditHistory", creditHistory);
        return fillTemplate("aboutCreditHistoryMessageParsemodeOff", dataMap);
    }

    public static String fullSearch(final String partner, final String aboutTransactions) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("partner", partner);
        dataMap.put("aboutTransactions", aboutTransactions);
        return fillTemplate("fullSearch", dataMap);
    }

    public static String transaction(final String date, final float value) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("date", date);
        dataMap.put("value", value);
        return fillTemplate("transaction", dataMap);
    }

    public static String transactionParsemodeOff(final String date, final float value) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("date", date);
        dataMap.put("value", value);
        return fillTemplate("transactionParsemodeOff", dataMap);
    }

    public static String loanWithTransactions(final int loanId, final String dateStart, final String dateEnd,
                                              final float value, final String transactions) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("loanId", loanId);
        dataMap.put("dateStart", dateStart);
        dataMap.put("dateEnd", dateEnd);
        dataMap.put("value", value);
        dataMap.put("transactions", transactions);
        return fillTemplate("loanWithTransactions", dataMap);
    }

    public static String loanWithTransactionsParsemodeOff(final int loanId, final String dateStart,
                                                          final String dateEnd, final float value,
                                                          final String transactions) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("loanId", loanId);
        dataMap.put("dateStart", dateStart);
        dataMap.put("dateEnd", dateEnd);
        dataMap.put("value", value);
        dataMap.put("transactions", transactions);
        return fillTemplate("loanWithTransactionsParsemodeOff", dataMap);
    }

    public static String loanWithoutTransactions(final int loanId, final String dateStart, final String dateEnd,
                                                 final float value) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("loanId", loanId);
        dataMap.put("dateStart", dateStart);
        dataMap.put("dateEnd", dateEnd);
        dataMap.put("value", value);
        return fillTemplate("loanWithoutTransactions", dataMap);
    }

    public static String loanWithoutTransactionsParsemodeOff(final int loanId, final String dateStart,
                                                             final String dateEnd, final float value) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("loanId", loanId);
        dataMap.put("dateStart", dateStart);
        dataMap.put("dateEnd", dateEnd);
        dataMap.put("value", value);
        return fillTemplate("loanWithoutTransactionsParsemodeOff", dataMap);
    }

    public static String todayDebtsMessage(final String todayDebts) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("todayDebts", todayDebts);
        return fillTemplate("todayDebtsMessage", dataMap);
    }

    public static String simpleDebts(final String userTgId, final String name, final String value,
                                     final String returnDate) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userTgId", userTgId);
        dataMap.put("name", name);
        dataMap.put("value", value);
        dataMap.put("returnDate", returnDate);
        return fillTemplate("simpleDebts", dataMap);
    }

    public static String overdueDebtors(final String name, final String value, final String returnDate) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", name);
        dataMap.put("value", value);
        dataMap.put("returnDate", returnDate);
        return fillTemplate("overdueDebtors", dataMap);
    }

    public static String notOverdueDebtors(final String name, final String value, final String returnDate,
                                           final String lastContribution) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", name);
        dataMap.put("value", value);
        dataMap.put("returnDate", returnDate);
        dataMap.put("lastContribution", lastContribution);
        return fillTemplate("notOverdueDebtors", dataMap);
    }

    public static String fullSearchFilenameAboutFullcredit(final String name, final String nowDate) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", name);
        dataMap.put("nowDate", nowDate);
        return fillTemplate("fullSearchFilenameAboutFullcredit", dataMap);
    }
}
