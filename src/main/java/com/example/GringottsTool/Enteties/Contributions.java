package com.example.GringottsTool.Enteties;

import java.util.ArrayList;

public class Contributions {
    String name;
    ArrayList<Contribution> pays;
    String also;

    public Contributions(String name, ArrayList<Contribution> pay, String also) {
        this.name = name;
        this.pays = pay;
        this.also = also;
    }

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append("Статистика по платежам");
        for (Contribution pay : pays){
            res.append(pay.toString());
        }
        if (also.equals("не было")){
            res.append("\nРанее - ").append(also);
        }else res.append("\nРанее - ").append(also).append(" рублей");
        return res.toString();
    }

    public static class Contribution{
        private String date;
        private String sum;

        public Contribution(String date, String sum) {
            this.date = date;
            this.sum = sum;
        }

        public String getDate() {
            return date;
        }

        public String getSum() {
            return sum;
        }

        @Override
        public String toString() {
            String result = String.format("\n%s - %s рублей", this.date, this.sum);
            return result;
        }
    }
}
