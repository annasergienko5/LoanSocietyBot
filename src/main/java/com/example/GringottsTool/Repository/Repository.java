package com.example.GringottsTool.Repository;

import com.example.GringottsTool.Enteties.Transaction;
import com.example.GringottsTool.Enteties.QueueItem;
import com.example.GringottsTool.Enteties.Cards;
import com.example.GringottsTool.Enteties.Contributions;
import com.example.GringottsTool.Enteties.Info;
import com.example.GringottsTool.Enteties.Partner;
import com.example.GringottsTool.Exeptions.InvalidDataException;
import com.example.GringottsTool.Exeptions.NoDataFound;

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
    List<Partner> getDebtors() throws IOException, ParseException, NoDataFound;
    List<Partner> getDuckList() throws IOException, NoDataFound;
    List<Partner> getTodayDebtors() throws IOException, NoDataFound;
    List<String> getProxy() throws IOException, NoDataFound;
    List<Transaction> getTransactions(Partner partner) throws IOException, InvalidDataException;
    Partner getPartnerByTgId(String tgId) throws IOException, NoDataFound;
    Queue<QueueItem> getQueue() throws NoDataFound, IOException, InvalidDataException;
    String addQueueItem(String tableIdString, int sum) throws NoDataFound, IOException, InvalidDataException;
}
