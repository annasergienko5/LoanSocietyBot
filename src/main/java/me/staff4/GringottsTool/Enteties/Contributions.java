package me.staff4.GringottsTool.Enteties;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

@AllArgsConstructor
@Getter
public final class Contributions {
    private String name;
    private ArrayList<Contribution> pays;
    private String also;

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append("Статистика по платежам");
        for (Contribution pay : pays) {
            res.append(pay.toString());
        }
        if (also.equals("не было")) {
            res.append("\nРанее - ").append(also);
        } else {
            res.append("\nРанее - ").append(also).append(" рублей");
        }
        return res.toString();
    }
@AllArgsConstructor
@Getter
    public static final class Contribution {
        private String date;
        private String sum;

        @Override
        public String toString() {
            String result = String.format("\n%s - %s рублей",
                    this.date,
                    this.sum);
            return result;
        }
    }
}
