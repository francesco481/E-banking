package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.accounts.Account;
import org.poo.management.accounts.AccountType;
import org.poo.management.Alias;
import org.poo.management.Database;
import org.poo.management.Transactions;

import java.util.ArrayList;

public final class SendMoney implements Order {
    private final Database database;
    private final CommandInput command;
    private final ArrayNode output;

    public SendMoney(final Database database, final CommandInput command,
                     final ArrayNode output) {
        this.database = database;
        this.command = command;
        this.output = output;
    }

    /**
     * Executes the command to transfer funds from one account to another.
     * This method verifies both the sender and receiver accounts, checks for sufficient funds,
     * processes the transfer, and records the transaction for both the sender and receiver.
     * If the transfer fails (e.g., insufficient funds, missing accounts), appropriate error
     * messages or actions are taken.
     *
     * @param timestamp the timestamp at which the command is executed.
     *                  Used for logging or tracking the operation.
     */
    @Override
    public void execute(final int timestamp) {
        String account1 = command.getAccount();
        String account2 = command.getReceiver();
        AccountType sender = null;
        AccountType receiver = null;
        int ok = 0;
        int st = 0;
        UserInput user1 = null;
        UserInput user2 = null;

        for (ArrayList<AccountType> accounts : this.database.getAccounts()) {
            int i = Database.findIBAN(accounts, account1);
            if (i != -1) {
                ok++;
                sender = accounts.get(i);
                user1 = database.getUsers().get(st);
            }

            i = Database.findIBAN(accounts, account2);
            if (i != -1) {
                ok++;
                receiver = accounts.get(i);
                user2 = database.getUsers().get(st);
            }

            st++;
        }

        if (sender != null && receiver == null) {
            for (Alias alias : database.getAliases()) {
                if (alias.getName().equals(account2)) {

                    st = 0;
                    for (ArrayList<AccountType> accounts : this.database.getAccounts()) {

                        int i = Database.findIBAN(accounts, alias.getIban());
                        if (i != -1) {
                            ok++;
                            receiver = accounts.get(i);
                            user2 = database.getUsers().get(st);
                        }

                        st++;
                    }
                }
            }
        }

        if (ok != 2) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "User not found");

            ObjectNode responseNode = mapper.createObjectNode();
            responseNode.put("command", "sendMoney");
            responseNode.set("output", outputNode);
            responseNode.put("timestamp", timestamp);

            output.add(responseNode);
            return;
        }

        assert sender != null;
        double ronAmount = command.getAmount() * Database.getRate(((Account) sender).getCurrency(), "RON");

        double comision = 1;

        if (user1.getPlan().equals("standard")) {
            comision = 1.002;
        }

        if (user1.getPlan().equals("silver")  &&  ronAmount >= 500) {
            comision = 1.001;
        }

        if (sender.getBalance() < command.getAmount()*comision) {
            Transactions transactions = new Transactions("Insufficient funds",
                                                            command.getTimestamp());

            user1.addTransaction(transactions);
            ((Account) sender).addTransaction(transactions);
            return;
        }

        sender.pay(command.getAmount() * comision);

        double amount  = command.getAmount() * Database.getRate(((Account) sender).getCurrency(),
                                                             ((Account) receiver).getCurrency());
        ((Account) receiver).addFunds(amount);

        Transactions transactions = new Transactions(timestamp, command.getDescription(), account1,
               account2, command.getAmount(), ((Account) sender).getCurrency(), "sent");

        user1.addTransaction(transactions);
        ((Account) sender).addTransaction(transactions);

        assert user2 != null;
        transactions = new Transactions(timestamp, command.getDescription(), account1, account2,
                            amount, ((Account) receiver).getCurrency(), "received");

        user2.addTransaction(transactions);
        ((Account) receiver).addTransaction(transactions);


        if (user1.getPlan().equals("silver")  &&  ronAmount >= 300) {
            user1.increaseGold();

            if (user1.getGold() >= 5) {
                user1.setPlan("gold");
                String iban = ((Account) sender).getIban();
                user1.addTransaction(new Transactions("Upgrade plan", command.getTimestamp(), iban, "gold"));
                ((Account) sender).addTransaction(new Transactions("Upgrade plan", command.getTimestamp(), iban, "gold"));
            }
        }
    }
}
