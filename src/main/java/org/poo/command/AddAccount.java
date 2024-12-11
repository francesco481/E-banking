package org.poo.command;

import org.poo.fileio.CommandInput;
import org.poo.management.Database;

public final class AddAccount implements Order {
    private final Database database;
    private final CommandInput command;

    public AddAccount(final Database database, final CommandInput command) {
        this.database = database;
        this.command = command;
    }

    /**
     * Executes the command to add a new account to the database.
     * This method is part of the Command design pattern and encapsulates
     * the action of creating and adding an account based on the provided input.
     *
     * @param timestamp the timestamp at which the command is executed.
     *                  This can be used for logging or auditing the action.
     */
    @Override
    public void execute(final int timestamp) {
        database.addAccount(command);
    }
}
