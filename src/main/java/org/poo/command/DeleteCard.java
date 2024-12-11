package org.poo.command;

import org.poo.fileio.CommandInput;
import org.poo.management.Database;

public final class DeleteCard implements Order {
    private final Database database;
    private final CommandInput command;

    public DeleteCard(final Database database, final CommandInput command) {
        this.database = database;
        this.command = command;
    }

    /**
     * Executes the command to delete a card from the database.
     * This method removes the card specified in the command input
     * and ensures the database reflects the updated state.
     *
     * @param timestamp the timestamp at which the command is executed.
     *                  Used for logging or auditing the operation.
     */
    @Override
    public void execute(final int timestamp) {
        database.deleteCard(command);
    }
}
