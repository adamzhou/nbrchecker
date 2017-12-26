package com.webex.nbr.checker;

/**
 * Created by linzhou on 15/12/2017.
 */
public class NbrCheckItem {
    private String rawDataPath;
    private long videoSession;
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String toString() {
        return result;
    }
}
