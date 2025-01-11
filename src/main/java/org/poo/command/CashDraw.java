package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.Database;
import org.poo.management.Transactions;
import org.poo.management.accounts.Account;
import org.poo.management.accounts.AccountType;
import org.poo.management.cards.Card;

public final class CashDraw implements Order {
    private final Database database;
    private final CommandInput command;
    private final ArrayNode output;

    private static final double STANDARD_COMISION = 1.002;
    private static final double SILVER_COMISION = 1.001;
    private static final int SILVER_AMOUNT = 500;
    private static final int GOLD_AMOUNT = 300;
    private static final int PRAG = 5;

    public CashDraw(final Database database, final CommandInput command, final ArrayNode output) {
        this.database = database;
        this.command = command;
        this.output = output;
    }

    @Override
    public void execute(final int timestamp) {
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
                        comision = STANDARD_COMISION;
                    }

                    if (user.getPlan().equals("silver") && command.getAmount() >= SILVER_AMOUNT) {
                        comision = SILVER_COMISION;
                    }

                    if (amount * comision > account.getBalance()) {
                        Transactions transactions = new Transactions("Insufficient funds",
                                                                    command.getTimestamp());
                        user.addTransaction(transactions);
                        ((Account) account).addTransaction(transactions);
                        return;
                    }

                    if (account.getBalance() - ((Account) account).getMinimum() >= amount * comision
                            && card.getStatus().equals("active")) {
                        account.pay(amount * comision);

                        Transactions transactions = new Transactions("Cash withdrawal of "
                                                    + String.format("%.1f", command.getAmount()),
                                                    command.getTimestamp(), command.getAmount());
                        user.addTransaction(transactions);
                        ((Account) account).addTransaction(transactions);

                        if (user.getPlan().equals("silver") && command.getAmount() >= GOLD_AMOUNT) {
                            user.increaseGold();

                            if (user.getGold() >= PRAG) {
                                user.setPlan("gold");
                            }
                        }
                        return;
                    }

                    if (account.getBalance() - ((Account) account).getMinimum() < amount * comision
                            && card.getStatus().equals("active")) {
                        Transactions transactions = new Transactions("Cannot perform payment due"
                                + " to a minimum balance being set", timestamp);

                        user.addTransaction(transactions);
                        ((Account) account).addTransaction(transactions);
                        return;
                    }
                }
            }

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
