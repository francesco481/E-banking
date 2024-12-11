package org.poo.command;

import org.poo.fileio.CommandInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Alias;
import org.poo.management.Database;

public final class SetAlias implements Order {
    private final Database database;
    private final CommandInput command;

    public SetAlias(final Database database, final CommandInput command) {
        this.database = database;
        this.command = command;
    }

    /**
     * Executes the command to add an alias to an account.
     * This method locates the specified account associated with the user's email,
     * and then adds the given alias to it. If the user is not found, the operation
     * is aborted.
     *
     * @param timestamp the timestamp at which the command is executed.
     *                  This is used for logging or tracking the operation.
     */
    @Override
    public void execute(final int timestamp) {
        int i = database.findUser(command.getEmail());

        if (i == -1) {
            return;
        }

        for (AccountType account : database.getAccounts().get(i)) {
            if (((Account) account).getIban().equals(command.getAccount())) {
                database.addAlias(new Alias(command.getEmail(), command.getAlias(),
                        command.getAccount()));
            }
        }
    }
}
