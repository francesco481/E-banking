package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.Database;
import org.poo.management.Transactions;
import org.poo.utils.ShowTransaction;

public class PrintTransactions implements Order {
    Database database;
    ObjectMapper mapper;
    ArrayNode output;
    CommandInput command;

    public PrintTransactions(Database database, ObjectMapper mapper, ArrayNode output, CommandInput command) {
        this.database = database;
        this.mapper = mapper;
        this.output = output;
        this.command = command;
    }

    @Override
    public void execute(int timestamp) {
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
