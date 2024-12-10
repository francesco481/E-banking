package org.poo.management.Accounts;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.management.Cards.Card;
import org.poo.management.Cards.CardFactory;
import org.poo.management.Cards.CardType;
import org.poo.management.Transactions;
import org.poo.utils.Utils;

import java.util.ArrayList;

@Getter
@Setter
public class Account {
    private String IBAN;
    private double balance;
    private String currency;
    private String type;
    private double minimum = 0;
    private ArrayList<CardType> cards = new ArrayList<>();
    private ArrayList<Transactions> transactions = new ArrayList<>();
    private double interest = -1;

    public Account(CommandInput command) {
        this.IBAN = Utils.generateIBAN();
        this.balance = 0;
        this.currency = command.getCurrency();
        this.type = command.getAccountType();
    }

    public void addCard(CommandInput command) {
        int type = 1;
        if(command.getCommand().equals("createCard"))
        {
            type = 0;
        }
        CardType card = CardFactory.getCard(type);
        cards.add(card);
    }

    public void addTransaction(Transactions transaction) {
        this.transactions.add(transaction);
    }

    public void addFunds(double amount) {
        this.balance += amount;
    }
}
