package org.poo.management;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Transactions {
    int timestamp = -1;
    String description = null;
    String senderIBAN = null;
    String receiverIBAN = null;
    double amount = -1;
    String currency = null;
    String transferType = null;
    String cardHolder = null;
    String card = null;
    String IBAN = null;
    String commerciant = null;
    String error = null;
    List<String> accounts = new ArrayList<>();

    public Transactions() {}

    public Transactions(int timestamp, String description, String senderIBAN, String receiverIBAN, double amount, String currency, String transferType) {
        this.timestamp = timestamp;
        this.description = description;
        this.senderIBAN = senderIBAN;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.currency = currency;
        this.transferType = transferType;
    }

    public Transactions(String description, int timestamp, String currency, double amount, List<String> accounts, String error) {
        this.timestamp = timestamp;
        this.description = description;
        this.currency = currency;
        this.amount = amount;
        this.accounts = accounts;
        this.error = error;
    }

    public Transactions(String description, int timestamp, String currency, double amount, List<String> accounts) {
        this.timestamp = timestamp;
        this.description = description;
        this.currency = currency;
        this.amount = amount;
        this.accounts = accounts;
    }

    public Transactions(String description, int timestamp, String card, String cardHolder, String IBAN)
    {
        this.description = description;
        this.timestamp = timestamp;
        this.card = card;
        this.cardHolder = cardHolder;
        this.IBAN = IBAN;
    }

    public Transactions(String description, int timestamp, String commerciant, double amount) {
        this.description = description;
        this.timestamp = timestamp;
        this.amount = amount;
        this.commerciant = commerciant;
    }

    public Transactions(String description, int timestamp) {
        this.description = description;
        this.timestamp = timestamp;
    }
}
