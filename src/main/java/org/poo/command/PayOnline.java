package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Cards.Card;
import org.poo.management.Database;
import org.poo.management.Transactions;

import java.util.ArrayList;

public final class PayOnline implements Order {
    private final Database database;
    private final CommandInput command;
    private final ObjectMapper mapper;
    private final ArrayNode output;

    public PayOnline(final Database database, final CommandInput command,
                     final ObjectMapper mapper, final ArrayNode output) {
        this.database = database;
        this.command = command;
        this.mapper = mapper;
        this.output = output;
    }

    /**
     * Executes the command to process an online payment using a specified card.
     * The method performs the following actions:
     * - Validates the user's existence in the database.
     * - Checks if the specified card exists and its status (active or frozen).
     * - Processes the payment if the account has sufficient funds and the card is active.
     * - Records transactions for the user and the account, including special cases such as
     *   card replacement or insufficient funds.
     * - Generates an error response if the card is not found in the database.
     *
     * @param timestamp the timestamp at which the command is executed.
     *                  Used for logging or tracking the operation.
     */
    @Override
    public void execute(final int timestamp) {
        int i = database.findUser(command.getEmail());
        if (i == -1) {
            return;
        }

        ArrayList<AccountType> curr = database.getAccounts().get(i);
        int ok = 1;
        for (AccountType account : curr) {
            for (Card card : ((Account) account).getCards()) {
                if (card.getCardNumber().equals(command.getCardNumber())) {
                    ok = 0;
                    if (card.getStatus().equals("frozen")) {
                        UserInput user = database.getUsers().get(i);
                        Transactions transactions  = new Transactions("The card is frozen",
                                                                                timestamp);
                        user.addTransaction(transactions);
                        ((Account) account).addTransaction(transactions);
                        return;
                    }
                    double amount = command.getAmount() * Database.getRate(command.getCurrency(),
                                                            ((Account) account).getCurrency());
                    if (account.getBalance() - ((Account) account).getMinimum() >= amount
                            && card.getStatus().equals("active")) {
                        account.pay(amount);
                        card.execPay();

                        UserInput user = database.getUsers().get(i);
                        ok = 2;
                        Transactions transactions  = new Transactions("Card payment",
                                            timestamp, command.getCommerciant(), amount);
                        user.addTransaction(transactions);
                        ((Account) account).addTransaction(transactions);

                        if (!card.getCardNumber().equals(command.getCardNumber())) {
                            Transactions transactions1 = new Transactions("The card has been "
                                    + "destroyed", timestamp, command.getCardNumber(),
                                                     user.getEmail(),
                                                    ((Account) account).getIban());

                            ((Account) account).addTransaction(transactions1);
                            user.addTransaction(transactions1);

                            Transactions transactions2 = new Transactions("New card created",
                                    timestamp, card.getCardNumber(), user.getEmail(),
                                    ((Account) account).getIban());

                            ((Account) account).addTransaction(transactions2);
                            user.addTransaction(transactions2);
                        }
                    }
                    if (account.getBalance() - ((Account) account).getMinimum() < amount
                            && card.getStatus().equals("active")) {
                        UserInput user = database.getUsers().get(i);
                        Transactions transactions = new Transactions("Insufficient funds",
                                                                    timestamp);

                        user.addTransaction(transactions);
                        ((Account) account).addTransaction(transactions);
                    }
                }
            }
        }

        if (ok == 1) {
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "Card not found");

            ObjectNode responseNode = mapper.createObjectNode();
            responseNode.put("command", "payOnline");
            responseNode.set("output", outputNode);
            responseNode.put("timestamp", timestamp);

            output.add(responseNode);
        }
    }
}
