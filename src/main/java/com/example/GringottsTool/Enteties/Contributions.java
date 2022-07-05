package com.example.GringottsTool.Enteties;

import java.util.HashMap;
import java.util.Map;

public class Contributions {
    String name;
    HashMap<String, String> pays;
    String also;

    public Contributions(String name, HashMap<String, String> pay, String also) {
        this.name = name;
        this.pays = pay;
        this.also = also;
    }

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append("Статистика по платежам");
        for (Map.Entry<String,String> pay : pays.entrySet()){
            res.append("\n").append(pay.getKey()).append(" - ").append(pay.getValue()).append(" рублей");
        }
        res.append("\nРанее - ").append(also).append(" рублей");
        return res.toString();
    }
}
