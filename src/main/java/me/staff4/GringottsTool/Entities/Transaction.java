package me.staff4.GringottsTool.Entities;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class Transaction {
    private final String date;
    private final float value;
}
