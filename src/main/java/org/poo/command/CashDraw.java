package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.accounts.Account;
import org.poo.management.accounts.AccountType;
import org.poo.management.cards.Card;
import org.poo.management.Database;
import org.poo.management.Transactions;

public class CashDraw implements Order {
    Database database;
    CommandInput command;
    ArrayNode output;

    public CashDraw(Database database, CommandInput command, ArrayNode output) {
        this.database = database;
        this.command = command;
        this.output = output;
    }

    @Override
    public void execute(int timestamp) {
        UserInput user = null;
        int i = 0;
        for (UserInput curr : database.getUsers()) {
            if (curr.getEmail().equals(command.getEmail())) {
                user = curr;
                break;
            }
            i++;
        }

        if (user == null) {
            //user not found
            return;
        }

        for (AccountType account : database.getAccounts().get(i)) {
            for (Card card : ((Account) account).getCards()) {
                if (card.getCardNumber().equals(command.getCardNumber())) {
                    if (card.getStatus().equals("frozen")) {
                        //card is frozen
                        return;
                    }

                    double amount = command.getAmount() * Database.getRate("RON",
                                            ((Account) account).getCurrency());
                    double comision = 1;

                    if (user.getPlan().equals("standard")) {
                        comision = 1.002;
                    }

                    if (user.getPlan().equals("silver")  &&  command.getAmount() >= 500) {
                        comision = 1.001;
                    }

                    if (amount*comision > account.getBalance()) {
                        user.addTransaction(new Transactions("Insufficient funds", command.getTimestamp()));
                        ((Account) account).addTransaction(new Transactions("Insufficient funds", command.getTimestamp()));
                        return;
                    }

                    if (account.getBalance() - ((Account) account).getMinimum() >= amount*comision
                            && card.getStatus().equals("active")) {
                        account.pay(amount * comision);

                        user.addTransaction(new Transactions("Cash withdrawal of " + String.format("%.1f", command.getAmount()), command.getTimestamp(), command.getAmount()));
                        ((Account) account).addTransaction(new Transactions("Cash withdrawal of " + String.format("%.1f", command.getAmount()), command.getTimestamp(), command.getAmount()));

                        if (user.getPlan().equals("silver")  &&  command.getAmount() >= 300) {
                            user.increaseGold();

                            if (user.getGold() >= 5) {
                                user.setPlan("gold");
                            }
                        }
                        return;
                    }

                    if (account.getBalance() - ((Account) account).getMinimum() < amount*comision
                            && card.getStatus().equals("active")) {
                        Transactions transactions = new Transactions("Cannot perform payment due to a minimum balance being set",
                                timestamp);

                        user.addTransaction(transactions);
                        ((Account) account).addTransaction(transactions);
                        return;
                    }
                }
            }

//            for (String card : ((Account) account).getUsed()) {
//                if (card.equals(command.getCardNumber())) {
//                    //used card
//                    return;
//                }
//            }

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode commandNode = mapper.createObjectNode();
            commandNode.put("command", "cashWithdrawal");

            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", command.getTimestamp());
            outputNode.put("description", "Card not found");

            commandNode.set("output", outputNode);
            commandNode.put("timestamp", timestamp);

            output.add(commandNode);
            return;
        }
    }
}
