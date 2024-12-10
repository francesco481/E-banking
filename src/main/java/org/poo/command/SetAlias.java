package org.poo.command;

import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Alias;
import org.poo.management.Database;

public class SetAlias implements Order {
    Database database;
    CommandInput command;

    public SetAlias(Database database, CommandInput command) {
        this.database = database;
        this.command = command;
    }

    @Override
    public void execute(int timestamp) {
        int i = database.findUser(command.getEmail());

        if (i == -1)
            return;

        for (AccountType account : database.getAccounts().get(i))
        {
            if (((Account) account).getIBAN().equals(command.getAccount())) {
                database.addAlias(new Alias(command.getEmail(), command.getAlias(), command.getAccount()));
            }
        }
    }
}
