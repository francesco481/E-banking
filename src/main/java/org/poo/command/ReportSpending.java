package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Database;

import javax.xml.crypto.Data;

public class ReportSpending implements Order {
    Database database;
    CommandInput command;
    ObjectMapper mapper;
    ArrayNode output;

    public ReportSpending(Database database, CommandInput command, ObjectMapper mapper, ArrayNode output) {
        this.database = database;
        this.command = command;
        this.mapper = mapper;
        this.output = output;
    }

    @Override
    public void execute(int timestamp) {
        for (int i = 0 ; i < database.getUsers().size() ; i++) {
            for (AccountType account : database.getAccounts().get(i)) {
                if (((Account) account).getIBAN().equals(command.getAccount())) {
                    ObjectNode outputNode = account.printSpendings(command.getStartTimestamp(), command.getEndTimestamp(), command.getTimestamp());
                    output.add(outputNode);
                    return;
                }
            }
        }

        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("command", "spendingsReport");

        ObjectNode errorOutput = mapper.createObjectNode();
        errorOutput.put("description", "Account not found");
        errorOutput.put("timestamp", timestamp);

        outputNode.set("output", errorOutput);
        outputNode.put("timestamp", timestamp);
        output.add(outputNode);
    }
}
