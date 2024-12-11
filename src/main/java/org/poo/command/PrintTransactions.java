package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.Database;
import org.poo.management.Transactions;
import org.poo.utils.ShowTransaction;

public final class PrintTransactions implements Order {
    private final Database database;
    private final ObjectMapper mapper;
    private final ArrayNode output;
    private final CommandInput command;

    public PrintTransactions(final Database database, final ObjectMapper mapper,
                             final ArrayNode output, final CommandInput command) {
        this.database = database;
        this.mapper = mapper;
        this.output = output;
        this.command = command;
    }

    /**
     * Executes the command to print all transactions for a specific user.
     * This method retrieves the transactions associated with the user's email and
     * formats them into a JSON array. The resulting JSON is used to display the
     * user's transaction history.
     *
     * @param timestamp the timestamp at which the command is executed.
     *                  This is typically used for logging or tracking the operation.
     */
    @Override
    public void execute(final int timestamp) {
        for (UserInput user : database.getUsers()) {
            if (user.getEmail().equals(command.getEmail())) {
                ArrayNode transactionsArray = mapper.createArrayNode();

                for (Transactions transaction : user.getTransactions()) {
                    ShowTransaction.extract(mapper, transaction, transactionsArray);
                }

                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("command", "printTransactions");
                outputNode.set("output", transactionsArray);
                outputNode.put("timestamp", timestamp);

                output.add(outputNode);
                return;
            }
        }
    }
}
