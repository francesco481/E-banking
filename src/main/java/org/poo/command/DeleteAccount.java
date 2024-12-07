package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.management.Database;

public class DeleteAccount implements Order {
    Database database;
    CommandInput command;
    ObjectMapper mapper;
    ArrayNode output;

    public DeleteAccount(Database database, CommandInput command, ObjectMapper mapper, ArrayNode output) {
        this.database = database;
        this.command = command;
        this.mapper = mapper;
        this.output = output;
    }

    @Override
    public void execute(int timestamp)
    {
        int ok = database.deleteAccount(command);
        String successMessage = (ok == 1) ? "Account deleted" : "Failed to delete account";

        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("success", successMessage);
        outputNode.put("timestamp", timestamp);

        ObjectNode responseNode = mapper.createObjectNode();
        responseNode.put("command", "deleteAccount");
        responseNode.set("output", outputNode);
        responseNode.put("timestamp", timestamp);

        output.add(responseNode);
    }
}
