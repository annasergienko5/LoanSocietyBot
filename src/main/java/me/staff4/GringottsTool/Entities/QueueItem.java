package me.staff4.GringottsTool.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QueueItem {
    private String name;
    private String tgId;
    private int sum;
}
