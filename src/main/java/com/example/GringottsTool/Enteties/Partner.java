package com.example.GringottsTool.Enteties;

public class Partner {
    private String name;
    private String tgId;
    private String vk;
    private String city;
    private int contributions;
    private double sumContributions;
    private int loan;
    private int debt;
    private String returnDate;
    private int dosrochka;
    private int prosrochka;
    private boolean elite;
    private boolean vznosZaMesac;

    public Partner(String name, String tgId, String vk, String city, int contributions, double sumContributions, int loan, int debt, String returnDate, int dosrochka, int prosrochka, boolean elite, boolean vznosZaMesac) {
        this.name = name;
        this.tgId = tgId;
        this.vk = vk;
        this.city = city;
        this.contributions = contributions;
        this.sumContributions = sumContributions;
        this.loan = loan;
        this.debt = debt;
        this.returnDate = returnDate;
        this.dosrochka = dosrochka;
        this.prosrochka = prosrochka;
        this.elite = elite;
        this.vznosZaMesac = vznosZaMesac;
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

    public String getName() {
        return name;
    }

    public int getDebt() {
        return debt;
    }

    public String getReturnDate() {
        return returnDate;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.name);
        sb.append("\nВсего взносов: " + this.contributions);
        sb.append("\nНа сумму: " + this.sumContributions);
        sb.append("\nx5: " + this.sumContributions * 5);
        sb.append("\nx0.6: " + (this.sumContributions * 0.6));
        sb.append("\nВсего займов: " + this.loan);
        if (this.returnDate != null) {
            sb.append("\nСейчас должен: " + this.debt);
            sb.append("\nДата возврата: " + this.returnDate);
        }
        if (this.dosrochka != 0){
            sb.append("\nДосрочных погашений: " + this.dosrochka);
        }else sb.append("\nДосрочных погашений: нет");
        if (this.prosrochka != 0){
            sb.append("\nПросрочек: " + this.prosrochka);
        }else sb.append("\nПросрочек: нет");
        if (this.elite){
            sb.append("\nВзносы за прошедшие 3 месяца: уплачено");
        }else sb.append("\nВзносы за прошедшие 3 месяца: не уплачено");
        if (this.vznosZaMesac){
            sb.append("\nВзносы за текущий месяц: уплачено");
        }else sb.append("\nВзносы за текущий месяц: не уплачено");
        return sb.toString();
    }
}
