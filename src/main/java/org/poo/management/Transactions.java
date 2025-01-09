package org.poo.management;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public final class Transactions {
    private int timestamp = -1;
    private String description = null;
    private String senderIBAN = null;
    private String receiverIBAN = null;
    private double amount = -1;
    private String currency = null;
    private String transferType = null;
    private String cardHolder = null;
    private String card = null;
    private String iban = null;
    private String commerciant = null;
    private String error = null;
    private List<String> accounts = new ArrayList<>();
    private String newPlan = null;

    public Transactions() {

    }

    public Transactions(final int timestamp, final String description, final String senderIBAN,
                        final String receiverIBAN, final double amount, final String currency,
                        final String transferType) {
        this.timestamp = timestamp;
        this.description = description;
        this.senderIBAN = senderIBAN;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.currency = currency;
        this.transferType = transferType;
    }

    public Transactions(final String description, final int timestamp, final String currency,
                        final double amount, final List<String> accounts, final String error) {
        this.timestamp = timestamp;
        this.description = description;
        this.currency = currency;
        this.amount = amount;
        this.accounts = accounts;
        this.error = error;
    }

    public Transactions(final String description, final int timestamp, final String currency,
                        final double amount, final List<String> accounts) {
        this.timestamp = timestamp;
        this.description = description;
        this.currency = currency;
        this.amount = amount;
        this.accounts = accounts;
    }

    public Transactions(final String description, final int timestamp, final String card,
                        final String cardHolder, final String iban) {
        this.description = description;
        this.timestamp = timestamp;
        this.card = card;
        this.cardHolder = cardHolder;
        this.iban = iban;
    }

    public Transactions(final String description, final int timestamp,
                        final String commerciant, final double amount) {
        this.description = description;
        this.timestamp = timestamp;
        this.amount = amount;
        this.commerciant = commerciant;
    }

    public Transactions(final String description, final int timestamp) {
        this.description = description;
        this.timestamp = timestamp;
    }

    public Transactions(final String description, final int timestamp,
                        final String iban, final String newPlan) {
        this.description = description;
        this.timestamp = timestamp;
        this.iban = iban;
        this.newPlan = newPlan;
    }

    public Transactions(final String description, final int timestamp,
                        final double amount, final String currency) {
        this.description = description;
        this.timestamp = timestamp;
        this.amount = amount;
        this.currency = currency;
    }

    public Transactions(final String description, final int timestamp,
                        final double amount) {
        this.description = description;
        this.timestamp = timestamp;
        this.amount = amount;
    }
}
