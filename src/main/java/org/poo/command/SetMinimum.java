package org.poo.command;

import org.poo.fileio.CommandInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Database;

import java.util.ArrayList;

public final class SetMinimum implements Order {
    private final Database database;
    private final CommandInput command;

    public SetMinimum(final Database database, final CommandInput command) {
        this.database = database;
        this.command = command;
    }

    /**
     * Executes the command to set the minimum balance for a specific account.
     * This method finds the specified account by its IBAN and updates its minimum balance
     * to the provided amount. If the account is not found, the operation does nothing.
     *
     * @param timestamp the timestamp at which the command is executed.
     *                  Used for logging or tracking the operation.
     */
    @Override
    public void execute(final int timestamp) {
        for (ArrayList<AccountType> accounts : database.getAccounts()) {
            for (AccountType account : accounts) {
                if (((Account) account).getIban().equals(command.getAccount())) {
                    ((Account) account).setMinimum(command.getAmount());
                    return;
                }
            }
        }
    }
}
