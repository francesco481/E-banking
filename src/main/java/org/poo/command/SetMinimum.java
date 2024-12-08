package org.poo.command;

import org.poo.fileio.CommandInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Database;

import java.util.ArrayList;

public class SetMinimum implements Order {
    Database database;
    CommandInput command;

    public SetMinimum(Database database, CommandInput command) {
        this.database = database;
        this.command = command;
    }

    @Override
    public void execute(int timestamp) {
        int ok = 1;

        for (ArrayList<AccountType> accounts : database.getAccounts()) {
            for (AccountType account : accounts) {
                if (((Account) account).getIBAN().equals(command.getAccount()))
                {
                    ((Account) account).setMinimum(command.getAmount());
                    ok = 0;
                    return;
                }
            }
        }

        if (ok == 1){

        }
    }
}
