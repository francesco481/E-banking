package org.poo.command;

import org.poo.fileio.CommandInput;
import org.poo.management.Database;

public class AddAccount implements Order {
    Database database;
    CommandInput command;

    public AddAccount(Database database, CommandInput command) {
        this.database = database;
        this.command = command;
    }
    @Override
    public void execute(int timestamp) {
        database.addAccount(command);
    }
}
