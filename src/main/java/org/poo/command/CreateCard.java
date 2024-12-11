package org.poo.command;

import org.poo.fileio.CommandInput;
import org.poo.management.Database;

public final class CreateCard implements Order {
    private final Database database;
    private final CommandInput command;

    public CreateCard(final Database database, final CommandInput command) {
        this.database = database;
        this.command = command;
    }

    /**
     * Executes the command to add a new card to the database.
     * This method encapsulates the action of creating and associating
     * a new card with an account based on the provided command input.
     *
     * @param timestamp the timestamp at which the command is executed.
     *                  This can be used for logging or auditing purposes.
     */
    @Override
    public void execute(final int timestamp) {
        database.addCard(command);
    }
}
