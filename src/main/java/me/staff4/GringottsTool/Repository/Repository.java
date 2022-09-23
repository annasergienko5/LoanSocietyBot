package me.staff4.GringottsTool.Repository;

import me.staff4.GringottsTool.Enteties.Transaction;
import me.staff4.GringottsTool.Enteties.QueueItem;
import me.staff4.GringottsTool.Enteties.Cards;
import me.staff4.GringottsTool.Enteties.Contributions;
import me.staff4.GringottsTool.Enteties.Info;
import me.staff4.GringottsTool.Enteties.Partner;
import me.staff4.GringottsTool.Exeptions.InvalidDataException;
import me.staff4.GringottsTool.Exeptions.NoDataFound;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Queue;
@org.springframework.stereotype.Repository
public interface Repository {
    List<Contributions> getContributions() throws NoDataFound, IOException;
    List<Cards> getCards() throws NoDataFound, IOException;
    Info getInfo() throws IOException, NoDataFound;
    List<Partner> getPartners(String nameOrTgId) throws IOException, NoDataFound;
    boolean isPartner(long checkingTgId) throws IOException, NoDataFound;
    List<Partner> getDebtors() throws IOException, ParseException, NoDataFound, InvalidDataException;
    List<Partner> getDuckList() throws IOException, NoDataFound, InvalidDataException;
    List<Partner> getTodayDebtors() throws IOException, NoDataFound, InvalidDataException;
    List<String> getProxy() throws IOException, NoDataFound;
    List<Transaction> getTransactions(Partner partner) throws IOException, InvalidDataException;
    Partner getPartnerByTgId(String tgId) throws IOException, NoDataFound;
    Queue<QueueItem> getQueue() throws NoDataFound, IOException, InvalidDataException;
    String addQueueItem(String tableIdString, int sum) throws NoDataFound, IOException, InvalidDataException;
    List<String> getAllPartners() throws NoDataFound, IOException;
}
