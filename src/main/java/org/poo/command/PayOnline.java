package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Cards.Card;
import org.poo.management.Cards.CardType;
import org.poo.management.Database;

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
                    double amount = command.getAmount() * Database.getRate(command.getCurrency(), ((Account) account).getCurrency());
                   // System.out.println(command.getAmount() + " " + command.getCurrency() + " " + amount);
                    if (account.getBalance() >= amount  &&
                        ((Card) card).getStatus().equals("active")) {
                        account.pay(amount);
                        card.pay();
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
