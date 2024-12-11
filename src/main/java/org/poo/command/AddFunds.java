package org.poo.command;

import org.poo.fileio.CommandInput;
import org.poo.management.Database;

public final class AddFunds implements Order {
    private final Database database;
    private final CommandInput command;

    public AddFunds(final Database database, final CommandInput command) {
        this.database = database;
        this.command = command;
    }

    /**
     * Executes the command to add funds to an account in the database.
     * This method encapsulates the action of updating the balance of
     * an account based on the provided command input.
     *
     * @param timestamp the timestamp at which the command is executed.
     *                  Typically used for logging or tracking the operation.
     */
    @Override
    public void execute(final int timestamp) {
        database.addFunds(command);
    }
}
