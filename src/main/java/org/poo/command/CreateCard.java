package org.poo.command;

import org.poo.fileio.CommandInput;
import org.poo.management.Database;

public class CreateCard implements Order {
    Database database;
    CommandInput command;

    public CreateCard(Database database, CommandInput command) {
        this.database = database;
        this.command = command;
    }
    @Override
    public void execute(int timestamp) {
        database.addCard(command);
    }
}
