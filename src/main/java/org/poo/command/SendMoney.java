package org.poo.command;

import org.poo.fileio.CommandInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Database;

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

        for (ArrayList<AccountType> accounts : this.database.getAccounts()) {
            int i = Database.findIBAN(accounts, account1);
            if (i != -1) {
                ok++;
                sender = accounts.get(i);
            }

            i = Database.findIBAN(accounts, account2);
            if (i != -1) {
                receiver = accounts.get(i);
                ok++;
            }
        }

        if (ok != 2)
            return;

        if (sender.getBalance() < command.getAmount())
            return;

        sender.pay(command.getAmount());
        double amount  = command.getAmount() * Database.getRate(((Account) sender).getCurrency(), ((Account) receiver).getCurrency());
        ((Account) receiver).addFunds(amount);
    }
}
