package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.management.Database;

public final class DeleteAccount implements Order {
    private final Database database;
    private final CommandInput command;
    private final ObjectMapper mapper;
    private final ArrayNode output;

    public DeleteAccount(final Database database, final CommandInput command,
                         final ObjectMapper mapper, final ArrayNode output) {
        this.database = database;
        this.command = command;
        this.mapper = mapper;
        this.output = output;
    }

    /**
     * Executes the command to delete an account from the database.
     * The method attempts to remove the specified account and generates a JSON response
     * indicating whether the operation was successful or failed. If the account could not
     * be deleted, an error message is included in the response with details for further
     * investigation.
     *
     * @param timestamp the timestamp at which the command is executed.
     *                  Typically used for logging or tracking the operation.
     */
    @Override
    public void execute(final int timestamp) {
        int ok = database.deleteAccount(command);
        String message = (ok == 1) ? "Account deleted" : "Account couldn't be deleted - "
                + "see org.poo.transactions for details";

        ObjectNode outputNode = mapper.createObjectNode();
        if (ok == 1) {
            outputNode.put("success", message);
        } else {
            outputNode.put("error", message);
        }
        outputNode.put("timestamp", timestamp);

        ObjectNode responseNode = mapper.createObjectNode();
        responseNode.put("command", "deleteAccount");
        responseNode.set("output", outputNode);
        responseNode.put("timestamp", timestamp);

        output.add(responseNode);
    }
}
