package org.poo.command;

import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Database;
import org.poo.management.Transactions;

import java.util.ArrayList;

public class SendMoney implements Order {
    Database database;
    CommandInput command;

    public SendMoney(Database database, CommandInput command) {
        this.database = database;
        this.command = command;
    }

    @Override
    public void execute(int timestamp) {
        String account1 = command.getAccount();
        String account2 = command.getReceiver();
        AccountType sender = null;
        AccountType receiver = null;
        int ok = 0;
        int st = 0;
        UserInput user1 = null;
        UserInput user2= null;

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

        if (ok != 2)
            return;

        assert sender != null;
        if (sender.getBalance() < command.getAmount())
            return;
        sender.pay(command.getAmount());

        assert receiver != null;
        double amount  = command.getAmount() * Database.getRate(((Account) sender).getCurrency(), ((Account) receiver).getCurrency());
        ((Account) receiver).addFunds(amount);

        assert user1 != null;
        user1.addTransaction(new Transactions(timestamp, command.getDescription(), account1, account2, command.getAmount(), ((Account) sender).getCurrency(), "sent"));

        assert user2 != null;
        user2.addTransaction(new Transactions(timestamp, command.getDescription(), account2, account1, amount, ((Account) receiver).getCurrency(), "received"));
    }
}
