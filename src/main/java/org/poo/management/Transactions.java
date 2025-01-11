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
    private List<Double> amounts = new ArrayList<>();
    private String newPlan = null;
    private String split;
    private String classicIban = null;
    private String savingsIban = null;

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
                        final double amount, final List<String> accounts, final String error,
                        final List<Double> amounts, final String split) {
        this.timestamp = timestamp;
        this.description = description;
        this.currency = currency;
        this.amount = amount;
        this.accounts = accounts;
        this.error = error;
        this.amounts = amounts;
        this.split = split;
    }

    public Transactions(final String description, final int timestamp, final String currency,
                        final double amount, final List<String> accounts,
                        final List<Double> amounts, final String split) {
        this.timestamp = timestamp;
        this.description = description;
        this.currency = currency;
        this.amount = amount;
        this.accounts = accounts;
        this.amounts = amounts;
        this.split = split;
    }

    public Transactions(final String description, final int timestamp, final String currency,
                        final double amount, final List<String> accounts,
                        final String error, final String split) {
        this.timestamp = timestamp;
        this.description = description;
        this.currency = currency;
        this.amount = amount;
        this.accounts = accounts;
        this.error = error;
        this.split = split;
    }

    public Transactions(final String description, final int timestamp, final String currency,
                        final double amount, final List<String> accounts, final String split) {
        this.timestamp = timestamp;
        this.description = description;
        this.currency = currency;
        this.amount = amount;
        this.accounts = accounts;
        this.split = split;
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

    public Transactions(final String description, final int timestamp,
                        final String classicIban, final String savingsIban,
                        final double amount) {
        this.description = description;
        this.timestamp = timestamp;
        this.classicIban = classicIban;
        this.savingsIban = savingsIban;
        this.amount = amount;
    }
}
