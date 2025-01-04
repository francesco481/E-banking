package org.poo.management.Accounts;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.management.Cards.Card;
import org.poo.management.Cards.ClassicCard;
import org.poo.management.Cards.OneCard;
import org.poo.management.Transactions;
import org.poo.utils.Utils;
import java.util.ArrayList;

@Getter
@Setter
public class Account {
    private String iban;
    private double balance;
    private String currency;
    private String type;
    private double minimum = 0;
    private ArrayList<Card> cards = new ArrayList<>();
    private ArrayList<Transactions> transactions = new ArrayList<>();
    private double interest = -1;

    public Account(final CommandInput command) {
        this.iban = Utils.generateIBAN();
        this.balance = 0;
        this.currency = command.getCurrency();
        this.type = command.getAccountType();
    }

    /**
     * Adds a new card to the list of cards associated with this account.
     * The method uses a factory to create a card based on the provided command input.
     * If the command specifies "createCard", the type is set to 0 (e.g., a standard card).
     * Otherwise, the type is set to 1 (e.g., a special card).
     *
     * @param command the command input containing information about the card to be created,
     *                including the desired type.
     */
    public void addCard(final CommandInput command) {
        Card card;
        if (command.getCommand().equals("createCard")) {
            card = new ClassicCard();
        } else {
            card = new OneCard();
        }
        cards.add(card);
    }

    /**
     * Adds a transaction to the list of transactions for this user.
     * This method appends the provided transaction to the internal list,
     * allowing it to be tracked as part of the user's transaction history.
     *
     * @param transaction the transaction to be added to the user.
     */
    public void addTransaction(final Transactions transaction) {
        this.transactions.add(transaction);
    }

    /**
     * Adds the specified amount to the account's balance.
     * This method updates the balance by incrementing it with the provided amount.
     *
     * @param amount the amount of funds to be added to the account.
     */
    public void addFunds(final double amount) {
        this.balance += amount;
    }
}
