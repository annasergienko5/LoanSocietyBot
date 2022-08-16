package com.example.GringottsTool.Enteties;

public class Partner {
    private int tableId;
    private String name;
    private String tgId;
    private String vk;
    private String city;
    private int maxMyLoan;
    private int contributions;
    private double sumContributions;
    private int loan;
    private int debt;
    private String returnDate;
    private int amountOfEarlyRepayment;
    private int amountOfOverdueRepayment;
    private boolean elite;
    private boolean payedInThisMonth;

    public Partner(int tableId, String name, String tgId, String vk, String city, int maxMyLoan, int contributions, double sumContributions, int loan, int debt, String returnDate, int amountOfEarlyRepayment, int amountOfOverdueRepayment, boolean elite, boolean payedInThisMonth) {
        this.tableId = tableId;
        this.name = name;
        this.tgId = tgId;
        this.vk = vk;
        this.city = city;
        this.maxMyLoan = maxMyLoan;
        this.contributions = contributions;
        this.sumContributions = sumContributions;
        this.loan = loan;
        this.debt = debt;
        this.returnDate = returnDate;
        this.amountOfEarlyRepayment = amountOfEarlyRepayment;
        this.amountOfOverdueRepayment = amountOfOverdueRepayment;
        this.elite = elite;
        this.payedInThisMonth = payedInThisMonth;
    }

    public Partner() {

    }

    public Partner(String name){
        this.name = name;
    }

    public Partner(String name, int debt, String returnDate) {
        this.name = name;
        this.debt = debt;
        this.returnDate = returnDate;
    }



    public String getTgId() {
        return tgId;
    }
    public void setTgId(String tgId) {
        this.tgId = tgId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDebt() {
        String result = String.format("%,d", debt).replace(",", " ");
        return result;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public double getSumContributions() {
        return sumContributions;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.name);
        sb.append("\nВсего взносов: " + this.contributions);
        sb.append("\nНа сумму: " + this.sumContributions);
        sb.append("\nМакс. займ: " + maxMyLoan);
        sb.append("\nx0.6: " + (this.sumContributions * 0.6));
        sb.append("\nВсего займов: " + this.loan);
        if (this.returnDate != null && !this.returnDate.equals("")) {
            sb.append("\nСейчас должен: " + this.debt);
            sb.append("\nДата возврата: " + this.returnDate);
        }
        if (this.amountOfEarlyRepayment != 0){
            sb.append("\nДосрочных погашений: " + this.amountOfEarlyRepayment);
        }else sb.append("\nДосрочных погашений: нет");
        if (this.amountOfOverdueRepayment != 0){
            sb.append("\nПросрочек: " + this.amountOfOverdueRepayment);
        }else sb.append("\nПросрочек: нет");
        if (this.elite){
            sb.append("\nВзносы за прошедшие 3 месяца: уплачено");
        }else sb.append("\nВзносы за прошедшие 3 месяца: не уплачено");
        if (this.payedInThisMonth){
            sb.append("\nВзносы за текущий месяц: уплачено");
        }else sb.append("\nВзносы за текущий месяц: не уплачено");
        return sb.toString();
    }

    public int getTableId() {
        return tableId;
    }
    public void setTableId(int tableId) {
        this.tableId = tableId;
    }
}
