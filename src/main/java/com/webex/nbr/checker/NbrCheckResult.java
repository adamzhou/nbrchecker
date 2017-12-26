package com.webex.nbr.checker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linzhou on 15/12/2017.
 */
public class NbrCheckResult {
    private List<NbrCheckItem> checkResult = new ArrayList<>();

    public void addItem(NbrCheckItem item) {
        if (item != null) {
            checkResult.add(item);
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (NbrCheckItem item : checkResult) {
            stringBuilder.append(item.toString()).append("\n");
        }
        return stringBuilder.toString();
    }
}
