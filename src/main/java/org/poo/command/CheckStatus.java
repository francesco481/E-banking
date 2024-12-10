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

public class CheckStatus implements Order {
    Database database;
    CommandInput command;
    ObjectMapper mapper;
    ArrayNode output;

    public CheckStatus(Database database, CommandInput command, ObjectMapper mapper, ArrayNode output)
    {
        this.database = database;
        this.command = command;
        this.mapper = mapper;
        this.output = output;
    }

    @Override
    public void execute(int timestamp) {
        int st = 0;
        int ok = 1;
        for (ArrayList<AccountType> accounts : database.getAccounts()) {
            for (AccountType account : accounts) {
                Account currAcc = (Account) account;
                for (CardType card : currAcc.getCards()) {
                    Card curr = (Card) card;
                    if (curr.getCardNumber().equals(command.getCardNumber())) {
                        ok = 0;
                        if (curr.getStatus().equals("active")) {
                            if (currAcc.getBalance() <= currAcc.getMinimum()) {
                                curr.setStatus("frozen");
                                UserInput user = database.getUsers().get(st);
                                user.addTransaction(new Transactions("You have reached the minimum amount of funds, the card will be frozen", timestamp));
                                currAcc.addTransaction(new Transactions("You have reached the minimum amount of funds, the card will be frozen", timestamp));
                            }
                        }
                    }
                }
            }
            st++;
        }

        if (ok == 1) {
            ObjectNode outputMessage = mapper.createObjectNode();
            outputMessage.put("command", "checkCardStatus");
            ObjectNode outputDetails = mapper.createObjectNode();
            outputDetails.put("timestamp", timestamp);
            outputDetails.put("description", "Card not found");
            outputMessage.set("output", outputDetails);
            outputMessage.put("timestamp", timestamp);
            output.add(outputMessage);
        }
    }
}
