package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Cards.Card;
import org.poo.management.Cards.CardType;
import org.poo.management.Database;
import org.poo.management.Transactions;

import java.util.ArrayList;

public class PayOnline implements Order {
    Database database;
    CommandInput command;
    ObjectMapper mapper;
    ArrayNode output;

    public PayOnline(Database database, CommandInput command, ObjectMapper mapper, ArrayNode output) {
        this.database = database;
        this.command = command;
        this.mapper = mapper;
        this.output = output;
    }

    @Override
    public void execute(int timestamp) {
        int i = database.findUser(command.getEmail());
        if (i == -1)
            return;

        ArrayList<AccountType> curr = database.getAccounts().get(i);
        int ok = 1;
        for (AccountType account : curr) {
            for (CardType card :  ((Account) account).getCards()) {
                if (((Card) card).getCardNumber().equals(command.getCardNumber())) {
                    ok = 0;
                    if (((Card) card).getStatus().equals("frozen")) {
                        UserInput user = database.getUsers().get(i);
                        user.addTransaction(new Transactions("The card is frozen", timestamp));
                        ((Account) account).addTransaction(new Transactions("The card is frozen", timestamp));
                        return;
                    }
                    double amount = command.getAmount() * Database.getRate(command.getCurrency(), ((Account) account).getCurrency());
                    if (account.getBalance() - ((Account) account).getMinimum() >= amount &&
                            ((Card) card).getStatus().equals("active")) {
                        account.pay(amount);
                        card.pay();

                        UserInput user = database.getUsers().get(i);
                        ok = 2;
                        user.addTransaction(new Transactions("Card payment", timestamp, command.getCommerciant(), amount));
                        ((Account) account).addTransaction(new Transactions("Card payment", timestamp, command.getCommerciant(), amount));

                        if (!((Card) card).getCardNumber().equals(command.getCardNumber())) {
                            ((Account) account).addTransaction(new Transactions("The card has been destroyed", timestamp, command.getCardNumber(), user.getEmail(), ((Account) account).getIBAN()));
                            user.addTransaction(new Transactions("The card has been destroyed", timestamp, command.getCardNumber(), user.getEmail(), ((Account) account).getIBAN()));

                            ((Account) account).addTransaction(new Transactions("New card created", timestamp, ((Card) card).getCardNumber(), user.getEmail(), ((Account) account).getIBAN()));
                            user.addTransaction(new Transactions("New card created", timestamp, ((Card) card).getCardNumber(), user.getEmail(), ((Account) account).getIBAN()));
                        }
                    }
                    if (account.getBalance() - ((Account) account).getMinimum() < amount &&
                            ((Card) card).getStatus().equals("active")) {
                        UserInput user = database.getUsers().get(i);
                        user.addTransaction(new Transactions("Insufficient funds", timestamp));
                        ((Account) account).addTransaction(new Transactions("Insufficient funds", timestamp));
                    }

                    if (account.getBalance() < ((Account) account).getMinimum()) {

                    }
                }
            }
        }

        if (ok == 1)
        {
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
